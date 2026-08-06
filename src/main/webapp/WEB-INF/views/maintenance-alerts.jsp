<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Predictive Maintenance</title>

        <style>
            body {
                margin: 0;
                padding: 30px;
                background-color: #f4f6f8;
                color: #1f2933;
                font-family: Arial, sans-serif;
            }

            .container {
                max-width: 1200px;
                margin: auto;
            }

            h1, h2 {
                color: #16324f;
            }

            .panel {
                margin-bottom: 30px;
                padding: 24px;
                background-color: white;
                border: 1px solid #d9e2ec;
                border-radius: 8px;
                overflow-x: auto;
            }

            .navigation {
                margin-bottom: 20px;
            }

            .navigation a {
                display: inline-block;
                margin-right: 8px;
                padding: 9px 14px;
                color: white;
                background-color: #1769aa;
                border-radius: 4px;
                text-decoration: none;
            }

            .success-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #155724;
                background-color: #d4edda;
                border: 1px solid #c3e6cb;
            }

            .error-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #721c24;
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
            }

            .alert-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #7a3700;
                background-color: #fff3cd;
                border: 1px solid #ffe69c;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th, td {
                padding: 11px;
                border: 1px solid #d9e2ec;
                text-align: left;
                vertical-align: top;
            }

            th {
                color: white;
                background-color: #274c77;
            }

            tr:nth-child(even) {
                background-color: #f7f9fb;
            }

            input, textarea {
                box-sizing: border-box;
                width: 100%;
                margin-bottom: 8px;
                padding: 8px;
                border: 1px solid #9fb3c8;
                border-radius: 4px;
            }

            textarea {
                min-width: 230px;
                resize: vertical;
            }

            button {
                padding: 9px 14px;
                color: white;
                background-color: #1769aa;
                border: 0;
                border-radius: 4px;
                cursor: pointer;
            }

            .schedule-button {
                background-color: #b06000;
            }

            .complete-button {
                background-color: #137333;
            }

            .status-alert {
                color: #b3261e;
                font-weight: bold;
            }

            .status-scheduled {
                color: #b06000;
                font-weight: bold;
            }

            .report-note {
                color: #52616b;
            }
        </style>
    </head>

    <body>
        <main class="container">
            <h1>Predictive Maintenance</h1>

            <nav class="navigation">
                <a href="${pageContext.request.contextPath}/equipment">
                    Equipment Status
                </a>

                <a href="${pageContext.request.contextPath}/inventory">
                    Inventory Report
                </a>
            </nav>

            <c:if test="${param.result eq 'schedule'}">
                <div class="success-message">
                    Maintenance task scheduled successfully.
                </div>
            </c:if>

            <c:if test="${param.result eq 'complete'}">
                <div class="success-message">
                    Maintenance completed and logged successfully.
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <section class="panel">
                <h2>Predictive Maintenance Alerts</h2>

                <p class="report-note">
                    An alert is generated when equipment usage reaches its
                    configured maintenance threshold.
                </p>

                <c:choose>
                    <c:when test="${empty maintenanceAlerts}">
                        <p>No predictive-maintenance alerts are active.</p>
                    </c:when>

                    <c:otherwise>
                        <div class="alert-message">
                            One or more machines require servicing.
                        </div>

                        <table>
                            <thead>
                                <tr>
                                    <th>Asset Tag</th>
                                    <th>Equipment</th>
                                    <th>Category</th>
                                    <th>Usage Hours</th>
                                    <th>Threshold</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="equipment"
                                           items="${maintenanceAlerts}">
                                    <tr>
                                        <td>
                                            <c:out value="${equipment.assetTag}"/>
                                        </td>

                                        <td>
                                            <c:out value="${equipment.make}"/>
                                            <c:out value="${equipment.model}"/>
                                        </td>

                                        <td>
                                            <c:out value="${equipment.category}"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${equipment.hoursOfUse}"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${equipment.maintenanceThreshold}"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <span class="status-alert">
                                                <c:out value="${equipment.status}"/>
                                            </span>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${equipment.status
                                                        eq 'DOWN_FOR_MAINTENANCE'}">
                                                    <span class="status-scheduled">
                                                        Already scheduled
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/maintenance">

                                                        <input type="hidden"
                                                               name="action"
                                                               value="schedule">

                                                        <input type="hidden"
                                                               name="equipmentId"
                                                               value="${equipment.equipmentId}">

                                                        <button type="submit"
                                                                class="schedule-button">
                                                            Schedule
                                                        </button>
                                                    </form>
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

            <section class="panel">
                <h2>Scheduled Maintenance</h2>

                <p class="report-note">
                    Shop-Techs can record completed maintenance activities
                    from this section.
                </p>

                <c:choose>
                    <c:when test="${empty scheduledEquipment}">
                        <p>No equipment is currently scheduled for maintenance.</p>
                    </c:when>

                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Asset Tag</th>
                                    <th>Equipment</th>
                                    <th>Usage at Service</th>
                                    <th>Complete Maintenance</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="equipment"
                                           items="${scheduledEquipment}">
                                    <tr>
                                        <td>
                                            <c:out value="${equipment.assetTag}"/>
                                        </td>

                                        <td>
                                            <c:out value="${equipment.make}"/>
                                            <c:out value="${equipment.model}"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${equipment.hoursOfUse}"
                                                maxFractionDigits="2"/>
                                            hours
                                        </td>

                                        <td>
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/maintenance">

                                                <input type="hidden"
                                                       name="action"
                                                       value="complete">

                                                <input type="hidden"
                                                       name="equipmentId"
                                                       value="${equipment.equipmentId}">

                                                <label>
                                                    Shop-Tech user ID
                                                    <input type="number"
                                                           name="shopTechId"
                                                           min="1"
                                                           required>
                                                </label>

                                                <label>
                                                    Maintenance description
                                                    <textarea name="description"
                                                              rows="3"
                                                              required></textarea>
                                                </label>

                                                <button type="submit"
                                                        class="complete-button">
                                                    Complete and Log
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="panel">
                <h2>Maintenance History</h2>

                <c:choose>
                    <c:when test="${empty maintenanceLogs}">
                        <p>No maintenance activities have been logged.</p>
                    </c:when>

                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Log ID</th>
                                    <th>Equipment ID</th>
                                    <th>Shop-Tech ID</th>
                                    <th>Date Logged</th>
                                    <th>Description</th>
                                    <th>Hours at Service</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="maintenanceLog"
                                           items="${maintenanceLogs}">
                                    <tr>
                                        <td>
                                            <c:out value="${maintenanceLog.logId}"/>
                                        </td>

                                        <td>
                                            <c:out value="${maintenanceLog.equipmentId}"/>
                                        </td>

                                        <td>
                                            <c:out value="${maintenanceLog.shopTechId}"/>
                                        </td>

                                        <td>
                                            <c:out value="${maintenanceLog.dateLogged}"/>
                                        </td>

                                        <td>
                                            <c:out value="${maintenanceLog.description}"/>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${maintenanceLog.hoursAtService}"
                                                maxFractionDigits="2"/>
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