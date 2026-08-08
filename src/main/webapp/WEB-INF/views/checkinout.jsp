<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Check In / Check Out</title>
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
    <a class="sidebar-link" href="controller?action=booking"><i class="fa-solid fa-calendar-check"></i> Booking</a>
    <a class="sidebar-link active" href="controller?action=checkinout"><i class="fa-solid fa-clock"></i> Check In / Out</a>
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
        <span class="current">Check In / Check Out</span>
    </div>

    <h2><i class="fa-solid fa-clock"></i> Check In / Check Out</h2>

    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert alert-error">
            <i class="fa-solid fa-circle-exclamation"></i> <%= request.getAttribute("errorMessage") %>
        </div>
    <% } %>
    <% if (request.getAttribute("successMessage") != null) { %>
        <div class="alert alert-success">
            <i class="fa-solid fa-circle-check"></i> <%= request.getAttribute("successMessage") %>
        </div>
    <% } %>

    <div class="card" style="max-width:440px;">
        <form action="controller" method="post">
            <input type="hidden" name="action" value="checkinout">

            <div class="form-group">
                <i class="fa-solid fa-hashtag field-icon"></i>
                <input type="number" id="equipmentId" name="equipmentId" class="form-control" placeholder=" " required>
                <label for="equipmentId">Equipment ID</label>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-right-left field-icon"></i>
                <select id="mode" name="mode" class="form-control">
                    <option value="checkin">Check In</option>
                    <option value="checkout">Check Out</option>
                </select>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-cube field-icon"></i>
                <input type="number" step="0.01" id="materialsConsumed" name="materialsConsumed" class="form-control" value="0">
                <label for="materialsConsumed" style="top:-9px; left:34px; font-size:11.5px; color:var(--accent);">
                    Materials Consumed (check-out only)
                </label>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-check"></i> Submit
            </button>
        </form>
    </div>

    <div class="app-footer">&copy; 2026 Campus Maker Space Co-op — CST8288 Final Project</div>
</main>

<button id="backToTop" class="back-to-top" title="Back to top"><i class="fa-solid fa-arrow-up"></i></button>
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>