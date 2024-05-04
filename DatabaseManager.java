import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Hotel";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sharry1017";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get the active database connection
    public Connection getConnection() {
        return connection;
    }

    public void saveUserDetails(String name, long phoneNumber) {
        try {
            String sql = "INSERT INTO hotel (customer_name, phone_number) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setLong(2, phoneNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateHotelDetails(long phoneNumber, String area, String hotel) {
        try {
            String sql = "UPDATE hotel SET area_name = ?, hotel_name = ? WHERE phone_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, area);
            statement.setString(2, hotel);
            statement.setLong(3, phoneNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveRoomDetails(long phoneNumber, int roomNumber, int totalPrice) {
        try {
            String sql = "INSERT INTO room_booking (phone_number, room_number, total_price) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, phoneNumber);
            statement.setInt(2, roomNumber);
            statement.setInt(3, totalPrice);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelRoomReservation(int roomNumber) {
        try {
            String sql = "DELETE FROM room_booking WHERE room_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, roomNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFoodOrder(long phoneNumber, String food, int quantity, int totalPrice) {
        try {
            String sql = "INSERT INTO food_order (phone_number, food, quantity, total_price) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, phoneNumber);
            statement.setString(2, food);
            statement.setInt(3, quantity);
            statement.setInt(4, totalPrice);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to close the database connection
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
