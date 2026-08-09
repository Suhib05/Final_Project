package com.cmsc.business.ledger;

import com.cmsc.dao.EquipmentDAO;
import com.cmsc.dao.EquipmentDAOImpl;
import com.cmsc.dao.TrainingDAO;
import com.cmsc.dao.TrainingDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Equipment;
import com.cmsc.model.Training;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.User;
import com.cmsc.strategy.LedgerService;
import com.cmsc.strategy.TrainingCreditStrategy;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Records the intro and safety training sessions Trainers deliver, and credits
 * the Trainer for each one.
 *
 * Delivering training is one of the four ways a member contributes to the
 * co-op. Logging a session writes a row to the trainings table and then calls
 * {@link LedgerService}, which selects the training credit rule and writes the
 * CREDIT transaction. The calculation stays in the Ledger module; this servlet
 * only reports who delivered what.
 *
 * @author Annour Mohamed
 */
@WebServlet(name = "TrainingServlet", urlPatterns = {"/training"})
public class TrainingServlet extends HttpServlet {

    private static final String TRAINING_VIEW = "/WEB-INF/views/training.jsp";

    private TrainingDAO trainingDAO;
    private EquipmentDAO equipmentDAO;
    private UserDAO userDAO;
    private LedgerService ledgerService;

    /**
     * Initializes the data access and business objects this servlet uses.
     */
    @Override
    public void init() {
        trainingDAO = new TrainingDAOImpl();
        equipmentDAO = new EquipmentDAOImpl();
        userDAO = new UserDAOImpl();
        ledgerService = new LedgerService();
    }

    /**
     * Displays the training log, and the delivery form when the signed-in
     * member is a Trainer.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the training view cannot be processed
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

        showTrainings(request);
        request.getRequestDispatcher(TRAINING_VIEW).forward(request, response);
    }

    /**
     * Records a delivered session and credits the Trainer.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the training view cannot be processed
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
            deliverTraining(request);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "The training session could not be recorded.");
        }

        showTrainings(request);
        request.getRequestDispatcher(TRAINING_VIEW).forward(request, response);
    }

    /**
     * Records one delivered session and awards the matching credit.
     *
     * @param request request carrying the form values
     */
    private void deliverTraining(HttpServletRequest request) {
        User trainer = requireTrainer(request);
        Equipment equipment = requireEquipment(request);

        Training training = new Training(
                trainer.getUserId(), equipment.getEquipmentId());

        Training saved = trainingDAO.save(training);

        if (saved == null) {
            throw new IllegalStateException(
                    "The training session could not be saved.");
        }

        ledgerService.awardCredit(trainer, TransactionSource.TRAINING, 1,
                saved.getTrainingId());

        request.setAttribute("successMessage",
                "Training on " + equipment.getAssetTag()
                + " recorded. You earned "
                + TrainingCreditStrategy.CREDIT_PER_SESSION + " credits.");
    }

    /**
     * Loads the training log and everything the view needs to render it.
     *
     * @param request request the attributes are placed on
     */
    private void showTrainings(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String userType = session == null
                    ? null : (String) session.getAttribute("userType");

            request.setAttribute("sessionUserType", userType);

            List<Training> trainings = trainingDAO.findAll();

            request.setAttribute("trainings", trainings);
            request.setAttribute("trainerNames", trainerNames(trainings));
            request.setAttribute("equipmentNames", equipmentNames());
            request.setAttribute("equipmentList", equipmentDAO.findAll());
            request.setAttribute("creditPerSession",
                    TrainingCreditStrategy.CREDIT_PER_SESSION);

            Integer userId = currentUserId(request);

            if (userId != null && "TRAINER".equals(userType)) {
                request.setAttribute("mySessionCount",
                        trainingDAO.countByTrainerId(userId));
            }
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage",
                    "Unable to load the training log.");
            request.setAttribute("trainings", List.of());
            request.setAttribute("equipmentList", List.of());
        }
    }

    /**
     * Builds a lookup of trainer identifier to display name, so the view can
     * show who delivered each session without querying inside the loop.
     *
     * @param trainings sessions being displayed
     * @return trainer identifier to name
     */
    private Map<Integer, String> trainerNames(List<Training> trainings) {
        Map<Integer, String> names = new LinkedHashMap<>();

        for (Training training : trainings) {
            if (!names.containsKey(training.getTrainerId())) {
                User trainer = userDAO.findById(training.getTrainerId());

                names.put(training.getTrainerId(),
                        trainer == null ? "Unknown" : trainer.getName());
            }
        }

        return names;
    }

    /**
     * Builds a lookup of equipment identifier to asset tag and model.
     *
     * @return equipment identifier to display name
     */
    private Map<Integer, String> equipmentNames() {
        Map<Integer, String> names = new LinkedHashMap<>();

        for (Equipment equipment : equipmentDAO.findAll()) {
            names.put(equipment.getEquipmentId(),
                    equipment.getAssetTag() + " " + equipment.getModel());
        }

        return names;
    }

    /**
     * Confirms a Trainer is signed in and returns their account.
     *
     * Only a Trainer can log a delivered session, because the credit lands on
     * whoever this returns.
     *
     * @param request request carrying the session
     * @return the signed-in Trainer
     */
    private User requireTrainer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new IllegalArgumentException("You must be signed in.");
        }

        if (!"TRAINER".equals(session.getAttribute("userType"))) {
            throw new IllegalArgumentException(
                    "Only a Trainer can record a delivered session.");
        }

        User trainer = userDAO.findById(
                (Integer) session.getAttribute("userId"));

        if (trainer == null) {
            throw new IllegalArgumentException(
                    "Your account could not be loaded.");
        }

        return trainer;
    }

    /**
     * Loads the equipment the session covered.
     *
     * @param request request carrying the form values
     * @return the matching equipment
     */
    private Equipment requireEquipment(HttpServletRequest request) {
        String raw = request.getParameter("equipmentId");

        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException(
                    "Choose the equipment the session covered.");
        }

        int equipmentId;

        try {
            equipmentId = Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "That equipment selection is not valid.");
        }

        Equipment equipment = equipmentDAO.findById(equipmentId);

        if (equipment == null) {
            throw new IllegalArgumentException(
                    "That equipment does not exist.");
        }

        return equipment;
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
