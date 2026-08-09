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
 * Handles FR-03 equipment booking. Reached via FrontControllerServlet
 * (/controller?action=booking).
 *
 * Booking starts an active session immediately. Usage time and material
 * consumption are completed through the consolidated equipment session
 * screen.
 *
 * @author Suhib
 */
@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {

    private final SessionDAO sessionDAO = new SessionDAOImpl();
    private final EquipmentDAO equipmentDAO = new EquipmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Show the booking form, listing available equipment
        request.setAttribute("equipmentList", equipmentDAO.findAll());
        forwardTo(request, response, "/WEB-INF/views/booking.jsp");
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

        Equipment equipment = equipmentDAO.findById(equipmentId);

        if (equipment == null) {
            request.setAttribute("errorMessage", "Equipment not found.");
            forwardTo(request, response, "/WEB-INF/views/booking.jsp");
            return;
        }

        if (!"AVAILABLE".equals(equipment.getStatus())) {
            request.setAttribute("errorMessage",
                    "That equipment is currently " + equipment.getStatus() + " and cannot be booked.");
            forwardTo(request, response, "/WEB-INF/views/booking.jsp");
            return;
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setEquipmentId(equipmentId);
        session.setStartTime(LocalDateTime.now());
        session.setMaterialsConsumed(0.0);
        sessionDAO.save(session);

        equipment.setStatus("IN_USE");
        equipmentDAO.update(equipment);

        response.sendRedirect(
                request.getContextPath() + "/equipment?booking=success"
        );
    }

    private void forwardTo(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}
