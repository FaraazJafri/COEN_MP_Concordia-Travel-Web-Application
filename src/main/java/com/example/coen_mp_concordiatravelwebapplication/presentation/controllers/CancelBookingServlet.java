package com.example.coen_mp_concordiatravelwebapplication.presentation.controllers;

import com.example.coen_mp_concordiatravelwebapplication.dataaccess.*;
import com.example.coen_mp_concordiatravelwebapplication.models.bookingModels.Booking;
import com.example.coen_mp_concordiatravelwebapplication.models.bookingModels.Customer;
import com.example.coen_mp_concordiatravelwebapplication.models.userModels.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CancelBookingServlet", value = "/CancelBookingServlet")
public class CancelBookingServlet extends HttpServlet {
    private PackageDAO packageDAO;
    private CustomerDAO customerDAO;
    private UserDAO userDAO;
    private BookingDAO bookingDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the implementation
        packageDAO = new PackageDAOImpl();
        customerDAO = new CustomerDAOImpl();
        userDAO = new UserDAOImpl();
        bookingDAO = new BookingDAOImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int customerID = 0;
        if ("Customer".equals(session.getAttribute("role"))) {
            String customerUsername = (String) session.getAttribute("username");
            customerID = userDAO.getID(customerUsername);
            Customer customer = customerDAO.getSelectedCustomer(String.valueOf(customerID));

            if (customer != null) {
                String firstName = customer.getFirstName();
                String lastName = customer.getLastName();

                String fullName = firstName + " " + lastName;

                request.setAttribute("selectedCustomer", fullName);
            }
            List<Booking> customerBookings = customerDAO.getSeletedCustomerBookings(String.valueOf(customerID));
            request.setAttribute("customerBookings", customerBookings);
        }
        List<User> customers = new ArrayList<>();
        if ("Admin".equals(session.getAttribute("role"))) {
            // Retrieve the list of customers from the database
            customers = userDAO.getOnlyCustomers();
        }
        if ("Agent".equals(session.getAttribute("role"))) {
            // Retrieve the list of customers from the database
            String customerUsername = (String) session.getAttribute("username");
            String userId = String.valueOf(userDAO.getID(customerUsername));
            customers = userDAO.getLinkedCustomers(userId);
        }

        // Set the list of customers as a request attribute
        request.setAttribute("customers", customers);

        // Forward the request to the JSP page
        request.getRequestDispatcher("cancelbooking.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customerId = request.getParameter("customerId");
        String bookingId = request.getParameter("bookingId");
        int rowsDeleted = bookingDAO.cancelBooking(customerId, bookingId);

        if (rowsDeleted > 0) {
            request.setAttribute("message", "Your Booking has been successfully cancelled!");
            doGet(request,response);
        } else {

            response.sendRedirect("CustomerBookingsServlet?customerId=" + customerId + "&error=1");
        }
    }


}
