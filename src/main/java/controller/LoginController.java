package controller;

import datasource.UserMapper;
import domain.Admin;
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

@WebServlet(name = "LoginController", value = {"/login"})
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);

        if (user != null) {
            // already logged in, redirect
            // System.out.println("Already logged in, redirecting...");
            response.sendRedirect("index");
        } else {
            request.getSession().invalidate();
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: implement login
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = null;

        if (email != null && password != null) {
            // email and password provided
            // check for Admin first
            user = UserMapper.fetchWithEmail(email);

            if (user != null) {
                // try checking password is valid
                if (AuthenticationEnforcer.checkPasswordMatchesHash(password, user.getPassHash())
                        && !(user instanceof StandardUser && !((StandardUser) user).isActive())
                ) {
                    request.getSession().setAttribute("userID", user.getId());
                    request.getSession().setAttribute("role",
                            user instanceof Admin ? "admin" : "standardUser");
                } else {
                    // System.out.println("Password mismatch!");
                    response.setStatus(400);
                    user = null;
                }
            } else {
                // couldn't find user
                response.setStatus(400);
            }
        }

        if (user == null) {
            // couldn't login
            request.setAttribute("error", "Username or password incorrect");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        } else {
            // login success
            response.sendRedirect("index");
        }
    }
}
