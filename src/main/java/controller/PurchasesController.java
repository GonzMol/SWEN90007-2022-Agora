package controller;

import datasource.OrderMapper;
import domain.Order;
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

@WebServlet(name = "PurchasesController", value = "/purchases")
public class PurchasesController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (!(user instanceof StandardUser)) {
            // not a standard user
            response.setStatus(user == null ? 401 : 403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        ArrayList<Order> orders = OrderMapper.fetchWithBuyerId(user.getId());
        if (orders != null) {
            for (int i = orders.size() - 1; i >= 0; i--) {
                if (orders.get(i).isCancelled()) {
                    orders.remove(i);
                }
            }
        }
        request.setAttribute("orders", orders);
        request.setAttribute("userRole", "buyer");
        dispatcher = request.getRequestDispatcher("viewOrders.jsp");
        dispatcher.forward(request, response);

    }
}
