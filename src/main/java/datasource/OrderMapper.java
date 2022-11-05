package datasource;

import domain.Listing;
import domain.Order;
import domain.ShippingDetails;
import domain.StandardUser;
import org.springframework.dao.ConcurrencyFailureException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class OrderMapper {

    private static void throwConcurrencyException(Order order) throws Exception {
        String sql = "SELECT version " +
                "FROM \"order\" " +
                "WHERE id = ?" +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, order.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                int version = rs.getInt("version");
                if (version > order.getVersion())
                    throw new ConcurrencyFailureException("Order has been modified by another user");
                else
                    throw new Exception("Unexpected error occurred");
            } else {
                throw new ConcurrencyFailureException("Order " + order.getId().toString() + " has been deleted");
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
    }

    public static int insert(Order order) {
        String sql = "INSERT INTO \"order\" (id, is_cancelled, date, quantity, buyer, listing, shipping_details, " +
                "version) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, order.getId());
            ps.setBoolean(2, order.isCancelled());
            ps.setObject(3, order.getDate());
            ps.setInt(4, order.getQuantity());
            ps.setObject(5, order.getBuyer().getId());
            ps.setObject(6, order.getListing().getId());
            ps.setObject(7, order.getShippingDetails().getId());
            ps.setInt(8, order.getVersion());
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
            // System.out.println("Successfully inserted new order");
        } else {
            // System.out.println("Failed to insert new order");
        }
        return count;
    }

    public static int insert(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO \"order\" (id, is_cancelled, date, quantity, buyer, listing, shipping_details, " +
                "version) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, order.getId());
            ps.setBoolean(2, order.isCancelled());
            ps.setObject(3, order.getDate());
            ps.setInt(4, order.getQuantity());
            ps.setObject(5, order.getBuyer().getId());
            ps.setObject(6, order.getListing().getId());
            ps.setObject(7, order.getShippingDetails().getId());
            ps.setInt(8, order.getVersion());
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
            // System.out.println("Successfully inserted new order");
        } else {
            // System.out.println("Failed to insert new order");
        }
        return count;
    }

    public static int update(Order order) throws Exception {
        String sql = "UPDATE \"order\" " +
                "SET is_cancelled = ?, quantity = ?, version = ? " +
                "WHERE id = ? and version = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, order.isCancelled());
            ps.setInt(2, order.getQuantity());
            ps.setInt(3, order.getVersion() + 1);
            ps.setObject(4, order.getId());
            ps.setInt(5, order.getVersion());
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
            // System.out.println("Successfully updated order with id: " + order.getId().toString());
        } else {
            throwConcurrencyException(order);
        }
        return count;
    }

    public static int update(Order order, Connection conn) throws Exception {
        String sql = "UPDATE \"order\" " +
                "SET is_cancelled = ?, quantity = ?, version = ? " +
                "WHERE id = ? and version = ?";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, order.isCancelled());
            ps.setInt(2, order.getQuantity());
            ps.setInt(3, order.getVersion() + 1);
            ps.setObject(4, order.getId());
            ps.setInt(5, order.getVersion());
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
            // System.out.println("Successfully updated order with id: " + order.getId().toString());
        } else {
            throwConcurrencyException(order);
        }
        return count;
    }

    public static Order fetch(UUID id) {
        String sql = "SELECT * " +
                "FROM \"order\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        Order result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isCancelled = rs.getBoolean("is_cancelled");
                OffsetDateTime date = rs.getObject("date", OffsetDateTime.class);
                int quantity = rs.getInt("quantity");
                int version = rs.getInt("version");
                StandardUser buyer = (StandardUser) UserMapper.fetch(rs.getObject("buyer", UUID.class));
                Listing listing = ListingMapper.fetch(rs.getObject("listing", UUID.class));
                ShippingDetails shippingDetails =
                        ShippingDetailsMapper.fetch(rs.getObject("shipping_details", UUID.class));
                result = new Order(id, isCancelled, date, quantity, buyer, listing, shippingDetails, version);
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
            // System.out.println("Successfully fetched order with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch order with id: " + id.toString());
        }
        return result;
    }

    public static Order fetch(UUID id, boolean forUpdate, Connection conn) throws SQLException {
        String sql = "SELECT * " +
                "FROM \"order\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        if (forUpdate) {
            sql += " FOR UPDATE";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Order result = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isCancelled = rs.getBoolean("is_cancelled");
                OffsetDateTime date = rs.getObject("date", OffsetDateTime.class);
                int quantity = rs.getInt("quantity");
                int version = rs.getInt("version");
                StandardUser buyer = (StandardUser) UserMapper.fetch(rs.getObject("buyer", UUID.class));
                Listing listing = ListingMapper.fetch(rs.getObject("listing", UUID.class));
                ShippingDetails shippingDetails =
                        ShippingDetailsMapper.fetch(rs.getObject("shipping_details", UUID.class));
                result = new Order(id, isCancelled, date, quantity, buyer, listing, shippingDetails, version);
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
        if (result != null) {
            // System.out.println("Successfully fetched order with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch order with id: " + id.toString());
        }
        return result;
    }

    public static ArrayList<Order> fetchAll() {
        String sql = "SELECT * " +
                "FROM \"order\"";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Order> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                boolean isCancelled = rs.getBoolean("is_cancelled");
                OffsetDateTime date = rs.getObject("date", OffsetDateTime.class);
                int quantity = rs.getInt("quantity");
                int version = rs.getInt("version");
                StandardUser buyer = (StandardUser) UserMapper.fetch(rs.getObject("buyer", UUID.class));
                Listing listing = ListingMapper.fetch(rs.getObject("listing", UUID.class));
                Order order = new Order(id, isCancelled, date, quantity, buyer, listing, version);
                result.add(order);
            }
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
            return null;
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
        if (!result.isEmpty()) {
            // System.out.println("Successfully fetched " + result.size() + " order(s)");
        } else {
            // System.out.println("No orders found");
        }
        return result;
    }

    public static ArrayList<Order> fetchWithBuyerId(UUID buyerId) {
        String sql = "SELECT * " +
                "FROM \"order\" " +
                "WHERE buyer = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Order> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, buyerId);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                boolean isCancelled = rs.getBoolean("is_cancelled");
                OffsetDateTime date = rs.getObject("date", OffsetDateTime.class);
                int quantity = rs.getInt("quantity");
                int version = rs.getInt("version");
                StandardUser buyer = (StandardUser) UserMapper.fetch(rs.getObject("buyer", UUID.class));
                Listing listing = ListingMapper.fetch(rs.getObject("listing", UUID.class));
                Order order = new Order(id, isCancelled, date, quantity, buyer, listing, version);
                result.add(order);
            }
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
            return null;
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
        if (!result.isEmpty()) {
            // System.out.println("Successfully fetched " + result.size() + " order(s) for buyer with id: " +
                    // buyerId.toString());
        } else {
            // System.out.println("No orders found for buyer with id: " + buyerId.toString());
        }
        return result;
    }

    public static ArrayList<Order> fetchWithSellerId(UUID sellerId) {
        String sql = "SELECT \"order\".*, listing.owner, co_seller.user " +
                "FROM ((\"order\" " +
                "INNER JOIN listing on \"order\".listing = listing.id) " +
                "LEFT JOIN co_seller on listing.id = co_seller.listing) " +
                "WHERE \"owner\" = ? OR \"user\" = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Order> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, sellerId);
            ps.setObject(2, sellerId);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                boolean isCancelled = rs.getBoolean("is_cancelled");
                OffsetDateTime date = rs.getObject("date", OffsetDateTime.class);
                int quantity = rs.getInt("quantity");
                int version = rs.getInt("version");
                StandardUser buyer = (StandardUser) UserMapper.fetch(rs.getObject("buyer", UUID.class));
                Listing listing = ListingMapper.fetch(rs.getObject("listing", UUID.class));
                Order order = new Order(id, isCancelled, date, quantity, buyer, listing, version);
                result.add(order);
            }
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
            return null;
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
        if (!result.isEmpty()) {
            // System.out.println("Successfully fetched " + result.size() + " order(s) for seller with id: " +
                    // sellerId.toString());
        } else {
            // System.out.println("No orders found for seller with id: " + sellerId.toString());
        }
        return result;
    }
}
