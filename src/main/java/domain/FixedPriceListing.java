package domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class FixedPriceListing extends Listing {

    private BigDecimal price;
    private int stock;

    // Constructor for creating a new fixed price listing
    public FixedPriceListing(StandardUser owner, String title, String description, Category category,
                             Condition condition, BigDecimal price, int stock) {
        super(owner, title, description, category, condition);
        this.price = price;
        this.stock = stock;
    }

    // Constructor for loading an existing fixed price listing
    public FixedPriceListing(UUID id, boolean isActive, OffsetDateTime dateListed, StandardUser owner,
                             ArrayList<StandardUser> coSellers, String title, String description, Category category,
                             Condition condition, BigDecimal price, int stock, int version) {
        super(id, isActive, dateListed, owner, coSellers, title, description, category, condition, version);
        this.price = price;
        this.stock = stock;
    }

    // Constructor for lazy load
    public FixedPriceListing(UUID id, boolean isActive, OffsetDateTime dateListed, StandardUser owner, String title,
                             BigDecimal price, int stock, int version) {
        super(id, isActive, dateListed, owner, null, title, null, null, null, version);
        this.price = price;
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
