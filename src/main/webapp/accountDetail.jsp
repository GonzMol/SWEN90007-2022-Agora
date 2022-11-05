<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="domain.User" %>
<%@ page import="domain.StandardUser" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<!DOCTYPE html>
<html>
    <%          User user = (User) request.getAttribute("user");
                String firstName=null;
                String lastName=null;
                String phoneNumber=null;
                String street = null;
                String state = null;
                String country = null;
                String postcode = null;
                String email = null;
                String userName = null;
                String city = null;
                email = user.getEmail();
                userName = user.getUsername();
                if (user instanceof StandardUser) {
                    firstName = ((StandardUser) user).getShippingDetails().getFirstName();
                    lastName = ((StandardUser) user).getShippingDetails().getLastName();
                    phoneNumber = ((StandardUser) user).getShippingDetails().getPhoneNumber();
                    street = ((StandardUser) user).getShippingDetails().getStreet();
                    state = ((StandardUser) user).getShippingDetails().getState();
                    city = ((StandardUser) user).getShippingDetails().getCity();
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
                    <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Account detail</h5>
                </div>

                <%if (request.getSession().getAttribute("role").equals("admin")){%>
                        <div class="col-md-6">
                            <h5>Email</h5>
                            <p><%=email%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Username</h5>
                            <p><%=userName%></p>
                        </div>
                        <div class="col-12 mt-5">
                            <form action="${pageContext.request.contextPath}/edit-password" method="get">
                                <button type="submit" class="btn btn-primary float-end me-2">Change password</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/edit-account" method="get">
                                <button type="submit" class="btn btn-primary float-end me-2">Change email</button>
                            </form>
                        </div>
                    <%}else {%>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <h5>First name</h5>
                                <p><%=firstName%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Last name</h5>
                            <p><%=lastName%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Username</h5>
                            <p><%=userName%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Email</h5>
                            <p><%=email%></p>
                        </div>
                        <div class="col-md-12">
                            <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Contact info</h5>
                        </div>
                        <div class="col-md-6">
                            <h5>Mobile phone</h5>
                            <p><%=phoneNumber%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Street</h5>
                            <p><%=street%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>State</h5>
                            <p><%=state%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>City</h5>
                            <p><%=city%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Country</h5>
                            <p><%=country%></p>
                        </div>
                        <div class="col-md-6">
                            <h5>Postcode</h5>
                            <p><%=postcode%></p>
                        </div>
                        <div class="col-12">
                            <a href="edit-account">
                                <button type="button" class="btn btn-primary">Change Account Details</button>
                            </a>
                        </div>
                        <div class="col-12">
                            <a href="edit-password">
                                <button type="button" class="btn btn-primary">Change Password</button>
                            </a>
                        </div>
                    <%}%>
                </div>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

</body>