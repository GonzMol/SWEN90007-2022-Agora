<%@ page import="domain.AuctionListing" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.format.FormatStyle" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="domain.StandardUser" %>
<%@ page import="java.time.OffsetDateTime" %>
<%@ page import="java.time.ZoneOffset" %>
<%@ page import="java.time.ZoneId" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<%
    AuctionListing listing = (AuctionListing) request.getAttribute("listing");
    ArrayList<String> userEmails = (ArrayList<String>) request.getAttribute("userEmails");
    OffsetDateTime now = OffsetDateTime.now();
    boolean editable = now.isBefore(listing.getStartTime());
    String formattedStartTime = listing.getStartTime()
            .atZoneSameInstant(ZoneId.of("Australia/Sydney"))//.toLocalDateTime()
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    String formattedEndTime = listing.getEndTime()
            .atZoneSameInstant(ZoneId.of("Australia/Sydney"))//.toLocalDateTime()
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    String htmlFormatString = "yyyy-MM-dd'T'HH:mm";
%>
<html>
<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp" />
<div class="container mt-5">
    <div class="row">
        <div class="col-md-6 offset-md-3 border p-4 shadow bg-light">
            <div class="col-12">
                <h3 class="fw-normal text-secondary fs-4 text-uppercase mb-4">Edit Auction Listing</h3>
                <% if (!editable) { %>
                    <p>The auction has started and can no longer be edited.</p>
                <% } %>
            </div>
<%--            change to modify listing controller--%>
            <form action="${pageContext.request.contextPath}/edit-listing" method="post"
                onclick='
                    startTime.setCustomValidity(
                        new Date(startTime.value)
                        < new Date(endTime.value)
                        ? "" : "Start time must precede end time."
                    )
                    category.setCustomValidity(!category ? "Category not set" : "")
                    condition.setCustomValidity(!condition ? "Condition not set" : "")'>
                <input type="hidden" value="<%=listing.getVersion()%>" name="version"/>
                <input type="hidden" name="id" value="<%=listing.getId()%>" >
                <div class="row g-3">
                    <div class="col-md-12">
                        <h5>Product title</h5>
                        <input type="text" class="form-control" placeholder="Enter Product name"
                               value="<%=listing.getTitle()%>" name="title" required
                               <% if (!editable) { %>disabled<% } %>
                        >
                    </div>
                    <div class="col-md-6">
                        <h5>Start price</h5>
                    </div>
                    <div class="col-md-6">
                        <input type="text" class="form-control" placeholder="Starting price"
                               value="<%=listing.getStartPrice()%>" name="startPrice" required
                               min="0.05" step="0.05"
                            <% if (!editable) { %>disabled<% } %>
                        >
                    </div>
                    <div class="col-md-6">
                        <h5>Current start time</h5>
                        <p><%=formattedStartTime%></p>
<%--                        <p><%=DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(listing.getStartTime())%></p>--%>
                    </div>
                    <div class="col-md-6">
                        <h5>Modify Start time</h5>
                        <input type="datetime-local" class="form-control" placeholder="Start time"
                               value="<%=listing.getStartTime()%>" name="startTime" required id=""
                            min="<%=DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(now)%>"
                               id="modified-start-time"
                               <% if (!editable) { %>disabled<% } %>
                        >
                    </div>
                    <div class="col-md-6">
                        <h5>Current end time</h5>
                        <p><%=formattedEndTime%></p>
<%--                        <p><%=DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(listing.getEndTime().toLocalDateTime())%></p>--%>
                    </div>
                    <div class="col-md-6">
                        <h5>Modify End time</h5>
                        <input type="datetime-local" class="form-control" placeholder="End time"
                               value="<%=listing.getEndTime()%>" name="endTime" required
                               min="<%=DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(now)%>"
                               id="modified-end-time"
                               <% if (!editable) { %>disabled<% } %>
                        >
                    </div>

                    <div class="col-6">
                        <h5>Category</h5>
                        <select class="form-select" name="category" required
                                <% if (!editable) { %>disabled<% } %>
                        >
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
                    <div class="col-6">
                        <h5>Condition</h5>
                        <select class="form-select" name="condition" required
                                <% if (!editable) { %>disabled<% } %>
                        >
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
                        <textarea class="form-control" placeholder="Description" name="description"
                                  <% if (!editable) { %>disabled<% } %>
                        ><%=listing.getDescription()%></textarea>
                    </div>
                    <% if (listing.getOwner().getId().equals(request.getSession().getAttribute("userID"))) { %>
                    <div class="col-12">
                        <h5>Add co-sellers</h5>
                        <select class="form-select" name="coSeller" multiple
                                <% if (!editable) { %>disabled<% } %>
                        >
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
                                <% if (isSelected) {%>selected<%}%>
                            ><%=userEmail%></option>
                            <%}%>
                        </select>
                    </div>
                    <% } %>
                    <div class="col-12 mt-5">
                        <button type="submit" class="btn btn-primary float-end me-2"
                                <% if (!editable) { %>disabled<% } %>
                        >Update</button>
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

<jsp:include page="footer.jsp"/>
</body>
</html>
