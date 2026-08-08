<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consumable Inventory</title>
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
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/inventory"><i class="fa-solid fa-boxes-stacked"></i> Inventory</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/maintenance"><i class="fa-solid fa-toolbox"></i> Maintenance</a>
    <% } %>
    <a class="sidebar-link" href="controller?action=ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="controller?action=reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Inventory</span>
    </div>

    <h2><i class="fa-solid fa-boxes-stacked"></i> Consumable Inventory Report</h2>

    <div style="margin-bottom:20px;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/equipment">
            <i class="fa-solid fa-screwdriver-wrench"></i> Equipment Status
        </a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>

    <div class="card">
        <p class="form-hint">
            Consumption rate is measured as estimated units used per day. Projected depletion is calculated
            using the current stock level divided by the daily consumption rate.
        </p>

        <c:choose>
            <c:when test="${empty consumableList}">
                <div class="empty-state">
                    <i class="fa-solid fa-boxes-stacked"></i>
                    <p>No consumable inventory information is available.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-toolbar">
                    <div class="table-search">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text" placeholder="Search consumables...">
                    </div>
                </div>

                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Consumable <i class="fa-solid fa-sort"></i></th>
                                <th>Unit</th>
                                <th data-sort>Current Stock <i class="fa-solid fa-sort"></i></th>
                                <th>Reorder Threshold</th>
                                <th>Daily Consumption</th>
                                <th>Projected Depletion</th>
                                <th data-sort>Stock Status <i class="fa-solid fa-sort"></i></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="consumable" items="${consumableList}">
                                <tr>
                                    <td><c:out value="${consumable.type}"/></td>
                                    <td><c:out value="${consumable.unit}"/></td>
                                    <td><fmt:formatNumber value="${consumable.stockLevel}" maxFractionDigits="2"/></td>
                                    <td><fmt:formatNumber value="${consumable.reorderThreshold}" maxFractionDigits="2"/></td>
                                    <td><fmt:formatNumber value="${consumable.consumptionRate}" maxFractionDigits="2"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${consumable.consumptionRate > 0}">
                                                <fmt:formatNumber value="${projectedDepletionDays[consumable.consumableId]}" maxFractionDigits="1"/> days
                                            </c:when>
                                            <c:otherwise>
                                                No usage data
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${consumable.lowStock}">
                                                <span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> Restocking required</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success"><span class="badge-dot"></span> Stock available</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="app-footer">&copy; 2026 Campus Maker Space Co-op — CST8288 Final Project</div>
</main>

<button id="backToTop" class="back-to-top" title="Back to top"><i class="fa-solid fa-arrow-up"></i></button>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>