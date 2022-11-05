package controller;

import domain.User;
import util.AuthenticationEnforcer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AccountDetailController", value = {"/account-detail", "/account"})
public class AccountDetailController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (user == null) {
            // user not logged in
            dispatcher = request.getRequestDispatcher("error.jsp");
            response.setStatus(401);
        } else {
            request.setAttribute("user", user);
            dispatcher = request.getRequestDispatcher("accountDetail.jsp");
        }
        dispatcher.forward(request,response);
    }
}
