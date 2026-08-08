<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Ledger</title>
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
    <a class="sidebar-link active" href="${pageContext.request.contextPath}/ledger"><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
    <a class="sidebar-link" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
</nav>

<main class="app-main">
    <div class="breadcrumb-bar">
        <a href="dashboard.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <i class="fa-solid fa-chevron-right" style="font-size:10px;"></i>
        <span class="current">My Ledger</span>
    </div>

    <h2><i class="fa-solid fa-money-bill-transfer"></i> My Ledger</h2>

    <div style="display:flex; gap:12px; margin-bottom:20px; flex-wrap:wrap;">
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/reports"><i class="fa-solid fa-chart-column"></i> Reports</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/workorders"><i class="fa-solid fa-clipboard-list"></i> Work Orders</a>
        <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/equipment"><i class="fa-solid fa-screwdriver-wrench"></i> Equipment Status</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error"><i class="fa-solid fa-circle-exclamation"></i> <c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success"><i class="fa-solid fa-circle-check"></i> <c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty infoMessage}">
        <div class="alert alert-warning"><i class="fa-solid fa-circle-info"></i> <c:out value="${infoMessage}"/></div>
    </c:if>

    <c:if test="${not empty ledgerReport}">
        <div class="stat-grid">
            <div class="stat-card">
                <div class="stat-icon icon-bg-success"><i class="fa-solid fa-arrow-up"></i></div>
                <div><div class="stat-value"><fmt:formatNumber value="${creditBalance}" maxFractionDigits="2"/></div><div class="stat-label">Credit Balance</div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-bg-danger"><i class="fa-solid fa-arrow-down"></i></div>
                <div><div class="stat-value"><fmt:formatNumber value="${debitBalance}" maxFractionDigits="2"/></div><div class="stat-label">Outstanding Debit</div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-bg-accent"><i class="fa-solid fa-calendar-days"></i></div>
                <div><div class="stat-value"><fmt:formatNumber value="${ledgerReport.creditTotal}" maxFractionDigits="2"/></div><div class="stat-label">Credited This Month</div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-bg-warning"><i class="fa-solid fa-calendar-days"></i></div>
                <div><div class="stat-value"><fmt:formatNumber value="${ledgerReport.debitTotal}" maxFractionDigits="2"/></div><div class="stat-label">Debited This Month</div></div>
            </div>
        </div>

        <c:if test="${ledgerReport.settlementDue}">
            <div class="card">
                <h3><i class="fa-solid fa-hand-holding-dollar"></i> Settle Your Balance</h3>
                <p class="form-hint">
                    Any debit left at the end of the month must be settled within ten days. Your accumulated
                    credits are applied first
                    <c:if test="${creditsApplicable > 0}">
                        &mdash; up to <fmt:formatNumber value="${creditsApplicable}" maxFractionDigits="2"/> of your balance can be cleared by credits
                    </c:if>
                    and anything remaining is cleared by payment.
                </p>

                <form method="post" action="${pageContext.request.contextPath}/ledger" style="max-width:320px;">
                    <div class="form-group">
                        <i class="fa-solid fa-coins field-icon"></i>
                        <input type="number" id="payment" name="payment" min="0" step="0.01" class="form-control" placeholder=" ">
                        <label for="payment">Payment amount (blank = credits only)</label>
                    </div>
                    <button type="submit" class="btn btn-primary"><i class="fa-solid fa-check"></i> Settle</button>
                </form>
            </div>
        </c:if>
    </c:if>

    <div class="card">
        <h3><i class="fa-solid fa-calendar-days"></i> This Month</h3>
        <p class="form-hint">Every credit earned, debit incurred and settlement made appears here, newest last.</p>

        <c:choose>
            <c:when test="${empty ledgerEntries}">
                <div class="empty-state">
                    <i class="fa-solid fa-receipt"></i>
                    <p>No ledger activity this month.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper" data-searchable-table>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th data-sort>Date <i class="fa-solid fa-sort"></i></th>
                                <th data-sort>Type <i class="fa-solid fa-sort"></i></th>
                                <th>Source</th>
                                <th data-sort>Amount <i class="fa-solid fa-sort"></i></th>
                                <th>Reference</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="entry" items="${ledgerEntries}">
                                <tr>
                                    <td><c:out value="${entry.transactionDate}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${entry.credit}">
                                                <span class="badge badge-success"><span class="badge-dot"></span> Credit</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger"><span class="badge-dot"></span> Debit</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${entry.source}"/></td>
                                    <td><fmt:formatNumber value="${entry.amount}" maxFractionDigits="2"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${empty entry.referenceId}">&mdash;</c:when>
                                            <c:otherwise><c:out value="${entry.referenceId}"/></c:otherwise>
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

    <c:if test="${not empty fullHistory}">
        <div class="card">
            <h3><i class="fa-solid fa-clock-rotate-left"></i> Full History</h3>
            <div class="table-wrapper" data-searchable-table>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th data-sort>Date <i class="fa-solid fa-sort"></i></th>
                            <th data-sort>Type <i class="fa-solid fa-sort"></i></th>
                            <th>Source</th>
                            <th data-sort>Amount <i class="fa-solid fa-sort"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="entry" items="${fullHistory}">
                            <tr>
                                <td><c:out value="${entry.transactionDate}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${entry.credit}">
                                            <span class="badge badge-success"><span class="badge-dot"></span> Credit</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger"><span class="badge-dot"></span> Debit</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${entry.source}"/></td>
                                <td><fmt:formatNumber value="${entry.amount}" maxFractionDigits="2"/></td>
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