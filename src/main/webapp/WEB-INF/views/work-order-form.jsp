<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Work Orders</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%
    boolean loggedIn = session.getAttribute("userId") != null;
    String userType = loggedIn ? (String) session.getAttribute("userType") : null;
    boolean isShopTech = "SHOP_TECH".equals(userType);
%>

<% if (loggedIn) { %>
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
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>
<main class="app-main">
<% } else { %>
<div class="app-topbar" style="justify-content:center;">
    <div class="brand"><i class="fa-solid fa-screwdriver-wrench"></i> CMSC — Campus Maker Space Co-op</div>
</div>
<main class="app-main full-width" style="max-width:900px; margin:0 auto;">
<% } %>

    <div class="breadcrumb-bar">
        <% if (loggedIn) { %>
            <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
            <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <% } %>
        <span class="current">Work Orders</span>
    </div>

    <h2><i class="fa-solid fa-clipboard-list"></i> Fabrication Work Orders</h2>

    <% if (loggedIn) { %>
    <div style="display:flex; gap:12px; margin-bottom:20px;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
    </div>
    <% } %>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> <c:out value="${successMessage}"/></div>
    </c:if>

    <div class="card">
        <h3><i class="fa-solid fa-list-check"></i> Open Queue</h3>
        <p class="form-hint">
            Jobs are handled first come, first served. Requests from external clients and from members
            appear in the same queue and are treated identically once submitted.
        </p>

        <c:choose>
            <c:when test="${empty openQueue}">
                <div class="empty-state">
                    <i class="fa-solid fa-clipboard"></i>
                    <p>The queue is empty.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort># <i class="fa-solid fa-sort"></i></th>
                                <th>Requester</th>
                                <th>Origin</th>
                                <th>Description</th>
                                <th data-sort>Submitted <i class="fa-solid fa-sort"></i></th>
                                <th>Status</th>
                                <c:if test="${sessionUserType eq 'SHOP_TECH'}">
                                    <th>Action</th>
                                </c:if>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="job" items="${openQueue}">
                                <tr>
                                    <td><c:out value="${job.workOrderId}"/></td>
                                    <td><c:out value="${job.requesterName}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${job.external}">
                                                <span class="badge badge-warning">External client</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-neutral">Member</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${job.description}"/></td>
                                    <td><c:out value="${job.dateSubmitted}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${job.status eq 'SUBMITTED'}">
                                                <span class="badge badge-neutral"><span class="badge-dot"></span> Waiting</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning"><span class="badge-dot"></span> In progress</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <c:if test="${sessionUserType eq 'SHOP_TECH'}">
                                        <td>
                                            <form method="post" action="${pageContext.request.contextPath}/workorders" style="margin:0;">
                                                <input type="hidden" name="workOrderId" value="${job.workOrderId}">
                                                <c:choose>
                                                    <c:when test="${job.status eq 'SUBMITTED'}">
                                                        <input type="hidden" name="action" value="claim">
                                                        <button class="btn btn-primary btn-sm" type="submit"><i class="fa-solid fa-hand"></i> Claim</button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="hidden" name="action" value="complete">
                                                        <button class="btn btn-success btn-sm" type="submit"><i class="fa-solid fa-check"></i> Mark complete</button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty sessionUserType}">
        <div class="card">
            <h3><i class="fa-solid fa-plus"></i> Raise a Work Order</h3>
            <p class="form-hint">For members who would rather not spend the time doing the work themselves.</p>

            <form method="post" action="${pageContext.request.contextPath}/workorders" style="max-width:500px;">
                <input type="hidden" name="action" value="submitInternal">
                <div class="form-group">
                    <i class="fa-solid fa-pen field-icon"></i>
                    <textarea id="description" name="description" class="form-control" style="padding-top:13px; min-height:90px; resize:vertical;" placeholder=" " required></textarea>
                    <label for="description">What needs making</label>
                </div>
                <button type="submit" class="btn btn-primary"><i class="fa-solid fa-paper-plane"></i> Submit to queue</button>
            </form>
        </div>
    </c:if>

    <div class="card">
        <h3><i class="fa-solid fa-globe"></i> External Client Request</h3>
        <p class="form-hint">
            Not a member of the co-op? Submit a job here. Your request is converted into a standard
            work order on arrival and joins the same queue.
        </p>

        <form method="post" action="${pageContext.request.contextPath}/workorders" style="max-width:500px;">
            <input type="hidden" name="action" value="submitExternal">

            <div class="form-group">
                <i class="fa-solid fa-building field-icon"></i>
                <input type="text" id="companyName" name="companyName" class="form-control" placeholder=" " required>
                <label for="companyName">Company name</label>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-envelope field-icon"></i>
                <input type="email" id="contactEmail" name="contactEmail" class="form-control" placeholder=" " required>
                <label for="contactEmail">Contact email</label>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-pen field-icon"></i>
                <textarea id="jobDetails" name="jobDetails" class="form-control" style="padding-top:13px; min-height:90px; resize:vertical;" placeholder=" " required></textarea>
                <label for="jobDetails">Describe the work</label>
            </div>

            <button type="submit" class="btn btn-primary"><i class="fa-solid fa-paper-plane"></i> Submit request</button>
        </form>
    </div>

    <c:if test="${not empty completedOrders}">
        <div class="card">
            <h3><i class="fa-solid fa-circle-check"></i> Completed</h3>
            <div class="table-wrapper" data-searchable-table>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th data-sort># <i class="fa-solid fa-sort"></i></th>
                            <th>Requester</th>
                            <th>Origin</th>
                            <th data-sort>Completed <i class="fa-solid fa-sort"></i></th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="job" items="${completedOrders}">
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
                                <td><span class="badge badge-success"><span class="badge-dot"></span> Complete</span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <div class="app-footer">&copy; 2026 Campus Maker Space Co-op — CST8288 Final Project</div>
</main>

<button id="backToTop" class="back-to-top" title="Back to top"><i class="fa-solid fa-arrow-up"></i></button>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>