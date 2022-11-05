package domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Bid {
    private final UUID id;
    private final OffsetDateTime time;
    private final BigDecimal amount;
    private final StandardUser user;

    // Constructor for creating a new bid
    public Bid(BigDecimal amount, StandardUser user) {
        this.id = UUID.randomUUID();
        this.time = OffsetDateTime.now();
        this.amount = amount;
        this.user = user;
    }

    // Constructor for loading an existing bid
    public Bid(UUID id, OffsetDateTime time, BigDecimal amount, StandardUser user) {
        this.id = id;
        this.time = time;
        this.amount = amount;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public StandardUser getUser() {
        return user;
    }
}
