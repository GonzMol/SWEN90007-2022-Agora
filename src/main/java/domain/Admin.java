package domain;

import java.util.UUID;

public class Admin extends User {

    // Constructor for creating a new admin
    public Admin(String username, String email, String passHash) {
        super(username, email, passHash);
    }

    // Constructor for loading an existing admin
    public Admin(UUID id, String username, String email, String passHash) {
        super(id, username, email, passHash);
    }

    // Constructor for lazy load
    public Admin(UUID id, String username, String email) {
        super(id, username, email, null);
    }
}
