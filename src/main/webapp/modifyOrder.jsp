<%@ page import="domain.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<%
    Order order = (Order) request.getAttribute("order");
    String title = order.getListing().getTitle();
    Integer quantity = order.getQuantity();
    Listing listing = order.getListing();
    ShippingDetails shippingDetails = order.getShippingDetails();
    String firstName=shippingDetails.getFirstName();
    String lastName=shippingDetails.getLastName();
    String phoneNumber=shippingDetails.getPhoneNumber();
    String street = shippingDetails.getStreet();
    String state = shippingDetails.getState();
    String country = shippingDetails.getCountry();
    String postcode = shippingDetails.getPostcode();
    String city = shippingDetails.getCity();
    boolean isBuyer = order.getBuyer().getId().equals(request.getSession().getAttribute("userID"));
    boolean isActive = order.getListing().isActive();
%>
<!DOCTYPE html>
<html>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />

<div class="container mt-5">
    <div class="row">
        <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
            <div class="col-12">
                <h2 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Modify order</h2>
            </div>
            <form action="${pageContext.request.contextPath}/edit-order" method="post">
                <input type="hidden" value="<%=order.getVersion()%>" name="version"/>
                <input type="hidden" value="<%=order.getId()%>" name="id"/>
                <div class="row g-3">
                    <div class="col-md-6">
                        <h5>Title</h5>
                        <p><%=title%></p>
                    </div>

                    <div class="col-md-6">
                        <h5>Price</h5>
                        <% if (listing instanceof FixedPriceListing) {%>
                            <p>$<%=((FixedPriceListing) listing).getPrice()%></p>
                        <%}else {%>
                            <p>$<%=((AuctionListing) listing).getHighestBid().getAmount()%></p>
                        <%}%>
                    </div>
                    <div class="col-md-6">
                        <%if(listing instanceof FixedPriceListing) {%>
                        <h5>Modify Quantity</h5>
                        <%if (((FixedPriceListing) listing).getStock()!=0) {%>
                            <select class="card-body form-select-sm bg-light shadow-none border-0 mt-3" name="quantity"
                                <% if (!isActive) { %>disabled<% } %>>
                                <option selected><%=quantity%></option>

                                <%for (int i =1; i<=((FixedPriceListing) listing).getStock();i++){ %>
                                <option><%=i %></option>
                                <%}%>
                            </select>
                        <%}%>
                        <%}%>
                    </div>
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <h5>First name</h5>
                        <input type="text" class="form-control" value="<%=firstName%>" name="firstName"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-6">
                        <h5>Last name</h5>
                        <input type="text" class="form-control" value="<%=lastName%>" name="lastName"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-12">
                        <h5>Phone number</h5>
                        <input type="text" class="form-control" value="<%=phoneNumber%>" name="phoneNumber"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-6">
                        <h5>Street</h5>
                        <input type="text" class="form-control" value="<%=street%>" name="street"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-6">
                        <h5>City</h5>
                        <input type="text" class="form-control" value="<%=city%>" name="city"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-5">
                        <h5>State</h5>
                        <input type="text" class="form-control" value="<%=state%>" name="state"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-4">
                        <h5>Country</h5>
                        <input type="text" class="form-control" value="<%=country%>" name="country"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>
                    <div class="col-md-3">
                        <h5>Postcode</h5>
                        <input type="number" class="form-control" value="<%=postcode%>" name="postcode"
                               <% if (!isActive || !isBuyer) { %>disabled<% } %>>
                    </div>

                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end">Save change</button>
                        <a href="<%=isBuyer ? "purchases" : "sales"%>">
                            <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel Editing</button>
                        </a>
                    </div>
                </div>
            </form>
            <form action="${pageContext.request.contextPath}/cancel-order" method="post">
                <input hidden value="<%=order.getId().toString()%>" name="id"/>
                <button type="submit" class="btn btn-outline-secondary float-end me-2">Cancel Order</button>
            </form>
    </div>
</div>
<jsp:include page="footer.jsp"/>

</body>