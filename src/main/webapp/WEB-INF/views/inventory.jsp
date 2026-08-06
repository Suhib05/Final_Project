<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Consumable Inventory</title>

        <style>
            body {
                margin: 0;
                padding: 30px;
                background-color: #f4f6f8;
                color: #1f2933;
                font-family: Arial, sans-serif;
            }

            .container {
                max-width: 1100px;
                margin: auto;
            }

            h1 {
                color: #16324f;
            }

            .panel {
                padding: 24px;
                background-color: white;
                border: 1px solid #d9e2ec;
                border-radius: 8px;
                overflow-x: auto;
            }

            .report-note {
                margin-bottom: 20px;
                color: #52616b;
            }

            .error-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #721c24;
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th, td {
                padding: 12px;
                border: 1px solid #d9e2ec;
                text-align: left;
            }

            th {
                color: white;
                background-color: #274c77;
            }

            tr:nth-child(even) {
                background-color: #f7f9fb;
            }

            .stock-normal {
                color: #137333;
                font-weight: bold;
            }

            .stock-low {
                color: #b3261e;
                font-weight: bold;
            }

            .navigation {
                margin-bottom: 20px;
            }

            .navigation a {
                display: inline-block;
                padding: 9px 14px;
                color: white;
                background-color: #1769aa;
                border-radius: 4px;
                text-decoration: none;
            }
        </style>
    </head>

    <body>
        <main class="container">
            <h1>Consumable Inventory Report</h1>

            <nav class="navigation">
                <a href="${pageContext.request.contextPath}/equipment">
                    Equipment Status
                </a>
            </nav>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <section class="panel">
                <p class="report-note">
                    Consumption rate is measured as estimated units used per
                    day. Projected depletion is calculated using the current
                    stock level divided by the daily consumption rate.
                </p>

                <c:choose>
                    <c:when test="${empty consumableList}">
                        <p>No consumable inventory information is available.</p>
                    </c:when>

                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Consumable</th>
                                    <th>Unit</th>
                                    <th>Current Stock</th>
                                    <th>Reorder Threshold</th>
                                    <th>Daily Consumption</th>
                                    <th>Projected Depletion</th>
                                    <th>Stock Status</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="consumable"
                                           items="${consumableList}">
                                    <tr>
                                        <td>
                                            <c:out value="${consumable.type}"/>
                                        </td>

                                        <td>
                                            <c:out value="${consumable.unit}"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${consumable.stockLevel}"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${consumable.reorderThreshold}"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${consumable.consumptionRate}"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${consumable.consumptionRate > 0}">
                                                    <fmt:formatNumber
                                                        value="${projectedDepletionDays[
                                                            consumable.consumableId]}"
                                                        maxFractionDigits="1"/>
                                                    days
                                                </c:when>
                                                <c:otherwise>
                                                    No usage data
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${consumable.lowStock}">
                                                    <span class="stock-low">
                                                        Restocking required
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="stock-normal">
                                                        Stock available
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>
    </body>
</html>