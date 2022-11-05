package datasource;

import domain.Bid;
import domain.StandardUser;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class BidMapper {

    public static int insert(Bid bid, UUID listingId) {
        String sql = "INSERT INTO bid (id, \"time\", amount, listing, \"user\") " +
                "VALUES (?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, bid.getId());
            ps.setObject(2, bid.getTime());
            ps.setBigDecimal(3, bid.getAmount());
            ps.setObject(4, listingId);
            ps.setObject(5, bid.getUser().getId());
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
            // System.out.println("Successfully inserted new bid");
        } else {
            // System.out.println("Failed to insert new bid");
        }
        return count;
    }

    public static int insert(Bid bid, UUID listingId, Connection conn) throws SQLException {
        String sql = "INSERT INTO bid (id, \"time\", amount, listing, \"user\") " +
                "VALUES (?,?,?,?,?)";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, bid.getId());
            ps.setObject(2, bid.getTime());
            ps.setBigDecimal(3, bid.getAmount());
            ps.setObject(4, listingId);
            ps.setObject(5, bid.getUser().getId());
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
            // System.out.println("Successfully inserted new bid");
        } else {
            // System.out.println("Failed to insert new bid");
        }
        return count;
    }

    public static ArrayList<Bid> fetchWithListingId(UUID listingId) {
        String sql = "SELECT * " +
                "FROM bid " +
                "INNER JOIN \"user\" on bid.\"user\" = \"user\".id "+
                "WHERE listing = ? AND \"user\".is_active = TRUE";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Bid> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, listingId);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                OffsetDateTime time = rs.getObject("time", OffsetDateTime.class);
                BigDecimal amount = rs.getObject("amount", BigDecimal.class);
                StandardUser user = (StandardUser) UserMapper.fetch(rs.getObject("user", UUID.class));
                Bid bid = new Bid(id, time, amount, user);
                result.add(bid);
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
            // System.out.println("Successfully fetched " + result.size() + " bid(s) for listing with id: " +
                    // listingId.toString());
        } else {
            // System.out.println("No bids found for listing with id: " + listingId.toString());
        }
        return result;
    }
}
