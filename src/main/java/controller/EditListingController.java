package controller;

import controller.utils.ListingUtil;
import datasource.ListingMapper;
import datasource.UserMapper;
import domain.*;
import org.springframework.dao.ConcurrencyFailureException;
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
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "EditListingController", value = "/edit-listing")
public class EditListingController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String listingID = request.getParameter("id");
        Listing listing = null;
        boolean success = false;

        RequestDispatcher dispatcher;
        if (user == null) {
            response.setStatus(401);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (listingID != null) {
            listing = ListingMapper.fetch(UUID.fromString(listingID));
            if (listing == null) {
                // listing not found
                response.setStatus(404);
            } else {
                // listing found, check if user can edit
                if (ListingUtil.checkCanEditListing(user, listing, response)) {
                    // user has access (active seller)
                    success = true;
                }
            }
        } else {
            // listing ID not provided
            response.setStatus(400);
        }

        if (success) {
            // TODO: use editing pages for Listings
            ArrayList<String> userEmails = UserMapper.fetchAllEmails(true, true);
            for (int i = userEmails.size() - 1; i >= 0; i--) {
                if (userEmails.get(i).equals(user.getEmail())) {
                    userEmails.remove(i);
                }
            }
            request.setAttribute("user", user);
            request.setAttribute("userEmails", userEmails);
            request.setAttribute("listing", listing);
            dispatcher = request.getRequestDispatcher(listing instanceof FixedPriceListing
                    ? "modifyFixedPriceListing.jsp" : "modifyAuctionListing.jsp");
        } else {
            dispatcher = request.getRequestDispatcher("error.jsp");
        }
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String listingID = request.getParameter("id");
        int version = Integer.parseInt(request.getParameter("version"));
        Listing listing = null;
        int result = 200;

        if (listingID != null) {
            // System.out.println("ID okay");
            // System.out.println(UUID.fromString(listingID));
            listing = ListingMapper.fetch(UUID.fromString(listingID));
            // System.out.println("Fetched listing");
            if (listing == null) {
                // listing not found
                // System.out.println("Error: no listing found for id " + listingID);
                response.setStatus(404);
            } else {
                // listing found, check if user can edit
                listing.setVersion(version);
                if (ListingUtil.checkCanEditListing(user, listing, response)) {
                    // user has access (active seller)
                    // System.out.println("User can edit the listing");
                    if (listing instanceof AuctionListing) {
                        if (ListingUtil.verifyValidAuctionListingInputs(request)) {
                            result = updateAuctionListing(request, response,
                                    (AuctionListing) listing, (StandardUser) user);
                        } else {
                            result = 400;
                        }
                    } else if (listing instanceof FixedPriceListing) {
                        if (ListingUtil.verifyValidFixedPriceListingInputs(request)) {
                            result = updateFixedPriceListing(request, response,
                                    (FixedPriceListing) listing, (StandardUser) user);
                        } else {
                            result = 400;
                        }
                    }
                }
            }
        } else {
            // listing ID not provided
            result = 400;
        }

        if (result == 200 && listing != null) {
            response.sendRedirect("listing?id=" + listing.getId().toString());
        } else {
            // some error occurred
            response.setStatus(result);
            if (listing != null) {
                response.sendRedirect("edit-listing?id=" + listing.getId().toString());
            } else {
                RequestDispatcher dispatcher;
                dispatcher = request.getRequestDispatcher("error.jsp");
                dispatcher.forward(request, response);
            }
        }
    }

    private int updateFixedPriceListing(
            HttpServletRequest request, HttpServletResponse response,
            FixedPriceListing listing, StandardUser user) {
        listing.setPrice(new BigDecimal(request.getParameter("price")));
        listing.setStock(Integer.parseInt(request.getParameter("stock")));
        return updateCommonListingFields(request, response, listing, user);
    }

    private int updateAuctionListing(
            HttpServletRequest request, HttpServletResponse response,
            AuctionListing listing, StandardUser user) {
        listing.setStartPrice(new BigDecimal(request.getParameter("startPrice")));
        listing.setStartTime(ListingUtil.parseHTMLDateTimeLocal(request.getParameter("startTime")));
        listing.setEndTime(ListingUtil.parseHTMLDateTimeLocal(request.getParameter("endTime")));
        return updateCommonListingFields(request, response, listing, user);
    }

    private int updateCommonListingFields(
            HttpServletRequest request, HttpServletResponse response,
            Listing listing, StandardUser user) {

        listing.setCategory(Listing.Category.valueOf(request.getParameter("category")));
        listing.setCondition(Listing.Condition.valueOf(request.getParameter("condition")));
        listing.setDescription(request.getParameter("description"));
        listing.setTitle(request.getParameter("title"));

        String[] coSellerEmails = request.getParameterValues("coSeller");

        if (user.getId().equals(listing.getOwner().getId())) {
            // only owner can edit the co-sellers

            ArrayList<StandardUser> coSellers = new ArrayList<>();
            if (coSellerEmails != null) {
                for (String coSellerEmail : coSellerEmails) {
                    StandardUser coSeller = (StandardUser) UserMapper.fetchWithEmail(coSellerEmail);
                    if (coSeller == null) {
                        return 404;
                    }
                    coSellers.add(coSeller);
                }
            }
            listing.setCoSellers(coSellers);
        } else if (coSellerEmails != null && coSellerEmails.length > 0) {
            // non-owner trying to update cosellers
            return 400;
        }

        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        uow.registerDirty(listing);

        try {
            uow.commit();
            return 200;
        } catch (ConcurrencyFailureException e) {
            e.printStackTrace();
            return 409;
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }
}
