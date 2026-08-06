<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Equipment Status</title>

        <style>
            body {
                margin: 0;
                padding: 30px;
                background-color: #f4f6f8;
                color: #1f2933;
                font-family: Arial, sans-serif;
            }

            .container {
                max-width: 1500px;
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

            .form-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(220px, 1fr));
                gap: 16px;
            }

            label {
                display: block;
                margin-bottom: 6px;
                font-weight: bold;
            }

            input, select {
                box-sizing: border-box;
                width: 100%;
                padding: 9px;
                border: 1px solid #9fb3c8;
                border-radius: 4px;
            }

            button {
                margin-top: 10px;
                padding: 9px 14px;
                color: white;
                background-color: #1769aa;
                border: 0;
                border-radius: 4px;
                cursor: pointer;
            }

            .check-in-button {
                background-color: #137333;
            }

            .check-out-button {
                background-color: #b06000;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th, td {
                padding: 10px;
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

            .available {
                color: #137333;
                font-weight: bold;
            }

            .in-use {
                color: #b06000;
                font-weight: bold;
            }

            .down {
                color: #b3261e;
                font-weight: bold;
            }

            .maintenance-alert {
                color: #b3261e;
                font-weight: bold;
            }

            .restricted-note {
                color: #52616b;
                font-size: 0.9rem;
            }

            .session-form {
                min-width: 210px;
            }

            .session-form input,
            .session-form select {
                margin-bottom: 7px;
            }

            @media (max-width: 750px) {
                .form-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>

    <body>
        <main class="container">
            <h1>Campus Maker Space Equipment</h1>

            <nav class="navigation">
                <a href="${pageContext.request.contextPath}/inventory">
                    Inventory Report
                </a>

                <a href="${pageContext.request.contextPath}/maintenance">
                    Maintenance
                </a>
            </nav>

            <c:if test="${param.registered eq 'true'}">
                <div class="success-message">
                    Equipment registered successfully.
                </div>
            </c:if>

            <c:if test="${param.session eq 'checkIn'}">
                <div class="success-message">
                    Equipment check-in completed successfully.
                </div>
            </c:if>

            <c:if test="${param.session eq 'checkOut'}">
                <div class="success-message">
                    Equipment check-out completed successfully.
                </div>
            </c:if>

            <c:if test="${not empty param.sessionError}">
                <div class="error-message">
                    <c:out value="${param.sessionError}"/>
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <section class="panel">
                <h2>Register Equipment</h2>

                <p class="restricted-note">
                    This operation is restricted to Shop-Techs and
                    administrators.
                </p>

                <form method="post"
                      action="${pageContext.request.contextPath}/equipment">

                    <div class="form-grid">
                        <div>
                            <label for="assetTag">Asset tag</label>
                            <input id="assetTag"
                                   name="assetTag"
                                   type="text"
                                   required>
                        </div>

                        <div>
                            <label for="category">Category</label>
                            <select id="category"
                                    name="category"
                                    required>
                                <option value="">Select a category</option>
                                <option value="PRINTER_3D">
                                    3D Printer
                                </option>
                                <option value="LASER_CUTTER">
                                    Laser Cutter
                                </option>
                                <option value="CNC">
                                    CNC Machine
                                </option>
                            </select>
                        </div>

                        <div>
                            <label for="make">Make</label>
                            <input id="make"
                                   name="make"
                                   type="text"
                                   required>
                        </div>

                        <div>
                            <label for="model">Model</label>
                            <input id="model"
                                   name="model"
                                   type="text"
                                   required>
                        </div>

                        <div>
                            <label for="maintenanceThreshold">
                                Maintenance threshold (hours)
                            </label>
                            <input id="maintenanceThreshold"
                                   name="maintenanceThreshold"
                                   type="number"
                                   min="0.01"
                                   step="0.01"
                                   required>
                        </div>

                        <div>
                            <label for="accessCreditRate">
                                Access credit rate per hour
                            </label>
                            <input id="accessCreditRate"
                                   name="accessCreditRate"
                                   type="number"
                                   min="0"
                                   step="0.01"
                                   required>
                        </div>
                    </div>

                    <button type="submit">Register Equipment</button>
                </form>
            </section>

            <section class="panel">
                <h2>Equipment and Active Session Report</h2>

                <p class="restricted-note">
                    The report shows equipment availability, the current user,
                    elapsed access time and materials recorded for the active
                    session.
                </p>

                <c:choose>
                    <c:when test="${empty equipmentList}">
                        <p>No equipment is currently registered.</p>
                    </c:when>

                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Asset Tag</th>
                                    <th>Equipment</th>
                                    <th>Category</th>
                                    <th>Status</th>
                                    <th>Current User</th>
                                    <th>Elapsed Time</th>
                                    <th>Materials Used</th>
                                    <th>Credit Rate</th>
                                    <th>Maintenance</th>
                                    <th>Session Action</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="equipment"
                                           items="${equipmentList}">

                                    <c:set var="activeSession"
                                           value="${activeSessionsByEquipment[
                                               equipment.equipmentId]}"/>

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
                                            <c:choose>
                                                <c:when test="${equipment.status
                                                        eq 'AVAILABLE'}">
                                                    <span class="available">
                                                        Available
                                                    </span>
                                                </c:when>

                                                <c:when test="${equipment.status
                                                        eq 'IN_USE'}">
                                                    <span class="in-use">
                                                        In Use
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="down">
                                                        Down for Maintenance
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty activeSession}">
                                                    User #
                                                    <c:out value="${activeSession.userId}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    —
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty activeSession}">
                                                    <fmt:formatNumber
                                                        value="${elapsedHoursByEquipment[
                                                            equipment.equipmentId]}"
                                                        maxFractionDigits="3"/>
                                                    hours
                                                </c:when>
                                                <c:otherwise>
                                                    —
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty activeSession}">
                                                    <fmt:formatNumber
                                                        value="${activeSession.materialsConsumed}"
                                                        maxFractionDigits="2"/>

                                                    <c:if test="${not empty
                                                            activeSession.consumableId}">
                                                        <br>
                                                        Consumable #
                                                        <c:out value="${activeSession.consumableId}"/>
                                                    </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    —
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${equipment.accessCreditRate}"
                                                maxFractionDigits="2"/>
                                            credits/hour
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${equipment.hoursOfUse
                                                        >= equipment.maintenanceThreshold}">
                                                    <span class="maintenance-alert">
                                                        Service required
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    No alert
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${equipment.status
                                                        eq 'AVAILABLE'}">

                                                    <form class="session-form"
                                                          method="post"
                                                          action="${pageContext.request.contextPath}/sessions">

                                                        <input type="hidden"
                                                               name="action"
                                                               value="checkIn">

                                                        <input type="hidden"
                                                               name="equipmentId"
                                                               value="${equipment.equipmentId}">

                                                        <label>
                                                            User ID
                                                            <input type="number"
                                                                   name="userId"
                                                                   min="1"
                                                                   required>
                                                        </label>

                                                        <label>
                                                            Consumable
                                                            <select name="consumableId">
                                                                <option value="">
                                                                    No consumable
                                                                </option>

                                                                <c:forEach var="consumable"
                                                                           items="${consumableList}">
                                                                    <c:if test="${consumable.stockLevel > 0}">
                                                                        <option value="${consumable.consumableId}">
                                                                            <c:out value="${consumable.type}"/>
                                                                            -
                                                                            <c:out value="${consumable.stockLevel}"/>
                                                                            <c:out value="${consumable.unit}"/>
                                                                        </option>
                                                                    </c:if>
                                                                </c:forEach>
                                                            </select>
                                                        </label>

                                                        <button type="submit"
                                                                class="check-in-button">
                                                            Check In
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <c:when test="${equipment.status
                                                        eq 'IN_USE'
                                                        and not empty activeSession}">

                                                    <form class="session-form"
                                                          method="post"
                                                          action="${pageContext.request.contextPath}/sessions">

                                                        <input type="hidden"
                                                               name="action"
                                                               value="checkOut">

                                                        <input type="hidden"
                                                               name="equipmentId"
                                                               value="${equipment.equipmentId}">

                                                        <label>
                                                            Materials consumed
                                                            <input type="number"
                                                                   name="materialsConsumed"
                                                                   min="0"
                                                                   step="0.01"
                                                                   value="0"
                                                                   required>
                                                        </label>

                                                        <button type="submit"
                                                                class="check-out-button">
                                                            Check Out
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <c:when test="${equipment.status
                                                        eq 'IN_USE'}">
                                                    Active-session details
                                                    are unavailable.
                                                </c:when>

                                                <c:otherwise>
                                                    Equipment unavailable
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