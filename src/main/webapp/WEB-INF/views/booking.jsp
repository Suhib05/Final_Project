<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Book Equipment</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
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
    <a class="sidebar-link active" href="controller?action=booking"><i class="fa-solid fa-calendar-check"></i> Booking</a>
    <a class="sidebar-link" href="controller?action=checkinout"><i class="fa-solid fa-clock"></i> Check In / Out</a>
    <% if (isShopTech) { %>
    <a class="sidebar-link" href="controller?action=equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment</a>
    <a class="sidebar-link" href="controller?action=maintenance"><i class="fa-solid fa-toolbox"></i> Maintenance</a>
    <% } %>
    <a class="sidebar-link" href="controller?action=ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="controller?action=reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Book Equipment</span>
    </div>

    <h2><i class="fa-solid fa-calendar-check"></i> Book Equipment</h2>

    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert alert-error">
            <i class="fa-solid fa-circle-exclamation"></i> <%= request.getAttribute("errorMessage") %>
        </div>
    <% } %>

    <div class="card">
        <c:choose>
            <c:when test="${empty equipmentList}">
                <div class="empty-state">
                    <i class="fa-solid fa-inbox"></i>
                    <p>No equipment is currently available.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-toolbar">
                    <div class="table-search">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text" placeholder="Search equipment...">
                    </div>
                </div>

                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Asset Tag <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Category <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Make / Model <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Status <i class="fa-solid fa-sort"></i></th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="equipment" items="${equipmentList}">
                                <tr>
                                    <td>${equipment.assetTag}</td>
                                    <td>${equipment.category}</td>
                                    <td>${equipment.make} ${equipment.model}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${equipment.status == 'AVAILABLE'}">
                                                <span class="badge badge-success"><span class="badge-dot"></span> Available</span>
                                            </c:when>
                                            <c:when test="${equipment.status == 'IN_USE'}">
                                                <span class="badge badge-warning"><span class="badge-dot"></span> In Use</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger"><span class="badge-dot"></span> ${equipment.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${equipment.status == 'AVAILABLE'}">
                                            <form action="controller" method="post" style="margin:0;">
                                                <input type="hidden" name="action" value="booking">
                                                <input type="hidden" name="equipmentId" value="${equipment.equipmentId}">
                                                <button type="submit" class="btn btn-primary btn-sm">
                                                    <i class="fa-solid fa-calendar-check"></i> Book
                                                </button>
                                            </form>
                                        </c:if>
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
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>