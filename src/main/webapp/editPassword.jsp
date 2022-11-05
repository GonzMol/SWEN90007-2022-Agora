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
                <h2 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Change password</h2>
            </div>
            <form action="${pageContext.request.contextPath}/edit-password" method="post"
                  oninput='confirmNewPassword.setCustomValidity(confirmNewPassword.value !== newPassword.value ? "Passwords do not match." : "")'>
                <div class="row g-3">
                    <div class="col-md-12">
                        <label>Current Password</label>
                        <input type="password" class="form-control" placeholder="Current Password" name="oldPassword" required>
                    </div>
                    <div class="col-md-12">
                        <label>New Password</label>
                        <input type="password" class="form-control" placeholder="New Password" name="newPassword" required>
                    </div>
                    <div class="col-md-12">
                        <label>New Password</label>
                        <input type="password" class="form-control" placeholder="Confirm New Password" name="confirmNewPassword" required>
                    </div>
                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end">Confirm</button>
                        <a href="account">
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
</html>