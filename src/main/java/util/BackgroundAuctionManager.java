package util;

import datasource.ListingMapper;
import domain.*;
import util.UnitOfWork;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.*;

@WebListener
public class BackgroundAuctionManager implements ServletContextListener {
    private static ScheduledExecutorService executor;
    private static HashMap<UUID, ScheduledFuture<?>> schedule;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        executor = Executors.newSingleThreadScheduledExecutor();
        schedule = new HashMap<>();
        loadExistingAuctions();
        // System.out.println("Scheduler created");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        executor.shutdownNow();
        // System.out.println("Scheduler shutdown");
    }

    private void loadExistingAuctions() {
        ArrayList<Listing> activeListings = ListingMapper.fetchAll(true);
        if (activeListings != null) {
            for (Listing listing : activeListings) {
                if (listing instanceof AuctionListing) {
                    newAuction((AuctionListing) listing);
                }
            }
        }
    }

    public static void newAuction(AuctionListing auctionListing) throws RejectedExecutionException {
        OffsetDateTime currTime = OffsetDateTime.now();
        OffsetDateTime endTime = auctionListing.getEndTime();
        if (endTime.isAfter(currTime)) {
            // Schedule end auction task
            long delay = endTime.toEpochSecond() - currTime.toEpochSecond();
            ScheduledFuture<?> task = executor.schedule(() -> endAuction(auctionListing.getId()), delay,
                    TimeUnit.SECONDS);
            schedule.put(auctionListing.getId(), task);
            // System.out.println("Successfully scheduled new end auction task");
        } else {
            // Auction has already ended
            endAuction(auctionListing);
        }
    }

    public static void updateAuction(AuctionListing auctionListing) throws RejectedExecutionException {
        UUID id = auctionListing.getId();
        ScheduledFuture<?> task = schedule.remove(id);
        if (task != null) {
            boolean cancelResult = task.cancel(false);
            if (cancelResult) {
                // System.out.println("Successfully cancelled old end auction task");
            } else {
                // System.out.println("Scheduled end auction task is already running or completed");
            }
        }
        if (auctionListing.isActive()) {
            newAuction(auctionListing);
        }
    }

    private static void endAuction(UUID auctionListingId) {
        Listing auctionListing = ListingMapper.fetch(auctionListingId);
        if (auctionListing != null) {
            endAuction((AuctionListing) auctionListing);
        }
        schedule.remove(auctionListingId);
    }

    private static void endAuction(AuctionListing auctionListing) {
        // Initialise unit of work
        UnitOfWork.newCurrent();
        UnitOfWork uow = UnitOfWork.getCurrent();

        // Set auction listing to inactive
        auctionListing.setActive(false);
        uow.registerDirty(auctionListing);

        Bid highestBid = auctionListing.getHighestBid();
        if (highestBid != null) {
            // Create shipping details for the order
            StandardUser buyer = highestBid.getUser();
            ShippingDetails buyerShippingDetails = buyer.getShippingDetails();
            ShippingDetails orderShippingDetails = new ShippingDetails(
                    buyerShippingDetails.getFirstName(),
                    buyerShippingDetails.getLastName(),
                    buyerShippingDetails.getPhoneNumber(),
                    buyerShippingDetails.getCountry(),
                    buyerShippingDetails.getStreet(),
                    buyerShippingDetails.getCity(),
                    buyerShippingDetails.getState(),
                    buyerShippingDetails.getPostcode()
            );
            uow.registerNew(orderShippingDetails);

            // Create order
            Order order = new Order(1, buyer, auctionListing, orderShippingDetails);
            uow.registerNew(order);
        }

        // Commit all changes
        try {
            uow.commit();
            // System.out.println("Successfully ended auction " + auctionListing.getId().toString());
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }
    }
}
