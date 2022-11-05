package controller;

import datasource.ShippingDetailsMapper;
import datasource.UserMapper;
import domain.Admin;
import domain.ShippingDetails;
import domain.StandardUser;
import domain.User;
import util.AuthenticationEnforcer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "RegisterController", value = {"/register"})
public class RegisterController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // redirect to register.jsp
        if (AuthenticationEnforcer.getUserFromSession(request) != null) {
            // logged in, redirect away
            response.sendRedirect("index");
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String isAdmin = request.getParameter("isAdmin");

        boolean success = false;
        User user = null;

        if (username != null && email != null && password != null) {
            // check that email and username are not taken
            ArrayList<String> userEmails = UserMapper.fetchAllEmails(false, false);
            ArrayList<String> userUsernames = UserMapper.fetchAllUsernames();

            if (userEmails.contains(email)) {
                // email taken
                request.setAttribute("error", "Email already taken, try again.");
            } else if (userUsernames.contains(username)) {
                // username taken
                request.setAttribute("error", "Username already taken, try again.");
            } else {
                // username and email available
                if (isAdmin != null) {
                    // try creating an Admin
                    if (UserMapper.fetchAdmin() != null) {
                        // already exists an admin, don't create a new one
                        response.setStatus(403);
                    } else {
                        String passHash = AuthenticationEnforcer.hashPassword(password);
                        user = new Admin(username, email, passHash);
                        int result = UserMapper.insert(user);

                        if (result > 0) {
                            HttpSession session = request.getSession();
                            session.setAttribute("user", user);
                            success = true;
                        } else {
                            // result is in error
                            response.setStatus(500);
                            request.setAttribute("error", "Unable to register, please try again.");
                        }
                    }
                } else {
                    String firstName = request.getParameter("firstName");
                    String lastName = request.getParameter("lastName");
                    String phone = request.getParameter("phone");
                    String street = request.getParameter("street");
                    String city = request.getParameter("city");
                    String country = request.getParameter("country");
                    String state = request.getParameter("state");
                    String postcode = request.getParameter("postcode");

                    if (firstName != null && lastName != null && phone != null
                            && street != null && city != null && country != null
                            && state != null && postcode != null
                    ) {
                        // try inserting a new user
                        String passHash = AuthenticationEnforcer.hashPassword(password);

                        ShippingDetails shippingDetails = new ShippingDetails(
                                firstName, lastName, phone, country, street, city, state, postcode
                        );
                        ShippingDetailsMapper.insert(shippingDetails);

                        user = new StandardUser(username, email, passHash, shippingDetails);
                        int result = UserMapper.insert(user);

                        if (result > 0) {
                            success = true;
                        } else {
                            // result is in error
                            response.setStatus(500);
                            request.setAttribute("error", "Unable to register, please try again.");
                        }
                    } else {
                        response.setStatus(403);
                        request.setAttribute("error", "Some fields missing values, fill all fields and try again.");
                    }
                }
            }
        }

        if (user != null && success) {
            request.getSession().setAttribute("userID", user.getId());
            request.getSession().setAttribute("role",
                    user instanceof Admin ? "admin" : "standardUser");
            response.sendRedirect("index");
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
            dispatcher.forward(request, response);
        }

    }

}

