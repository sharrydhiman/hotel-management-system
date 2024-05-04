import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;


public class HotelOperationsWindow extends JFrame {
    private final DatabaseManager dbManager;
    private final long phoneNumber;

    public HotelOperationsWindow(DatabaseManager dbManager, long phoneNumber) {
        super("Hotel Operations");
        this.dbManager = dbManager;
        this.phoneNumber = phoneNumber;
        initializeGUI();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLayout(new GridLayout(4, 1));

        JButton bookRoomButton = new JButton("Book a Room");
        bookRoomButton.addActionListener(e -> bookRoom());
        add(bookRoomButton);

        JButton cancelRoomButton = new JButton("Cancel Room");
        cancelRoomButton.addActionListener(e -> cancelRoom());
        add(cancelRoomButton);

        JButton orderFoodButton = new JButton("Order Food");
        orderFoodButton.addActionListener(e -> orderFood());
        add(orderFoodButton);

        JButton endProgramButton = new JButton("End Program");
        endProgramButton.addActionListener(e -> {
            dbManager.close();
            dispose();
            System.exit(0);
        });
        add(endProgramButton);
    }

    private void validateIntegerInput(String input) throws InvalidIntegerInputException {
        try {
            int value = Integer.parseInt(input);

            if (value <= 0) {
                throw new InvalidIntegerInputException("Value must be a positive integer.");
            }

            // If no exception is thrown, the input is valid
        } catch (NumberFormatException e) {
            throw new InvalidIntegerInputException("Input must be a valid integer without decimals.");
        }
    }

    private void cancelRoom() {
        JFrame cancelFrame = new JFrame("Cancel Room");
        cancelFrame.setSize(300, 200);
        cancelFrame.setLayout(new GridLayout(3, 2));

        JTextField roomNumberField = new JTextField();
        JTextField phoneNumberField = new JTextField();

        JButton cancelButton = getjButton(roomNumberField, phoneNumberField, cancelFrame);

        cancelFrame.add(new JLabel("Room Number:"));
        cancelFrame.add(roomNumberField);

        cancelFrame.add(new JLabel("Phone Number:"));
        cancelFrame.add(phoneNumberField);

        cancelFrame.add(cancelButton);

        cancelFrame.setVisible(true);
    }

    private JButton getjButton(JTextField roomNumberField, JTextField phoneNumberField, JFrame cancelFrame) {
        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.addActionListener(e -> {
            try {

                int roomNumber = Integer.parseInt(roomNumberField.getText());
                long phoneNumber = Long.parseLong(phoneNumberField.getText());

                if (!validateRoomAndPhoneNumber(roomNumber, phoneNumber)) {
                    throw new RoomMismatchException("Room number and phone number do not match any existing booking.");
                }

                dbManager.cancelRoomReservation(roomNumber);

                JOptionPane.showMessageDialog(cancelFrame, "Room reservation canceled successfully.");

            } catch (RoomMismatchException ex) {
                JOptionPane.showMessageDialog(cancelFrame, ex.getMessage());
            }
        });
        return cancelButton;
    }

    private boolean validateRoomAndPhoneNumber(int roomNumber, long phoneNumber) throws RoomMismatchException {
        try {
            String query = "SELECT * FROM room_booking WHERE room_number = ? AND phone_number = ?";
            PreparedStatement statement = dbManager.getConnection().prepareStatement(query);
            statement.setInt(1, roomNumber);
            statement.setLong(2, phoneNumber);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // Returns true if there's a match
        } catch (SQLException e) {
            throw new RoomMismatchException("An error occurred while validating the booking.");
        }
    }

    public enum RoomType {
        STANDARD(2),  // 2 people max
        QUEEN_SUITE(4), // 4 people max
        KING_SUITE(4),      // 4 people max
        DELUXE_SUITE(6);    // 6 people max

        private final int maxCapacity;

        RoomType(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }
    }

    private void bookRoom() {
        JFrame bookingFrame = new JFrame("Room Booking");
        bookingFrame.setSize(300, 200);
        bookingFrame.setLayout(new GridLayout(4, 2));

        JTextField daysField = new JTextField();
        JTextField peopleField = new JTextField();

        RoomType[] roomTypes = RoomType.values(); // Get all room types
        JComboBox<RoomType> roomTypeComboBox = new JComboBox<>(roomTypes); // Allow selection of room type

        JButton calculateButton = new JButton("Calculate Total Price");
        calculateButton.addActionListener(e -> {
            try {
                validateIntegerInput(daysField.getText());
                validateIntegerInput(peopleField.getText());

                int days = Integer.parseInt(daysField.getText());
                int people = Integer.parseInt(peopleField.getText());

                RoomType selectedRoomType = (RoomType) roomTypeComboBox.getSelectedItem();

                // Validate capacity based on selected room type
                assert selectedRoomType != null;
                if (people > selectedRoomType.getMaxCapacity()) {
                    throw new MaxCapacityExceededException(
                            "The selected room type can accommodate a maximum of " +
                                    selectedRoomType.getMaxCapacity() + " people."
                    );
                }

                int totalPrice = days * people * 1500; // Assuming $1500 per day per person
                int roomNumber = assignRoomNumber(); // Assign a random room number

                // Ask for confirmation before booking the room
                int response = JOptionPane.showConfirmDialog(bookingFrame,
                        "Total Price: $" + totalPrice + "\nRoom Number: " + roomNumber +
                                "\nRoom Type: " + selectedRoomType.name() +
                                "\nDo you want to book this room?", "Confirm Booking",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    dbManager.saveRoomDetails(phoneNumber, roomNumber, totalPrice); // Save booking details
                    JOptionPane.showMessageDialog(bookingFrame,
                            "Room booked successfully!\nRoom Number: " + roomNumber);
                } else {
                    JOptionPane.showMessageDialog(bookingFrame, "Booking cancelled.");
                }
            } catch (InvalidIntegerInputException | MaxCapacityExceededException ex) {
                JOptionPane.showMessageDialog(bookingFrame, ex.getMessage());
            }
        });

        bookingFrame.add(new JLabel("Room Type:"));
        bookingFrame.add(roomTypeComboBox);

        bookingFrame.add(new JLabel("Number of Days:"));
        bookingFrame.add(daysField);

        bookingFrame.add(new JLabel("Number of People:"));
        bookingFrame.add(peopleField);

        bookingFrame.add(calculateButton);

        bookingFrame.setVisible(true);
    }

    private int assignRoomNumber() {
        Random random = new Random();
        return random.nextInt(100) + 1; // Random room number between 1 and 100
    }



    private void orderFood() {
        JFrame orderFrame = new JFrame("Order Food");
        orderFrame.setSize(400, 250);
        orderFrame.setLayout(new GridLayout(5, 2));

        String[] foodItems = {"Pizza", "Burger", "Pasta", "Salad"};
        JComboBox<String> foodComboBox = new JComboBox<>(foodItems);

        JTextField quantityField = new JTextField();

        JButton calculateButton = new JButton("Calculate Total Price");
        calculateButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                int price = getPrice((String) Objects.requireNonNull(foodComboBox.getSelectedItem()));
                validateIntegerInput(String.valueOf(quantity));//Validate it is >0

                int totalPrice = quantity * price;

                // Ask for confirmation before placing the food order
                int response = JOptionPane.showConfirmDialog(orderFrame,
                        "Food: " + foodComboBox.getSelectedItem() + "\nQuantity: " + quantity +
                                "\nTotal Price: $" + totalPrice +
                                "\nDo you want to place this order?", "Confirm Order",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    dbManager.saveFoodOrder(phoneNumber, (String) foodComboBox.getSelectedItem(), quantity, totalPrice);

                    JOptionPane.showMessageDialog(orderFrame, "Food order placed successfully!");
                } else {
                    JOptionPane.showMessageDialog(orderFrame, "Order cancelled.");
                }
            }  catch (InvalidIntegerInputException ex) {
                JOptionPane.showMessageDialog(quantityField, "Enter Valid Quantity");
            }
        });

        orderFrame.add(new JLabel("Select Food:"));
        orderFrame.add(foodComboBox);

        orderFrame.add(new JLabel("Quantity:"));
        orderFrame.add(quantityField);

        orderFrame.add(calculateButton);

        orderFrame.setVisible(true);
    }

    private int getPrice(String food) {
        return switch (food) {
            case "Pizza" -> 10;
            case "Burger" -> 8;
            case "Pasta" -> 12;
            case "Salad" -> 6;
            default -> 0;
        };
    }
}
