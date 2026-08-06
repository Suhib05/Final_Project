<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CMSC - Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<% if (session.getAttribute("userId") == null) {
       response.sendRedirect("login.jsp");
       return;
   }
%>

<div class="nav-bar">
    <span>CMSC — Campus Maker Space Co-op</span>
    <span>
        Welcome, <%= session.getAttribute("userName") %> (<%= session.getAttribute("userType") %>)
        &nbsp;|&nbsp;
        <a href="controller?action=logout">Log Out</a>
    </span>
</div>

<div class="page-container wide">
    <h1>Dashboard</h1>
    <p>Choose what you'd like to do:</p>

    <ul>
        <li><a href="controller?action=booking">Book Equipment</a></li>
        <li><a href="checkinout.jsp">Check In / Check Out</a></li>

        <% String userType = (String) session.getAttribute("userType"); %>
        <% if ("SHOP_TECH".equals(userType)) { %>
            <li><a href="controller?action=equipment">Manage Equipment</a></li>
            <li><a href="controller?action=maintenance">Maintenance Alerts</a></li>
        <% } %>

        <li><a href="controller?action=ledger">My Ledger</a></li>
        <li><a href="controller?action=reports">Reports</a></li>
    </ul>
</div>

</body>
</html>