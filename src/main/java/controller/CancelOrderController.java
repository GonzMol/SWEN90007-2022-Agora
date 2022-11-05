package controller;

import datasource.OrderMapper;
import domain.*;
import util.AuthenticationEnforcer;
import util.UnitOfWork;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.ConcurrencyFailureException;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "CancelOrderController", value = "/cancel-order")
public class CancelOrderController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String orderID = request.getParameter("id");
        Order order = OrderMapper.fetch(UUID.fromString(orderID));
        // System.out.println("Order found?");
        if (order == null) {
            // not a valid listing
            response.setStatus(400);
            request.setAttribute("errorMessage", "Listing ID invalid");
            return;
        }
        // System.out.println("Order found!");
        if (!(user instanceof StandardUser)) {
            // not an admin, show error page
            if (user == null) {
                response.setStatus(401);
                request.setAttribute("errorMessage", "Unauthorised");
            } else {
                response.setStatus(403);
                request.setAttribute("errorMessage", "Access Denied");
            }
            return;
        }

        boolean isSeller = order.getListing().getOwner().getId().equals(user.getId());
        if (!isSeller) {
            for (StandardUser coSeller : order.getListing().getCoSellers()) {
                if (coSeller.getId().equals(user.getId())) {
                    isSeller = true;
                    break;
                }
            }
        }

        if (!order.getBuyer().getId().equals(user.getId()) && !isSeller) {
            // wrong user for order
            response.setStatus(403);
            request.setAttribute("errorMessage", "Access Denied");
            return;
        }

        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        if (order.getListing() instanceof FixedPriceListing) {
            // System.out.println("Updating stock for fixed price listing");
            FixedPriceListing listing = (FixedPriceListing) order.getListing();
            listing.setStock(listing.getStock() + order.getQuantity());
            uow.registerDirty(listing);
        }
        uow.registerDeleted(order);

        try {
            uow.commit();
            response.sendRedirect(!isSeller ? "purchases" : "sales");
        } catch (ConcurrencyFailureException e) {
            response.setStatus(409);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}
