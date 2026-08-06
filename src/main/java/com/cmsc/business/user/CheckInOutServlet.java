package com.cmsc.business.user;

import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.SessionDAO;
import com.cmsc.dao.SessionDAOImpl;
import com.cmsc.model.Equipment;
import com.cmsc.model.Session;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles FR-03 check-in/check-out. Reached via FrontControllerServlet
 * (/controller?action=checkinout&mode=checkin|checkout).
 *
 * DEPENDS ON MEMBER 2's Session model, SessionDAO, and EquipmentDAO —
 * will not compile until those exist and match the method signatures
 * used below.
 *
 * @author Suhib
 */
@WebServlet("/CheckInOutServlet")
public class CheckInOutServlet extends HttpServlet {

    private final SessionDAO sessionDAO = new SessionDAOImpl();
    private final EquipmentDAO equipmentDAO = new EquipmentDAOImpl();

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
        forwardTo(request, response, "/equipment-status.jsp");
    }

    private void checkOut(int userId, int equipmentId,
                           HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session activeSession = sessionDAO.findActiveSession(userId, equipmentId);
        if (activeSession == null) {
            request.setAttribute("errorMessage", "No active session found to check out.");
            forwardTo(request, response, "/equipment-status.jsp");
            return;
        }

        double materialsConsumed = parseDoubleOrZero(request.getParameter("materialsConsumed"));

        activeSession.setEndTime(LocalDateTime.now());
        activeSession.setMaterialsConsumed(materialsConsumed);
        sessionDAO.update(activeSession);

        Equipment equipment = equipmentDAO.findById(equipmentId);
        equipment.setStatus("AVAILABLE");
        equipmentDAO.update(equipment);

        request.setAttribute("successMessage", "Checked out successfully.");
        forwardTo(request, response, "/equipment-status.jsp");
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