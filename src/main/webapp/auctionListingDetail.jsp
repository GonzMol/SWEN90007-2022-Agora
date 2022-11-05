<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="domain.AuctionListing" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.format.FormatStyle" %>
<%@ page import="domain.StandardUser" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.UUID" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.time.OffsetDateTime" %>
<%@ page import="java.time.ZoneId" %>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<%
    AuctionListing listing = (AuctionListing) request.getAttribute("listing");
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
    OffsetDateTime currentTime = OffsetDateTime.now();
    OffsetDateTime startTime = listing.getStartTime();
    OffsetDateTime endTime = listing.getEndTime();
    String formattedStartTime = listing.getStartTime()
            .atZoneSameInstant(ZoneId.of("Australia/Sydney"))//.toLocalDateTime()
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    String formattedEndTime = listing.getEndTime()
            .atZoneSameInstant(ZoneId.of("Australia/Sydney"))//.toLocalDateTime()
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    BigDecimal currentBid = listing.getHighestBid() == null ? listing.getStartPrice() : listing.getHighestBid().getAmount();
    boolean isHighestBidder = (listing.getHighestBid() != null && listing.getHighestBid().getUser().getId().equals(userID));
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
    <%--            title + condition in the card header--%>
    <div class="card-header">
        <%=listing.getTitle()%> [<%=listing.getCondition()%>]
    </div>
        <div class="card-body">
            <p class="mt-2 mb-2">Listed by: <%=sellerString.toString()%></p>
            <h5>Highest bid</h5>
            <h4 class="card-title mt-2">$<%=currentBid%></h4>
            <% if (isHighestBidder) { %>
                <h5 class="card-title mt-1 mb-2">You have the highest bid!</h5>
            <% } %>
            <p><%=listing.getCategory()%></p>

            <h5 class="card-text mt-5">Description:</h5>
            <p class="card-text mt-3" style="white-space: pre-wrap"><%=listing.getDescription() %></p>
            <div class="row row-cols-1 row-cols-md-3 g-4">
                <div class="col">
                    <h5 class="card-text mt-5">Starting price:</h5>
                    <p class="card-text mt-3">$<%=listing.getStartPrice() %></p>
                </div>
                <div class="col">
                    <h5 class="card-text mt-5">Start time:</h5>
                    <p class="card-text mt-3">
                        <%=formattedStartTime%>
                    </p>
                </div>
                <div class="col">
                    <h5 class="card-text mt-5">End time:</h5>
                    <p class="card-text mt-3">
                        <%=formattedEndTime%>
                    </p>

                </div>

            </div>
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
                <% } else { %>
                    <% if (startTime.compareTo(currentTime) > 0) { %>
                    <p class="card-text mt-3">The auction has not started yet, please return later.</p>
                    <% } else if (endTime.compareTo(currentTime) < 0) { %>
                    <p class="card-text mt-3">The auction has ended.</p>
                    <% } else { %>
                    <form action="${pageContext.request.contextPath}/place-bid" method="post" class="card-body mt-5">
                        <div>
                            <input type="hidden" value="<%=listing.getId()%>" name="id">
                            <input type="number" class="card-text" placeholder="place a bid" name="amount"
                                   min="<%=(currentBid.add(new BigDecimal("0.05")))%>" step="0.05" required></div>
                        <br/>
                        <div><button type="submit" class="btn btn-primary">Place a bid</button></div>
                    </form>
                    <% } %>
                <% } %>
            <% } %>
        </div>
    <div class="card-footer text-muted">
        Item Detail
    </div>
</div>
<jsp:include page="footer.jsp"/>
</body>

</html>
