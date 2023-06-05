package com.example.coen_mp_concordiatravelwebapplication.dataaccess;

import com.example.coen_mp_concordiatravelwebapplication.config.CONFIG;
import com.example.coen_mp_concordiatravelwebapplication.models.bookingModels.Booking;
import com.microsoft.sqlserver.jdbc.ISQLServerConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {
    String url = CONFIG.SQLURL;
    String username = CONFIG.SQLUSER;
    String password = CONFIG.SQLPASS;

    @Override
    public void saveBooking(String bookingId, String packageIdToBook, String customerIdToBook, Timestamp departureDate) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "INSERT INTO bookings (bookingId, packageId, customerId, departureDate) VALUES (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, bookingId);
            statement.setString(2, packageIdToBook);
            statement.setString(3, customerIdToBook);
            statement.setTimestamp(4, departureDate);

            // Execute the query
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();

            // Retrieve all bookings from the database
            String selectQuery = "SELECT * FROM bookings";
            ResultSet resultSet = statement.executeQuery(selectQuery);


            while (resultSet.next()) {
                String bookingIdResult = resultSet.getString("bookingId");
                String packageIdResult = resultSet.getString("packageId");
                String customerIdResult = resultSet.getString("customerId");
                Timestamp departureDateResult = resultSet.getTimestamp("departureDate");

                Booking booking = new Booking(bookingIdResult, packageIdResult, customerIdResult, departureDateResult);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public int cancelBooking(String customerId, String bookingId) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String deleteQuery = "DELETE FROM bookings WHERE customerId = ? AND bookingId = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, customerId);
            deleteStatement.setString(2, bookingId);

            // Execute the delete statement
            int rowsDeleted = deleteStatement.executeUpdate();

            // Close the statement
            deleteStatement.close();
            return rowsDeleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Boolean modifyBooking(String bookingId, String packageId, Timestamp departureDate) {

        try (Connection connection = DriverManager.getConnection(CONFIG.SQLURL, CONFIG.SQLUSER, CONFIG.SQLPASS)) {
            String updateQuery = "UPDATE bookings SET packageId = ?, departureDate = ? WHERE bookingId = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, packageId);
            updateStatement.setString(2, String.valueOf(departureDate));
            updateStatement.setString(3, bookingId);
            updateStatement.executeUpdate();


            updateStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
