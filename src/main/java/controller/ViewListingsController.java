package controller;

import datasource.ListingMapper;
import domain.Listing;
import domain.StandardUser;
import domain.User;
import util.AuthenticationEnforcer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "ViewListingsController", value = {"/listings", "/all-listings"})
public class ViewListingsController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (user == null) {
            // not a user
            response.setStatus(401);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        ArrayList<Listing> listings;
        if (user instanceof StandardUser) {
            // standard user, get the listings for this user
            listings = ListingMapper.fetchWithSellerId(user.getId(), true);
            dispatcher = request.getRequestDispatcher("viewListings.jsp");
        } else {
            listings = ListingMapper.fetchAll(false);
            dispatcher = request.getRequestDispatcher("adminViewListing.jsp");
        }

        request.setAttribute("listings", listings);
        dispatcher.forward(request, response);
    }
}
