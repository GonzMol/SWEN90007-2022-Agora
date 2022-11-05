package controller;

import datasource.ListingMapper;
import domain.Admin;
import domain.Listing;
import domain.User;
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

@WebServlet(name = "DeactivateListingController", value = "/deactivate-listing")
public class DeactivateListingController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String listingID = request.getParameter("id");
        Listing listing = ListingMapper.fetch(UUID.fromString(listingID));
        if (listing == null) {
            // not a valid listing
            response.setStatus(400);
            request.setAttribute("errorMessage", "Listing ID invalid");
            return;
        }
        if (!(user instanceof Admin)) {
            // not an admin, show error page
            if (user == null) {
                response.setStatus(401);
                request.setAttribute("errorMessage", "Unauthorised");
                return;
            } else if (!user.getId().equals(listing.getOwner().getId())) {
                response.setStatus(403);
                request.setAttribute("errorMessage", "Access Denied");
                return;
            }
        }

        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        uow.registerDeleted(listing);

        try {
            uow.commit();
        } catch (ConcurrencyFailureException e) {
            // failed to update due to conflict
            response.setStatus(409);
            request.setAttribute("errorMessage", "Failed to deactivate the listing");
        } catch (Exception e) {
            // generic exception
            response.setStatus(500);
            e.printStackTrace();
        }
        response.sendRedirect("listings"); // TODO: update
    }
}
