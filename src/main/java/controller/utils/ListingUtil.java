package controller.utils;

import datasource.UserMapper;
import domain.Admin;
import domain.Listing;
import domain.StandardUser;
import domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class ListingUtil {

    public static boolean checkCanCreateListing() {

        return true;
    }

    public static boolean checkCanEditListing(User user, Listing listing, HttpServletResponse response) {
        if (user == null) {
            // not logged in
            response.setStatus(401);
            return false;
        }
        if (user instanceof Admin) {
            // reject admin
            response.setStatus(403);
            return false;
        }
        if (!(((StandardUser) user).isActive())) {
            // cannot act as seller
            response.setStatus(403);
            return false;
        }
        if (user.getId().equals(listing.getOwner().getId())) {
            // owner
            return true;
        }
        for (StandardUser coSeller : listing.getCoSellers()) {
            if (coSeller.getId().equals(user.getId())) {
                // co-seller
                return true;
            }
        }
        // user cannot sell this listing
        response.setStatus(403);
        return false;
    }

    public static boolean verifyValidFixedPriceListingInputs(HttpServletRequest request) {
        if (checkInvalidBasicInputDetails(request)) {
            return false;
        }
        // check price
        if (request.getParameter("price") != null) {
            try {
                new BigDecimal(request.getParameter("price"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        // check stock
        if (request.getParameter("stock") != null) {
            try {
                Integer.parseInt(request.getParameter("stock"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static OffsetDateTime parseHTMLDateTimeLocal(String htmlDateTimeLocal) {
        return OffsetDateTime.of(
                LocalDateTime.parse(htmlDateTimeLocal),
                OffsetDateTime.now().getOffset()
        );
    }

    public static boolean verifyValidAuctionListingInputs(HttpServletRequest request) {
        if (checkInvalidBasicInputDetails(request)) {
            return false;
        }
        // check start price and end price
        if (request.getParameter("startPrice") != null) {
            try {
                new BigDecimal(request.getParameter("startPrice"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        // check start and end times
        if (request.getParameter("startTime") != null && request.getParameter("endTime") != null) {
            try {
                // System.out.println("Attempting to parse times...");
                parseHTMLDateTimeLocal(request.getParameter("startTime"));
                parseHTMLDateTimeLocal(request.getParameter("endTime"));
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean checkInvalidBasicInputDetails(HttpServletRequest request) {
        // check through the generic Listing inputs to see if they're valid

        // check co-sellers exist and are active; this one can be null
        String[] coSellerIDs = request.getParameterValues("coSellers");
        if (coSellerIDs != null) {
            for (String id : coSellerIDs) {
                StandardUser coSeller = (StandardUser) UserMapper.fetch(UUID.fromString(id));
                if (coSeller == null) {
                    // ID not found
                    return true;
                }
                if (!coSeller.isActive()) {
                    // co-seller is inactive
                    return true;
                }
            }
        }

        // check title
        if (request.getParameter("title") == null) {
            return true;
        }
        // check description
        if (request.getParameter("description") == null) {
            return true;
        }

        // check category and condition
        try {
            Listing.Category.valueOf(request.getParameter("category"));
            Listing.Condition.valueOf(request.getParameter("condition"));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

}
