<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<!DOCTYPE html>
<html>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />

<div class="container mt-5">
        <div class="row">
                <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
                        <div class="col-12">
                                <h2 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Sign Up</h2>
                        </div>
                        <form action="${pageContext.request.contextPath}/register" method="post"
                              oninput='up2.setCustomValidity(up2.value !== password.value ? "Passwords do not match." : "")'>
                                <div class="col-12">
                                        <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Account</h5>
                                </div>
                                <div class="row g-3">
                                        <div class="col-md-12">
                                                <label>Username</label>
                                                <input type="text" class="form-control" placeholder="User Name" name="username" required>
                                        </div>
                                        <div class="col-md-12">
                                                <label>Email</label>
                                                <input type="text" class="form-control" placeholder="Email" name="email" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>Password</label>
                                                <input type="password" class="form-control" placeholder="Password" name="password" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>Confirm password</label>
                                                <input type="password" class="form-control" placeholder="Confirm Password" name="up2" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>First name</label>
                                                <input type="text" class="form-control" placeholder="First Name" name="firstName" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>Last name</label>
                                                <input type="text" class="form-control" placeholder="Last Name" name="lastName" required>
                                        </div>
                                        <div class="col-12">
                                                <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Contact info</h5>
                                        </div>
                                        <div class="col-md-12">
                                                <label>Mobile phone</label>
                                                <input type="tel" class="form-control" placeholder="Mobile Phone" name="phone" required>
                                        </div>
                                        <div class="col-md-12">
                                                <label>Street</label>
                                                <input type="text" class="form-control" placeholder="Street" name="street" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>city</label>
                                                <input type="text" class="form-control" placeholder="City" name="city" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>Country</label>
                                                <input type="text" class="form-control" placeholder="Country" name="country" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>State</label>
                                                <input type="text" class="form-control" placeholder="State" name="state" required>
                                        </div>
                                        <div class="col-md-6">
                                                <label>Postcode</label>
                                                <input type="number" class="form-control" placeholder="Postcode" name="postcode" required>
                                        </div>
                                        <div class="col-12 mt-5">
                                                <button type="submit" class="btn btn-primary float-end">Confirm</button>
                                                <a href="index">
                                                <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                                                </a>
                                        </div>
                                </div>
                        </form>
                </div>
        </div>
</div>
<jsp:include page="footer.jsp"/>

</body>