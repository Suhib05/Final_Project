<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Work Orders</title>

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

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th, td {
                padding: 12px;
                border: 1px solid #d9e2ec;
                text-align: left;
                vertical-align: top;
            }

            th {
                color: white;
                background-color: #274c77;
            }

            tr:nth-child(even) {
                background-color: #f7f9fb;
            }

            .badge {
                display: inline-block;
                padding: 3px 9px;
                border-radius: 10px;
                font-size: 0.82em;
                font-weight: bold;
            }

            .badge-internal {
                color: #1e3a5f;
                background-color: #dbeafe;
            }

            .badge-external {
                color: #7a4100;
                background-color: #fde8c9;
            }

            .status-submitted {
                color: #7a4100;
                font-weight: bold;
            }

            .status-progress {
                color: #1769aa;
                font-weight: bold;
            }

            .status-complete {
                color: #137333;
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

            label {
                display: block;
                margin-bottom: 6px;
                color: #52616b;
            }

            input[type="text"],
            input[type="email"],
            textarea {
                width: 100%;
                max-width: 520px;
                margin-bottom: 14px;
                padding: 9px;
                border: 1px solid #d9e2ec;
                border-radius: 4px;
                font-family: inherit;
            }

            textarea {
                min-height: 90px;
            }

            button {
                padding: 10px 18px;
                color: white;
                background-color: #274c77;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            button.inline {
                padding: 6px 12px;
                font-size: 0.85em;
            }
        </style>
    </head>

    <body>
        <main class="container">
            <h1>Fabrication Work Orders</h1>

            <nav class="navigation">
                <a href="${pageContext.request.contextPath}/ledger">
                    My Ledger
                </a>
                <a href="${pageContext.request.contextPath}/reports">
                    Reports
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

            <section class="panel">
                <h2>Open Queue</h2>

                <p class="report-note">
                    Jobs are handled first come, first served, so the queue is
                    ordered by submission time. Requests from external clients
                    and from members appear in the same queue and are treated
                    identically once they are in the system.
                </p>

                <c:choose>
                    <c:when test="${empty openQueue}">
                        <p>The queue is empty.</p>
                    </c:when>

                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Requester</th>
                                    <th>Origin</th>
                                    <th>Description</th>
                                    <th>Submitted</th>
                                    <th>Status</th>
                                    <c:if test="${sessionUserType
                                                  eq 'SHOP_TECH'}">
                                        <th>Action</th>
                                    </c:if>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="job" items="${openQueue}">
                                    <tr>
                                        <td>
                                            <c:out value="${job.workOrderId}"/>
                                        </td>

                                        <td>
                                            <c:out
                                                value="${job.requesterName}"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${job.external}">
                                                    <span class="badge
                                                                 badge-external">
                                                        External client
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge
                                                                 badge-internal">
                                                        Member
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:out value="${job.description}"/>
                                        </td>

                                        <td>
                                            <c:out
                                                value="${job.dateSubmitted}"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${job.status
                                                                eq 'SUBMITTED'}">
                                                    <span
                                                        class="status-submitted">
                                                        Waiting
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span
                                                        class="status-progress">
                                                        In progress
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <c:if test="${sessionUserType
                                                      eq 'SHOP_TECH'}">
                                            <td>
                                                <form method="post"
                                                      action="${pageContext.request.contextPath}/workorders">
                                                    <input type="hidden"
                                                           name="workOrderId"
                                                           value="${job.workOrderId}">

                                                    <c:choose>
                                                        <c:when
                                                            test="${job.status
                                                                    eq 'SUBMITTED'}">
                                                            <input type="hidden"
                                                                   name="action"
                                                                   value="claim">
                                                            <button
                                                                class="inline"
                                                                type="submit">
                                                                Claim
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="hidden"
                                                                   name="action"
                                                                   value="complete">
                                                            <button
                                                                class="inline"
                                                                type="submit">
                                                                Mark complete
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </form>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </section>

            <c:if test="${not empty sessionUserType}">
                <section class="panel">
                    <h2>Raise a Work Order</h2>

                    <p class="report-note">
                        For members who would rather not spend the time doing
                        the work themselves.
                    </p>

                    <form method="post"
                          action="${pageContext.request.contextPath}/workorders">
                        <input type="hidden" name="action"
                               value="submitInternal">

                        <label for="description">What needs making</label>
                        <textarea id="description" name="description"
                                  required></textarea>

                        <button type="submit">Submit to queue</button>
                    </form>
                </section>
            </c:if>

            <section class="panel">
                <h2>External Client Request</h2>

                <p class="report-note">
                    Not a member of the co-op? Submit a job here. Your request
                    is converted into a standard work order on arrival and
                    joins the same queue.
                </p>

                <form method="post"
                      action="${pageContext.request.contextPath}/workorders">
                    <input type="hidden" name="action" value="submitExternal">

                    <label for="companyName">Company name</label>
                    <input type="text" id="companyName" name="companyName"
                           required>

                    <label for="contactEmail">Contact email</label>
                    <input type="email" id="contactEmail" name="contactEmail"
                           required>

                    <label for="jobDetails">Describe the work</label>
                    <textarea id="jobDetails" name="jobDetails"
                              required></textarea>

                    <button type="submit">Submit request</button>
                </form>
            </section>

            <c:if test="${not empty completedOrders}">
                <section class="panel">
                    <h2>Completed</h2>

                    <table>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Requester</th>
                                <th>Origin</th>
                                <th>Completed</th>
                                <th>Status</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="job" items="${completedOrders}">
                                <tr>
                                    <td><c:out value="${job.workOrderId}"/></td>
                                    <td>
                                        <c:out value="${job.requesterName}"/>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${job.external}">
                                                External client
                                            </c:when>
                                            <c:otherwise>
                                                Member
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:out value="${job.dateCompleted}"/>
                                    </td>

                                    <td>
                                        <span class="status-complete">
                                            Complete
                                        </span>
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
