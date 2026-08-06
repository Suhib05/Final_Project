<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Booking Confirmed</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="page-container">
        <h1>Booking Confirmed</h1>

        <% if (request.getAttribute("successMessage") != null) { %>
            <div class="success-message"><%= request.getAttribute("successMessage") %></div>
        <% } %>

        <p style="margin-top:20px;"><a href="dashboard.jsp">Back to Dashboard</a></p>
    </div>
</body>
</html>