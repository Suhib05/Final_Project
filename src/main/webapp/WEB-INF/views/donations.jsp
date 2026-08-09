<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donate Materials</title>
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
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/donations"><i class="fa-solid fa-hand-holding-heart"></i> Donate</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/training"><i class="fa-solid fa-chalkboard-user"></i> Training</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Donate</span>
    </div>

    <h2><i class="fa-solid fa-hand-holding-heart"></i> Donate Materials</h2>

    <div style="display:flex; gap:12px; margin-bottom:20px; flex-wrap:wrap;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
        <% if (isShopTech) { %>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/inventory"><i class="fa-solid fa-boxes-stacked"></i> Inventory Report</a>
        <% } %>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> <c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty lowStock}">
        <div class="alert alert-warning">
            <i class="fa-solid fa-triangle-exclamation"></i>
            Some materials are running low. Donations are especially welcome.
        </div>
    </c:if>

    <div class="card">
        <h3><i class="fa-solid fa-hand-holding-heart"></i> Donate to the Co-op</h3>
        <p class="form-hint">
            Donating material earns you
            <fmt:formatNumber value="${creditPerUnit}" maxFractionDigits="2"/>
            credits per unit. The stock is shared, so what you give back is
            available to every member.
        </p>

        <form method="post" action="${pageContext.request.contextPath}/donations" style="max-width:420px;">
            <input type="hidden" name="action" value="donate">

            <div class="form-group">
                <i class="fa-solid fa-cube field-icon"></i>
                <select id="consumableId" name="consumableId" class="form-control" required>
                    <option value="">Material</option>
                    <c:forEach var="item" items="${consumables}">
                        <option value="${item.consumableId}">
                            <c:out value="${item.type}"/>
                            (<c:out value="${item.unit}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-scale-balanced field-icon"></i>
                <input type="number" id="quantity" name="quantity" min="0.01" step="0.01" class="form-control" placeholder=" " required>
                <label for="quantity">Quantity donated</label>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-check"></i> Record Donation
            </button>
        </form>
    </div>

    <% if (isShopTech) { %>
    <div class="card">
        <h3><i class="fa-solid fa-truck-ramp-box"></i> Record a Co-op Restock</h3>
        <p class="form-hint">
            For material the co-op bought itself. Stock rises and no credit is
            awarded, because nobody contributed it personally.
        </p>

        <form method="post" action="${pageContext.request.contextPath}/donations" style="max-width:420px;">
            <input type="hidden" name="action" value="restock">

            <div class="form-group">
                <i class="fa-solid fa-cube field-icon"></i>
                <select id="restockConsumableId" name="consumableId" class="form-control" required>
                    <option value="">Material</option>
                    <c:forEach var="item" items="${consumables}">
                        <option value="${item.consumableId}">
                            <c:out value="${item.type}"/>
                            (<c:out value="${item.unit}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-scale-balanced field-icon"></i>
                <input type="number" id="restockQuantity" name="quantity" min="0.01" step="0.01" class="form-control" placeholder=" " required>
                <label for="restockQuantity">Quantity purchased</label>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-check"></i> Record Restock
            </button>
        </form>
    </div>
    <% } %>

    <div class="card">
        <h3><i class="fa-solid fa-boxes-stacked"></i> Current Stock</h3>

        <c:choose>
            <c:when test="${empty consumables}">
                <div class="empty-state">
                    <i class="fa-solid fa-boxes-stacked"></i>
                    <p>No materials are tracked yet.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Material <i class="fa-solid fa-sort"></i></th>
                                <th>Unit</th>
                                <th data-sort>In Stock <i class="fa-solid fa-sort"></i></th>
                                <th>Reorder At</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${consumables}">
                                <tr>
                                    <td><c:out value="${item.type}"/></td>
                                    <td><c:out value="${item.unit}"/></td>
                                    <td><fmt:formatNumber value="${item.stockLevel}" maxFractionDigits="2"/></td>
                                    <td><fmt:formatNumber value="${item.reorderThreshold}" maxFractionDigits="2"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.lowStock}">
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

    <div class="app-footer">&copy; 2026 Campus Maker Space Co-op &mdash; CST8288 Final Project</div>
</main>

<button id="backToTop" class="back-to-top" title="Back to top"><i class="fa-solid fa-arrow-up"></i></button>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
