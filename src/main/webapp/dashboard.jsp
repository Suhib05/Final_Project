<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Dashboard</title>
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
    <a class="sidebar-link active" href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
    <a class="sidebar-link" href="controller?action=booking"><i class="fa-solid fa-calendar-check"></i> Booking</a>
    <a class="sidebar-link" href="controller?action=checkinout"><i class="fa-solid fa-clock"></i> Check In / Out</a>
    <% if (isShopTech) { %>
    <a class="sidebar-link" href="controller?action=equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment</a>
    <a class="sidebar-link" href="controller?action=inventory"><i class="fa-solid fa-boxes-stacked"></i> Inventory</a>
    <a class="sidebar-link" href="controller?action=maintenance"><i class="fa-solid fa-toolbox"></i> Maintenance</a>
    <% } %>
    <a class="sidebar-link" href="controller?action=donations"><i class="fa-solid fa-hand-holding-heart"></i> Donate</a>
    <a class="sidebar-link" href="controller?action=training"><i class="fa-solid fa-chalkboard-user"></i> Training</a>
    <a class="sidebar-link" href="controller?action=ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="controller?action=workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="controller?action=reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <i class="fa-solid fa-gauge"></i> <span class="current">Dashboard</span>
    </div>

    <h2>Welcome back, <%= session.getAttribute("userName") %> 👋</h2>
    <p style="color:var(--text-muted); margin-bottom:24px;">
        Role: <span class="badge badge-neutral"><%= userType %></span>
    </p>

    <div class="card">
        <h3>Quick Actions</h3>
        <div style="display:flex; flex-wrap:wrap; gap:14px; margin-top:16px;">
            <a class="btn btn-primary" href="controller?action=booking">
                <i class="fa-solid fa-calendar-check"></i> Book Equipment
            </a>
            <a class="btn btn-outline" href="controller?action=checkinout">
                <i class="fa-solid fa-clock"></i> Check In / Check Out
            </a>
            <% if (isShopTech) { %>
            <a class="btn btn-outline" href="controller?action=equipment">
                <i class="fa-solid fa-screwdriver-wrench"></i> Manage Equipment
            </a>
            <a class="btn btn-outline" href="controller?action=maintenance">
                <i class="fa-solid fa-toolbox"></i> Maintenance Alerts
            </a>
            <% } %>
            <a class="btn btn-outline" href="controller?action=donations">
                <i class="fa-solid fa-hand-holding-heart"></i> Donate Materials
            </a>
            <a class="btn btn-outline" href="controller?action=training">
                <i class="fa-solid fa-chalkboard-user"></i>
                <%= "TRAINER".equals(userType) ? "Record Training" : "Training Log" %>
            </a>
            <a class="btn btn-outline" href="controller?action=workorders">
                <i class="fa-solid fa-clipboard-list"></i> Work Orders
            </a>
            <a class="btn btn-outline" href="controller?action=ledger">
                <i class="fa-solid fa-money-bill-transfer"></i> My Ledger
            </a>
            <a class="btn btn-outline" href="controller?action=reports">
                <i class="fa-solid fa-chart-column"></i> Reports
            </a>
        </div>
    </div>

    <div class="app-footer">&copy; 2026 Campus Maker Space Co-op — CST8288 Final Project</div>
</main>

<button id="backToTop" class="back-to-top" title="Back to top"><i class="fa-solid fa-arrow-up"></i></button>
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>