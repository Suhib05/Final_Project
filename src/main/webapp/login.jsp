<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Login</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card">
        <h1><i class="fa-solid fa-screwdriver-wrench"></i> CMSC</h1>
        <p class="auth-subtitle">Campus Maker Space Co-op</p>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-error">
                <i class="fa-solid fa-circle-exclamation"></i> <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <div class="alert alert-success">
                <i class="fa-solid fa-circle-check"></i> <%= request.getAttribute("successMessage") %>
            </div>
        <% } %>

        <form action="controller" method="post">
            <input type="hidden" name="action" value="login">

            <div class="form-group">
                <i class="fa-solid fa-envelope field-icon"></i>
                <input type="email" id="email" name="email" class="form-control" placeholder=" " required>
                <label for="email">Email</label>
            </div>

            <div class="form-group">
                <i class="fa-solid fa-lock field-icon"></i>
                <input type="password" id="password" name="password" class="form-control" placeholder=" " required>
                <label for="password">Password</label>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-right-to-bracket"></i> Log In
            </button>
        </form>

        <p class="auth-footer-link">
            Don't have an account? <a href="register.jsp">Register here</a>
        </p>
    </div>
</div>

<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>