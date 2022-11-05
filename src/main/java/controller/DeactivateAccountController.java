package controller;

import datasource.UserMapper;
import datasource.ListingMapper;
import domain.Admin;
import domain.Listing;
import domain.StandardUser;
import domain.User;
import util.AuthenticationEnforcer;
import util.UnitOfWork;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "DeactivateAccountController", value = "/deactivate-account")
public class DeactivateAccountController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        if (!(user instanceof Admin)) {
            // not an admin, show 403 page
            if (user == null) {
                response.setStatus(401);
                request.setAttribute("errorMessage", "Unauthorised");
            } else {
                response.setStatus(403);
                request.setAttribute("errorMessage", "Access Denied");
            }
            return;
        }
        String userID = request.getParameter("id");
        StandardUser userToDeactivate = (StandardUser) UserMapper.fetch(UUID.fromString(userID));
        if (userToDeactivate == null) {
            // not a valid user
            response.setStatus(400);
            request.setAttribute("errorMessage", "User ID Invalid");
            return;
        }

        ArrayList<Listing> listings = ListingMapper.fetchAll(true);

        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        if (listings != null) {
            for (Listing l : listings) {
                if (l.getOwner().getId().equals(userToDeactivate.getId())) {
                    // deactivate this listing
                    uow.registerDeleted(l);
                } else {
                    for (int i = 0; i < l.getCoSellers().size(); i++) {
                        if (l.getCoSellers().get(i).getId().equals(userToDeactivate.getId())) {
                            // remove this user
                            l.getCoSellers().remove(i);
                            uow.registerDirty(l);
                            break;
                        }
                    }
                }
            }
        }

        uow.registerDeleted(userToDeactivate);
        try {
            uow.commit();
            // System.out.println("Successfully deactivated user " + userToDeactivate.getId().toString());
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }

        response.sendRedirect("all-accounts"); // TODO: update
    }
}
