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
    <h5>Your Product Listings</h5>
    <div class="container mt-2 mb-2">
        <div class="row">
            <div class="col-12">
                <form action="create-fixed-price-listing">
                    <button type="submit" class="btn btn-primary">
                        New Fixed-Price Listing
                    </button>
                </form>
                <form action="create-auction-listing">
                    <button type="submit" class="btn btn-primary">
                        New Auction Listing
                    </button>
                </form>
            </div>
        </div>
    </div>
    <%if (listings!=null && !listings.isEmpty()){%>

            <%for (Listing l :listings){%>
                <%if (l instanceof FixedPriceListing) {%>
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
                    </div>
                </a>
                <%   } else {%>
                    <a href="listing?id=<%=l.getId()%>" class="list-group-item list-group-item-action" aria-current="true">
                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="mb-1"><%=l.getTitle()%></h5>
                            <small class="text-success">Auction</small>
                        </div>
                        <div class="d-flex w-100 justify-content-between">
                            <%--                                            display the highest bid for the auction listing--%>
                            <small>Created by <%=l.getOwner().getUsername()%></small>
                            <%if (((AuctionListing) l).getHighestBid() == null) {%>
                            <h5 class="mb-1">$<%=((AuctionListing) l).getStartPrice()%></h5>
                            <%}else{%>
                            <h5 class="mb-1">$<%=((AuctionListing) l).getHighestBid().getAmount()%></h5>
                            <%}%>
                        </div>
                    </a>
            <%}%>
         <%} } else {%>
    <p>You don't have any listings.</p>
    <%}%>
</div>
<jsp:include page="footer.jsp"/>
</body>

</html>
