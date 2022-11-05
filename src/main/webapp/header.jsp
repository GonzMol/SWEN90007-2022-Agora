
<%
    String role = (String)request.getSession().getAttribute("role");
%>
<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="index">Agora</a>
        <div class="collapse navbar-collapse" id="collapsibleNavbar">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="search">Search</a>
                </li>
                <% if (role == null) {%>
                <li class="nav-item">
                    <a class="nav-link" href="login">Login</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="register">Register</a>
                </li>
                <% } else if (role.equals("admin")) { %>
                <li class="nav-item">
                    <a class="nav-link" href="listings">Manage All Listings</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="accounts">Manage All Accounts</a>
                </li>
                <% } else { %>
                <li class="nav-item">
                    <a class="nav-link" href="listings">My Listings</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="purchases">My Purchases</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="sales">My Sales</a>
                </li>
                <% } %>
                <% if (role != null) {%>
                <li class="nav-item">
                    <a class="nav-link" href="account">My Account</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="logout">Logout</a>
                </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

