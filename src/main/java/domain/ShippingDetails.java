package domain;

import java.util.UUID;

public class ShippingDetails {
    private final UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String street;
    private String city;
    private String state;
    private String postcode;

    // Constructor for creating new shipping details
    public ShippingDetails(String firstName, String lastName, String phoneNumber, String country,
                           String street, String city, String state, String postcode) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
    }

    // Constructor for loading existing shipping details
    public ShippingDetails(UUID id, String firstName, String lastName, String phoneNumber, String country,
                           String street, String city, String state, String postcode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
