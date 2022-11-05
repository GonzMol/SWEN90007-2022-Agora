<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="domain.User" %>
<%@ page import="domain.StandardUser" %>

<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<!DOCTYPE html>
<html>

    <%          User user = (User) request.getAttribute("user");
                String firstName = null;
                String lastName = null;
                String phoneNumber = null;
                String street = null;
                String state = null;
                String city = null;
                String country = null;
                String postcode = null;
                String email=null;
                String userName = null;
                email = user.getEmail();
                userName = user.getUsername();
                if (user instanceof StandardUser) {
                    firstName = ((StandardUser) user).getShippingDetails().getFirstName();
                    lastName = ((StandardUser) user).getShippingDetails().getLastName();
                    phoneNumber = ((StandardUser) user).getShippingDetails().getPhoneNumber();
                    street = ((StandardUser) user).getShippingDetails().getStreet();
                    city = ((StandardUser) user).getShippingDetails().getCity();
                    state = ((StandardUser) user).getShippingDetails().getState();
                    country = ((StandardUser) user).getShippingDetails().getCountry();
                    postcode = ((StandardUser) user).getShippingDetails().getPostcode();

                }
                %>
<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />

<div class="container mt-5">
    <div class="row">
        <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
            <div class="col-12">
                <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Modify Account Detail</h5>
            </div>
            <form action="${pageContext.request.contextPath}/edit-account" method="post">

                <%if (request.getSession().getAttribute("role").equals("admin")){%>
                    <div class="col-md-12">
                        <h5>Email</h5>
                        <input type="text" class="form-control" placeholder="Email" value="<%=email%>" name="email" required>
                    </div>
                    <div class="col-md-12">
                        <h5>Current password</h5>
                        <input type="password" class="form-control" placeholder="Current Password" name="password" required>
                    </div>
                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end">Save Change</button>
                        <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                    </div>
                <%}else {%>
                <div class="row g-3">
                    <div class="col-md-12">
                        <h5>First name</h5>
                           <input type="text" class="form-control" name="firstName" value="<%=firstName%>" required>
                    </div>
                    <div class="col-md-12">
                        <h5>Last name</h5>
                        <input type="text" class="form-control" placeholder="Last Name" value="<%=lastName%>" name="lastName" required>
                    </div>
                    <div class="col-md-6">
                        <h5>Username</h5>
                    </div>
                    <div class="col-md-6">
                        <p><%=userName%></p>
                    </div>
                    <div class="col-md-12">
                        <h5>Email</h5>
                        <input type="text" class="form-control" placeholder="Email" value="<%=email%>" name="email" required>
                    </div>

                    <div class="col-12">
                        <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Contact info</h5>
                    </div>
                    <div class="col-md-12">
                        <h5>Mobile phone</h5>
                        <input type="tel" class="form-control" placeholder="Mobile Phone" value="<%=phoneNumber%>" name="phoneNumber" required>
                    </div>
                    <div class="col-md-12">
                        <h5>Street</h5>
                        <input type="text" class="form-control" placeholder="Street" value="<%=street%>" name="street" required>
                    </div>
                    <div class="col-md-5">
                        <h5>City</h5>
                        <input type="text" class="form-control" placeholder="City" value="<%=city%>" name="city" required>
                    </div>
                    <div class="col-md-6">
                        <h5>State</h5>
                        <input type="text" class="form-control" placeholder="State" value="<%=state%>" name="state" required>
                    </div>
                    <div class="col-md-6">
                        <h5>Country</h5>
                        <input type="text" class="form-control" placeholder="Country" value="<%=country%>" name="country" required>
                    </div>
                    <div class="col-md-6">
                        <h5>Postcode</h5>
                        <input type="number" class="form-control" placeholder="Postcode" value="<%=postcode%>" name="postcode" required>
                    </div>
                    <div class="col-md-12">
                        <h5>Current password</h5>
                        <input type="password" class="form-control" placeholder="Current Password" name="password" required>
                    </div>
                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end">Save Change</button>
                        <a href="account">
                            <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                        </a>
                    </div>
                </div>
                <%}%>
            </form>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

</body>