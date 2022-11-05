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
                    <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Order ID</h5>
                </div>
                <div class="row g-3">
                    <div class="col-md-6">
                        <h5>Product</h5>
                    </div>
                    <div class="col-md-6">
                        <p>Iphone 10</p>
                    </div>
                    <div class="col-md-6">
                        <h5>Quantity</h5>
                    </div>
                    <div class="col-md-6">
                        <p>1</p>
                    </div>
                    <div class="col-md-6">
                        <h5>Price</h5>
                    </div>
                    <div class="col-md-6">
                        <p>$100</p>
                    </div>
                    <div class="col-md-6">
                        <h5>First name</h5>
                    </div>
                    <div class="col-md-6">
                        <p>Aperson</p>
                    </div>
                    <div class="col-md-6">
                        <h5>Last name</h5>
                    </div>
                    <div class="col-md-6">
                        <p>Bbb</p>
                    </div>
                    <div class="col-md-6">
                        <h5>Email</h5>
                    </div>
                    <div class="col-md-6">
                        <p>email@address.com</p>
                    </div>
                    <div class="col-md-6">
                        <h5>Street</h5>
                    </div>
                    <div class="col-md-6">
                        <p>285 cardigan street</p>
                    </div>
                    <div class="col-md-6">
                        <h5>City</h5>
                    </div>
                    <div class="col-md-6">
                        <p>Melbourne</p>
                    </div>
                    <div class="col-md-6">
                        <h5>State</h5>
                    </div>
                    <div class="col-md-6">
                        <p>Victoria</p>
                    </div>
                    <div class="col-md-6">
                        <h5>Country</h5>
                    </div>
                    <div class="col-md-6">
                        <p>Australia</p>
                    </div>
                    <div class="col-md-6">
                        <h5>postcode</h5>
                    </div>
                    <div class="col-md-6">
                        <p>3053</p>
                    </div>
                    <div class="col-12 mt-5">
                        <button type="button" class="btn btn-primary float-end">Modify order</button>
                        <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                    </div>
                </div>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

</body>