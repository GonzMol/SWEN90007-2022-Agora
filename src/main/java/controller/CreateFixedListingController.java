package controller;

import controller.utils.ListingUtil;
import datasource.DB;
import datasource.ListingMapper;
import datasource.UserMapper;
import domain.FixedPriceListing;
import domain.Listing;
import domain.StandardUser;
import domain.User;
import util.AuthenticationEnforcer;
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
import java.util.ArrayList;

@WebServlet(name = "CreateFixedListingController", value = "/create-fixed-price-listing")
public class CreateFixedListingController extends HttpServlet {

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
            RequestDispatcher dispatcher = request.getRequestDispatcher("createListing.jsp");
            dispatcher.forward(request, response);
        } else {
            // not a standard user
            if (user != null) {
                // non-standard user or not allowed to create listings
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

        boolean success = false;

        if (ListingUtil.verifyValidFixedPriceListingInputs(request)) {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            Listing.Category category = Listing.Category.valueOf(request.getParameter("category"));
            Listing.Condition condition = Listing.Condition.valueOf(request.getParameter("condition"));
            String[] coSellerEmails = request.getParameterValues("coSeller");
            int stock = Integer.parseInt(request.getParameter("stock"));
            FixedPriceListing listing = new FixedPriceListing(
                    (StandardUser) user, title, description, category, condition, price, stock
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
            response.sendRedirect("listings");
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("createListing.jsp");
            dispatcher.forward(request, response);
        }
    }
}
