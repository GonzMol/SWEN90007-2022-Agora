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
import java.util.UUID;

@WebServlet(name = "OrderDetailController", value = {"/order"})
public class OrderDetailController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = AuthenticationEnforcer.getUserFromSession(request);
        RequestDispatcher dispatcher;
        if (!(user instanceof StandardUser)) {
            // not a standard user
            if (user == null) {
                response.setStatus(401);
            } else {
                response.setStatus(403);
            }
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        String orderID = request.getParameter("id");
        if (orderID == null) {
            // no ID, 400 page
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        Order order = OrderMapper.fetch(UUID.fromString(orderID));
        if (order == null) {
            // no matching order, 400 page
            response.setStatus(400);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        if (!order.getBuyer().getId().equals(user.getId())) {
            // not the buyer
            response.setStatus(403);
            dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        request.setAttribute("order", order);
        dispatcher = request.getRequestDispatcher("orderDetail.jsp");
        dispatcher.forward(request, response);
    }
}
