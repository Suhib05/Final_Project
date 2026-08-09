package com.cmsc.business.user;

import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.SessionDAO;
import com.cmsc.dao.SessionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Equipment;
import com.cmsc.model.Session;
import com.cmsc.model.User;
import com.cmsc.observer.MaintenanceObserver;
import com.cmsc.strategy.LedgerService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Handles FR-03 check-in/check-out. Reached via FrontControllerServlet
 * (/controller?action=checkinout&mode=checkin|checkout).
 *
 * @author Suhib
 */
@WebServlet("/CheckInOutServlet")
public class CheckInOutServlet extends HttpServlet {

    private final SessionDAO sessionDAO = new SessionDAOImpl();
    private final EquipmentDAO equipmentDAO = new EquipmentDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final LedgerService ledgerService = new LedgerService();

    /**
     * Someone navigating to the check-in/out screen (via the dashboard
     * link) rather than submitting the form yet.
     * @param request
     * @param response
     * @throws jakarta.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        forwardTo(request, response, "/WEB-INF/views/checkinout.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) httpSession.getAttribute("userId");
        int equipmentId = Integer.parseInt(request.getParameter("equipmentId"));
        String mode = request.getParameter("mode");

        if ("checkin".equals(mode)) {
            checkIn(userId, equipmentId, request, response);
        } else if ("checkout".equals(mode)) {
            checkOut(userId, equipmentId, request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown mode: " + mode);
        }
    }

    private void checkIn(int userId, int equipmentId,
                          HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session session = new Session();
        session.setUserId(userId);
        session.setEquipmentId(equipmentId);
        session.setStartTime(LocalDateTime.now());
        sessionDAO.save(session);

        Equipment equipment = equipmentDAO.findById(equipmentId);
        equipment.setStatus("IN_USE");
        equipmentDAO.update(equipment);

        request.setAttribute("successMessage", "Checked in successfully.");
        forwardTo(request, response, "/WEB-INF/views/equipment-status.jsp");
    }

    private void checkOut(int userId, int equipmentId,
                           HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session activeSession = sessionDAO.findActiveByEquipment(equipmentId);

        if (activeSession == null || activeSession.getUserId() != userId) {
            request.setAttribute("errorMessage", "No active session found to check out.");
            forwardTo(request, response, "/WEB-INF/views/equipment-status.jsp");
            return;
        }

        double materialsConsumed = parseDoubleOrZero(request.getParameter("materialsConsumed"));

        activeSession.setEndTime(LocalDateTime.now());
        activeSession.setMaterialsConsumed(materialsConsumed);
        sessionDAO.update(activeSession);

        Equipment equipment = equipmentDAO.findById(equipmentId);

        /*
         * Logging the hours is what makes predictive maintenance work: it
         * raises hoursOfUse towards the threshold and notifies the observers,
         * so a machine registered today eventually reports its own alert
         * without anyone editing the database by hand.
         */
        equipment.attach(new MaintenanceObserver());
        equipment.logUsage(elapsedHours(activeSession));
        equipment.updateStatus("AVAILABLE");
        equipmentDAO.update(equipment);

        chargeForSession(activeSession, equipment);

        request.setAttribute("successMessage", "Checked out successfully.");
        forwardTo(request, response, "/WEB-INF/views/equipment-status.jsp");
    }

    /**
     * Debits the member for the session they just finished (FR-06).
     *
     * The charge is the access hours used multiplied by the equipment's credit
     * rate, plus the materials consumed. The arithmetic and the ledger write
     * belong to the Ledger module, so this servlet only supplies the figures.
     * A session that used no time and no material is not charged.
     *
     * @param session the session that has just ended
     * @param equipment the equipment it ran on, carrying the credit rate
     */
    private void chargeForSession(Session session, Equipment equipment) {
        if (session.getStartTime() == null || session.getEndTime() == null) {
            return;
        }

        double accessHours = elapsedHours(session);

        if (accessHours <= 0 && session.getMaterialsConsumed() <= 0) {
            return;
        }

        User user = userDAO.findById(session.getUserId());

        if (user == null) {
            return;
        }

        ledgerService.recordEquipmentUse(
                user,
                accessHours,
                equipment.getAccessCreditRate(),
                session.getMaterialsConsumed(),
                session.getSessionId()
        );
    }

    /**
     * Returns how long a finished session ran, in hours.
     *
     * @param session the session that has ended
     * @return elapsed hours, or 0 when either timestamp is missing
     */
    private double elapsedHours(Session session) {
        if (session.getStartTime() == null || session.getEndTime() == null) {
            return 0;
        }

        return Duration
                .between(session.getStartTime(), session.getEndTime())
                .toMinutes() / 60.0;
    }

    private double parseDoubleOrZero(String value) {
        try {
            return value == null ? 0.0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void forwardTo(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}