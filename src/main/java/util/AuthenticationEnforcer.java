package util;

import datasource.UserMapper;
import domain.StandardUser;
import domain.User;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class AuthenticationEnforcer {
    public static User getUserFromSession(HttpServletRequest request) {
        return getUserFromSession(request, null);
    }

    public static User getUserFromSession(HttpServletRequest request, Connection conn) {
        UUID userID = (UUID) request.getSession().getAttribute("userID");
        if (userID == null) {
            return null;
        } else {
            User user = null;
            if (conn == null) {
                user = UserMapper.fetch(userID);
            } else {
                try {
                    user = UserMapper.fetch(userID, true, conn);
                } catch (SQLException e) {
                    // System.out.println(e.getMessage());
                }
            }
            if (user instanceof StandardUser && !((StandardUser) user).isActive()) {
                // user has been deactivated, log them out
                request.getSession().invalidate();
                return null;
            }
            return user;
        }
    }

    public static String hashPassword(String password) {
        Pbkdf2PasswordEncoder encoder = getEncoder();
        return encoder.encode(password);
    }

    public static Pbkdf2PasswordEncoder getEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    public static boolean checkPasswordMatchesHash(String password, String hash) {
        return getEncoder().matches(password, hash);
    }
}
