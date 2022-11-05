package datasource;

import domain.ShippingDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ShippingDetailsMapper {

    public static int insert(ShippingDetails shippingDetails) {
        String sql = "INSERT INTO shipping_details (id, first_name, last_name, phone_number, country, street, city, " +
                "state, postcode) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, shippingDetails.getId());
            ps.setString(2, shippingDetails.getFirstName());
            ps.setString(3, shippingDetails.getLastName());
            ps.setString(4, shippingDetails.getPhoneNumber());
            ps.setString(5, shippingDetails.getCountry());
            ps.setString(6, shippingDetails.getStreet());
            ps.setString(7, shippingDetails.getCity());
            ps.setString(8, shippingDetails.getState());
            ps.setString(9, shippingDetails.getPostcode());
            count = ps.executeUpdate();
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
        if (count > 0) {
            // System.out.println("Successfully inserted new shipping details");
        } else {
            // System.out.println("Failed to insert new shipping details");
        }
        return count;
    }

    public static int insert(ShippingDetails shippingDetails, Connection conn) throws SQLException {
        String sql = "INSERT INTO shipping_details (id, first_name, last_name, phone_number, country, street, city, " +
                "state, postcode) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, shippingDetails.getId());
            ps.setString(2, shippingDetails.getFirstName());
            ps.setString(3, shippingDetails.getLastName());
            ps.setString(4, shippingDetails.getPhoneNumber());
            ps.setString(5, shippingDetails.getCountry());
            ps.setString(6, shippingDetails.getStreet());
            ps.setString(7, shippingDetails.getCity());
            ps.setString(8, shippingDetails.getState());
            ps.setString(9, shippingDetails.getPostcode());
            count = ps.executeUpdate();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
        if (count > 0) {
            // System.out.println("Successfully inserted new shipping details");
        } else {
            // System.out.println("Failed to insert new shipping details");
        }
        return count;
    }

    public static int update(ShippingDetails shippingDetails) {
        String sql = "UPDATE shipping_details " +
                "SET first_name = ?, last_name = ?, phone_number = ?, country = ?, street = ?, city = ?, state = ?, " +
                "postcode = ? " +
                "WHERE id = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setString(1, shippingDetails.getFirstName());
            ps.setString(2, shippingDetails.getLastName());
            ps.setString(3, shippingDetails.getPhoneNumber());
            ps.setString(4, shippingDetails.getCountry());
            ps.setString(5, shippingDetails.getStreet());
            ps.setString(6, shippingDetails.getCity());
            ps.setString(7, shippingDetails.getState());
            ps.setString(8, shippingDetails.getPostcode());
            ps.setObject(9, shippingDetails.getId());
            count = ps.executeUpdate();
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
        if (count > 0) {
            // System.out.println("Successfully updated shipping details with id: " + shippingDetails.getId().toString());
        } else {
            // System.out.println("Failed to update shipping details with id: " + shippingDetails.getId().toString());
        }
        return count;
    }

    public static int update(ShippingDetails shippingDetails, Connection conn) throws SQLException {
        String sql = "UPDATE shipping_details " +
                "SET first_name = ?, last_name = ?, phone_number = ?, country = ?, street = ?, city = ?, state = ?, " +
                "postcode = ? " +
                "WHERE id = ?";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, shippingDetails.getFirstName());
            ps.setString(2, shippingDetails.getLastName());
            ps.setString(3, shippingDetails.getPhoneNumber());
            ps.setString(4, shippingDetails.getCountry());
            ps.setString(5, shippingDetails.getStreet());
            ps.setString(6, shippingDetails.getCity());
            ps.setString(7, shippingDetails.getState());
            ps.setString(8, shippingDetails.getPostcode());
            ps.setObject(9, shippingDetails.getId());
            count = ps.executeUpdate();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
        if (count > 0) {
            // System.out.println("Successfully updated shipping details with id: " + shippingDetails.getId().toString());
        } else {
            // System.out.println("Failed to update shipping details with id: " + shippingDetails.getId().toString());
        }
        return count;
    }

    public static ShippingDetails fetch(UUID id) {
        String sql = "SELECT * " +
                "FROM shipping_details " +
                "WHERE id = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ShippingDetails result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String country = rs.getString("country");
                String street = rs.getString("street");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String postcode = rs.getString("postcode");
                result = new ShippingDetails(id, firstName, lastName, phoneNumber, country, street, city, state,
                        postcode);
            }
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
        if (result != null) {
            // System.out.println("Successfully fetched shipping details with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch shipping details with id: " + id.toString());
        }
        return result;
    }
}
