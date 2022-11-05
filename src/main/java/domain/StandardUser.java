package domain;

import datasource.UserMapper;

import java.util.UUID;

public class StandardUser extends User {
    private boolean isActive;
    private ShippingDetails shippingDetails;

    // Constructor for creating a new standard user
    public StandardUser(String username, String email, String passHash, ShippingDetails shippingDetails) {
        super(username, email, passHash);
        this.isActive = true;
        this.shippingDetails = shippingDetails;
    }

    // Constructor for loading an existing standard user
    public StandardUser(UUID id, String username, String email, String passHash, boolean isActive,
                        ShippingDetails shippingDetails) {
        super(id, username, email, passHash);
        this.isActive = isActive;
        this.shippingDetails = shippingDetails;
    }

    // Constructor for lazy load
    public StandardUser(UUID id, String username, String email, boolean isActive) {
        super(id, username, email, null);
        this.isActive = isActive;
        this.shippingDetails = null;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ShippingDetails getShippingDetails() {
        if (shippingDetails == null) {
            StandardUser standardUser = (StandardUser) UserMapper.fetchPopulated(getId());
            shippingDetails = standardUser.getShippingDetails();
        }
        return shippingDetails;
    }

    public void setShippingDetails(ShippingDetails shippingDetails) {
        this.shippingDetails = shippingDetails;
    }
}
