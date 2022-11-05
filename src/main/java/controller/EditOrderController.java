package controller;

import datasource.OrderMapper;
import domain.*;
import util.AuthenticationEnforcer;
import util.UnitOfWork;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "EditOrderController", value = "/edit-order")
public class EditOrderController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String orderID = request.getParameter("id");
        RequestDispatcher dispatcher;

        if (orderID == null) {
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        Order order = OrderMapper.fetch(UUID.fromString(orderID));
        if (order == null || order.isCancelled()) {
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (!(user instanceof StandardUser)) {
            // not a standard user
            response.setStatus(user == null ? 401 : 403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        boolean isBuyer = user.getId().equals(order.getBuyer().getId());
        boolean isSeller = user.getId().equals(order.getListing().getOwner().getId());
        if (!isSeller) {
            for (StandardUser coSeller : order.getListing().getCoSellers()) {
                if (coSeller.getId().equals(user.getId())) {
                    isSeller = true;
                    break;
                }
            }
        }

        if (!isSeller && !isBuyer) {
            // neither a buyer or seller
            response.setStatus(403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        request.setAttribute("order", order);
        dispatcher = request.getRequestDispatcher("modifyOrder.jsp");
        dispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String orderID = request.getParameter("id");
        int version = Integer.parseInt(request.getParameter("version"));
        RequestDispatcher dispatcher;

        if (orderID == null) {
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        Order order = OrderMapper.fetch(UUID.fromString(orderID));
        if (order == null || !(order.getListing() instanceof FixedPriceListing)) {
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        order.setVersion(version);

        String amountString = request.getParameter("quantity");
        int amount = 0;
        if (amountString != null) {
            try {
                amount = Integer.parseInt(amountString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (!(user instanceof StandardUser)) {
            // not a standard user
            response.setStatus(user == null ? 401 : 403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        boolean isBuyer = user.getId().equals(order.getBuyer().getId());
        boolean isSeller = user.getId().equals(order.getListing().getOwner().getId());
        if (!isSeller) {
            for (StandardUser coSeller : order.getListing().getCoSellers()) {
                if (coSeller.getId().equals(user.getId())) {
                    isSeller = true;
                    break;
                }
            }
        }

        if (!isSeller && !isBuyer) {
            // neither a buyer nor seller
            response.setStatus(403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request,response);
            return;
        }

        if (amount <= 0) {
            // not a fixed-price listing or amount not specified
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (!order.getListing().isActive() || (
                order.getListing() instanceof FixedPriceListing
                        && amount > ((FixedPriceListing) order.getListing()).getStock())
        ) {
            // listing has expired or doesn't have enough stock
            request.setAttribute("errorMessage", "Listing has expired");
            // System.out.println("Attempting to update order for inactive listing, or amount too high");
            if (order.getListing() instanceof FixedPriceListing) {
                // System.out.println("Remaining stock: " + ((FixedPriceListing) order.getListing()).getStock());
                // System.out.println("Tried to set: " + amount);
            }
            response.setStatus(400);
            response.sendRedirect("index");
            return;
        }

        // System.out.println("Preparing order update...");
        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        ((FixedPriceListing) order.getListing()).setStock(
                ((FixedPriceListing) order.getListing()).getStock()
                + order.getQuantity() - amount
        );
        uow.registerDirty(order.getListing());

        if (isBuyer) {
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
                dispatcher.forward(request, response);
                return;
            }
            order.getShippingDetails().setFirstName(firstName);
            order.getShippingDetails().setLastName(lastName);
            order.getShippingDetails().setPostcode(postcode);
            order.getShippingDetails().setPhoneNumber(phone);
            order.getShippingDetails().setState(state);
            order.getShippingDetails().setStreet(street);
            order.getShippingDetails().setCountry(country);
            order.getShippingDetails().setCity(city);
            uow.registerDirty(order.getShippingDetails());
        }

        order.setQuantity(amount);
        uow.registerDirty(order);

        try {
            uow.commit();
            // System.out.println("Successfully updated order " + order.getId().toString());
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }
        response.sendRedirect(isBuyer ? "purchases" : "sales");
    }
}
