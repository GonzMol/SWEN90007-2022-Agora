package controller;

import datasource.ListingMapper;
import domain.FixedPriceListing;
import domain.Listing;
import domain.User;
import util.AuthenticationEnforcer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "ViewListingController", value = {"/listing"})
public class ViewListingController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String listingID = request.getParameter("id");

        Listing listing = null;

        boolean success = false;

        if (listingID != null) {
            listing = ListingMapper.fetch(UUID.fromString(listingID));
        } else {
            // listing ID not provided
            response.setStatus(400);
        }
        if (listing == null) {
            // listing not found
            response.setStatus(404);
        } else {
            // listing found, send object
            request.setAttribute("listing", listing);
            if (user != null) {
                request.setAttribute("user", user);
            }
            success = true;
        }

        RequestDispatcher dispatcher;
        if (success) {
            dispatcher = request.getRequestDispatcher(
                    listing instanceof FixedPriceListing ? "listingDetail.jsp" : "auctionListingDetail.jsp");
        } else {
            dispatcher = request.getRequestDispatcher("error.jsp");
        }
        dispatcher.forward(request, response);
    }
}
