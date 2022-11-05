package controller;

import datasource.ListingMapper;
import domain.Listing;
import domain.StandardUser;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "HomepageController", value = {"", "/index", "/search"})
public class HomepageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET and POST do the same thing for this page
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("searchQuery");
        String searchBy = request.getParameter("searchBy");
        ArrayList<Listing> listings = ListingMapper.fetchAll(true);
        ArrayList<Listing> filteredListings = new ArrayList<>();
        if (listings != null) {
            if (searchQuery != null && !searchQuery.equals("")) {
                if (searchBy == null || searchBy.equals("listing")) {
                    // search by listing
                    // filter listings
                    for (Listing l : listings) {
                        if (l.getTitle().toUpperCase().contains(searchQuery.toUpperCase())) {
                            filteredListings.add(l);
                        }
                    }
                } else {
                    // search by user
                    // filter listings
                    for (Listing l : listings) {
                        if (l.getOwner().getUsername().toUpperCase().contains(searchQuery.toUpperCase())) {
                            filteredListings.add(l);
                        } else {
                            for (StandardUser u : l.getCoSellers()) {
                                if (u.getUsername().toUpperCase().contains(searchQuery.toUpperCase())) {
                                    filteredListings.add(l);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                filteredListings.addAll(listings);
            }
        }
        request.setAttribute("listings", filteredListings);
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    }
}
