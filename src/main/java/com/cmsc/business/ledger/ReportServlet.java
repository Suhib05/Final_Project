package com.cmsc.business.ledger;

import com.cmsc.dao.ConsumableDAO;
import com.cmsc.dao.ConsumableDAOImpl;
import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.TransactionDAO;
import com.cmsc.dao.TransactionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.dao.WorkOrderDAO;
import com.cmsc.dao.WorkOrderDAOImpl;
import com.cmsc.model.EquipmentStatusReport;
import com.cmsc.model.Transaction;
import com.cmsc.model.TransactionType;
import com.cmsc.model.User;
import com.cmsc.model.UserLedgerReport;
import com.cmsc.model.WorkOrderStatus;
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
 * Produces the FR-06 reports.
 *
 * What the page shows depends on who is asking. A User sees their own activity;
 * a Trainer or Shop-Tech additionally sees what they have contributed and
 * earned; a Shop-Tech also sees the co-op-wide picture of equipment and stock,
 * because they are the ones who act on it.
 *
 * Ledger figures are aggregated through TransactionDAO, which this module owns.
 * Equipment and stock figures come from the DAOs owned by Member 2; this
 * servlet only arranges them into an {@link EquipmentStatusReport}.
 *
 * @author Annour Mohamed
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {

    private static final String REPORT_VIEW = "/WEB-INF/views/reports.jsp";

    private TransactionDAO transactionDAO;
    private WorkOrderDAO workOrderDAO;
    private UserDAO userDAO;
    private EquipmentDAO equipmentDAO;
    private ConsumableDAO consumableDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the data access and business objects this servlet uses.
     */
    @Override
    public void init() {
        transactionDAO = new TransactionDAOImpl();
        workOrderDAO = new WorkOrderDAOImpl();
        userDAO = new UserDAOImpl();
        equipmentDAO = new EquipmentDAOImpl();
        consumableDAO = new ConsumableDAOImpl();
        ledgerService = new LedgerService(transactionDAO, userDAO);
    }

    /**
     * Builds the report appropriate to the signed-in member's role.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the report view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String userType = String.valueOf(session.getAttribute("userType"));

        request.setAttribute("sessionUserType", userType);

        try {
            buildActivityReport(userId, request);

            if ("TRAINER".equals(userType) || "SHOP_TECH".equals(userType)) {
                buildContributionReport(userId, userType, request);
            }

            if ("SHOP_TECH".equals(userType)) {
                buildCoopReport(request);
            }
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "Unable to build the report.");
        }

        request.getRequestDispatcher(REPORT_VIEW).forward(request, response);
    }

    /**
     * Builds the member's own ledger activity for the current month.
     *
     * @param userId member the report describes
     * @param request request the attributes are placed on
     */
    private void buildActivityReport(int userId, HttpServletRequest request) {
        User user = userDAO.findById(userId);

        if (user == null) {
            request.setAttribute("errorMessage",
                    "Your account could not be loaded.");
            return;
        }

        LocalDateTime periodStart
                = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime periodEnd = periodStart.plusMonths(1);

        List<Transaction> entries
                = ledgerService.getStatement(userId, periodStart, periodEnd);

        request.setAttribute("ledgerReport",
                new UserLedgerReport(user, entries, periodStart, periodEnd));
        request.setAttribute("lifetimeCredits",
                transactionDAO.sumByType(userId, TransactionType.CREDIT));
        request.setAttribute("lifetimeDebits",
                transactionDAO.sumByType(userId, TransactionType.DEBIT));
        request.setAttribute("ledgerBalance",
                transactionDAO.calculateBalance(userId));
    }

    /**
     * Builds the contribution figures a Trainer or Shop-Tech is reported on.
     *
     * @param userId member the report describes
     * @param userType the member's role
     * @param request request the attributes are placed on
     */
    private void buildContributionReport(int userId, String userType,
            HttpServletRequest request) {

        List<Transaction> ledger = transactionDAO.findByUserId(userId);

        double contributionCredits = ledger.stream()
                .filter(Transaction::isCredit)
                .mapToDouble(Transaction::getAmount)
                .sum();

        request.setAttribute("contributionCredits", contributionCredits);
        request.setAttribute("contributionEntries", ledger.stream()
                .filter(Transaction::isCredit)
                .toList());

        if ("SHOP_TECH".equals(userType)) {
            request.setAttribute("myCompletedOrders",
                    workOrderDAO.findByShopTech(userId).stream()
                            .filter(order -> order.getStatus()
                            == WorkOrderStatus.COMPLETED)
                            .toList());
        }
    }

    /**
     * Builds the co-op-wide picture of equipment, maintenance and stock.
     *
     * @param request request the attributes are placed on
     */
    private void buildCoopReport(HttpServletRequest request) {
        EquipmentStatusReport report = new EquipmentStatusReport(
                equipmentDAO.findAll(),
                equipmentDAO.findRequiringMaintenance(),
                consumableDAO.findLowStock()
        );

        request.setAttribute("equipmentReport", report);
        request.setAttribute("openWorkOrders", workOrderDAO.findOpenQueue());
    }
}
