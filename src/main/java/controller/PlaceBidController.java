package controller;

import datasource.BidMapper;
import datasource.DB;
import datasource.ListingMapper;
import domain.*;
import util.AuthenticationEnforcer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet(name = "PlaceBidController", value = "/place-bid")
public class PlaceBidController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String listingID = request.getParameter("id");
        String bidString = request.getParameter("amount");

        BigDecimal bidAmount = new BigDecimal(0);
        if (bidString != null) {
            try {
                bidAmount = new BigDecimal(bidString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        RequestDispatcher dispatcher;
        if (!(user instanceof StandardUser)) {
            // not a standard user
            response.setStatus(user == null ? 401 : 403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        // System.out.println("Verified standard user");

        Connection conn = null;
        try {
            conn = DB.connect();
            conn.setAutoCommit(false);
            Listing listing = ListingMapper.fetch(UUID.fromString(listingID), true, conn);
            // System.out.println("Fetched listing");
            if (!(listing instanceof AuctionListing) || !listing.isActive()) {
                // not an auction listing
                response.setStatus(400);
                dispatcher = request.getRequestDispatcher("error.jsp");
                dispatcher.forward(request, response);
                return;
            }
            // System.out.println("Verified is auction listing");

            if (((AuctionListing) listing).getHighestBid() != null) {
                if (bidAmount.compareTo(((AuctionListing) listing).getHighestBid().getAmount()) <= 0) {
                    // not highest bid
                    response.setStatus(400);
                    dispatcher = request.getRequestDispatcher("error.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            }
            // System.out.println("Verified new highest bid");

            boolean isSeller = false;
            if (listing.getOwner().getId().equals(user.getId())) {
                // is owner
                isSeller = true;
            } else {
                for (StandardUser coSeller : listing.getCoSellers()) {
                    if (coSeller.getId().equals(user.getId())) {
                        isSeller = true;
                        break;
                    }
                }
            }
            if (isSeller) {
                // is a seller, cannot bid
                response.setStatus(403);
                dispatcher = request.getRequestDispatcher("error.jsp");
                dispatcher.forward(request, response);
                return;
            }
            // System.out.println("Verified not seller");

            Bid bid = new Bid(bidAmount, (StandardUser) user);
            BidMapper.insert(bid, listing.getId(), conn);
            conn.commit();
            // System.out.println("Inserted Bid into DB");
            response.sendRedirect("listing?id=" + listingID);
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    // System.out.println("Transaction has been rolled back");
                }
            } catch (SQLException excep) {
                // System.out.println(excep.getMessage());
            }
            response.setStatus(400);
            response.sendRedirect("listing?id=" + listingID);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
    }
}
