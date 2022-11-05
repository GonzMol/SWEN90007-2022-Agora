<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<!DOCTYPE html>
<html>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />

<div class="container alert alert-danger mt-5 w-75" role="alert">
    <h4 class="alert-heading"><%=response.getStatus()%> Error</h4>
    <p>An error (code: <%=response.getStatus()%>) occurred processing your request. Please try again.</p>
    <hr>
    <p class="mb-0">Try again</p>
</div>

<jsp:include page="footer.jsp"/>
</body>

</html>
