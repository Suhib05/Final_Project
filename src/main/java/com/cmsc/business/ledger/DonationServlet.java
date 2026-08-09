package com.cmsc.business.ledger;

import com.cmsc.dao.ConsumableDAO;
import com.cmsc.dao.ConsumableDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Consumable;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.User;
import com.cmsc.strategy.DonationCreditStrategy;
import com.cmsc.strategy.LedgerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Handles material coming into the co-op: donations from members, which earn
 * credit, and restocks recorded by a Shop-Tech, which do not.
 *
 * Donating consumable material is one of the four ways a member contributes.
 * A donation raises the stock level and then calls {@link LedgerService},
 * which selects the donation credit rule and writes the CREDIT transaction.
 * A restock is the co-op buying its own supplies, so the stock rises and no
 * credit is awarded.
 *
 * Without this servlet, stock could only ever fall as sessions consumed it,
 * and the reorder alerts had no way of being cleared.
 *
 * @author Annour Mohamed
 */
@WebServlet(name = "DonationServlet", urlPatterns = {"/donations"})
public class DonationServlet extends HttpServlet {

    private static final String DONATION_VIEW
            = "/WEB-INF/views/donations.jsp";

    private ConsumableDAO consumableDAO;
    private UserDAO userDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the data access and business objects this servlet uses.
     */
    @Override
    public void init() {
        consumableDAO = new ConsumableDAOImpl();
        userDAO = new UserDAOImpl();
        ledgerService = new LedgerService();
    }

    /**
     * Displays the current stock and the donation form.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the donation view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (currentUserId(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        showStock(request);
        request.getRequestDispatcher(DONATION_VIEW).forward(request, response);
    }

    /**
     * Records a donation or a restock.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the donation view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (currentUserId(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String action = request.getParameter("action");

            if ("restock".equals(action)) {
                restock(request);
            } else {
                donate(request);
            }
        } catch (IllegalArgumentException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "The stock change could not be recorded.");
        }

        showStock(request);
        request.getRequestDispatcher(DONATION_VIEW).forward(request, response);
    }

    /**
     * Records a member's donation and credits them for it.
     *
     * @param request request carrying the form values
     */
    private void donate(HttpServletRequest request) {
        User donor = requireSignedInUser(request);
        Consumable consumable = requireConsumable(request);
        double quantity = requireQuantity(request);

        consumable.updateStock(consumable.getStockLevel() + quantity);

        if (consumableDAO.update(consumable) == null) {
            throw new IllegalStateException(
                    "The donation could not be recorded.");
        }

        ledgerService.awardCredit(donor, TransactionSource.DONATION, quantity,
                consumable.getConsumableId());

        request.setAttribute("successMessage",
                "Thank you. " + quantity + " " + consumable.getUnit() + " of "
                + consumable.getType() + " added to co-op stock, and "
                + new DonationCreditStrategy().calculateCredit(quantity)
                + " credits added to your account.");
    }

    /**
     * Records a co-op purchase. Stock rises and nobody is credited, because
     * the co-op paid for the material itself.
     *
     * @param request request carrying the form values
     */
    private void restock(HttpServletRequest request) {
        requireShopTech(request);

        Consumable consumable = requireConsumable(request);
        double quantity = requireQuantity(request);

        consumable.updateStock(consumable.getStockLevel() + quantity);

        if (consumableDAO.update(consumable) == null) {
            throw new IllegalStateException(
                    "The restock could not be recorded.");
        }

        request.setAttribute("successMessage",
                "Restocked " + quantity + " " + consumable.getUnit() + " of "
                + consumable.getType() + ".");
    }

    /**
     * Loads the current stock and everything the view needs to render it.
     *
     * @param request request the attributes are placed on
     */
    private void showStock(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);

            request.setAttribute("sessionUserType", session == null
                    ? null : session.getAttribute("userType"));
            request.setAttribute("consumables", consumableDAO.findAll());
            request.setAttribute("lowStock", consumableDAO.findLowStock());
            request.setAttribute("creditPerUnit",
                    DonationCreditStrategy.CREDIT_PER_UNIT);
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "Unable to load the current stock.");
            request.setAttribute("consumables", List.of());
            request.setAttribute("lowStock", List.of());
        }
    }

    /**
     * Confirms someone is signed in and returns their account.
     *
     * @param request request carrying the session
     * @return the signed-in member
     */
    private User requireSignedInUser(HttpServletRequest request) {
        Integer userId = currentUserId(request);

        if (userId == null) {
            throw new IllegalArgumentException("You must be signed in.");
        }

        User user = userDAO.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException(
                    "Your account could not be loaded.");
        }

        return user;
    }

    /**
     * Confirms the signed-in member is a Shop-Tech.
     *
     * @param request request carrying the session
     */
    private void requireShopTech(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null
                || !"SHOP_TECH".equals(session.getAttribute("userType"))) {
            throw new IllegalArgumentException(
                    "Only a Shop-Tech can record a co-op restock.");
        }
    }

    /**
     * Loads the consumable named by the form.
     *
     * @param request request carrying the form values
     * @return the matching consumable
     */
    private Consumable requireConsumable(HttpServletRequest request) {
        String raw = request.getParameter("consumableId");

        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Choose a material.");
        }

        int consumableId;

        try {
            consumableId = Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "That material selection is not valid.");
        }

        Consumable consumable = consumableDAO.findById(consumableId);

        if (consumable == null) {
            throw new IllegalArgumentException(
                    "That material does not exist.");
        }

        return consumable;
    }

    /**
     * Reads the quantity being added to stock.
     *
     * @param request request carrying the form values
     * @return quantity, greater than zero
     */
    private double requireQuantity(HttpServletRequest request) {
        String raw = request.getParameter("quantity");

        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Enter a quantity.");
        }

        try {
            double quantity = Double.parseDouble(raw.trim());

            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero.");
            }

            return quantity;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Quantity must be a valid number.");
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
}
