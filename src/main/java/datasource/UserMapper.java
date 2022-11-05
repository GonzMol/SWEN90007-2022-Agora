package datasource;

import domain.Admin;
import domain.ShippingDetails;
import domain.StandardUser;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class UserMapper {

    public static int insert(User user) {
        String sql = "INSERT INTO \"user\" (id, is_admin, is_active, username, email, pass_hash, shipping_details) " +
                "VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, user.getId());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPassHash());
            if (user instanceof StandardUser) {
                ps.setBoolean(2, false);
                ps.setBoolean(3, ((StandardUser) user).isActive());
                ps.setObject(7, ((StandardUser) user).getShippingDetails().getId());
            } else {
                ps.setBoolean(2, true);
                ps.setNull(3, Types.BOOLEAN);
                ps.setNull(7, Types.OTHER);
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
            // System.out.println("Successfully inserted new user");
        } else {
            // System.out.println("Failed to inserted new user");
        }
        return count;
    }

    public static int update(User user) {
        String sql = "UPDATE \"user\" " +
                "SET is_active = ?, email = ?, pass_hash = ? " +
                "WHERE id = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            if (user instanceof StandardUser) {
                ps.setBoolean(1, ((StandardUser) user).isActive());
            } else {
                ps.setNull(1, Types.BOOLEAN);
            }
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassHash());
            ps.setObject(4, user.getId());
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
            // System.out.println("Successfully updated user with id: " + user.getId().toString());
        } else {
            // System.out.println("Failed to update user with id: " + user.getId().toString());
        }
        return count;
    }

    public static int update(User user, Connection conn) throws SQLException {
        String sql = "UPDATE \"user\" " +
                "SET is_active = ?, email = ?, pass_hash = ? " +
                "WHERE id = ?";
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement(sql);
            if (user instanceof StandardUser) {
                ps.setBoolean(1, ((StandardUser) user).isActive());
            } else {
                ps.setNull(1, Types.BOOLEAN);
            }
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassHash());
            ps.setObject(4, user.getId());
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
            // System.out.println("Successfully updated user with id: " + user.getId().toString());
        } else {
            // System.out.println("Failed to update user with id: " + user.getId().toString());
        }
        return count;
    }

    public static User fetch(UUID id) {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        User result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                String username = rs.getString("username");
                String email = rs.getString("email");
                if (!isAdmin) {
                    boolean isActive = rs.getBoolean("is_active");
                    result = new StandardUser(id, username, email, isActive);
                } else {
                    result = new Admin(id, username, email);
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
            // System.out.println("Successfully fetched user with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch user with id: " + id.toString());
        }
        return result;
    }

    public static User fetch(UUID id, boolean forUpdate, Connection conn) throws SQLException {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        if (forUpdate) {
            sql += " FOR UPDATE";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        User result = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                String username = rs.getString("username");
                String email = rs.getString("email");
                if (!isAdmin) {
                    boolean isActive = rs.getBoolean("is_active");
                    result = new StandardUser(id, username, email, isActive);
                } else {
                    result = new Admin(id, username, email);
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
            // System.out.println("Successfully fetched user with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch user with id: " + id.toString());
        }
        return result;
    }

    public static User fetchPopulated(UUID id) {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE id = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        User result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String passHash = rs.getString("pass_hash");
                if (!isAdmin) {
                    boolean isActive = rs.getBoolean("is_active");
                    ShippingDetails shippingDetails =
                            ShippingDetailsMapper.fetch(rs.getObject("shipping_details", UUID.class));
                    result = new StandardUser(id, username, email, passHash, isActive, shippingDetails);
                } else {
                    result = new Admin(id, username, email, passHash);
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
            // System.out.println("Successfully fetched user with id: " + id.toString());
        } else {
            // System.out.println("Failed to fetch user with id: " + id.toString());
        }
        return result;
    }

    public static ArrayList<StandardUser> fetchAllStandardUsers() {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE is_admin = FALSE";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<StandardUser> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String username = rs.getString("username");
                String email = rs.getString("email");
                boolean isActive = rs.getBoolean("is_active");
                StandardUser user = new StandardUser(id, username, email, isActive);
                result.add(user);
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
        if (!result.isEmpty()) {
            // System.out.println("Successfully fetched " + result.size() + " user(s)");
        } else {
            // System.out.println("No users found");
        }
        return result;
    }

    public static Admin fetchAdmin() {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE is_admin = TRUE " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        Admin result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String username = rs.getString("username");
                String email = rs.getString("email");
                result = new Admin(id, username, email);
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
            // System.out.println("Successfully fetched admin");
        } else {
            // System.out.println("No admin found");
        }
        return result;
    }

    public static User fetchWithEmail(String email) {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE email = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        User result = null;
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                UUID id = rs.getObject("id", UUID.class);
                String username = rs.getString("username");
                if (!isAdmin) {
                    boolean isActive = rs.getBoolean("is_active");
                    result = new StandardUser(id, username, email, isActive);
                } else {
                    result = new Admin(id, username, email);
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
            // System.out.println("Successfully fetched user with email: " + email);
        } else {
            // System.out.println("Failed to fetch user with email: " + email);
        }
        return result;
    }

    public static User fetchWithEmail(String email, Connection conn) throws SQLException {
        String sql = "SELECT * " +
                "FROM \"user\" " +
                "WHERE email = ? " +
                "LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        User result = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                UUID id = rs.getObject("id", UUID.class);
                String username = rs.getString("username");
                if (!isAdmin) {
                    boolean isActive = rs.getBoolean("is_active");
                    result = new StandardUser(id, username, email, isActive);
                } else {
                    result = new Admin(id, username, email);
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
            // System.out.println("Successfully fetched user with email: " + email);
        } else {
            // System.out.println("Failed to fetch user with email: " + email);
        }
        return result;
    }

    public static ArrayList<String> fetchAllEmails(boolean activeOnly, boolean nonAdmin) {
        String sql = "SELECT email " +
                "FROM \"user\" ";
        if (activeOnly) {
            sql += " WHERE is_active = TRUE";
        }
        if (nonAdmin) {
            if (activeOnly) {
                sql += " AND is_admin = FALSE";
            } else {
                sql += " WHERE is_admin = FALSE";
            }
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                String email = rs.getString("email");
                result.add(email);
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
        if (!result.isEmpty()) {
            // System.out.println("Successfully fetched " + result.size() + " user email(s)");
        } else {
            // System.out.println("No user emails found");
        }
        return result;
    }

    public static ArrayList<String> fetchAllUsernames() {
        String sql = "SELECT username " +
                "FROM \"user\"";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            conn = DB.connect();
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                String username = rs.getString("username");
                result.add(username);
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
        if (!result.isEmpty()) {
            // System.out.println("Successfully fetched " + result.size() + " username(s)");
        } else {
            // System.out.println("No usernames found");
        }
        return result;
    }
}
