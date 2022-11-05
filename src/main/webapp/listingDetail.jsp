<%@ page import="domain.FixedPriceListing" %>
<%@ page import="domain.User" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="domain.StandardUser" %>
<%@ page import="java.util.UUID" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<%
    FixedPriceListing listing = (FixedPriceListing) request.getAttribute("listing");
    ArrayList<StandardUser> coSellers = listing.getCoSellers();
    UUID userID = (UUID) request.getSession().getAttribute("userID");
    String role = (String) request.getSession().getAttribute("role");
    boolean isSeller = false;
    if (userID != null) {
        if (role.equals("standardUser")) {
            isSeller = listing.getOwner().getId().equals(userID);
            if (!isSeller) {
                for (StandardUser coSeller: coSellers) {
                    if (coSeller.getId().equals(userID)) {
                        isSeller = true;
                        break;
                    }
                }
            }
        }
    }
    StringBuilder sellerString = new StringBuilder(listing.getOwner().getUsername());
    for (StandardUser coSeller : listing.getCoSellers()) {
        sellerString.append(", ").append(coSeller.getUsername());
    }
%>
<!DOCTYPE html>
<html>

    <body class="d-flex flex-column min-vh-100">
        <jsp:include page="header.jsp" />
        <div class="container card text-center mt-5">

            <div class="card-header">
                <%=listing.getTitle() %> [<%=listing.getCondition()%>]
            </div>
            <div class="card-body">
                <p class="mt-2 mb-2">Listed by: <%=sellerString.toString()%></p>
                <p><%=listing.getCategory()%></p>
                <h5 class="card-text mt-5">Description:</h5>
                <p class="card-text mt-3" style="white-space: pre-wrap"><%=listing.getDescription() %></p>
                <h4 class="card-title mt-5">$<%=listing.getPrice()%> </h4>
                <p class="card-title mt-5"><strong>Stock remaining: <%=listing.getStock()%></strong></p>
                <% if (role == null) { %>
                <form  class="card-body" action="${pageContext.request.contextPath}/login" method="get">
                    <button type="submit" class="btn btn-primary">Login</button>
                </form>
                <% } else if (role.equals("admin")) { %>
                <form  class="card-body" action="${pageContext.request.contextPath}/deactivate-listing" method="post">
                    <input type="hidden" value="<%=listing.getId()%>" name="id">
                    <button type="submit" class="btn btn-primary">Remove Listing</button>
                </form>
                <% } else { %>
                    <% if (isSeller) { %>
                    <form  class="card-body" action="${pageContext.request.contextPath}/edit-listing" method="get">
                        <input type="hidden" value="<%=listing.getId()%>" name="id">
                        <button type="submit" class="btn btn-primary">Edit Listing</button>
                    </form>
                    <% } else {
                        if (listing.getStock() > 0) {
                    %>
                        <form  class="card-body" action="${pageContext.request.contextPath}/checkout" method="get"
                            onclick='quantity.setCustomValidity(isNaN(quantity.value) ? "Select a quantity" : "")'
                        >
                            <input type="hidden" value="<%=listing.getId()%>" name="id">
                            <select class="card-body form-select-sm bg-light shadow-none border-0 mt-3" name="quantity">
                                <option selected>Quantity</option>
                                <%for (int i =1; i<=listing.getStock();i++){ %>
                                <option><%=i %></option>
                                <%}%>
                            </select><br/>
                            <button type="submit" class="btn btn-primary mt-2">Checkout</button>
                        </form>
                        <% }
                    } %>
                <% } %>
            </div>


            <div class="card-footer text-muted">
                Item Detail
            </div>
        </div>
        </div>
        <jsp:include page="footer.jsp"/>
    </body>

</html>
