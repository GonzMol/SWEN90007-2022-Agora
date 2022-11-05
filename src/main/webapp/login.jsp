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
                <h2 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Log in</h2>
            </div>
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="col-12">
                    <h5 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Account</h5>
                </div>
                <div class="row g-3">
                    <div class="col-md-12">
                        <label>Email</label>
                        <input type="text" class="form-control" placeholder="Email" name="email" required>
                    </div>
                    <div class="col-md-12">
                        <label>Password</label>
                        <input type="password" class="form-control" placeholder="Password" name="password" required>
                    </div>

                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end">Login</button>
                        <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                    </div>
                </div>
            </form>

        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

</body>
</html>