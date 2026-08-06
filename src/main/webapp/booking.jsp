<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Book Equipment</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="page-container wide">
        <h1>Book Equipment</h1>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message"><%= request.getAttribute("errorMessage") %></div>
        <% } %>

        <table>
            <tr>
                <th>Asset Tag</th>
                <th>Category</th>
                <th>Make / Model</th>
                <th>Status</th>
                <th></th>
            </tr>
            <c:forEach var="equipment" items="${equipmentList}">
                <tr>
                    <td>${equipment.assetTag}</td>
                    <td>${equipment.category}</td>
                    <td>${equipment.make} ${equipment.model}</td>
                    <td>${equipment.status}</td>
                    <td>
                        <c:if test="${equipment.status == 'AVAILABLE'}">
                            <form action="controller" method="post" style="margin:0;">
                                <input type="hidden" name="action" value="booking">
                                <input type="hidden" name="equipmentId" value="${equipment.equipmentId}">
                                <button type="submit">Book</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <p style="margin-top:20px;"><a href="dashboard.jsp">Back to Dashboard</a></p>
    </div>
</body>
</html>