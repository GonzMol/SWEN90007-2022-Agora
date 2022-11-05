<%@ page import="domain.FixedPriceListing" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="domain.StandardUser" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">

<%
    FixedPriceListing listing = (FixedPriceListing) request.getAttribute("listing");
    ArrayList<String> userEmails = (ArrayList<String>) request.getAttribute("userEmails");
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />

<div class="container mt-5">
    <div class="row">
        <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
            <div class="col-12">
                <h3 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Create Fixed Price Listing</h3>
            </div>
<%--            change to modify listing controller--%>
            <form action="${pageContext.request.contextPath}/edit-listing" method="post"
                  onclick='
                    category.setCustomValidity(!category ? "Category not set" : "")
                    condition.setCustomValidity(!condition ? "Condition not set" : "")
                    '>
                <input type="hidden" value="<%=listing.getVersion()%>" name="version"/>
                <div class="row g-3">
                    <div class="col-md-12">
                        <h5>Title</h5>
                        <input type="text" class="form-control" placeholder="Enter Product name"
                               value="<%=listing.getTitle()%>" name="title" required>
                    </div>
                    <input type="hidden" name="id" value="<%=listing.getId()%>" >
                    <div class="col-md-6">
                        <h5>In stock</h5>
                        <input min="0" step="1" type="number" class="form-control" placeholder="Enter Number of products in stock"
                               value="<%=listing.getStock()%>" name="stock" required>
                    </div>
                    <div class="col-md-6">
                        <h5>Price</h5>
                        <input type="number" min="0.05" step="0.05"
                               class="form-control" placeholder="price"
                               value="<%=listing.getPrice()%>" name="price" required>
                    </div>
                    <div class="col-12">
                        <h5>Category</h5>
                        <select class="form-select" name="category" required>
<%--                            <option selected><%=listing.getCategory()%></option>--%>
                            <option value="ELECTRONICS"
                                    <% if (listing.getCategory().toString().equals("ELECTRONICS")) {%>
                                    selected
                                    <% } %>
                            >ELECTRONICS</option>
                            <option value="HOME_AND_GARDEN"
                                    <% if (listing.getCategory().toString().equals("HOME_AND_GARDEN")) {%>
                                    selected
                                    <% } %>
                            >HOME_AND_GARDEN</option>
                            <option value="CLOTHING_AND_ACCESSORIES"
                                    <% if (listing.getCategory().toString().equals("CLOTHING_AND_ACCESSORIES")) {%>
                                    selected
                                    <% } %>
                            >CLOTHING_AND_ACCESSORIES</option>
                            <option value="ENTERTAINMENT"
                                    <% if (listing.getCategory().toString().equals("ENTERTAINMENT")) {%>
                                    selected
                                    <% } %>
                            >ENTERTAINMENT</option>
                            <option value="HEALTH_AND_BEAUTY"
                                    <% if (listing.getCategory().toString().equals("HEALTH_AND_BEAUTY")) {%>
                                    selected
                                    <% } %>
                            >HEALTH_AND_BEAUTY</option>
                            <option value="SPORTS"
                                    <% if (listing.getCategory().toString().equals("SPORTS")) {%>
                                    selected
                                    <% } %>
                            >SPORTS</option>
                            <option value="TOYS"
                                    <% if (listing.getCategory().toString().equals("TOYS")) {%>
                                    selected
                                    <% } %>
                            >TOYS</option>
                            <option value="PETS"
                                    <% if (listing.getCategory().toString().equals("PETS")) {%>
                                    selected
                                    <% } %>
                            >PETS</option>
                            <option value="OTHER"
                                    <% if (listing.getCategory().toString().equals("OTHER")) {%>
                                    selected
                                    <% } %>
                            >OTHER</option>
                        </select>
                    </div>
                    <div class="col-12">
                        <h5>Condition</h5>
                        <select class="form-select" name="condition" required>
<%--                            <option selected><%=listing.getCondition()%></option>--%>
                            <option value="NEW"
                                    <% if (listing.getCondition().toString().equals("NEW")) {%>
                                    selected
                                    <% } %>
                            >NEW</option>
                            <option value="USED"
                                    <% if (listing.getCondition().toString().equals("USED")) {%>
                                    selected
                                    <% } %>
                            >USED</option>
                        </select>
                    </div>
                    <div class="col-12">
                        <h5>Description</h5>
                        <textarea class="form-control" placeholder="Description" name="description" required
                        ><%=listing.getDescription()%></textarea>
                    </div>
                    <% if (listing.getOwner().getId().equals(request.getSession().getAttribute("userID"))) { %>
                    <div class="col-12">
                        <h5>Edit co-sellers</h5>
                        <select class="form-select" name="coSeller" multiple>
                            <% for (String userEmail : userEmails){
                                boolean isSelected = false;
                                for (StandardUser coSeller : listing.getCoSellers()) {
                                    if (coSeller.getEmail().equals(userEmail)) {
                                        isSelected = true;
                                        break;
                                    }
                                }
                            %>
                                <option value="<%=userEmail%>"
                                        <% if (isSelected) {%>selected<%}%>><%=userEmail%></option>
                            <%}%>
                        </select>
                    </div>
                    <% } %>
                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end me-2">Update</button>
                        <a href="deactivate-listing?id=<%=listing.getId().toString()%>">
                            <button type="button" class="btn btn-primary float-end me-2">Deactivate</button>
                        </a>
                        <a href="listing?id=<%=listing.getId().toString()%>">
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
