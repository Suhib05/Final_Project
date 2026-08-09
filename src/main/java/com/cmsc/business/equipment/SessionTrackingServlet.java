package com.cmsc.business.equipment;

import com.cmsc.dao.ConsumableDAO;
import com.cmsc.dao.ConsumableDAOImpl;
import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.SessionDAO;
import com.cmsc.dao.SessionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Consumable;
import com.cmsc.model.Equipment;
import com.cmsc.model.Session;
import com.cmsc.model.User;
import com.cmsc.observer.InventoryObserver;
import com.cmsc.observer.MaintenanceObserver;
import com.cmsc.strategy.LedgerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Handles equipment check-in and check-out sessions.
 *
 * The servlet records session duration, updates equipment status, subtracts
 * consumed materials and notifies maintenance and inventory observers.
 *
 * @author Saleh Alhajj
 */
@WebServlet(name = "SessionTrackingServlet", urlPatterns = {"/sessions"})
public class SessionTrackingServlet extends HttpServlet {

    private EquipmentDAO equipmentDAO;
    private SessionDAO sessionDAO;
    private ConsumableDAO consumableDAO;
    private UserDAO userDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the DAOs required for session tracking, plus the ledger
     * service used to charge the member when a session ends.
     */
    @Override
    public void init() {
        equipmentDAO = new EquipmentDAOImpl();
        sessionDAO = new SessionDAOImpl();
        consumableDAO = new ConsumableDAOImpl();
        userDAO = new UserDAOImpl();
        ledgerService = new LedgerService();
    }

    /**
     * Redirects direct GET requests to the equipment-status report.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException when the redirect cannot be written
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/equipment"
        );
    }

    /**
     * Starts or ends an equipment session.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when request processing fails
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = requireParameter(request, "action");

            switch (action) {
                case "checkIn" -> checkIn(request);
                case "checkOut" -> checkOut(request);
                default -> throw new IllegalArgumentException(
                        "Unknown session action."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/equipment?session=" + action
            );
        } catch (IllegalArgumentException exception) {
            redirectWithError(
                    request, response, exception.getMessage()
            );
        } catch (RuntimeException exception) {
            redirectWithError(
                    request,
                    response,
                    "Unable to process the equipment session."
            );
        }
    }

    private void checkIn(HttpServletRequest request) {
        int userId = requirePositiveInteger(request, "userId");
        int equipmentId = requirePositiveInteger(
                request, "equipmentId"
        );
        Integer consumableId = readOptionalPositiveInteger(
                request, "consumableId"
        );

        Equipment equipment = equipmentDAO.findById(equipmentId);

        if (equipment == null) {
            throw new IllegalArgumentException(
                    "The selected equipment does not exist."
            );
        }

        if (!"AVAILABLE".equals(equipment.getStatus())) {
            throw new IllegalArgumentException(
                    "The selected equipment is not available."
            );
        }

        if (sessionDAO.findActiveByEquipment(equipmentId) != null) {
            throw new IllegalArgumentException(
                    "The equipment already has an active session."
            );
        }

        if (consumableId != null) {
            Consumable consumable
                    = consumableDAO.findById(consumableId);

            if (consumable == null) {
                throw new IllegalArgumentException(
                        "The selected consumable does not exist."
                );
            }

            if (consumable.getStockLevel() <= 0) {
                throw new IllegalArgumentException(
                        "The selected consumable is out of stock."
                );
            }
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setEquipmentId(equipmentId);
        session.setConsumableId(consumableId);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(null);
        session.setMaterialsConsumed(0.0);

        if (sessionDAO.createSession(session) == null) {
            throw new IllegalStateException(
                    "The equipment session could not be created."
            );
        }

        equipment.updateStatus("IN_USE");

        if (equipmentDAO.update(equipment) == null) {
            throw new IllegalStateException(
                    "The equipment status could not be updated."
            );
        }
    }

    private void checkOut(HttpServletRequest request) {
        int equipmentId = requirePositiveInteger(
                request, "equipmentId"
        );
        double materialsConsumed = requireNonNegativeNumber(
                request, "materialsConsumed"
        );

        Session activeSession
                = sessionDAO.findActiveByEquipment(equipmentId);

        if (activeSession == null) {
            throw new IllegalArgumentException(
                    "No active session exists for this equipment."
            );
        }

        Equipment equipment = equipmentDAO.findById(equipmentId);

        if (equipment == null) {
            throw new IllegalArgumentException(
                    "The selected equipment does not exist."
            );
        }

        updateConsumableStock(activeSession, materialsConsumed);

        activeSession.endSession();
        activeSession.setMaterialsConsumed(materialsConsumed);

        double sessionHours = activeSession.calculateUsage();

        if (sessionDAO.updateSession(activeSession) == null) {
            throw new IllegalStateException(
                    "The equipment session could not be completed."
            );
        }

        MaintenanceObserver maintenanceObserver
                = new MaintenanceObserver();
        InventoryObserver inventoryObserver
                = new InventoryObserver(consumableDAO);

        equipment.attach(maintenanceObserver);
        equipment.attach(inventoryObserver);

        equipment.logUsage(sessionHours);
        equipment.updateStatus("AVAILABLE");

        if (equipmentDAO.update(equipment) == null) {
            throw new IllegalStateException(
                    "The equipment usage could not be updated."
            );
        }

        chargeForSession(activeSession, equipment, sessionHours,
                materialsConsumed);
    }

    /**
     * Debits the member for the session they just finished (FR-06).
     *
     * The charge is the access hours used multiplied by the equipment's credit
     * rate, plus the materials consumed. The arithmetic and the ledger write
     * belong to the Ledger module, so this servlet only supplies the figures.
     *
     * @param session the session that has just ended
     * @param equipment the equipment it ran on, carrying the credit rate
     * @param sessionHours hours the session ran for
     * @param materialsConsumed quantity of material used
     */
    private void chargeForSession(Session session, Equipment equipment,
            double sessionHours, double materialsConsumed) {

        if (sessionHours <= 0 && materialsConsumed <= 0) {
            return;
        }

        User user = userDAO.findById(session.getUserId());

        if (user == null) {
            return;
        }

        ledgerService.recordEquipmentUse(
                user,
                sessionHours,
                equipment.getAccessCreditRate(),
                materialsConsumed,
                session.getSessionId()
        );
    }

    private void updateConsumableStock(
            Session activeSession, double materialsConsumed) {

        Integer consumableId = activeSession.getConsumableId();

        if (consumableId == null) {
            if (materialsConsumed > 0) {
                throw new IllegalArgumentException(
                        "No consumable was selected for this session."
                );
            }

            return;
        }

        Consumable consumable
                = consumableDAO.findById(consumableId);

        if (consumable == null) {
            throw new IllegalArgumentException(
                    "The session consumable no longer exists."
            );
        }

        if (materialsConsumed > consumable.getStockLevel()) {
            throw new IllegalArgumentException(
                    "Materials consumed cannot exceed the current stock."
            );
        }

        consumable.setStockLevel(
                consumable.getStockLevel() - materialsConsumed
        );

        if (consumableDAO.update(consumable) == null) {
            throw new IllegalStateException(
                    "The consumable stock could not be updated."
            );
        }
    }

    private void redirectWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws IOException {

        String encodedMessage = URLEncoder.encode(
                message, StandardCharsets.UTF_8
        );

        response.sendRedirect(
                request.getContextPath()
                + "/equipment?sessionError=" + encodedMessage
        );
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

    private int requirePositiveInteger(
            HttpServletRequest request, String parameterName) {

        String value = requireParameter(request, parameterName);

        try {
            int parsedValue = Integer.parseInt(value);

            if (parsedValue <= 0) {
                throw new IllegalArgumentException(
                        parameterName + " must be greater than zero."
                );
            }

            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    parameterName + " must be a valid whole number."
            );
        }
    }

    private Integer readOptionalPositiveInteger(
            HttpServletRequest request, String parameterName) {

        String value = request.getParameter(parameterName);

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            int parsedValue = Integer.parseInt(value);

            if (parsedValue <= 0) {
                throw new IllegalArgumentException(
                        parameterName + " must be greater than zero."
                );
            }

            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    parameterName + " must be a valid whole number."
            );
        }
    }

    private double requireNonNegativeNumber(
            HttpServletRequest request, String parameterName) {

        String value = requireParameter(request, parameterName);

        try {
            double parsedValue = Double.parseDouble(value);

            if (parsedValue < 0) {
                throw new IllegalArgumentException(
                        parameterName + " cannot be negative."
                );
            }

            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    parameterName + " must be a valid number."
            );
        }
    }
}