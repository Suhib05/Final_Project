package com.cmsc.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Sanity-check servlet used only during initial environment setup.
 * Confirms that Payara is correctly routing requests to servlets
 * before any real business logic is written.
 *
 * Delete this class once the real FrontControllerServlet is in place.
 *
 * @author TODO-your-name
 */
@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h1>Servlet routing works.</h1>");
            out.println("<p>TestServlet reached successfully via /test</p>");
            out.println("</body></html>");
        }
    }
}
