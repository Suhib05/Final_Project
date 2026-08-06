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
                request.getRequestDispatcher("/CheckInOutServlet")
                        .forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }
}