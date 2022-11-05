<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="domain.User" %>
<%@ page import="domain.StandardUser" %>
<%@ page import="domain.ShippingDetails" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<!DOCTYPE html>
<html>
    <%          ShippingDetails shippingDetails = (ShippingDetails) request.getAttribute("shippingDetails");
                String firstName=shippingDetails.getFirstName();
                String lastName=shippingDetails.getLastName();
                String phoneNumber=shippingDetails.getPhoneNumber();
                String street = shippingDetails.getStreet();
                String state = shippingDetails.getState();
                String country = shippingDetails.getCountry();
                String postcode = shippingDetails.getPostcode();
                String city = shippingDetails.getCity();

                %>
<body class="d-flex flex-column min-vh-100">
    <jsp:include page="header.jsp" />

    <div class="container mt-5">
        <div class="row">
            <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
                <div class="col-12">
                    <h3 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Checkout detail</h3>
                </div>
<%--                connect to checkout controller--%>
                <form action="${pageContext.request.contextPath}/checkout" method="post">
                    <div class="row g-3">
                        <input type="hidden" value="<%=request.getParameter("id")%>" name="id">
                        <input type="hidden" value="<%=request.getParameter("quantity")%>" name="quantity">

                        <p><%=request.getParameter("quantity")%></p>
<%--                        <p><%=%></p>--%>
                        <div class="col-md-12">
                            <label>First name</label>
                            <input type="text" class="form-control" value="<%=firstName%>" name="firstName">
                        </div>
                        <div class="col-md-12">
                            <label>Last name</label>
                            <input type="text" class="form-control" value="<%=lastName%>" name="lastName">
                        </div>
                        <div class="col-md-12">
                            <label>Phone number</label>
                            <input type="text" class="form-control" value="<%=phoneNumber%>" name="phoneNumber">
                        </div>
                        <div class="col-md-12">
                            <label>Street</label>
                            <input type="text" class="form-control" value="<%=street%>" name="street">
                        </div>
                        <div class="col-md-12">
                            <label>City</label>
                            <input type="text" class="form-control" value="<%=city%>" name="city">
                        </div>
                        <div class="col-md-5">
                            <label>State</label>
                            <input type="text" class="form-control" value="<%=state%>" name="state">
                        </div>
                        <div class="col-md-4">
                            <label>Country</label>
                            <input type="text" class="form-control" value="<%=country%>" name="country">
                        </div>
                        <div class="col-md-3">
                        <label>Postcode</label>
                        <input type="number" class="form-control" value="<%=postcode%>" name="postcode">
                        </div>
                        <div class="col-12 mt-5">
                            <button type="submit" class="btn btn-primary float-end">Confirm</button>
                            <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>

</body>