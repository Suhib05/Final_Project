<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Equipment Status</title>
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
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/inventory"><i class="fa-solid fa-boxes-stacked"></i> Inventory</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/maintenance"><i class="fa-solid fa-toolbox"></i> Maintenance</a>
    <% } %>
    <a class="sidebar-link" href="controller?action=donations"><i class="fa-solid fa-hand-holding-heart"></i> Donate</a>
    <a class="sidebar-link" href="controller?action=training"><i class="fa-solid fa-chalkboard-user"></i> Training</a>
    <a class="sidebar-link" href="controller?action=ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="controller?action=workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="controller?action=reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">Equipment</span>
    </div>

    <h2><i class="fa-solid fa-screwdriver-wrench"></i> Campus Maker Space Equipment</h2>

    <c:if test="${sessionScope.userType eq 'SHOP_TECH'}">
    <div style="display:flex; gap:12px; margin-bottom:20px;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/inventory">
            <i class="fa-solid fa-boxes-stacked"></i> Inventory Report
        </a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/maintenance">
            <i class="fa-solid fa-toolbox"></i> Maintenance
        </a>
    </div>
    </c:if>

    <c:if test="${param.registered eq 'true'}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> Equipment registered successfully.</div>
    </c:if>
    <c:if test="${param.booking eq 'success'}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> Equipment booked and checked in successfully.</div>
    </c:if>
    <c:if test="${param.session eq 'checkIn'}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> Equipment check-in completed successfully.</div>
    </c:if>
    <c:if test="${param.session eq 'checkOut'}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> Equipment check-out completed successfully.</div>
    </c:if>
    <c:if test="${not empty param.sessionError}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${param.sessionError}"/></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>

    <c:if test="${sessionScope.userType eq 'SHOP_TECH'}">
    <div class="card">
        <h3><i class="fa-solid fa-plus"></i> Register Equipment</h3>
        <p class="form-hint">This operation is restricted to Shop-Techs and administrators.</p>

        <form method="post" action="${pageContext.request.contextPath}/equipment">
            <div style="display:grid; grid-template-columns:repeat(auto-fit,minmax(220px,1fr)); gap:16px;">

                <div class="form-group">
                    <i class="fa-solid fa-tag field-icon"></i>
                    <input id="assetTag" name="assetTag" type="text" class="form-control" placeholder=" " required>
                    <label for="assetTag">Asset tag</label>
                </div>

                <div class="form-group">
                    <i class="fa-solid fa-list field-icon"></i>
                    <select id="category" name="category" class="form-control" required>
                        <option value="">Select a category</option>
                        <option value="PRINTER_3D">3D Printer</option>
                        <option value="LASER_CUTTER">Laser Cutter</option>
                        <option value="CNC">CNC Machine</option>
                    </select>
                </div>

                <div class="form-group">
                    <i class="fa-solid fa-industry field-icon"></i>
                    <input id="make" name="make" type="text" class="form-control" placeholder=" " required>
                    <label for="make">Make</label>
                </div>

                <div class="form-group">
                    <i class="fa-solid fa-cube field-icon"></i>
                    <input id="model" name="model" type="text" class="form-control" placeholder=" " required>
                    <label for="model">Model</label>
                </div>

                <div class="form-group">
                    <i class="fa-solid fa-box-open field-icon"></i>
                    <select id="consumableType" name="consumableType" class="form-control" required>
                        <option value="">Select a consumable type</option>
                        <c:forEach var="consumable" items="${consumableList}">
                            <option value="${consumable.type}"><c:out value="${consumable.type}"/></option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <i class="fa-solid fa-gauge-high field-icon"></i>
                    <input id="maintenanceThreshold" name="maintenanceThreshold" type="number" min="0.01" step="0.01" class="form-control" placeholder=" " required>
                    <label for="maintenanceThreshold">Maintenance threshold (hours)</label>
                </div>

                <div class="form-group">
                    <i class="fa-solid fa-coins field-icon"></i>
                    <input id="accessCreditRate" name="accessCreditRate" type="number" min="0" step="0.01" class="form-control" placeholder=" " required>
                    <label for="accessCreditRate">Access credit rate / hour</label>
                </div>
            </div>

            <button type="submit" class="btn btn-primary"><i class="fa-solid fa-floppy-disk"></i> Register Equipment</button>
        </form>
    </div>
    </c:if>

    <div class="card">
        <h3><i class="fa-solid fa-table"></i> Equipment and Active Session Report</h3>
        <p class="form-hint">Shows equipment availability, current user, elapsed access time and materials for the active session.</p>

        <c:choose>
            <c:when test="${empty equipmentList}">
                <div class="empty-state">
                    <i class="fa-solid fa-inbox"></i>
                    <p>No equipment is currently registered.</p>
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
                                <th data-sort>Equipment <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Category <i class="fa-solid fa-sort"></i></th>
                                <th>Consumable Type</th>
                                <th data-sort>Status <i class="fa-solid fa-sort"></i></th>
                                <th>Current User</th>
                                <th>Elapsed Time</th>
                                <th>Materials Used</th>
                                <th>Credit Rate</th>
                                <th>Diagnostic</th>
                                <th>Session Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="equipment" items="${equipmentList}">
                                <c:set var="activeSession" value="${activeSessionsByEquipment[equipment.equipmentId]}"/>
                                <tr>
                                    <td><c:out value="${equipment.assetTag}"/></td>
                                    <td><c:out value="${equipment.make}"/> <c:out value="${equipment.model}"/></td>
                                    <td><c:out value="${equipment.category}"/></td>
                                    <td><c:out value="${equipment.consumableType}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${equipment.status eq 'AVAILABLE'}">
                                                <span class="badge badge-success"><span class="badge-dot"></span> Available</span>
                                            </c:when>
                                            <c:when test="${equipment.status eq 'IN_USE'}">
                                                <span class="badge badge-warning"><span class="badge-dot"></span> In Use</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger"><span class="badge-dot"></span> Down for Maintenance</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty activeSession}">
                                                <c:out value="${activeUserNamesByEquipment[equipment.equipmentId]}"/>
                                                <small>(#<c:out value="${activeSession.userId}"/>)</small>
                                            </c:when>
                                            <c:otherwise>&mdash;</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty activeSession}">
                                                <fmt:formatNumber value="${elapsedHoursByEquipment[equipment.equipmentId]}" maxFractionDigits="3"/> hrs
                                            </c:when>
                                            <c:otherwise>&mdash;</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty activeSession}">
                                                <fmt:formatNumber value="${activeSession.materialsConsumed}" maxFractionDigits="2"/>
                                                <c:if test="${not empty activeSession.consumableId}">
                                                    <br><small><c:out value="${consumableNamesById[activeSession.consumableId]}"/></small>
                                                </c:if>
                                            </c:when>
                                            <c:otherwise>&mdash;</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatNumber value="${equipment.accessCreditRate}" maxFractionDigits="2"/> /hr</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${equipment.diagnosticStatus eq 'SERVICE_REQUIRED'}">
                                                <span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> Service required</span>
                                            </c:when>
                                            <c:when test="${equipment.diagnosticStatus eq 'WARNING'}">
                                                <span class="badge badge-warning"><i class="fa-solid fa-stethoscope"></i> Warning</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success"><i class="fa-solid fa-circle-check"></i> Normal</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${equipment.status eq 'AVAILABLE'}">
                                                <form method="post" action="${pageContext.request.contextPath}/sessions" style="min-width:200px;">
                                                    <input type="hidden" name="action" value="checkIn">
                                                    <input type="hidden" name="equipmentId" value="${equipment.equipmentId}">
                                                    <label style="font-size:12px; display:block; margin-bottom:4px;">Consumable</label>
                                                    <select name="consumableId" class="form-control" style="margin-bottom:8px;">
                                                        <option value="">No consumable</option>
                                                        <c:forEach var="consumable" items="${consumableList}">
                                                            <c:if test="${consumable.stockLevel > 0}">
                                                                <option value="${consumable.consumableId}">
                                                                    <c:out value="${consumable.type}"/> - <c:out value="${consumable.stockLevel}"/> <c:out value="${consumable.unit}"/>
                                                                </option>
                                                            </c:if>
                                                        </c:forEach>
                                                    </select>
                                                    <button type="submit" class="btn btn-success btn-sm"><i class="fa-solid fa-right-to-bracket"></i> Check In</button>
                                                </form>
                                            </c:when>
                                            <c:when test="${equipment.status eq 'IN_USE'
                                                    and not empty activeSession
                                                    and (sessionScope.userType eq 'SHOP_TECH'
                                                    or activeSession.userId eq sessionScope.userId)}">
                                                <form method="post" action="${pageContext.request.contextPath}/sessions" style="min-width:200px;">
                                                    <input type="hidden" name="action" value="checkOut">
                                                    <input type="hidden" name="equipmentId" value="${equipment.equipmentId}">
                                                    <label style="font-size:12px; display:block; margin-bottom:4px;">Materials consumed</label>
                                                    <input type="number" name="materialsConsumed" min="0" step="0.01" value="0" class="form-control" style="padding-left:12px; margin-bottom:8px;" required>
                                                    <button type="submit" class="btn btn-warning btn-sm"><i class="fa-solid fa-right-from-bracket"></i> Check Out</button>
                                                </form>
                                            </c:when>
                                            <c:when test="${equipment.status eq 'IN_USE' and not empty activeSession}">
                                                <small class="form-hint">In use by another member</small>
                                            </c:when>
                                            <c:when test="${equipment.status eq 'IN_USE'}">
                                                <small class="form-hint">Active-session details are unavailable.</small>
                                            </c:when>
                                            <c:otherwise>
                                                <small class="form-hint">Equipment unavailable</small>
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
