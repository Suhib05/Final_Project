<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String userType = (String) session.getAttribute("userType");
    boolean isShopTech = "SHOP_TECH".equals(userType);
%>

<div class="app-topbar">
    <div class="brand">
        <button class="icon-btn sidebar-toggle"><i class="fa-solid fa-bars"></i></button>
        <i class="fa-solid fa-screwdriver-wrench"></i> CMSC
    </div>
    <div class="topbar-actions">
        <span style="font-size:13.5px; color:var(--text-muted);">
            <i class="fa-solid fa-user-circle"></i> <%= session.getAttribute("userName") %>
            <span class="badge badge-neutral" style="margin-left:6px;"><%= userType %></span>
        </span>
        <button class="icon-btn" id="darkModeToggle" title="Toggle dark mode"><i class="fa-solid fa-moon"></i></button>
        <a href="controller?action=logout" class="btn btn-outline btn-sm">
            <i class="fa-solid fa-right-from-bracket"></i> Log Out
        </a>
    </div>
</div>

<nav class="app-sidebar">
    <a class="sidebar-link" href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
    <a class="sidebar-link" href="controller?action=booking"><i class="fa-solid fa-calendar-check"></i> Booking</a>
    <a class="sidebar-link" href="controller?action=checkinout"><i class="fa-solid fa-clock"></i> Check In / Out</a>
    <% if (isShopTech) { %>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/inventory"><i class="fa-solid fa-boxes-stacked"></i> Inventory</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/maintenance"><i class="fa-solid fa-toolbox"></i> Maintenance</a>
    <% } %>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Reports</span>
    </div>

    <h2><i class="fa-solid fa-chart-column"></i> Reports</h2>

    <div style="display:flex; gap:12px; margin-bottom:20px; flex-wrap:wrap;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment Status</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>

    <c:if test="${not empty ledgerReport}">
        <div class="card">
            <h3>
                <i class="fa-solid fa-user"></i> My Activity &mdash;
                <c:out value="${ledgerReport.userName}"/> (<c:out value="${ledgerReport.userType}"/>)
            </h3>
            <p class="form-hint">Figures for the current month, with lifetime totals read straight from the ledger.</p>

            <div class="stat-grid">
                <div class="stat-card">
                    <div class="stat-icon icon-bg-success"><i class="fa-solid fa-arrow-up"></i></div>
                    <div><div class="stat-value"><fmt:formatNumber value="${ledgerReport.creditTotal}" maxFractionDigits="2"/></div><div class="stat-label">Credited This Month</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-danger"><i class="fa-solid fa-arrow-down"></i></div>
                    <div><div class="stat-value"><fmt:formatNumber value="${ledgerReport.debitTotal}" maxFractionDigits="2"/></div><div class="stat-label">Debited This Month</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-accent"><i class="fa-solid fa-coins"></i></div>
                    <div><div class="stat-value"><fmt:formatNumber value="${lifetimeCredits}" maxFractionDigits="2"/></div><div class="stat-label">Lifetime Credits</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-warning"><i class="fa-solid fa-coins"></i></div>
                    <div><div class="stat-value"><fmt:formatNumber value="${lifetimeDebits}" maxFractionDigits="2"/></div><div class="stat-label">Lifetime Debits</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-accent"><i class="fa-solid fa-scale-balanced"></i></div>
                    <div><div class="stat-value"><fmt:formatNumber value="${ledgerBalance}" maxFractionDigits="2"/></div><div class="stat-label">Ledger Balance</div></div>
                </div>
            </div>

            <c:if test="${ledgerReport.settlementDue}">
                <div class="alert alert-warning">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    You have <fmt:formatNumber value="${ledgerReport.outstandingDebit}" maxFractionDigits="2"/> outstanding.
                    Settle it from the ledger page within ten days of month end.
                </div>
            </c:if>
        </div>
    </c:if>

    <c:if test="${not empty contributionEntries}">
        <div class="card">
            <h3><i class="fa-solid fa-hand-holding-heart"></i> My Contributions</h3>
            <p class="form-hint">
                Everything you have earned credit for, totalling
                <fmt:formatNumber value="${contributionCredits}" maxFractionDigits="2"/> credits.
            </p>

            <div class="table-wrapper" data-searchable-table>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th data-sort>Date <i class="fa-solid fa-sort"></i></th>
                            <th>Contribution</th>
                            <th data-sort>Credit Earned <i class="fa-solid fa-sort"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="entry" items="${contributionEntries}">
                            <tr>
                                <td><c:out value="${entry.transactionDate}"/></td>
                                <td><c:out value="${entry.source}"/></td>
                                <td><span class="badge badge-success"><fmt:formatNumber value="${entry.amount}" maxFractionDigits="2"/></span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty myCompletedOrders}">
        <div class="card">
            <h3><i class="fa-solid fa-clipboard-check"></i> Work Orders I Completed</h3>
            <div class="table-wrapper" data-searchable-table>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th data-sort># <i class="fa-solid fa-sort"></i></th>
                            <th>Requester</th>
                            <th>Origin</th>
                            <th data-sort>Completed <i class="fa-solid fa-sort"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="job" items="${myCompletedOrders}">
                            <tr>
                                <td><c:out value="${job.workOrderId}"/></td>
                                <td><c:out value="${job.requesterName}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${job.external}"><span class="badge badge-neutral">External client</span></c:when>
                                        <c:otherwise><span class="badge badge-neutral">Member</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${job.dateCompleted}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty equipmentReport}">
        <div class="card">
            <h3><i class="fa-solid fa-building"></i> Co-op Status</h3>
            <p class="form-hint">The whole space at a glance. Equipment and stock figures come from the equipment and inventory modules.</p>

            <div class="stat-grid">
                <div class="stat-card">
                    <div class="stat-icon icon-bg-accent"><i class="fa-solid fa-screwdriver-wrench"></i></div>
                    <div><div class="stat-value"><c:out value="${equipmentReport.totalEquipment}"/></div><div class="stat-label">Machines</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-success"><i class="fa-solid fa-circle-check"></i></div>
                    <div><div class="stat-value"><c:out value="${equipmentReport.availableCount}"/></div><div class="stat-label">Available</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-warning"><i class="fa-solid fa-gears"></i></div>
                    <div><div class="stat-value"><c:out value="${equipmentReport.inUseCount}"/></div><div class="stat-label">In Use</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-danger"><i class="fa-solid fa-triangle-exclamation"></i></div>
                    <div><div class="stat-value"><c:out value="${equipmentReport.alertCount}"/></div><div class="stat-label">Maintenance Alerts</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-danger"><i class="fa-solid fa-boxes-stacked"></i></div>
                    <div><div class="stat-value"><c:out value="${equipmentReport.lowStockCount}"/></div><div class="stat-label">Low Stock</div></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon icon-bg-accent"><i class="fa-solid fa-clipboard-list"></i></div>
                    <div><div class="stat-value"><c:out value="${openWorkOrders.size()}"/></div><div class="stat-label">Open Work Orders</div></div>
                </div>
            </div>

            <c:if test="${not empty equipmentReport.maintenanceAlerts}">
                <h3 style="margin-top:28px;"><i class="fa-solid fa-toolbox"></i> Machines Needing Service</h3>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Asset Tag <i class="fa-solid fa-sort"></i></th>
                                <th>Machine</th>
                                <th data-sort>Hours of Use <i class="fa-solid fa-sort"></i></th>
                                <th>Threshold</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="machine" items="${equipmentReport.maintenanceAlerts}">
                                <tr>
                                    <td><c:out value="${machine.assetTag}"/></td>
                                    <td><c:out value="${machine.make}"/> <c:out value="${machine.model}"/></td>
                                    <td><fmt:formatNumber value="${machine.hoursOfUse}" maxFractionDigits="1"/></td>
                                    <td><fmt:formatNumber value="${machine.maintenanceThreshold}" maxFractionDigits="1"/></td>
                                    <td><span class="badge badge-danger"><c:out value="${machine.status}"/></span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <c:if test="${not empty equipmentReport.lowStockConsumables}">
                <h3 style="margin-top:28px;"><i class="fa-solid fa-boxes-stacked"></i> Materials To Restock</h3>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Material <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Stock <i class="fa-solid fa-sort"></i></th>
                                <th>Unit</th>
                                <th>Reorder At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${equipmentReport.lowStockConsumables}">
                                <tr>
                                    <td><c:out value="${item.type}"/></td>
                                    <td><span class="badge badge-danger"><fmt:formatNumber value="${item.stockLevel}" maxFractionDigits="2"/></span></td>
                                    <td><c:out value="${item.unit}"/></td>
                                    <td><fmt:formatNumber value="${item.reorderThreshold}" maxFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </c:if>

    <div class="app-footer">&copy; 2026 Campus Maker Space Co-op — CST8288 Final Project</div>
</main>

<button id="backToTop" class="back-to-top" title="Back to top"><i class="fa-solid fa-arrow-up"></i></button>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>