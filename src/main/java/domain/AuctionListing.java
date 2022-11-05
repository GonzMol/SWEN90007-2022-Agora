package domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class AuctionListing extends Listing {
    private BigDecimal startPrice;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private final ArrayList<Bid> bids;

    // Constructor for creating a new auction listing
    public AuctionListing(StandardUser owner, String title, String description, Category category, Condition condition,
                          BigDecimal startPrice, OffsetDateTime startTime, OffsetDateTime endTime) {
        super(owner, title, description, category, condition);
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bids = new ArrayList<>();
    }

    // Constructor for loading an existing auction listing
    public AuctionListing(UUID id, boolean isActive, OffsetDateTime dateListed, StandardUser owner,
                          ArrayList<StandardUser> coSellers, ArrayList<Bid> bids, String title, String description,
                          Category category, Condition condition, BigDecimal startPrice, OffsetDateTime startTime,
                          OffsetDateTime endTime, int version) {
        super(id, isActive, dateListed, owner, coSellers, title, description, category, condition, version);
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bids = bids;
    }

    // Constructor for lazy load
    public AuctionListing(UUID id, boolean isActive, OffsetDateTime dateListed, StandardUser owner, ArrayList<Bid> bids,
                          String title, BigDecimal startPrice, OffsetDateTime startTime, OffsetDateTime endTime,
                          int version) {
        super(id, isActive, dateListed, owner, null, title, null, null, null,
                version);
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bids = bids;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public Bid getHighestBid() {
        if (bids.isEmpty()) {
            return null;
        } else {
            bids.sort(Comparator.comparing(Bid::getAmount));
            return bids.get(bids.size() - 1);
        }
    }
}
