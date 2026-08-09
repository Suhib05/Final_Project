<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Predictive Maintenance</title>
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
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/maintenance"><i class="fa-solid fa-toolbox"></i> Maintenance</a>
    <% } %>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/donations"><i class="fa-solid fa-hand-holding-heart"></i> Donate</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/training"><i class="fa-solid fa-chalkboard-user"></i> Training</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Maintenance</span>
    </div>

    <h2><i class="fa-solid fa-toolbox"></i> Predictive Maintenance</h2>

    <div style="display:flex; gap:12px; margin-bottom:20px;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment Status</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/inventory"><i class="fa-solid fa-boxes-stacked"></i> Inventory Report</a>
    </div>

    <c:if test="${param.result eq 'schedule'}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> Maintenance task scheduled successfully.</div>
    </c:if>
    <c:if test="${param.result eq 'complete'}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> Maintenance completed and logged successfully.</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>

    <div class="card">
        <h3><i class="fa-solid fa-stethoscope"></i> Equipment Diagnostics</h3>
        <p class="form-hint">Diagnostic status is calculated from current machine state, accumulated usage and the configured maintenance threshold.</p>

        <div class="table-wrapper" data-searchable-table>
            <table class="data-table">
                <thead>
                    <tr>
                        <th data-sort>Asset Tag <i class="fa-solid fa-sort"></i></th>
                        <th>Equipment</th>
                        <th>Usage Hours</th>
                        <th>Threshold</th>
                        <th>Diagnostic</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="equipment" items="${allEquipment}">
                        <tr>
                            <td><c:out value="${equipment.assetTag}"/></td>
                            <td><c:out value="${equipment.make}"/> <c:out value="${equipment.model}"/></td>
                            <td><fmt:formatNumber value="${equipment.hoursOfUse}" maxFractionDigits="2"/></td>
                            <td><fmt:formatNumber value="${equipment.maintenanceThreshold}" maxFractionDigits="2"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${equipment.diagnosticStatus eq 'SERVICE_REQUIRED'}">
                                        <span class="badge badge-danger">Service required</span>
                                    </c:when>
                                    <c:when test="${equipment.diagnosticStatus eq 'WARNING'}">
                                        <span class="badge badge-warning">Warning</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-success">Normal</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="card">
        <h3><i class="fa-solid fa-triangle-exclamation"></i> Predictive Maintenance Alerts</h3>
        <p class="form-hint">An alert is generated when equipment usage reaches its configured maintenance threshold.</p>

        <c:choose>
            <c:when test="${empty maintenanceAlerts}">
                <div class="empty-state">
                    <i class="fa-solid fa-circle-check"></i>
                    <p>No predictive-maintenance alerts are active.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-warning"><i class="fa-solid fa-triangle-exclamation"></i> One or more machines require servicing.</div>

                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Asset Tag <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Equipment <i class="fa-solid fa-sort"></i></th>
                                <th>Category</th>
                                <th data-sort>Usage Hours <i class="fa-solid fa-sort"></i></th>
                                <th>Threshold</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="equipment" items="${maintenanceAlerts}">
                                <tr>
                                    <td><c:out value="${equipment.assetTag}"/></td>
                                    <td><c:out value="${equipment.make}"/> <c:out value="${equipment.model}"/></td>
                                    <td><c:out value="${equipment.category}"/></td>
                                    <td><fmt:formatNumber value="${equipment.hoursOfUse}" maxFractionDigits="2"/></td>
                                    <td><fmt:formatNumber value="${equipment.maintenanceThreshold}" maxFractionDigits="2"/></td>
                                    <td><span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> <c:out value="${equipment.status}"/></span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${equipment.status eq 'DOWN_FOR_MAINTENANCE'}">
                                                <span class="badge badge-warning">Already scheduled</span>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="post" action="${pageContext.request.contextPath}/maintenance" style="margin:0;">
                                                    <input type="hidden" name="action" value="schedule">
                                                    <input type="hidden" name="equipmentId" value="${equipment.equipmentId}">
                                                    <button type="submit" class="btn btn-warning btn-sm"><i class="fa-solid fa-calendar-plus"></i> Schedule</button>
                                                </form>
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

    <div class="card">
        <h3><i class="fa-solid fa-calendar-check"></i> Scheduled Maintenance</h3>
        <p class="form-hint">Shop-Techs can record completed maintenance activities from this section.</p>

        <c:choose>
            <c:when test="${empty scheduledEquipment}">
                <div class="empty-state">
                    <i class="fa-solid fa-clipboard-check"></i>
                    <p>No equipment is currently scheduled for maintenance.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Asset Tag</th>
                                <th>Equipment</th>
                                <th>Usage at Service</th>
                                <th>Complete Maintenance</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="equipment" items="${scheduledEquipment}">
                                <tr>
                                    <td><c:out value="${equipment.assetTag}"/></td>
                                    <td><c:out value="${equipment.make}"/> <c:out value="${equipment.model}"/></td>
                                    <td><fmt:formatNumber value="${equipment.hoursOfUse}" maxFractionDigits="2"/> hours</td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/maintenance" style="min-width:240px;">
                                            <input type="hidden" name="action" value="complete">
                                            <input type="hidden" name="equipmentId" value="${equipment.equipmentId}">

                                            <label style="font-size:12px; display:block; margin-bottom:4px;">Hours spent</label>
                                            <input type="number" name="hoursSpent" min="0.25" step="0.25" class="form-control" style="padding-left:12px; margin-bottom:6px;" required>

                                            <label style="font-size:12px; display:block; margin-bottom:4px;">Maintenance description</label>
                                            <textarea name="description" rows="3" class="form-control" style="padding-left:12px; margin-bottom:8px; resize:vertical;" required></textarea>

                                            <button type="submit" class="btn btn-success btn-sm"><i class="fa-solid fa-check"></i> Complete and Log</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <h3><i class="fa-solid fa-clock-rotate-left"></i> Maintenance History</h3>

        <c:choose>
            <c:when test="${empty maintenanceLogs}">
                <div class="empty-state">
                    <i class="fa-solid fa-inbox"></i>
                    <p>No maintenance activities have been logged.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Log ID <i class="fa-solid fa-sort"></i></th>
                                <th>Equipment ID</th>
                                <th>Shop-Tech ID</th>
                                <th data-sort>Date Logged <i class="fa-solid fa-sort"></i></th>
                                <th>Description</th>
                                <th>Hours at Service</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="maintenanceLog" items="${maintenanceLogs}">
                                <tr>
                                    <td><c:out value="${maintenanceLog.logId}"/></td>
                                    <td><c:out value="${maintenanceLog.equipmentId}"/></td>
                                    <td><c:out value="${maintenanceLog.shopTechId}"/></td>
                                    <td><c:out value="${maintenanceLog.dateLogged}"/></td>
                                    <td><c:out value="${maintenanceLog.description}"/></td>
                                    <td><fmt:formatNumber value="${maintenanceLog.hoursAtService}" maxFractionDigits="2"/></td>
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
