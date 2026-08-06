<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>My Ledger</title>

        <style>
            body {
                margin: 0;
                padding: 30px;
                background-color: #f4f6f8;
                color: #1f2933;
                font-family: Arial, sans-serif;
            }

            .container {
                max-width: 1100px;
                margin: auto;
            }

            h1 {
                color: #16324f;
            }

            h2 {
                margin-top: 0;
                color: #16324f;
                font-size: 1.1em;
            }

            .panel {
                margin-bottom: 24px;
                padding: 24px;
                background-color: white;
                border: 1px solid #d9e2ec;
                border-radius: 8px;
                overflow-x: auto;
            }

            .report-note {
                margin-bottom: 20px;
                color: #52616b;
            }

            .error-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #721c24;
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
            }

            .success-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #14532d;
                background-color: #d7f5e0;
                border: 1px solid #a7e0bb;
            }

            .info-message {
                margin-bottom: 18px;
                padding: 12px;
                color: #1e3a5f;
                background-color: #dbeafe;
                border: 1px solid #bfdbfe;
            }

            .summary {
                display: flex;
                flex-wrap: wrap;
                gap: 16px;
                margin-bottom: 24px;
            }

            .summary-card {
                flex: 1 1 180px;
                padding: 18px;
                background-color: white;
                border: 1px solid #d9e2ec;
                border-radius: 8px;
            }

            .summary-card .label {
                color: #52616b;
                font-size: 0.85em;
                text-transform: uppercase;
            }

            .summary-card .value {
                margin-top: 6px;
                font-size: 1.6em;
                font-weight: bold;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th, td {
                padding: 12px;
                border: 1px solid #d9e2ec;
                text-align: left;
            }

            th {
                color: white;
                background-color: #274c77;
            }

            tr:nth-child(even) {
                background-color: #f7f9fb;
            }

            .credit {
                color: #137333;
                font-weight: bold;
            }

            .debit {
                color: #b3261e;
                font-weight: bold;
            }

            .navigation {
                margin-bottom: 20px;
            }

            .navigation a {
                display: inline-block;
                margin-right: 8px;
                padding: 9px 14px;
                color: white;
                background-color: #1769aa;
                border-radius: 4px;
                text-decoration: none;
            }

            form.settle label {
                display: block;
                margin-bottom: 6px;
                color: #52616b;
            }

            form.settle input[type="number"] {
                width: 200px;
                padding: 9px;
                border: 1px solid #d9e2ec;
                border-radius: 4px;
            }

            form.settle button {
                margin-left: 8px;
                padding: 10px 18px;
                color: white;
                background-color: #274c77;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }
        </style>
    </head>

    <body>
        <main class="container">
            <h1>My Ledger</h1>

            <nav class="navigation">
                <a href="${pageContext.request.contextPath}/reports">
                    Reports
                </a>
                <a href="${pageContext.request.contextPath}/workorders">
                    Work Orders
                </a>
                <a href="${pageContext.request.contextPath}/equipment">
                    Equipment Status
                </a>
            </nav>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="success-message">
                    <c:out value="${successMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty infoMessage}">
                <div class="info-message">
                    <c:out value="${infoMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty ledgerReport}">
                <section class="summary">
                    <div class="summary-card">
                        <div class="label">Credit Balance</div>
                        <div class="value credit">
                            <fmt:formatNumber value="${creditBalance}"
                                              maxFractionDigits="2"/>
                        </div>
                    </div>

                    <div class="summary-card">
                        <div class="label">Outstanding Debit</div>
                        <div class="value debit">
                            <fmt:formatNumber value="${debitBalance}"
                                              maxFractionDigits="2"/>
                        </div>
                    </div>

                    <div class="summary-card">
                        <div class="label">Credited This Month</div>
                        <div class="value">
                            <fmt:formatNumber
                                value="${ledgerReport.creditTotal}"
                                maxFractionDigits="2"/>
                        </div>
                    </div>

                    <div class="summary-card">
                        <div class="label">Debited This Month</div>
                        <div class="value">
                            <fmt:formatNumber
                                value="${ledgerReport.debitTotal}"
                                maxFractionDigits="2"/>
                        </div>
                    </div>
                </section>

                <c:if test="${ledgerReport.settlementDue}">
                    <section class="panel">
                        <h2>Settle Your Balance</h2>

                        <p class="report-note">
                            Any debit left at the end of the month must be
                            settled within ten days. Your accumulated credits
                            are applied first
                            <c:if test="${creditsApplicable > 0}">
                                &mdash; up to
                                <fmt:formatNumber value="${creditsApplicable}"
                                                  maxFractionDigits="2"/>
                                of your balance can be cleared by credits
                            </c:if>
                            and anything remaining is cleared by payment.
                        </p>

                        <form class="settle" method="post"
                              action="${pageContext.request.contextPath}/ledger">
                            <label for="payment">
                                Payment amount (leave blank to pay with
                                credits only)
                            </label>

                            <input type="number" id="payment" name="payment"
                                   min="0" step="0.01" placeholder="0.00">

                            <button type="submit">Settle</button>
                        </form>
                    </section>
                </c:if>
            </c:if>

            <section class="panel">
                <h2>This Month</h2>

                <p class="report-note">
                    Every credit earned, debit incurred and settlement made
                    appears here as one entry, newest last.
                </p>

                <c:choose>
                    <c:when test="${empty ledgerEntries}">
                        <p>No ledger activity this month.</p>
                    </c:when>

                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Type</th>
                                    <th>Source</th>
                                    <th>Amount</th>
                                    <th>Reference</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="entry" items="${ledgerEntries}">
                                    <tr>
                                        <td>
                                            <c:out
                                                value="${entry.transactionDate}"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${entry.credit}">
                                                    <span class="credit">
                                                        Credit
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="debit">
                                                        Debit
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td><c:out value="${entry.source}"/></td>

                                        <td>
                                            <fmt:formatNumber
                                                value="${entry.amount}"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when
                                                    test="${empty entry.referenceId}">
                                                    &mdash;
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out
                                                        value="${entry.referenceId}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </section>

            <c:if test="${not empty fullHistory}">
                <section class="panel">
                    <h2>Full History</h2>

                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Type</th>
                                <th>Source</th>
                                <th>Amount</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="entry" items="${fullHistory}">
                                <tr>
                                    <td>
                                        <c:out
                                            value="${entry.transactionDate}"/>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${entry.credit}">
                                                <span class="credit">
                                                    Credit
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="debit">
                                                    Debit
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td><c:out value="${entry.source}"/></td>

                                    <td>
                                        <fmt:formatNumber
                                            value="${entry.amount}"
                                            maxFractionDigits="2"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </section>
            </c:if>
        </main>
    </body>
</html>
