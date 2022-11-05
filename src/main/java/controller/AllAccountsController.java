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
import java.util.ArrayList;

@WebServlet(name = "AllAccountsController", value = {"/accounts", "/all-accounts"})
public class AllAccountsController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (!(user instanceof Admin)) {
            // not an Admin
            response.setStatus(404);    // pretend page doesn't exist
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        ArrayList<StandardUser> users = UserMapper.fetchAllStandardUsers();
        request.setAttribute("users", users);
        dispatcher = request.getRequestDispatcher("adminHome.jsp");
        dispatcher.forward(request, response);
    }
}
