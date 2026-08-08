<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Register</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card" style="max-width:460px;">
        <h1><i class="fa-solid fa-user-plus"></i> Create Account</h1>
        <p class="auth-subtitle">Join the Campus Maker Space Co-op</p>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-error">
                <i class="fa-solid fa-circle-exclamation"></i> <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form action="controller" method="post">
            <input type="hidden" name="action" value="register">

            <div class="form-group">
                <i class="fa-solid fa-user field-icon"></i>
                <input type="text" id="name" name="name" class="form-control" placeholder=" " required>
                <label for="name">Full Name</label>
            </div>

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

            <div class="form-group">
                <i class="fa-solid fa-id-badge field-icon"></i>
                <select id="userType" name="userType" class="form-control">
                    <option value="USER">User</option>
                    <option value="TRAINER">Trainer</option>
                    <option value="SHOP_TECH">Shop-Tech</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-user-plus"></i> Register
            </button>
        </form>

        <p class="auth-footer-link">
            Already have an account? <a href="login.jsp">Log in here</a>
        </p>
    </div>
</div>

<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>