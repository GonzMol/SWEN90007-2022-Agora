package controller;

import datasource.UserMapper;
import domain.User;
import util.AuthenticationEnforcer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EditPasswordController", value = "/edit-password")
public class EditPasswordController extends HttpServlet {
    // cancel order
    // save the change
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (user == null) {
            response.setStatus(401);
            dispatcher = request.getRequestDispatcher("error.jsp");
        } else {
            request.setAttribute("user", user);
            dispatcher = request.getRequestDispatcher("editPassword.jsp");
        }
        dispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        String existingPassword = request.getParameter("oldPassword");
        RequestDispatcher dispatcher;
        if (user == null) {
            response.setStatus(401);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
        } else if (existingPassword == null) {
            response.setStatus(403);
            dispatcher = request.getRequestDispatcher("editPassword.jsp");
            dispatcher.forward(request, response);
        } else {
            // check if correct
            boolean success = false;

            if (AuthenticationEnforcer.checkPasswordMatchesHash(existingPassword, user.getPassHash())) {
                // matching password
                String password = request.getParameter("newPassword");
                if (password != null) {
                    // new password entered
                    user.setPassHash(AuthenticationEnforcer.hashPassword(password));
                    UserMapper.update(user);
                    success = true;
                }

                if (success) {
                    response.sendRedirect("account");
                } else {
                    response.setStatus(500);
                    dispatcher = request.getRequestDispatcher("editPassword.jsp");
                    dispatcher.forward(request, response);
                }
            } else {
                // password mismatch
                response.setStatus(400);
                request.setAttribute("errorMessage", "Incorrect password, cannot update.");
                dispatcher = request.getRequestDispatcher("editPassword.jsp");
                dispatcher.forward(request, response);
            }
        }
    }
}
