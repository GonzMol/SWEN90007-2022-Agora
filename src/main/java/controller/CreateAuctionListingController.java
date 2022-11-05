package controller;

import controller.utils.ListingUtil;
import datasource.DB;
import datasource.ListingMapper;
import datasource.UserMapper;
import domain.AuctionListing;
import domain.Listing;
import domain.StandardUser;
import domain.User;
import util.AuthenticationEnforcer;
import util.BackgroundAuctionManager;
import util.UnitOfWork;

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
import java.time.OffsetDateTime;
import java.util.ArrayList;

@WebServlet(name = "CreateAuctionListingController", value = "/create-auction-listing")
public class CreateAuctionListingController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // check logged in as user
        User user = AuthenticationEnforcer.getUserFromSession(request);
        if (user instanceof StandardUser && ((StandardUser) user).isActive()) {
            // standard user
            ArrayList<String> userEmails = UserMapper.fetchAllEmails(true, true);
            for (int i = userEmails.size() - 1; i >= 0; i--) {
                if (userEmails.get(i).equals(user.getEmail())) {
                    userEmails.remove(i);
                }
            }
            request.setAttribute("user", user);
            request.setAttribute("userEmails", userEmails);
            RequestDispatcher dispatcher = request.getRequestDispatcher("createAuction.jsp");
            dispatcher.forward(request, response);
        } else {
            // not a standard user or not allowed to sell
            if (user != null) {
                // non-standard user
                response.setStatus(403);
            } else {
                // not logged in
                response.setStatus(401);
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        if (!(user instanceof StandardUser)) {
            // not a standard user, do nothing
            // System.out.println("User not logged in or not a Standard user!");
            if (user != null) {
                // non-standard user
                response.setStatus(403);
            } else {
                // not logged in
                response.setStatus(401);
            }
            return;
        }

        AuctionListing listing = null;
        boolean success = false;
        if (ListingUtil.verifyValidAuctionListingInputs(request)) {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            BigDecimal startPrice = new BigDecimal(request.getParameter("startPrice"));
            Listing.Category category = Listing.Category.valueOf(request.getParameter("category"));
            Listing.Condition condition = Listing.Condition.valueOf(request.getParameter("condition"));
            OffsetDateTime startTime = ListingUtil.parseHTMLDateTimeLocal(request.getParameter("startTime"));
            OffsetDateTime endTime = ListingUtil.parseHTMLDateTimeLocal(request.getParameter("endTime"));
            String[] coSellerEmails = request.getParameterValues("coSeller");
            listing = new AuctionListing(
                    (StandardUser) user, title, description, category,
                    condition, startPrice, startTime, endTime
            );

            ArrayList<StandardUser> coSellers = new ArrayList<>();
            if (coSellerEmails != null) {
                for (String coSellerEmail : coSellerEmails) {
                    coSellers.add((StandardUser) UserMapper.fetchWithEmail(coSellerEmail));
                }
            }
            listing.setCoSellers(coSellers);

            UnitOfWork.newCurrent();
            UnitOfWork uow = UnitOfWork.getCurrent();

            uow.registerNew(listing);

            try {
                uow.commit();
                success = true;
            } catch (Exception e) {
                // System.out.println(e.getMessage());
                request.setAttribute("errorMessage", "An error occurred creating the listing, please try again");
                response.setStatus(500);
            }
        } else {
            request.setAttribute("errorMessage", "Error: not all fields entered!");
            // System.out.println("Error: missing data in some fields");
            response.setStatus(400);
        }

        if (success) {
            BackgroundAuctionManager.newAuction(listing);
            response.sendRedirect("listings");
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("createAuction.jsp");
            dispatcher.forward(request, response);
        }
    }
}
