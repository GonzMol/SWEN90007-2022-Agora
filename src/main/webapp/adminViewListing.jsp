<%@ page import="domain.Listing" %>
<%@ page import="domain.FixedPriceListing" %>
<%@ page import="domain.AuctionListing" %>

<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">

<%ArrayList<Listing> listings = (ArrayList<Listing>) request.getAttribute("listings"); %>

<!DOCTYPE html>
<html>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />
<div class="container list-group mt-5 w-50">
    <%
        if (listings!=null && !listings.isEmpty()){
            for (Listing l :listings){%>
    <%--                        <p><%=l.getTitle()%></p>--%>
    <%if (l instanceof FixedPriceListing) {%>
    <div class="list-group-item list-group-item-action" aria-current="true">
        <a href="listing?id=<%=l.getId()%>" class="list-group-item list-group-item-action" aria-current="true">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1"><%=l.getTitle()%></h5>
                <%if (((FixedPriceListing) l).getStock()==0) {%>
                <small class="text-danger"><strong>Sold Out</strong></small>
                <%}else {%>
                <small class="text-primary"><%=((FixedPriceListing) l).getStock() %> items left</small>

                <%}%>
            </div>

        <div class="d-flex w-100 justify-content-between">
            <small>Created by <%=l.getOwner().getUsername()%></small>
            <h5 class="mb-1">$<%=((FixedPriceListing) l).getPrice()%></h5>
                <%if (l.isActive()){%>
                <h5>Active</h5>
                <form action="${pageContext.request.contextPath}/deactivate-listing" method="post">
                    <input value="<%=l.getId()%>" name="id" type="hidden">
                    <button class="btn btn-primary mt-3 float-end">Deactivate</button>
                </form>
                <%}else{%>
                <h5 class="fw-light">Inactive</h5>
                <%}%>
        </div>
        </a>
     </div>
    <%   } else {%>
    <a href="listing?id=<%=l.getId()%>" class="list-group-item list-group-item-action" aria-current="true">

    <div class="list-group-item list-group-item-action" aria-current="true">
        <div class="d-flex w-100 justify-content-between">
            <h5 class="mb-1"><%=l.getTitle()%></h5>
            <small class="text-success">Auction</small>
        </div>
        <div class="d-flex w-100 justify-content-between">
            <%--display the highest bid for the auction listing--%>
            <small>Created by <%=l.getOwner().getUsername()%></small>


                <%if (((AuctionListing) l).getHighestBid() == null) {%>
            <h5 class="mb-1">$<%=((AuctionListing) l).getStartPrice()%></h5>
            <%}else{%>
            <h5 class="mb-1">$<%=((AuctionListing) l).getHighestBid().getAmount()%></h5>
            <%}%>
                <%if (l.isActive()){%>
                    <h5>Active</h5>
                    <form action="${pageContext.request.contextPath}/deactivate-listing" method="post">
                        <input value="<%=l.getId()%>" name="id" type="hidden">
                        <button class="btn btn-primary mt-3 float-end">Deactivate</button>
                    </form>
                <%}else{%>
                    <h5 class="fw-light">Inactive</h5>
                <%}%>
        </div>



    </div>
    <%}%>
    <%}
    } else {%>
    <p>No listings to display.</p>
    <%}%>
    </a>

</div>


<jsp:include page="footer.jsp"/>
</body>

</html>
