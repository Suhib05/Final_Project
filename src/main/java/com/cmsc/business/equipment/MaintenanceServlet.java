package com.cmsc.business.equipment;

import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.MaintenanceDAO;
import com.cmsc.dao.MaintenanceDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Equipment;
import com.cmsc.model.MaintenanceLog;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.User;
import com.cmsc.observer.MaintenanceObserver;
import com.cmsc.strategy.LedgerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    private UserDAO userDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the DAOs required by this servlet, plus the ledger service
     * used to credit the Shop-Tech for the time they log.
     */
    @Override
    public void init() {
        equipmentDAO = new EquipmentDAOImpl();
        maintenanceDAO = new MaintenanceDAOImpl();
        userDAO = new UserDAOImpl();
        ledgerService = new LedgerService();
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

        int shopTechId = requireSignedInShopTech(request);
        String description = requireParameter(
                request, "description"
        );
        double hoursSpent = requireHoursSpent(request);

        MaintenanceLog maintenanceLog = new MaintenanceLog();
        maintenanceLog.setEquipmentId(equipment.getEquipmentId());
        maintenanceLog.setShopTechId(shopTechId);
        maintenanceLog.setDescription(description);
        maintenanceLog.setHoursAtService(
                equipment.getHoursOfUse()
        );

        MaintenanceLog savedLog = maintenanceDAO.save(maintenanceLog);

        if (savedLog == null) {
            throw new IllegalStateException(
                    "The maintenance log could not be saved."
            );
        }

        awardMaintenanceCredit(shopTechId, hoursSpent, savedLog.getLogId());

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

    /**
     * Credits the Shop-Tech for the time they spent on the job.
     *
     * The calculation itself belongs to the Ledger module: this servlet only
     * reports what happened and who did it, and LedgerService selects the
     * matching credit rule and writes the transaction.
     *
     * @param shopTechId Shop-Tech who carried out the work
     * @param hoursSpent hours of labour logged
     * @param logId maintenance log the credit relates to
     */
    private void awardMaintenanceCredit(int shopTechId, double hoursSpent,
            int logId) {

        User shopTech = userDAO.findById(shopTechId);

        if (shopTech == null) {
            throw new IllegalStateException(
                    "The Shop-Tech account could not be loaded."
            );
        }

        ledgerService.awardCredit(shopTech, TransactionSource.MAINTENANCE,
                hoursSpent, logId);
    }

    /**
     * Reads the hours of labour the Shop-Tech is claiming.
     *
     * Maintenance is credited by time rather than by job, because the work
     * varies: clearing a nozzle and rebuilding an axis are both one log. The
     * figure has to come from the form, since the schema records only the
     * machine's usage hours at the point of service, not the labour spent.
     *
     * @param request request carrying the form values
     * @return hours spent, greater than zero
     */
    private double requireHoursSpent(HttpServletRequest request) {
        String value = requireParameter(request, "hoursSpent");

        try {
            double hoursSpent = Double.parseDouble(value);

            if (hoursSpent <= 0) {
                throw new IllegalArgumentException(
                        "Hours spent must be greater than zero."
                );
            }

            return hoursSpent;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Hours spent must be a valid number."
            );
        }
    }

    /**
     * Returns the identifier of the Shop-Tech carrying out the work, taken
     * from the session rather than from the form.
     *
     * The maintenance credit is awarded to whoever this identifies, so it must
     * not be something the browser can supply. Reading it from the session
     * means a member can only ever log maintenance against their own account.
     *
     * @param request request carrying the session
     * @return the signed-in Shop-Tech's user identifier
     */
    private int requireSignedInShopTech(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new IllegalArgumentException(
                    "You must be signed in to log maintenance."
            );
        }

        if (!"SHOP_TECH".equals(session.getAttribute("userType"))) {
            throw new IllegalArgumentException(
                    "Only a Shop-Tech can log maintenance."
            );
        }

        return (Integer) session.getAttribute("userId");
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