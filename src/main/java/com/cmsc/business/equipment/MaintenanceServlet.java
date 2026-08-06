package com.cmsc.business.equipment;

import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.MaintenanceDAO;
import com.cmsc.dao.MaintenanceDAOImpl;
import com.cmsc.model.Equipment;
import com.cmsc.model.MaintenanceLog;
import com.cmsc.observer.MaintenanceObserver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates predictive-maintenance alerts and allows Shop-Techs to schedule
 * and complete equipment maintenance.
 *
 * This servlet belongs to the Business layer. Equipment and maintenance-log
 * persistence is delegated to the DAO layer.
 *
 * @author Saleh Alhajj
 */
@WebServlet(name = "MaintenanceServlet", urlPatterns = {"/maintenance"})
public class MaintenanceServlet extends HttpServlet {

    private static final String MAINTENANCE_VIEW
            = "/WEB-INF/views/maintenance-alerts.jsp";

    private EquipmentDAO equipmentDAO;
    private MaintenanceDAO maintenanceDAO;

    /**
     * Initializes the DAOs required by this servlet.
     */
    @Override
    public void init() {
        equipmentDAO = new EquipmentDAOImpl();
        maintenanceDAO = new MaintenanceDAOImpl();
    }

    /**
     * Displays predictive alerts, scheduled equipment and maintenance logs.
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

        displayMaintenancePage(request, response);
    }

    /**
     * Processes maintenance scheduling and completion.
     *
     * Role verification will be performed by the shared front controller
     * after Member 1's authentication module is integrated.
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
                case "schedule" -> scheduleMaintenance(request);
                case "complete" -> completeMaintenance(request);
                default -> throw new IllegalArgumentException(
                        "Unknown maintenance action."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/maintenance?result=" + action
            );
        } catch (IllegalArgumentException exception) {
            request.setAttribute(
                    "errorMessage", exception.getMessage()
            );
            displayMaintenancePage(request, response);
        } catch (RuntimeException exception) {
            request.setAttribute(
                    "errorMessage",
                    "Unable to process the maintenance request."
            );
            displayMaintenancePage(request, response);
        }
    }

    private void scheduleMaintenance(HttpServletRequest request) {
        Equipment equipment = findRequestedEquipment(request);

        equipment.updateStatus("DOWN_FOR_MAINTENANCE");

        if (equipmentDAO.update(equipment) == null) {
            throw new IllegalStateException(
                    "The maintenance task could not be scheduled."
            );
        }
    }

    private void completeMaintenance(HttpServletRequest request) {
        Equipment equipment = findRequestedEquipment(request);

        int shopTechId = requirePositiveInteger(
                request, "shopTechId"
        );
        String description = requireParameter(
                request, "description"
        );

        MaintenanceLog maintenanceLog = new MaintenanceLog();
        maintenanceLog.setEquipmentId(equipment.getEquipmentId());
        maintenanceLog.setShopTechId(shopTechId);
        maintenanceLog.setDescription(description);
        maintenanceLog.setHoursAtService(
                equipment.getHoursOfUse()
        );

        if (maintenanceDAO.save(maintenanceLog) == null) {
            throw new IllegalStateException(
                    "The maintenance log could not be saved."
            );
        }

        /*
         * hoursOfUse represents component wear since the most recent
         * maintenance. It is reset when maintenance is completed.
         */
        equipment.setHoursOfUse(0.0);
        equipment.updateStatus("AVAILABLE");

        if (equipmentDAO.update(equipment) == null) {
            throw new IllegalStateException(
                    "The equipment status could not be updated."
            );
        }
    }

    private Equipment findRequestedEquipment(
            HttpServletRequest request) {

        int equipmentId = requirePositiveInteger(
                request, "equipmentId"
        );

        Equipment equipment = equipmentDAO.findById(equipmentId);

        if (equipment == null) {
            throw new IllegalArgumentException(
                    "The selected equipment does not exist."
            );
        }

        return equipment;
    }

    private void displayMaintenancePage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Equipment> allEquipment = equipmentDAO.findAll();

            if (allEquipment == null) {
                allEquipment = List.of();
            }

            MaintenanceObserver maintenanceObserver
                    = new MaintenanceObserver();

            List<Equipment> scheduledEquipment = new ArrayList<>();

            for (Equipment equipment : allEquipment) {
                equipment.attach(maintenanceObserver);
                equipment.notifyObservers();

                if ("DOWN_FOR_MAINTENANCE".equals(
                        equipment.getStatus())) {

                    scheduledEquipment.add(equipment);
                }
            }

            List<MaintenanceLog> maintenanceLogs
                    = maintenanceDAO.findAll();

            if (maintenanceLogs == null) {
                maintenanceLogs = List.of();
            }

            request.setAttribute(
                    "maintenanceAlerts",
                    maintenanceObserver.getMaintenanceAlerts()
            );
            request.setAttribute(
                    "scheduledEquipment",
                    scheduledEquipment
            );
            request.setAttribute(
                    "maintenanceLogs",
                    maintenanceLogs
            );
        } catch (RuntimeException exception) {
            if (request.getAttribute("errorMessage") == null) {
                request.setAttribute(
                        "errorMessage",
                        "Unable to load maintenance information."
                );
            }

            request.setAttribute("maintenanceAlerts", List.of());
            request.setAttribute("scheduledEquipment", List.of());
            request.setAttribute("maintenanceLogs", List.of());
        }

        request.getRequestDispatcher(MAINTENANCE_VIEW)
                .forward(request, response);
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
}