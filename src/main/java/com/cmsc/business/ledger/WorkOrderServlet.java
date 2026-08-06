package com.cmsc.business.ledger;

import com.cmsc.adapter.ExternalJobAdapter;
import com.cmsc.adapter.ExternalJobRequest;
import com.cmsc.dao.TransactionDAO;
import com.cmsc.dao.TransactionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.dao.WorkOrderDAO;
import com.cmsc.dao.WorkOrderDAOImpl;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.User;
import com.cmsc.model.WorkOrder;
import com.cmsc.model.WorkOrderImpl;
import com.cmsc.model.WorkOrderStatus;
import com.cmsc.strategy.LedgerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles fabrication job requests: submission by members and by external
 * clients, the open queue, and claiming and completing a job.
 *
 * The servlet is the Client of the Adapter pattern. It depends only on the
 * {@link WorkOrder} interface, so a request that arrived through the public
 * external form and one raised by a member are handled by the same code from
 * the moment they enter the system. Completing a job credits the Shop-Tech
 * through {@link LedgerService}.
 *
 * @author Annour Mohamed
 */
@WebServlet(name = "WorkOrderServlet", urlPatterns = {"/workorders"})
public class WorkOrderServlet extends HttpServlet {

    private static final String WORK_ORDER_VIEW
            = "/WEB-INF/views/work-order-form.jsp";

    private WorkOrderDAO workOrderDAO;
    private UserDAO userDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the data access and business objects this servlet uses.
     */
    @Override
    public void init() {
        workOrderDAO = new WorkOrderDAOImpl();
        userDAO = new UserDAOImpl();

        TransactionDAO transactionDAO = new TransactionDAOImpl();
        ledgerService = new LedgerService(transactionDAO, userDAO);
    }

    /**
     * Displays the open queue and the submission form.
     *
     * The page is reachable without signing in, because external clients are
     * not registered members and must still be able to submit a request.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the work order view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        showQueue(request);
        request.getRequestDispatcher(WORK_ORDER_VIEW)
                .forward(request, response);
    }

    /**
     * Carries out the action named by the form: submitting an internal or
     * external request, claiming a job, or completing one.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the work order view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) {
                request.setAttribute("errorMessage",
                        "No action was requested.");
            } else {
                switch (action) {
                    case "submitInternal" ->
                        submitInternal(request);
                    case "submitExternal" ->
                        submitExternal(request);
                    case "claim" ->
                        claim(request);
                    case "complete" ->
                        complete(request);
                    default ->
                        request.setAttribute("errorMessage",
                                "Unknown action: " + action);
                }
            }
        } catch (IllegalArgumentException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "The work order could not be processed.");
        }

        showQueue(request);
        request.getRequestDispatcher(WORK_ORDER_VIEW)
                .forward(request, response);
    }

    /**
     * Records a job raised by a signed-in member.
     *
     * @param request request carrying the form values
     */
    private void submitInternal(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new IllegalArgumentException(
                    "You must be signed in to raise a member work order.");
        }

        String requesterName = (String) session.getAttribute("userName");
        String description = required(request.getParameter("description"),
                "A description of the work is required.");

        WorkOrder job = new WorkOrderImpl(requesterName, description);
        workOrderDAO.save(job);

        request.setAttribute("successMessage",
                "Your work order has been added to the queue.");
    }

    /**
     * Records a job submitted through the public external form.
     *
     * The request arrives in the external client's own shape and is wrapped by
     * {@link ExternalJobAdapter} so that everything downstream sees only the
     * internal {@link WorkOrder} interface.
     *
     * @param request request carrying the form values
     */
    private void submitExternal(HttpServletRequest request) {
        String companyName = required(request.getParameter("companyName"),
                "A company name is required.");
        String contactEmail = required(request.getParameter("contactEmail"),
                "A contact email address is required.");
        String jobDetails = required(request.getParameter("jobDetails"),
                "A description of the work is required.");

        ExternalJobRequest external = new ExternalJobRequest(
                companyName, contactEmail, jobDetails);

        WorkOrder job = new ExternalJobAdapter(external);
        workOrderDAO.save(job);

        request.setAttribute("successMessage",
                "Thank you. Your request has been added to the queue and a "
                + "Shop-Tech will pick it up in turn.");
    }

    /**
     * Assigns an open job to the signed-in Shop-Tech.
     *
     * @param request request carrying the job identifier
     */
    private void claim(HttpServletRequest request) {
        User shopTech = requireShopTech(request);
        WorkOrder job = requireJob(request);

        if (job.getStatus() != WorkOrderStatus.SUBMITTED) {
            throw new IllegalArgumentException(
                    "That job has already been claimed.");
        }

        job.setStatus(WorkOrderStatus.IN_PROGRESS);
        job.setAssignedShopTechId(shopTech.getUserId());
        workOrderDAO.update(job);

        request.setAttribute("successMessage",
                "You have claimed work order " + job.getWorkOrderId() + ".");
    }

    /**
     * Marks a job complete and credits the Shop-Tech who carried it out.
     *
     * @param request request carrying the job identifier
     */
    private void complete(HttpServletRequest request) {
        User shopTech = requireShopTech(request);
        WorkOrder job = requireJob(request);

        if (job.getStatus() == WorkOrderStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "That job is already complete.");
        }

        Integer assignedTo = job.getAssignedShopTechId();

        if (assignedTo != null && assignedTo != shopTech.getUserId()) {
            throw new IllegalArgumentException(
                    "That job is assigned to another Shop-Tech.");
        }

        job.setStatus(WorkOrderStatus.COMPLETED);
        job.setAssignedShopTechId(shopTech.getUserId());
        job.setDateCompleted(LocalDateTime.now());
        workOrderDAO.update(job);

        ledgerService.awardCredit(shopTech, TransactionSource.WORK_ORDER, 1,
                job.getWorkOrderId());

        request.setAttribute("successMessage",
                "Work order " + job.getWorkOrderId()
                + " completed and credited to your account.");
    }

    /**
     * Loads the open queue and the signed-in member's own jobs.
     *
     * @param request request the attributes are placed on
     */
    private void showQueue(HttpServletRequest request) {
        try {
            List<WorkOrder> queue = workOrderDAO.findOpenQueue();

            request.setAttribute("openQueue", queue);
            request.setAttribute("completedOrders",
                    workOrderDAO.findByStatus(WorkOrderStatus.COMPLETED));

            HttpSession session = request.getSession(false);

            if (session != null) {
                request.setAttribute("sessionUserType",
                        session.getAttribute("userType"));

                Object userId = session.getAttribute("userId");

                if (userId instanceof Integer id) {
                    request.setAttribute("myAssignedOrders",
                            workOrderDAO.findByShopTech(id));
                }
            }
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "Unable to load the work order queue.");
            request.setAttribute("openQueue", List.of());
            request.setAttribute("completedOrders", List.of());
        }
    }

    /**
     * Confirms a Shop-Tech is signed in and returns their account.
     *
     * @param request request carrying the session
     * @return the signed-in Shop-Tech
     */
    private User requireShopTech(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new IllegalArgumentException("You must be signed in.");
        }

        if (!"SHOP_TECH".equals(session.getAttribute("userType"))) {
            throw new IllegalArgumentException(
                    "Only a Shop-Tech can claim or complete work orders.");
        }

        User user = userDAO.findById((Integer) session.getAttribute("userId"));

        if (user == null) {
            throw new IllegalArgumentException(
                    "Your account could not be loaded.");
        }

        return user;
    }

    /**
     * Loads the job named by the form.
     *
     * @param request request carrying the job identifier
     * @return the matching job
     */
    private WorkOrder requireJob(HttpServletRequest request) {
        String raw = request.getParameter("workOrderId");

        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("No work order was selected.");
        }

        int workOrderId;

        try {
            workOrderId = Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "That work order identifier is not valid.");
        }

        WorkOrder job = workOrderDAO.findById(workOrderId);

        if (job == null) {
            throw new IllegalArgumentException(
                    "That work order no longer exists.");
        }

        return job;
    }

    /**
     * Checks a form field was filled in.
     *
     * @param value raw form value
     * @param message message to report when it was not
     * @return the trimmed value
     */
    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }
}
