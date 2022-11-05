<%@ page import="domain.Order" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.format.FormatStyle" %>
<%@ page import="java.time.ZoneId" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">

<%
    ArrayList<Order> orders = (ArrayList<Order>) request.getAttribute("orders");
    String heading = (String)request.getAttribute("heading");
    heading = (heading != null) ? heading : "All Purchases";
%>

<!DOCTYPE html>
<html>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />
<div class="container list-group mt-5 w-50">
    <h5><%=heading%></h5>
    <%if (orders!=null && !orders.isEmpty()){ %>
        <%for (Order o :orders){%>
            <a href="edit-order?id=<%=o.getId().toString()%>" class="list-group-item list-group-item-action" aria-current="true">
                <div class="d-flex w-100 justify-content-between">
                    <h5 class="mb-1"><%=o.getListing().getTitle()%></h5>
                    <small class="text-primary">purchased <%=o.getQuantity()%> item(s)</small>
                    <p>purchased on <%=DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(o.getDate().atZoneSameInstant(ZoneId.of("Australia/Sydney")).toLocalDateTime())%></>
                </div>
            </a>
        <%}%>
    <%}else {%>
        <p>No orders to display.</p>
    <%}%>
</div>


<jsp:include page="footer.jsp"/>
</body>
</html>