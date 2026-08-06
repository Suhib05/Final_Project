package com.cmsc.business.user;

import com.cmsc.builder.UserBuilder;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.User;
import com.cmsc.model.UserType;
import com.cmsc.util.PasswordUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles FR-01 registration. Reached via FrontControllerServlet
 * (/controller?action=register), which forwards here.
 *
 * @author Suhib
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userTypeParam = request.getParameter("userType");

        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            request.setAttribute("errorMessage", "All fields are required.");
            forwardTo(request, response, "/register.jsp");
            return;
        }

        if (userDAO.findByEmail(email) != null) {
            request.setAttribute("errorMessage", "An account with this email already exists.");
            forwardTo(request, response, "/register.jsp");
            return;
        }

        UserType userType = parseUserType(userTypeParam);
        String hashedPassword = PasswordUtil.hash(password);

        User newUser = new UserBuilder()
                .setName(name)
                .setEmail(email)
                .setPasswordHash(hashedPassword)
                .setUserType(userType)
                .build();

        userDAO.save(newUser);

        request.setAttribute("successMessage", "Registration successful. Please log in.");
        forwardTo(request, response, "/login.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Someone navigating straight to the registration form (not submitting it yet)
        forwardTo(request, response, "/register.jsp");
    }

    private UserType parseUserType(String value) {
        try {
            return UserType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return UserType.USER;
        }
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