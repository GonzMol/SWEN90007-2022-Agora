package controller;

import datasource.DB;
import datasource.ShippingDetailsMapper;
import datasource.UserMapper;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "EditAccountController", value = "/edit-account")
public class EditAccountController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (user == null) {
            response.setStatus(401);
            dispatcher = request.getRequestDispatcher("error.jsp");
        } else {
            ArrayList<String> userEmails = UserMapper.fetchAllEmails(true, true);
            for (int i = userEmails.size() - 1; i >= 0; i--) {
                if (userEmails.get(i).equals(user.getEmail())) {
                    userEmails.remove(i);
                }
            }
            request.setAttribute("user", user);
            request.setAttribute("userEmails", userEmails);
            dispatcher = request.getRequestDispatcher("modifyAccountDetail.jsp");
        }
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher;
        Connection conn = null;
        try {
            conn = DB.connect();
            conn.setAutoCommit(false);
            User user = AuthenticationEnforcer.getUserFromSession(request, conn);
            String password = request.getParameter("password");
            if (user == null) {
                response.setStatus(401);
                dispatcher = request.getRequestDispatcher("error.jsp");
                dispatcher.forward(request, response);
            } else if (password == null) {
                response.setStatus(400);
                dispatcher = request.getRequestDispatcher("modifyAccountDetail.jsp");
                dispatcher.forward(request, response);
            } else {
                // check if correct
                boolean success = false;

                if (AuthenticationEnforcer.checkPasswordMatchesHash(password, user.getPassHash())) {
                    // correct password
                    // System.out.println("Password matches hash!");
                    String email = request.getParameter("email");
                    if (email != null) {
                        // System.out.println("Email provided!");
                        // password and email entered
                        if (UserMapper.fetchWithEmail(email, conn) == null || email.equals(user.getEmail())) {
                            // email is unused, or matches own, proceed
                            // System.out.println("Email is fine!");
                            user.setEmail(email);
                            if (user instanceof StandardUser) {
                                // also update default shipping information
                                // System.out.println("Standard user!");
                                ShippingDetails shippingDetails = ((StandardUser) user).getShippingDetails();
                                String firstName = request.getParameter("firstName");
                                String lastName = request.getParameter("lastName");
                                String phone = request.getParameter("phoneNumber");
                                String street = request.getParameter("street");
                                String city = request.getParameter("city");
                                String state = request.getParameter("state");
                                String country = request.getParameter("country");
                                String postcode = request.getParameter("postcode");
                                if (firstName != null && lastName != null && phone != null
                                        && street != null && city != null && state != null
                                        && country != null && postcode != null
                                ) {
                                    shippingDetails.setCity(city);
                                    shippingDetails.setCountry(country);
                                    shippingDetails.setFirstName(firstName);
                                    shippingDetails.setLastName(lastName);
                                    shippingDetails.setPostcode(postcode);
                                    shippingDetails.setPhoneNumber(phone);
                                    shippingDetails.setState(state);
                                    shippingDetails.setStreet(street);
                                    ShippingDetailsMapper.update(shippingDetails, conn);
                                }
                            }
                            UserMapper.update(user, conn);
                        } else {
                            // System.out.println("Email already exists!");
                            response.setStatus(400);
                            request.setAttribute("errorMessage", "This email is already in use, try again.");
                            dispatcher = request.getRequestDispatcher("modifyAccountDetail.jsp");
                            dispatcher.forward(request, response);
                        }
                    }
                } else {
                    // password mismatch
                    response.setStatus(400);
                    request.setAttribute("errorMessage", "Incorrect password, cannot update.");
                    dispatcher = request.getRequestDispatcher("modifyAccountDetail.jsp");
                    dispatcher.forward(request, response);
                }
            }
            conn.commit();
            response.sendRedirect("account");
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    // System.out.println("Transaction has been rolled back");
                }
            } catch (SQLException excep) {
                // System.out.println(excep.getMessage());
            }
            response.setStatus(500);
            dispatcher = request.getRequestDispatcher("modifyAccountDetail.jsp");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
    }

}
