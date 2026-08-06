package com.cmsc.business.equipment;

import com.cmsc.dao.ConsumableDAO;
import com.cmsc.dao.ConsumableDAOImpl;
import com.cmsc.model.Consumable;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates the consumable inventory report, including current stock,
 * consumption rate, low-stock status and projected time until depletion.
 *
 * This servlet belongs to the Business layer and delegates database
 * operations to ConsumableDAO.
 *
 * @author Saleh Alhajj
 */
@WebServlet(name = "InventoryServlet", urlPatterns = {"/inventory"})
public class InventoryServlet extends HttpServlet {

    private static final String INVENTORY_VIEW
            = "/WEB-INF/views/inventory.jsp";

    private ConsumableDAO consumableDAO;

    /**
     * Initializes the DAO used by the inventory report.
     */
    @Override
    public void init() {
        consumableDAO = new ConsumableDAOImpl();
    }

    /**
     * Generates and displays the current inventory report.
     *
     * The consumption rate represents estimated units used per day.
     * Projected depletion is calculated as current stock divided by the
     * consumption rate.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when the inventory view cannot be processed
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Consumable> consumables = consumableDAO.findAll();

            if (consumables == null) {
                consumables = List.of();
            }

            Map<Integer, Double> projectedDepletionDays
                    = calculateProjectedDepletionDays(consumables);

            request.setAttribute("consumableList", consumables);
            request.setAttribute(
                    "projectedDepletionDays",
                    projectedDepletionDays
            );
        } catch (RuntimeException exception) {
            request.setAttribute(
                    "errorMessage",
                    "Unable to load the inventory report."
            );
            request.setAttribute("consumableList", List.of());
            request.setAttribute(
                    "projectedDepletionDays",
                    Map.of()
            );
        }

        request.getRequestDispatcher(INVENTORY_VIEW)
                .forward(request, response);
    }

    private Map<Integer, Double> calculateProjectedDepletionDays(
            List<Consumable> consumables) {

        Map<Integer, Double> projections = new LinkedHashMap<>();

        for (Consumable consumable : consumables) {
            double consumptionRate = consumable.getConsumptionRate();

            if (consumptionRate > 0) {
                double projectedDays
                        = consumable.getStockLevel() / consumptionRate;

                projections.put(
                        consumable.getConsumableId(),
                        projectedDays
                );
            }
        }

        return projections;
    }
}