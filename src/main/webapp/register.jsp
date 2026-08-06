<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Register</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="page-container">
        <h1>Create an Account</h1>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message"><%= request.getAttribute("errorMessage") %></div>
        <% } %>

        <form action="controller" method="post">
            <input type="hidden" name="action" value="register">

            <label for="name">Full Name</label>
            <input type="text" id="name" name="name" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <label for="userType">Account Type</label>
            <select id="userType" name="userType">
                <option value="USER">User</option>
                <option value="TRAINER">Trainer</option>
                <option value="SHOP_TECH">Shop-Tech</option>
            </select>

            <button type="submit">Register</button>
        </form>
    </div>
</body>
</html>