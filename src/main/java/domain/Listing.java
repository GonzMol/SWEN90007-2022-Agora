package domain;

import datasource.ListingMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Listing {
    public enum Category {
        ELECTRONICS,
        HOME_AND_GARDEN,
        CLOTHING_AND_ACCESSORIES,
        ENTERTAINMENT,
        HEALTH_AND_BEAUTY,
        SPORTS,
        TOYS,
        PETS,
        OTHER
    }

    public enum Condition {
        NEW,
        USED
    }

    private final UUID id;
    private boolean isActive;
    private final OffsetDateTime dateListed;
    private final StandardUser owner;
    private ArrayList<StandardUser> coSellers;
    private String title;
    private String description;
    private Category category;
    private Condition condition;
    private int version;

    // Constructor for creating a new listing
    public Listing(StandardUser owner, String title, String description, Category category, Condition condition) {
        this.id = UUID.randomUUID();
        this.isActive = true;
        this.dateListed = OffsetDateTime.now();
        this.owner = owner;
        this.coSellers = new ArrayList<>();
        this.title = title;
        this.description = description;
        this.category = category;
        this.condition = condition;
        this.version = 1;
    }

    // Constructor for loading an existing listing
    public Listing(UUID id, boolean isActive, OffsetDateTime dateListed, StandardUser owner,
                   ArrayList<StandardUser> coSellers, String title, String description, Category category,
                   Condition condition, int version) {
        this.id = id;
        this.isActive = isActive;
        this.dateListed = dateListed;
        this.owner = owner;
        this.coSellers = coSellers;
        this.title = title;
        this.description = description;
        this.category = category;
        this.condition = condition;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public OffsetDateTime getDateListed() {
        return dateListed;
    }

    public StandardUser getOwner() {
        return owner;
    }

    public ArrayList<StandardUser> getCoSellers() {
        if (coSellers == null) {
            load();
        }
        return coSellers;
    }

    public void setCoSellers(ArrayList<StandardUser> coSellers) {
        this.coSellers = coSellers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if (description == null) {
            load();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        if (category == null) {
            load();
        }
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Condition getCondition() {
        if (condition == null) {
            load();
        }
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public void load() {
        Listing listing = ListingMapper.fetch(id);
        if (coSellers == null) {
            coSellers = listing.getCoSellers();
        }
        if (description == null) {
            description = listing.getDescription();
        }
        if (category == null) {
            category = listing.getCategory();
        }
        if (condition == null) {
            condition = listing.getCondition();
        }
    }
}
