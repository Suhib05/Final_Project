package com.cmsc.business.equipment;

import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.factory.EquipmentFactory;
import com.cmsc.model.Equipment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.cmsc.dao.ConsumableDAO;
import com.cmsc.dao.ConsumableDAOImpl;
import com.cmsc.dao.SessionDAO;
import com.cmsc.dao.SessionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Consumable;
import com.cmsc.model.Session;
import com.cmsc.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles equipment registration and equipment-status reporting.
 *
 * This servlet belongs to the Business layer. Database operations are
 * delegated to EquipmentDAO, while equipment creation is delegated to the
 * Simple Factory implementation.
 *
 * @author Saleh Alhajj
 */
@WebServlet(name = "EquipmentServlet", urlPatterns = {"/equipment"})
public class EquipmentServlet extends HttpServlet {

    private static final String EQUIPMENT_VIEW
            = "/WEB-INF/views/equipment-status.jsp";

    private EquipmentDAO equipmentDAO;
    private SessionDAO sessionDAO;
    private ConsumableDAO consumableDAO;
    private UserDAO userDAO;

    /**
    * Initializes the DAOs required by this servlet.
    */
    @Override
    public void init() {
        equipmentDAO = new EquipmentDAOImpl();
        sessionDAO = new SessionDAOImpl();
        consumableDAO = new ConsumableDAOImpl();
        userDAO = new UserDAOImpl();
    }

    /**
     * Displays the current equipment-status report.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isSignedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        displayEquipmentPage(request, response);
    }

    /**
     * Registers equipment submitted by a Shop-Tech or administrator.
     *
     * @param request HTTP request containing equipment information
     * @param response HTTP response
     * @throws ServletException when request processing fails
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isShopTech(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Only a Shop-Tech can register equipment."
            );
            return;
        }

        try {
            String category = requireParameter(request, "category");

            Equipment equipment
                    = EquipmentFactory.createEquipment(category);

            equipment.setAssetTag(
                    requireParameter(request, "assetTag")
            );
            equipment.setMake(
                    requireParameter(request, "make")
            );
            equipment.setModel(
                    requireParameter(request, "model")
            );
            equipment.setConsumableType(
                    requireParameter(request, "consumableType")
            );
            equipment.setStatus("AVAILABLE");
            equipment.setHoursOfUse(0.0);
            equipment.setMaintenanceThreshold(
                    requirePositiveNumber(request, "maintenanceThreshold")
            );
            equipment.setAccessCreditRate(
                    requireNonNegativeNumber(request, "accessCreditRate")
            );

            Equipment savedEquipment = equipmentDAO.save(equipment);

            if (savedEquipment == null) {
                throw new IllegalStateException(
                        "The equipment could not be saved."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/equipment?registered=true"
            );
        } catch (IllegalArgumentException exception) {
            request.setAttribute(
                    "errorMessage", exception.getMessage()
            );
            displayEquipmentPage(request, response);
        } catch (RuntimeException exception) {
            request.setAttribute(
                    "errorMessage",
                    "Unable to register the equipment."
            );
            displayEquipmentPage(request, response);
        }
    }

    private void displayEquipmentPage(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    List<Equipment> equipmentList = List.of();
    List<Consumable> consumableList = List.of();

    Map<Integer, Session> activeSessionsByEquipment
            = new LinkedHashMap<>();

    Map<Integer, Double> elapsedHoursByEquipment
            = new LinkedHashMap<>();

    Map<Integer, String> activeUserNamesByEquipment
            = new LinkedHashMap<>();

    Map<Integer, String> consumableNamesById
            = new LinkedHashMap<>();

    try {
        List<Equipment> loadedEquipment
                = equipmentDAO.findAll();

        if (loadedEquipment != null) {
            equipmentList = loadedEquipment;
        }

        List<Consumable> loadedConsumables
                = consumableDAO.findAll();

        if (loadedConsumables != null) {
            consumableList = loadedConsumables;
        }

        for (Consumable consumable : consumableList) {
            consumableNamesById.put(
                    consumable.getConsumableId(), consumable.getType()
            );
        }

        for (Equipment equipment : equipmentList) {
            Session activeSession
                    = sessionDAO.findActiveByEquipment(
                            equipment.getEquipmentId()
                    );

            if (activeSession != null) {
                activeSessionsByEquipment.put(
                        equipment.getEquipmentId(),
                        activeSession
                );

                elapsedHoursByEquipment.put(
                        equipment.getEquipmentId(),
                        activeSession.calculateUsage()
                );

                User activeUser = userDAO.findById(
                        activeSession.getUserId()
                );

                if (activeUser != null) {
                    activeUserNamesByEquipment.put(
                            equipment.getEquipmentId(),
                            activeUser.getName()
                    );
                }
            }
        }
    } catch (RuntimeException exception) {
        if (request.getAttribute("errorMessage") == null) {
            request.setAttribute(
                    "errorMessage",
                    "Unable to load the equipment report."
            );
        }

        equipmentList = List.of();
        consumableList = List.of();
        activeSessionsByEquipment.clear();
        elapsedHoursByEquipment.clear();
        activeUserNamesByEquipment.clear();
        consumableNamesById.clear();
    }

    request.setAttribute("equipmentList", equipmentList);
    request.setAttribute("consumableList", consumableList);
    request.setAttribute(
            "activeSessionsByEquipment",
            activeSessionsByEquipment
    );
    request.setAttribute(
            "elapsedHoursByEquipment",
            elapsedHoursByEquipment
    );
    request.setAttribute(
            "activeUserNamesByEquipment",
            activeUserNamesByEquipment
    );
    request.setAttribute("consumableNamesById", consumableNamesById);

    request.getRequestDispatcher(EQUIPMENT_VIEW)
            .forward(request, response);
}

    private boolean isSignedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }

    private boolean isShopTech(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null
                && session.getAttribute("userId") != null
                && "SHOP_TECH".equals(session.getAttribute("userType"));
    }

    private String requireParameter(
            HttpServletRequest request, String parameterName) {

        String value = request.getParameter(parameterName);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    parameterName + " is required."
            );
        }

        return value.trim();
    }

    private double requirePositiveNumber(
            HttpServletRequest request, String parameterName) {

        double value = parseNumber(request, parameterName);

        if (value <= 0) {
            throw new IllegalArgumentException(
                    parameterName + " must be greater than zero."
            );
        }

        return value;
    }

    private double requireNonNegativeNumber(
            HttpServletRequest request, String parameterName) {

        double value = parseNumber(request, parameterName);

        if (value < 0) {
            throw new IllegalArgumentException(
                    parameterName + " cannot be negative."
            );
        }

        return value;
    }

    private double parseNumber(
            HttpServletRequest request, String parameterName) {

        String value = requireParameter(request, parameterName);

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    parameterName + " must be a valid number."
            );
        }
    }
}
