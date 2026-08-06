<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Check In / Check Out</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="page-container">
        <h1>Check In / Check Out</h1>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message"><%= request.getAttribute("errorMessage") %></div>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <div class="success-message"><%= request.getAttribute("successMessage") %></div>
        <% } %>

        <form action="controller" method="post">
            <input type="hidden" name="action" value="checkinout">

            <label for="equipmentId">Equipment ID</label>
            <input type="number" id="equipmentId" name="equipmentId" required>

            <label for="mode">Action</label>
            <select id="mode" name="mode">
                <option value="checkin">Check In</option>
                <option value="checkout">Check Out</option>
            </select>

            <label for="materialsConsumed">Materials Consumed (only needed for check-out)</label>
            <input type="number" step="0.01" id="materialsConsumed" name="materialsConsumed" value="0">

            <button type="submit">Submit</button>
        </form>

        <p style="margin-top:20px;"><a href="dashboard.jsp">Back to Dashboard</a></p>
    </div>
</body>
</html>