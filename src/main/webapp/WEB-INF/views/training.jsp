<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Training Sessions</title>
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
    boolean isTrainer = "TRAINER".equals(userType);
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
    <a class="sidebar-link" href="${pageContext.request.contextPath}/donations"><i class="fa-solid fa-hand-holding-heart"></i> Donate</a>
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/training"><i class="fa-solid fa-chalkboard-user"></i> Training</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Training</span>
    </div>

    <h2><i class="fa-solid fa-chalkboard-user"></i> Intro and Safety Training</h2>

    <div style="display:flex; gap:12px; margin-bottom:20px; flex-wrap:wrap;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> <c:out value="${successMessage}"/></div>
    </c:if>

    <% if (isTrainer) { %>
    <div class="stat-grid">
        <div class="stat-card">
            <div class="stat-icon icon-bg-accent"><i class="fa-solid fa-chalkboard-user"></i></div>
            <div>
                <div class="stat-value"><c:out value="${empty mySessionCount ? 0 : mySessionCount}"/></div>
                <div class="stat-label">Sessions I've Delivered</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon icon-bg-success"><i class="fa-solid fa-coins"></i></div>
            <div>
                <div class="stat-value">
                    <fmt:formatNumber value="${creditPerSession}" maxFractionDigits="2"/>
                </div>
                <div class="stat-label">Credits Per Session</div>
            </div>
        </div>
    </div>

    <div class="card">
        <h3><i class="fa-solid fa-plus"></i> Record a Delivered Session</h3>
        <p class="form-hint">
            Log a session once you have delivered it. Each session earns a flat
            credit, because the co-op values the session being run rather than
            how long it took.
        </p>

        <form method="post" action="${pageContext.request.contextPath}/training" style="max-width:420px;">
            <div class="form-group">
                <i class="fa-solid fa-screwdriver-wrench field-icon"></i>
                <select id="equipmentId" name="equipmentId" class="form-control" required>
                    <option value="">Equipment covered</option>
                    <c:forEach var="machine" items="${equipmentList}">
                        <option value="${machine.equipmentId}">
                            <c:out value="${machine.assetTag}"/> &mdash;
                            <c:out value="${machine.make}"/>
                            <c:out value="${machine.model}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-check"></i> Record Session
            </button>
        </form>
    </div>
    <% } else { %>
    <div class="alert alert-warning">
        <i class="fa-solid fa-circle-info"></i>
        Only Trainers can record delivered sessions. You can still see every
        session the co-op has run below.
    </div>
    <% } %>

    <div class="card">
        <h3><i class="fa-solid fa-list-check"></i> Training Log</h3>
        <p class="form-hint">
            Every intro and safety session delivered in the space, newest first.
            Each one credits the Trainer who ran it.
        </p>

        <c:choose>
            <c:when test="${empty trainings}">
                <div class="empty-state">
                    <i class="fa-solid fa-chalkboard-user"></i>
                    <p>No training sessions have been recorded yet.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort># <i class="fa-solid fa-sort"></i></th>
                                <th>Trainer</th>
                                <th>Equipment</th>
                                <th data-sort>Delivered <i class="fa-solid fa-sort"></i></th>
                                <th>Credit Earned</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="training" items="${trainings}">
                                <tr>
                                    <td><c:out value="${training.trainingId}"/></td>
                                    <td>
                                        <c:out value="${trainerNames[training.trainerId]}"/>
                                    </td>
                                    <td>
                                        <c:out value="${equipmentNames[training.equipmentId]}"/>
                                    </td>
                                    <td><c:out value="${training.dateDelivered}"/></td>
                                    <td>
                                        <span class="badge badge-success">
                                            <fmt:formatNumber value="${creditPerSession}" maxFractionDigits="2"/>
                                        </span>
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
