<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<%@ page import="domain.Listing" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<String> userEmails = (ArrayList<String>) request.getAttribute("userEmails");
%>
<html>

    <body class="d-flex flex-column min-vh-100">
        <jsp:include page="header.jsp" />

    <div class="container mt-5">
        <div class="row">
            <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
                <div class="col-12">
                    <h3 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Create Fixed Price Listing</h3>
                </div>
                <form action="${pageContext.request.contextPath}/create-fixed-price-listing" method="post"
                      onclick='
                                category.setCustomValidity(!category ? "Category not set" : "")
                                condition.setCustomValidity(!condition ? "Condition not set" : "")
                                '>
                    <div class="row g-3">
                        <div class="col-md-12">
                            <input type="text" class="form-control" placeholder="Title" name="title" required>
                        </div>
                        <div class="col-md-6">
                            <input type="number" class="form-control" placeholder="Stock" name="stock" required>
                        </div>
                        <div class="col-md-6">
                            <input type="text" class="form-control" placeholder="Price" name="price" required
                                   min="0.05" step="0.05">
                        </div>
                        <div class="col-12">
                            <select class="form-select" name="category" required>
                                <option selected value="">Category:</option>
                                <option value="ELECTRONICS">ELECTRONICS</option>
                                <option value="HOME_AND_GARDEN">HOME_AND_GARDEN</option>
                                <option value="CLOTHING_AND_ACCESSORIES">CLOTHING_AND_ACCESSORIES</option>
                                <option value="ENTERTAINMENT">ENTERTAINMENT</option>
                                <option value="HEALTH_AND_BEAUTY">HEALTH_AND_BEAUTY</option>
                                <option value="SPORTS">SPORTS</option>
                                <option value="TOYS">TOYS</option>
                                <option value="PETS">PETS</option>
                                <option value="OTHER">OTHER</option>
<%--                                <option value="<% Listing.Category.values(); %>"><% Listing.Category.values(); %></option>--%>

                            </select>
                        </div>
                        <div class="col-12">
                            <select class="form-select" name="condition" required>
                                <option selected value="">Condition</option>
                                <option value="NEW">NEW</option>
                                <option value="USED">USED</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <textarea class="form-control" placeholder="Description" name="description" required></textarea>
                        </div>
                        <div class="col-12">
                            <label>Add co-sellers</label>
                            <select class="form-select" name="coSeller" multiple>
                                <% for (String userEmail : userEmails){%>
                                    <option value="<%=userEmail%>"><%=userEmail%></option>
                                <%}%>
                            </select>
                        </div>
                        <div class="col-12 mt-5">
                            <button type="submit" class="btn btn-primary float-end">Post</button>
                            <a href="listings">
                                <button type="button" class="btn btn-outline-secondary float-end me-2">Cancel</button>
                            </a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
    </body>
</html>
