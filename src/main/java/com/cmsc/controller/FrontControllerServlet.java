package com.cmsc.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Front Controller for the CMSC application.
 * Acts as a single entry point for requests and
 * forwards them to the appropriate servlet.
 *
 * SHARED FILE — add your own case here when you build a new servlet.
 * Coordinate before editing; don't remove or change another member's
 * existing case.
 *
 * @author Suhib
 */
@WebServlet("/controller")
public class FrontControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            // --- Member 1: Users, Auth, Booking ---
            case "register":
                request.getRequestDispatcher("/RegisterServlet")
                        .forward(request, response);
                break;
            case "login":
                request.getRequestDispatcher("/LoginServlet")
                        .forward(request, response);
                break;
            case "logout":
                request.getRequestDispatcher("/LogoutServlet")
                        .forward(request, response);
                break;
            case "booking":
                request.getRequestDispatcher("/BookingServlet")
                        .forward(request, response);
                break;
            case "checkinout":
                request.getRequestDispatcher("/equipment")
                        .forward(request, response);
                break;

            // --- Member 2: Equipment / Inventory / Maintenance (confirmed) ---
            case "equipment":
                request.getRequestDispatcher("/equipment")
                        .forward(request, response);
                break;
            case "sessiontracking":
                request.getRequestDispatcher("/sessions")
                        .forward(request, response);
                break;
            case "inventory":
                request.getRequestDispatcher("/inventory")
                        .forward(request, response);
                break;
            case "maintenance":
                request.getRequestDispatcher("/maintenance")
                        .forward(request, response);
                break;

            // --- Member 3: Ledger / Reporting / Work Orders (confirmed) ---
            case "ledger":
                request.getRequestDispatcher("/ledger")
                        .forward(request, response);
                break;
            case "reports":
                request.getRequestDispatcher("/reports")
                        .forward(request, response);
                break;
            case "workorders":
                request.getRequestDispatcher("/workorders")
                        .forward(request, response);
                break;
            case "training":
                request.getRequestDispatcher("/training")
                        .forward(request, response);
                break;
            case "donations":
                request.getRequestDispatcher("/donations")
                        .forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }
}
