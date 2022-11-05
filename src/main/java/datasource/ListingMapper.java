package datasource;

import domain.*;
import org.springframework.dao.ConcurrencyFailureException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class ListingMapper {

    private static void throwConcurrencyException(Listing listing) throws Exception {
        String sql = "SELECT version " +
                "FROM \"listing\" " +
                "WHERE id = ?" +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, listing.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                int version = rs.getInt("version");
                if (version > listing.getVersion())
                    throw new ConcurrencyFailureException("Listing has been modified by another user");
                else
                    throw new Exception("Unexpected error occurred");
            } else {
                throw new ConcurrencyFailureException("Listing " + listing.getId().toString() + " has been deleted");
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

    public static int insert(Listing listing) {
        String sql = "INSERT INTO listing (id, \"type\", is_active, title, description, date_listed, \"category\", " +
                "condition, price, stock, start_time, end_time, \"owner\", version) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, listing.getId());
            ps.setBoolean(3, listing.isActive());
            ps.setString(4, listing.getTitle());
            ps.setString(5, listing.getDescription());
            ps.setObject(6, listing.getDateListed());
            ps.setString(7, String.valueOf(listing.getCategory()));
            ps.setString(8, String.valueOf(listing.getCondition()));
            ps.setObject(13, listing.getOwner().getId());
            ps.setInt(14, listing.getVersion());
            if (listing instanceof FixedPriceListing) {
                ps.setString(2, "FIXED");
                ps.setBigDecimal(9, ((FixedPriceListing) listing).getPrice());
                ps.setInt(10, ((FixedPriceListing) listing).getStock());
                ps.setNull(11, Types.TIMESTAMP_WITH_TIMEZONE);
                ps.setNull(12, Types.TIMESTAMP_WITH_TIMEZONE);
            } else {
                ps.setString(2, "AUCTION");
                ps.setBigDecimal(9, ((AuctionListing) listing).getStartPrice());
                ps.setNull(10, Types.INTEGER);
                ps.setObject(11, ((AuctionListing) listing).getStartTime());
                ps.setObject(12, ((AuctionListing) listing).getEndTime());
            }
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
            // System.out.println("Successfully inserted new listing");
        } else {
            // System.out.println("Failed to insert new listing");
        }
        return count;
    }

    public static int insert(Listing listing, Connection conn) throws Exception {
        String sql = "INSERT INTO listing (id, \"type\", is_active, title, description, date_listed, \"category\", " +
                "condition, price, stock, start_time, end_time, \"owner\", version) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, listing.getId());
            ps.setBoolean(3, listing.isActive());
            ps.setString(4, listing.getTitle());
            ps.setString(5, listing.getDescription());
            ps.setObject(6, listing.getDateListed());
            ps.setString(7, String.valueOf(listing.getCategory()));
            ps.setString(8, String.valueOf(listing.getCondition()));
            ps.setObject(13, listing.getOwner().getId());
            ps.setInt(14, listing.getVersion());
            if (listing instanceof FixedPriceListing) {
                ps.setString(2, "FIXED");
                ps.setBigDecimal(9, ((FixedPriceListing) listing).getPrice());
                ps.setInt(10, ((FixedPriceListing) listing).getStock());
                ps.setNull(11, Types.TIMESTAMP_WITH_TIMEZONE);
                ps.setNull(12, Types.TIMESTAMP_WITH_TIMEZONE);
            } else {
                ps.setString(2, "AUCTION");
                ps.setBigDecimal(9, ((AuctionListing) listing).getStartPrice());
                ps.setNull(10, Types.INTEGER);
                ps.setObject(11, ((AuctionListing) listing).getStartTime());
                ps.setObject(12, ((AuctionListing) listing).getEndTime());
            }
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
            // System.out.println("Successfully inserted new listing");
        } else {
            // System.out.println("Failed to insert new listing");
        }
        return count;
    }

    public static int update(Listing listing) throws Exception {
        String sql = "UPDATE listing " +
                "SET is_active = ?, title = ?, description = ?, \"category\" = ?, condition = ?, price = ?, " +
                "stock = ?, start_time = ?, end_time = ?, version = ? " +
                "WHERE id = ? and version = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, listing.isActive());
            ps.setString(2, listing.getTitle());
            ps.setString(3, listing.getDescription());
            ps.setString(4, String.valueOf(listing.getCategory()));
            ps.setString(5, String.valueOf(listing.getCondition()));
            ps.setInt(10, listing.getVersion() + 1);
            ps.setObject(11, listing.getId());
            ps.setInt(12, listing.getVersion());
            if (listing instanceof FixedPriceListing) {
                ps.setBigDecimal(6, ((FixedPriceListing) listing).getPrice());
                ps.setInt(7, ((FixedPriceListing) listing).getStock());
                ps.setNull(8, Types.DATE);
                ps.setNull(9, Types.DATE);
            } else {
                ps.setBigDecimal(6, ((AuctionListing) listing).getStartPrice());
                ps.setNull(7, Types.INTEGER);
                ps.setObject(8, ((AuctionListing) listing).getStartTime());
                ps.setObject(9, ((AuctionListing) listing).getEndTime());
            }
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
            // System.out.println("Successfully updated listing with id: " + listing.getId().toString());
        } else {
            throwConcurrencyException(listing);
        }
        return count;
    }

    public static int update(Listing listing, Connection conn) throws Exception {
        String sql = "UPDATE listing " +
                "SET is_active = ?, title = ?, description = ?, \"category\" = ?, condition = ?, price = ?, " +
                "stock = ?, start_time = ?, end_time = ?, version = ? " +
                "WHERE id = ? and version = ?";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, listing.isActive());
            ps.setString(2, listing.getTitle());
            ps.setString(3, listing.getDescription());
            ps.setString(4, String.valueOf(listing.getCategory()));
            ps.setString(5, String.valueOf(listing.getCondition()));
            ps.setInt(10, listing.getVersion() + 1);
            ps.setObject(11, listing.getId());
            ps.setInt(12, listing.getVersion());
            if (listing instanceof FixedPriceListing) {
                ps.setBigDecimal(6, ((FixedPriceListing) listing).getPrice());
                ps.setInt(7, ((FixedPriceListing) listing).getStock());
                ps.setNull(8, Types.DATE);
                ps.setNull(9, Types.DATE);
            } else {
                ps.setBigDecimal(6, ((AuctionListing) listing).getStartPrice());
                ps.setNull(7, Types.INTEGER);
                ps.setObject(8, ((AuctionListing) listing).getStartTime());
                ps.setObject(9, ((AuctionListing) listing).getEndTime());
            }
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
            // System.out.println("Successfully updated listing with id: " + listing.getId().toString());
        } else {
            throwConcurrencyException(listing);
        }
        return count;
    }

    public static Listing fetch(UUID id) {
        String sql = "SELECT * " +
                "FROM \"listing\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        Listing result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                String type = rs.getString("type");
                boolean isActive = rs.getBoolean("is_active");
                String title = rs.getString("title");
                String description = rs.getString("description");
                OffsetDateTime dateListed = rs.getObject("date_listed", OffsetDateTime.class);
                Listing.Category category = Listing.Category.valueOf(rs.getString("category"));
                Listing.Condition condition = Listing.Condition.valueOf(rs.getString("condition"));
                BigDecimal price = rs.getBigDecimal("price");
                StandardUser owner = (StandardUser) UserMapper.fetch(rs.getObject("owner", UUID.class));
                ArrayList<StandardUser> coSellers = fetchCoSellerWithListingId(id);
                int version = rs.getInt("version");
                if (type.equals("FIXED")) {
                    int stock = rs.getInt("stock");
                    result = new FixedPriceListing(id, isActive, dateListed, owner, coSellers, title, description,
                            category, condition, price, stock, version);
                } else {
                    OffsetDateTime startTime = rs.getObject("start_time", OffsetDateTime.class);
                    OffsetDateTime endTime = rs.getObject("end_time", OffsetDateTime.class);
                    ArrayList<Bid> bids = BidMapper.fetchWithListingId(id);
                    result = new AuctionListing(id, isActive, dateListed, owner, coSellers, bids, title, description,
                            category, condition, price, startTime, endTime, version);
                }
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
            // System.out.println("Successfully fetched listing with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch listing with id: " + id.toString());
        }
        return result;
    }

    public static Listing fetch(UUID id, boolean forUpdate, Connection conn) throws SQLException {
        String sql = "SELECT * " +
                "FROM \"listing\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        if (forUpdate) {
            sql += " FOR UPDATE";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Listing result = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                String type = rs.getString("type");
                boolean isActive = rs.getBoolean("is_active");
                String title = rs.getString("title");
                String description = rs.getString("description");
                OffsetDateTime dateListed = rs.getObject("date_listed", OffsetDateTime.class);
                Listing.Category category = Listing.Category.valueOf(rs.getString("category"));
                Listing.Condition condition = Listing.Condition.valueOf(rs.getString("condition"));
                BigDecimal price = rs.getBigDecimal("price");
                StandardUser owner = (StandardUser) UserMapper.fetch(rs.getObject("owner", UUID.class));
                ArrayList<StandardUser> coSellers = fetchCoSellerWithListingId(id);
                int version = rs.getInt("version");
                if (type.equals("FIXED")) {
                    int stock = rs.getInt("stock");
                    result = new FixedPriceListing(id, isActive, dateListed, owner, coSellers, title, description,
                            category, condition, price, stock, version);
                } else {
                    OffsetDateTime startTime = rs.getObject("start_time", OffsetDateTime.class);
                    OffsetDateTime endTime = rs.getObject("end_time", OffsetDateTime.class);
                    ArrayList<Bid> bids = BidMapper.fetchWithListingId(id);
                    result = new AuctionListing(id, isActive, dateListed, owner, coSellers, bids, title, description,
                            category, condition, price, startTime, endTime, version);
                }
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
            // System.out.println("Successfully fetched listing with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch listing with id: " + id.toString());
        }
        return result;
    }

    public static ArrayList<Listing> fetchAll(boolean activeOnly) {
        String sql = "SELECT * " +
                "FROM \"listing\"";
        if (activeOnly) {
            sql = sql + " WHERE is_active = TRUE";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Listing> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String type = rs.getString("type");
                boolean isActive = rs.getBoolean("is_active");
                String title = rs.getString("title");
                OffsetDateTime dateListed = rs.getObject("date_listed", OffsetDateTime.class);
                BigDecimal price = rs.getBigDecimal("price");
                StandardUser owner = (StandardUser) UserMapper.fetch(rs.getObject("owner", UUID.class));
                int version = rs.getInt("version");
                if (type.equals("FIXED")) {
                    int stock = rs.getInt("stock");
                    Listing listing = new FixedPriceListing(id, isActive, dateListed, owner, title, price, stock, version);
                    result.add(listing);
                } else {
                    OffsetDateTime startTime = rs.getObject("start_time", OffsetDateTime.class);
                    OffsetDateTime endTime = rs.getObject("end_time", OffsetDateTime.class);
                    ArrayList<Bid> bids = BidMapper.fetchWithListingId(id);
                    Listing listing = new AuctionListing(id, isActive, dateListed, owner, bids, title, price, startTime,
                            endTime, version);
                    result.add(listing);
                }
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
            // System.out.println("Successfully fetched " + result.size() + " listing(s)");
        } else {
            // System.out.println("No listings found");
        }
        return result;
    }

    public static ArrayList<Listing> fetchWithSellerId(UUID sellerId, boolean activeOnly) {
        String sql = "SELECT DISTINCT \"listing\".* " +
                "FROM (\"listing\" " +
                "LEFT JOIN co_seller on listing.id = co_seller.listing) " +
                "WHERE (\"owner\" = ? OR \"user\" = ?)";
        if (activeOnly) {
            sql = sql + " AND (is_active = TRUE)";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Listing> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, sellerId);
            ps.setObject(2, sellerId);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String type = rs.getString("type");
                boolean isActive = rs.getBoolean("is_active");
                String title = rs.getString("title");
                OffsetDateTime dateListed = rs.getObject("date_listed", OffsetDateTime.class);
                BigDecimal price = rs.getBigDecimal("price");
                StandardUser owner = (StandardUser) UserMapper.fetch(rs.getObject("owner", UUID.class));
                int version = rs.getInt("version");
                if (type.equals("FIXED")) {
                    int stock = rs.getInt("stock");
                    Listing listing = new FixedPriceListing(id, isActive, dateListed, owner, title, price, stock,
                            version);
                    result.add(listing);
                } else {
                    OffsetDateTime startTime = rs.getObject("start_time", OffsetDateTime.class);
                    OffsetDateTime endTime = rs.getObject("end_time", OffsetDateTime.class);
                    ArrayList<Bid> bids = BidMapper.fetchWithListingId(id);
                    Listing listing = new AuctionListing(id, isActive, dateListed, owner, bids, title, price, startTime,
                            endTime, version);
                    result.add(listing);
                }
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
            // System.out.println("Successfully fetched " + result.size() + " listing(s) for seller with id: " +
                    // sellerId.toString());
        } else {
            // System.out.println("No listings found for seller with id: " + sellerId.toString());
        }
        return result;
    }

    public static int insertCoSeller(UUID userId, UUID listingId) {
        String sql = "INSERT INTO co_seller (\"user\", listing) " +
                "VALUES (?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setObject(2, listingId);
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
            // System.out.println("Successfully inserted new co-seller");
        } else {
            // System.out.println("Failed to insert new co-seller");
        }
        return count;
    }

    public static int insertCoSeller(UUID userId, UUID listingId, Connection conn) throws SQLException {
        String sql = "INSERT INTO co_seller (\"user\", listing) " +
                "VALUES (?,?)";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setObject(2, listingId);
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
            // System.out.println("Successfully inserted new co-seller");
        } else {
            // System.out.println("Failed to insert new co-seller");
        }
        return count;
    }

    public static ArrayList<StandardUser> fetchCoSellerWithListingId(UUID listingId) {
        String sql = "SELECT * " +
                "FROM co_seller " +
                "INNER JOIN \"user\" on co_seller.\"user\" = \"user\".id " +
                "WHERE listing = ? AND \"user\".is_active = TRUE";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<StandardUser> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, listingId);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                StandardUser user = (StandardUser) UserMapper.fetch(rs.getObject("user", UUID.class));
                result.add(user);
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
            // System.out.println("Successfully fetched " + result.size() + " co-seller(s) for listing with id: " +
                    // listingId.toString());
        } else {
            // System.out.println("No co-sellers found for listing with id: " + listingId.toString());
        }
        return result;
    }

    public static int deleteCoSeller(UUID userId, UUID listingId) {
        String sql = "DELETE " +
                "FROM co_seller " +
                "WHERE \"user\" = ? AND listing = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setObject(2, listingId);
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
            // System.out.println("Successfully deleted co-seller with id: " + userId.toString() + " from listing " +
                    // listingId.toString());
        } else {
            // System.out.println("Failed to delete co-seller with id: " + userId.toString() + " from listing " +
                    // listingId.toString());
        }
        return count;
    }

    public static int deleteCoSeller(UUID userId, UUID listingId, Connection conn) throws SQLException {
        String sql = "DELETE " +
                "FROM co_seller " +
                "WHERE \"user\" = ? AND listing = ?";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setObject(2, listingId);
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
            // System.out.println("Successfully deleted co-seller with id: " + userId.toString() + " from listing " +
                    // listingId.toString());
        } else {
            // System.out.println("Failed to delete co-seller with id: " + userId.toString() + " from listing " +
                    // listingId.toString());
        }
        return count;
    }
}
