package domain;

import datasource.UserMapper;

import java.util.UUID;

public abstract class User {
    private final UUID id;
    private final String username;
    private String email;
    private String passHash;

    // Constructor for creating a new user
    public User(String username, String email, String passHash) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.passHash = passHash;
    }

    // Constructor for loading an existing user
    public User(UUID id, String username, String email, String passHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passHash = passHash;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassHash() {
        if (passHash == null) {
            User user = UserMapper.fetchPopulated(id);
            passHash = user.getPassHash();
        }
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
}
