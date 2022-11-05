package controller;

import datasource.ListingMapper;
import domain.*;
import util.AuthenticationEnforcer;
import util.UnitOfWork;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "CheckoutController", value = "/checkout")
public class CheckoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String amountString = request.getParameter("quantity");
        int amount = 0;
        if (amountString != null) {
            try {
                amount = Integer.parseInt(amountString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        RequestDispatcher dispatcher;
        if (!(user instanceof StandardUser)) {
            // not a standard user
            response.setStatus(user == null ? 401 : 403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        String listingID = request.getParameter("id");
        Listing listing = ListingMapper.fetch(UUID.fromString(listingID));
        if (!(listing instanceof FixedPriceListing) || amount <= 0) {
            // not a fixed-price listing or amount not specified
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (!listing.isActive()) {
            // listing has expired
            request.setAttribute("errorMessage", "Listing has expired");
            response.setStatus(403);
            response.sendRedirect("index");
            return;
        }

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
            // is a seller, cannot checkout
            response.setStatus(403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }


        request.setAttribute("shippingDetails", ((StandardUser) user).getShippingDetails());
        dispatcher = request.getRequestDispatcher("checkout.jsp");
        dispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String amountString = request.getParameter("quantity");
        int amount = 0;
        if (amountString != null) {
            try {
                amount = Integer.parseInt(amountString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        RequestDispatcher dispatcher;
        if (!(user instanceof StandardUser)) {
            // not a standard user
            response.setStatus(user == null ? 401 : 403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        String listingID = request.getParameter("id");
        Listing listing = ListingMapper.fetch(UUID.fromString(listingID));
        if (!(listing instanceof FixedPriceListing) || amount <= 0) {
            // not a fixed-price listing or amount not specified
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (!listing.isActive() || amount > ((FixedPriceListing) listing).getStock()) {
            // listing has expired or doesn't have enough stock
            request.setAttribute("errorMessage", "Listing has expired");
            response.setStatus(400);
            response.sendRedirect("index");
            return;
        }

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
            // is a seller, cannot checkout
            response.setStatus(403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        // shipping details for Order
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phoneNumber");
        String street = request.getParameter("street");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String country = request.getParameter("country");
        String postcode = request.getParameter("postcode");

        if (!(firstName != null && lastName != null && phone != null
                && street != null && city != null && state != null
                && country != null && postcode != null
        )) {
            // shipping details invalid
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        ((FixedPriceListing) listing).setStock(((FixedPriceListing) listing).getStock() - amount);
        uow.registerDirty(listing);

        Order order = new Order(amount, (StandardUser) user, listing, new ShippingDetails(
                firstName, lastName, phone, country, street, city, state, postcode
        ));
        uow.registerNew(order.getShippingDetails());
        uow.registerNew(order);

        try {
            uow.commit();
            // System.out.println("Successfully placed order on " + listing.getId().toString());
            response.sendRedirect("purchases");
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            request.setAttribute("errorMessage", "Could not place order");
            response.sendRedirect("listing?id=" + listingID);
        }
    }
}