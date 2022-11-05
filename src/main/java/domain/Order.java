package domain;

import datasource.OrderMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Order {
    private final UUID id;
    private boolean isCancelled;
    private final OffsetDateTime date;
    private int quantity;
    private final StandardUser buyer;
    private final Listing listing;
    private ShippingDetails shippingDetails;
    private int version;

    // Constructor for creating a new order
    public Order(int quantity, StandardUser buyer, Listing listing, ShippingDetails shippingDetails) {
        this.id = UUID.randomUUID();
        this.isCancelled = false;
        this.date = OffsetDateTime.now();
        this.quantity = quantity;
        this.buyer = buyer;
        this.listing = listing;
        this.shippingDetails = shippingDetails;
        this.version = 1;
    }

    // Constructor for loading an existing order
    public Order(UUID id, boolean isCancelled, OffsetDateTime date, int quantity, StandardUser buyer, Listing listing,
                 ShippingDetails shippingDetails, int version) {
        this.id = id;
        this.isCancelled = isCancelled;
        this.date = date;
        this.quantity = quantity;
        this.buyer = buyer;
        this.listing = listing;
        this.shippingDetails = shippingDetails;
        this.version = version;
    }

    // Constructor for lazy load
    public Order(UUID id, boolean isCancelled, OffsetDateTime date, int quantity, StandardUser buyer, Listing listing,
                 int version) {
        this.id = id;
        this.isCancelled = isCancelled;
        this.date = date;
        this.quantity = quantity;
        this.buyer = buyer;
        this.listing = listing;
        this.shippingDetails = null;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public StandardUser getBuyer() {
        return buyer;
    }

    public Listing getListing() {
        return listing;
    }

    public ShippingDetails getShippingDetails() {
        if (shippingDetails == null) {
            Order order = OrderMapper.fetch(id);
            shippingDetails = order.getShippingDetails();
        }
        return shippingDetails;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setShippingDetails(ShippingDetails shippingDetails) {
        this.shippingDetails = shippingDetails;
    }

    public int getVersion() {
        return this.version;
    }
}
