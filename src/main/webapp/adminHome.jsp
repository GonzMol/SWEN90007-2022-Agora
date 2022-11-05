<%@ page import="domain.StandardUser" %>
<%@ page import="java.util.ArrayList" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ArrayList<StandardUser> users = (ArrayList<StandardUser>) request.getAttribute("users");
%>
<html>
    <body class="d-flex flex-column min-vh-100">
    <jsp:include page="header.jsp" />

        <div class="container list-group mt-5 w-50">
            <%if (users!=null){%>
                <% for (StandardUser user : users){%>
                    <div class="list-group-item list-group-item-action" aria-current="true">
                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="mb-1">Username</h5>
                            <h5><%=user.getUsername()%></h5>
                        </div>
                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="mb-1">Email</h5>
                            <h5><%=user.getEmail()%></h5>
                        </div>
                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="mb-1">Status</h5>
                            <%if (user.isActive()){%>
                                <h5>Active</h5>
                                <form action="${pageContext.request.contextPath}/deactivate-account" method="post">
                                    <input value="<%=user.getId()%>" name="id" type="hidden">
                                    <button class="btn btn-primary mt-3 float-end">Deactivate</button>
                                </form>
                            <%}else{%>
                                <h5 class="fw-light">Inactive</h5>
                            <%}%>

                        </div>

                    </div>
            <%}%>
            <%}%>
        </div>

        <jsp:include page="footer.jsp"/>
    </body>
</html>
