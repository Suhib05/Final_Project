<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Reports</title>

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

            h2 {
                margin-top: 0;
                color: #16324f;
                font-size: 1.1em;
            }

            .panel {
                margin-bottom: 24px;
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

            .summary {
                display: flex;
                flex-wrap: wrap;
                gap: 16px;
                margin-bottom: 24px;
            }

            .summary-card {
                flex: 1 1 170px;
                padding: 18px;
                background-color: white;
                border: 1px solid #d9e2ec;
                border-radius: 8px;
            }

            .summary-card .label {
                color: #52616b;
                font-size: 0.85em;
                text-transform: uppercase;
            }

            .summary-card .value {
                margin-top: 6px;
                font-size: 1.6em;
                font-weight: bold;
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

            .credit {
                color: #137333;
                font-weight: bold;
            }

            .debit {
                color: #b3261e;
                font-weight: bold;
            }

            .alert {
                color: #b3261e;
                font-weight: bold;
            }

            .ok {
                color: #137333;
                font-weight: bold;
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
        </style>
    </head>

    <body>
        <main class="container">
            <h1>Reports</h1>

            <nav class="navigation">
                <a href="${pageContext.request.contextPath}/ledger">
                    My Ledger
                </a>
                <a href="${pageContext.request.contextPath}/workorders">
                    Work Orders
                </a>
                <a href="${pageContext.request.contextPath}/equipment">
                    Equipment Status
                </a>
            </nav>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty ledgerReport}">
                <section class="panel">
                    <h2>
                        My Activity &mdash;
                        <c:out value="${ledgerReport.userName}"/>
                        (<c:out value="${ledgerReport.userType}"/>)
                    </h2>

                    <p class="report-note">
                        Figures for the current month, with lifetime totals
                        read straight from the ledger.
                    </p>

                    <div class="summary">
                        <div class="summary-card">
                            <div class="label">Credited This Month</div>
                            <div class="value credit">
                                <fmt:formatNumber
                                    value="${ledgerReport.creditTotal}"
                                    maxFractionDigits="2"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Debited This Month</div>
                            <div class="value debit">
                                <fmt:formatNumber
                                    value="${ledgerReport.debitTotal}"
                                    maxFractionDigits="2"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Lifetime Credits</div>
                            <div class="value">
                                <fmt:formatNumber value="${lifetimeCredits}"
                                                  maxFractionDigits="2"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Lifetime Debits</div>
                            <div class="value">
                                <fmt:formatNumber value="${lifetimeDebits}"
                                                  maxFractionDigits="2"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Ledger Balance</div>
                            <div class="value">
                                <fmt:formatNumber value="${ledgerBalance}"
                                                  maxFractionDigits="2"/>
                            </div>
                        </div>
                    </div>

                    <c:if test="${ledgerReport.settlementDue}">
                        <p class="alert">
                            You have
                            <fmt:formatNumber
                                value="${ledgerReport.outstandingDebit}"
                                maxFractionDigits="2"/>
                            outstanding. Settle it from the ledger page within
                            ten days of month end.
                        </p>
                    </c:if>
                </section>
            </c:if>

            <c:if test="${not empty contributionEntries}">
                <section class="panel">
                    <h2>My Contributions</h2>

                    <p class="report-note">
                        Everything you have earned credit for, totalling
                        <fmt:formatNumber value="${contributionCredits}"
                                          maxFractionDigits="2"/>
                        credits.
                    </p>

                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Contribution</th>
                                <th>Credit Earned</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="entry"
                                       items="${contributionEntries}">
                                <tr>
                                    <td>
                                        <c:out
                                            value="${entry.transactionDate}"/>
                                    </td>
                                    <td><c:out value="${entry.source}"/></td>
                                    <td class="credit">
                                        <fmt:formatNumber
                                            value="${entry.amount}"
                                            maxFractionDigits="2"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </section>
            </c:if>

            <c:if test="${not empty myCompletedOrders}">
                <section class="panel">
                    <h2>Work Orders I Completed</h2>

                    <table>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Requester</th>
                                <th>Origin</th>
                                <th>Completed</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="job" items="${myCompletedOrders}">
                                <tr>
                                    <td><c:out value="${job.workOrderId}"/></td>
                                    <td>
                                        <c:out value="${job.requesterName}"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${job.external}">
                                                External client
                                            </c:when>
                                            <c:otherwise>
                                                Member
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:out value="${job.dateCompleted}"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </section>
            </c:if>

            <c:if test="${not empty equipmentReport}">
                <section class="panel">
                    <h2>Co-op Status</h2>

                    <p class="report-note">
                        The whole space at a glance. Equipment and stock
                        figures come from the equipment and inventory modules.
                    </p>

                    <div class="summary">
                        <div class="summary-card">
                            <div class="label">Machines</div>
                            <div class="value">
                                <c:out
                                    value="${equipmentReport.totalEquipment}"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Available</div>
                            <div class="value ok">
                                <c:out
                                    value="${equipmentReport.availableCount}"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">In Use</div>
                            <div class="value">
                                <c:out value="${equipmentReport.inUseCount}"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Maintenance Alerts</div>
                            <div class="value alert">
                                <c:out value="${equipmentReport.alertCount}"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Low Stock</div>
                            <div class="value alert">
                                <c:out
                                    value="${equipmentReport.lowStockCount}"/>
                            </div>
                        </div>

                        <div class="summary-card">
                            <div class="label">Open Work Orders</div>
                            <div class="value">
                                <c:out value="${openWorkOrders.size()}"/>
                            </div>
                        </div>
                    </div>

                    <c:if test="${not empty equipmentReport.maintenanceAlerts}">
                        <h2>Machines Needing Service</h2>

                        <table>
                            <thead>
                                <tr>
                                    <th>Asset Tag</th>
                                    <th>Machine</th>
                                    <th>Hours of Use</th>
                                    <th>Threshold</th>
                                    <th>Status</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="machine"
                                           items="${equipmentReport.maintenanceAlerts}">
                                    <tr>
                                        <td>
                                            <c:out value="${machine.assetTag}"/>
                                        </td>
                                        <td>
                                            <c:out value="${machine.make}"/>
                                            <c:out value="${machine.model}"/>
                                        </td>
                                        <td>
                                            <fmt:formatNumber
                                                value="${machine.hoursOfUse}"
                                                maxFractionDigits="1"/>
                                        </td>
                                        <td>
                                            <fmt:formatNumber
                                                value="${machine.maintenanceThreshold}"
                                                maxFractionDigits="1"/>
                                        </td>
                                        <td class="alert">
                                            <c:out value="${machine.status}"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                    <c:if test="${not empty
                                  equipmentReport.lowStockConsumables}">
                        <h2>Materials To Restock</h2>

                        <table>
                            <thead>
                                <tr>
                                    <th>Material</th>
                                    <th>Stock</th>
                                    <th>Unit</th>
                                    <th>Reorder At</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="item"
                                           items="${equipmentReport.lowStockConsumables}">
                                    <tr>
                                        <td><c:out value="${item.type}"/></td>
                                        <td class="alert">
                                            <fmt:formatNumber
                                                value="${item.stockLevel}"
                                                maxFractionDigits="2"/>
                                        </td>
                                        <td><c:out value="${item.unit}"/></td>
                                        <td>
                                            <fmt:formatNumber
                                                value="${item.reorderThreshold}"
                                                maxFractionDigits="2"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </section>
            </c:if>
        </main>
    </body>
</html>
