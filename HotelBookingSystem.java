import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class HotelBookingSystem extends JFrame {
    private JTextField nameField;
    private JTextField phoneNumberField;
    private JComboBox<String> areaComboBox;
    private JComboBox<String> hotelComboBox;

    private Map<String, String[]> areaToHotels; // Mapping of areas to hotels
    private final DatabaseManager dbManager;

    public HotelBookingSystem() {
        super("Hotel Booking System");
        dbManager = new DatabaseManager();
        initializeAreaToHotels();
        initializeGUI();
    }


    private void initializeAreaToHotels() {
        areaToHotels = new HashMap<>();
        areaToHotels.put("Hinjewadi", new String[]{"Orrietel Hotel","Bizz Tammana Hotel","Lemon Tree Hotel", "Holiday Inn"});
        areaToHotels.put("Kalyani Nagar", new String[]{"Royal Orchid Hotel","Hyatt Pune","Park Omante", "The Westin Pune"});
        areaToHotels.put("Viman Nagar", new String[]{"Lemon Tree Hotel","Hotel Parc Estique","Novotel Pune", "Four Points Pune"});
        areaToHotels.put("Baner", new String[]{"Hotel Amrita","FabHotel24","Serenity", "Monarch Entities Hotel"});
        areaToHotels.put("BHS", new String[]{"Surplas Giants Hotel","Mega Hotel","Cummins The Hotel", "Sky Stories"});
        areaToHotels.put("Aundh", new String[]{"Rajubhai Hotel","Orchid Hotel","Muran Motels", "Legend Stays"});
        areaToHotels.put("WAKAD", new String[]{"Orchid Hotel","Ginger Hotel","", "Hotel Ashoka"});
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        add(phoneNumberField);

        add(new JLabel("Choose an area:"));
        areaComboBox = new JComboBox<>(areaToHotels.keySet().toArray(new String[0]));
        areaComboBox.addActionListener(e -> updateHotelComboBox());
        add(areaComboBox);

        add(new JLabel("Choose a hotel:"));
        hotelComboBox = new JComboBox<>();
        updateHotelComboBox(); // Populate hotel combo box based on default area
        add(hotelComboBox);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitBooking());
        add(submitButton);

        setVisible(true);
    }

    private void updateHotelComboBox() {
        String selectedArea = (String) areaComboBox.getSelectedItem();
        hotelComboBox.removeAllItems(); // Clear existing items

        if (selectedArea != null && areaToHotels.containsKey(selectedArea)) {
            String[] hotels = areaToHotels.get(selectedArea);
            for (String hotel : hotels) {
                hotelComboBox.addItem(hotel);
            }
        }
    }

    private void validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {
        if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d+")) {
            throw new InvalidPhoneNumberException("Phone number must be a 10-digit numeric value.");
        }
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
    private void submitBooking() {
        String name = nameField.getText();
        String phoneNumberStr = phoneNumberField.getText();
        String area = (String) areaComboBox.getSelectedItem();
        String hotel = (String) hotelComboBox.getSelectedItem();

        try {
            validatePhoneNumber(phoneNumberStr); // Validate phone number
            validateIntegerInput(phoneNumberStr);//Validate it is >0
            long phoneNumber = Long.parseLong(phoneNumberStr);
            dbManager.saveUserDetails(name, phoneNumber);
            dbManager.updateHotelDetails(phoneNumber, area, hotel);

            HotelOperationsWindow operationsWindow = new HotelOperationsWindow(dbManager, phoneNumber);
            operationsWindow.setVisible(true);

            JOptionPane.showMessageDialog(this, "Registered successfully! Proceeding to main Menu...........");
        } catch (InvalidPhoneNumberException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid phone number format.");
        } catch (InvalidIntegerInputException e) {
            JOptionPane.showMessageDialog(phoneNumberField, e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelBookingSystem::new);
    }
}
