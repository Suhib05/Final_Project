package com.cmsc.business.ledger;

import com.cmsc.dao.TransactionDAO;
import com.cmsc.dao.TransactionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Transaction;
import com.cmsc.model.User;
import com.cmsc.model.UserLedgerReport;
import com.cmsc.strategy.LedgerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Shows a member their credit and debit history and lets them settle an
 * outstanding balance at month end (FR-06).
 *
 * This servlet belongs to the Business layer. It reads through TransactionDAO
 * and performs every change to the ledger through {@link LedgerService}, so
 * the balances on the users table and the rows in the transactions table are
 * always written together.
 *
 * @author Annour Mohamed
 */
@WebServlet(name = "LedgerServlet", urlPatterns = {"/ledger"})
public class LedgerServlet extends HttpServlet {

    private static final String LEDGER_VIEW = "/WEB-INF/views/ledger.jsp";

    private TransactionDAO transactionDAO;
    private UserDAO userDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the data access and business objects this servlet uses.
     */
    @Override
    public void init() {
        transactionDAO = new TransactionDAOImpl();
        userDAO = new UserDAOImpl();
        ledgerService = new LedgerService(transactionDAO, userDAO);
    }

    /**
     * Displays the signed-in member's ledger for the current month, together
     * with their running totals and anything left to settle.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the ledger view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = currentUserId(request);

        if (userId == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        showLedger(userId, request);
        request.getRequestDispatcher(LEDGER_VIEW).forward(request, response);
    }

    /**
     * Settles the member's outstanding balance. Accumulated credits are
     * applied first and the amount entered clears whatever remains.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the ledger view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = currentUserId(request);

        if (userId == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            User user = userDAO.findById(userId);

            if (user == null) {
                request.setAttribute("errorMessage",
                        "Your account could not be loaded.");
            } else {
                double payment = parsePayment(
                        request.getParameter("payment"));

                Transaction settlement = ledgerService.settle(user, payment);

                if (settlement == null) {
                    request.setAttribute("infoMessage",
                            "You have nothing outstanding to settle.");
                } else {
                    request.setAttribute("successMessage",
                            "Settled " + settlement.getAmount()
                            + " credits against your balance.");
                }
            }
        } catch (IllegalArgumentException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "Unable to settle your balance.");
        }

        showLedger(userId, request);
        request.getRequestDispatcher(LEDGER_VIEW).forward(request, response);
    }

    /**
     * Builds the ledger report for the current month and puts everything the
     * view needs on the request.
     *
     * @param userId member whose ledger is being shown
     * @param request request the attributes are placed on
     */
    private void showLedger(int userId, HttpServletRequest request) {
        try {
            User user = userDAO.findById(userId);

            if (user == null) {
                request.setAttribute("errorMessage",
                        "Your account could not be loaded.");
                request.setAttribute("ledgerEntries", List.of());
                return;
            }

            LocalDateTime periodStart = LocalDate.now()
                    .withDayOfMonth(1).atStartOfDay();
            LocalDateTime periodEnd = periodStart.plusMonths(1);

            List<Transaction> entries = ledgerService.getStatement(
                    userId, periodStart, periodEnd);

            UserLedgerReport report = new UserLedgerReport(
                    user, entries, periodStart, periodEnd);

            request.setAttribute("ledgerReport", report);
            request.setAttribute("ledgerEntries", entries);
            request.setAttribute("fullHistory",
                    ledgerService.getLedger(userId));
            request.setAttribute("creditBalance", user.getCreditBalance());
            request.setAttribute("debitBalance", user.getDebitBalance());
            request.setAttribute("creditsApplicable",
                    report.getCreditsApplicable(user.getCreditBalance()));
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "Unable to load your ledger.");
            request.setAttribute("ledgerEntries", List.of());
        }
    }

    /**
     * Reads the signed-in member from the session.
     *
     * @param request request carrying the session
     * @return the member identifier, or null when nobody is signed in
     */
    private Integer currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object userId = session.getAttribute("userId");

        return userId instanceof Integer ? (Integer) userId : null;
    }

    /**
     * Reads the payment amount from the form, treating a blank field as a
     * settlement paid entirely from accumulated credits.
     *
     * @param value raw form value
     * @return the payment amount, never negative
     */
    private double parsePayment(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            double payment = Double.parseDouble(value.trim());

            if (payment < 0) {
                throw new IllegalArgumentException(
                        "Payment cannot be negative.");
            }

            return payment;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Payment must be a number.");
        }
    }
}
