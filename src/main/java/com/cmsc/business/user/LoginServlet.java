package com.cmsc.business.user;

import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.User;
import com.cmsc.util.PasswordUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles FR-01 login. Reached via FrontControllerServlet
 * (/controller?action=login), which forwards here.
 *
 * @author Suhib
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Someone navigating straight to the login form (not submitting it yet)
        forwardTo(request, response, "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (isBlank(email) || isBlank(password)) {
            request.setAttribute("errorMessage", "Email and password are required.");
            forwardTo(request, response, "/login.jsp");
            return;
        }

        User user = userDAO.findByEmail(email);

        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            request.setAttribute("errorMessage", "Invalid email or password.");
            forwardTo(request, response, "/login.jsp");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userType", user.getUserType().name());

        response.sendRedirect("dashboard.jsp");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void forwardTo(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}