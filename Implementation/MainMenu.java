import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import com.mysql.cj.protocol.Resultset;
import java.sql.*;
import java.awt.event.KeyEvent;

public class MainMenu extends JFrame {

    private JButton viewSystemLogsButton;
    private JButton changeDoctorButton;
    private JButton arrangeBookingsButton;
    private JButton viewBookingsButton;
    private JButton rescheduleBookingsButton;
    private JButton visitDetailsButton;
    private JButton backButton;
    private JButton exitButton;

    private String dbRootPassword = "";// Change this in accordance with your DB's root password
    private String currentUser = "u1";// this is the default value, use u1 for testing.

    public static void main(String[] args) {
        MainMenu menu = new MainMenu("u1@gmail.com");
    }

    public MainMenu(String username) { // This form will be loaded once the user has logged in.
        // The current users' username will need to be passed into the contructor
        currentUser = username; // This sets the global variable

        // Set up the main menu
        setTitle("Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create a panel with a vertical box layout to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        // Create the button to take the user to the view system logs and adds it to the
        // panel
        viewSystemLogsButton = new JButton("View System Logs");
        viewSystemLogsButton.addActionListener(ev -> viewSystemLogs());
        panel.add(viewSystemLogsButton);
        // Create the button to take the user to the change doctor and adds it to the
        // panel
        changeDoctorButton = new JButton("Change/Choose Doctor");
        changeDoctorButton.addActionListener(ev -> changeDoctor());
        panel.add(changeDoctorButton);
        // Create the button to take the user to the arrange bookings and adds it to the
        // panel
        arrangeBookingsButton = new JButton("Arrange Bookings");
        arrangeBookingsButton.addActionListener(ev -> arrangeBookings());
        panel.add(arrangeBookingsButton);
        // Create the button to take the user to the view bookings and adds it to the
        // panel
        viewBookingsButton = new JButton("View Bookings");
        viewBookingsButton.addActionListener(ev -> viewBookings(currentUser));
        panel.add(viewBookingsButton);
        // Create the button to take the user to the reschedule bookings and adds it to
        // the panel
        rescheduleBookingsButton = new JButton("Reschedule Bookings");
        rescheduleBookingsButton.addActionListener(ev -> rescheduleBookings());
        panel.add(rescheduleBookingsButton);
        // Create the button to take the user to the visit details and adds it to the
        // panel
        visitDetailsButton = new JButton("Visit Details");
        visitDetailsButton.addActionListener(ev -> visitDetails());
        panel.add(visitDetailsButton);
        // Create the button to take the user back to the Login page and adds it to the
        // panel
        backButton = new JButton("Logout");
        backButton.addActionListener(ev -> back());
        panel.add(backButton);
        // Create the exit button and adds it to the panel
        exitButton = new JButton("Exit");
        exitButton.addActionListener(ev -> System.exit(0));
        panel.add(exitButton);

        /// THIS PART LOADS THE MESSAGES////////////////////////////

        ArrayList<String> allMessages = new ArrayList<>();
        allMessages = getMessage(currentUser);

        // This turns the arraylist into an array so that it can be put into a JTable
        String[][] data = new String[allMessages.size()][2];
        int i = 0;
        for (int j = 0; j < allMessages.size(); j++) {
            data[i][0] = allMessages.get(j); // This is the message

            j++;
            data[i][1] = allMessages.get(j); // This is the sender
            i++;
        }
        String[] columnNames = { "Messages", "From" };

        JTable msgTable = new JTable(data, columnNames);
        msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        panel.add(new JScrollPane(msgTable));

        // System.out.println(data[1][0]);

        ///////////////////////////////////////////////////////
        // Add the panel to the JFrame and make the JFrame visible
        add(panel);
        setVisible(true);

    }

    /*
     * This method returns an arraylist of the messages recieved by the current user
     * from the database
     * Ordering them starting with the most recent message.
     */
    public ArrayList<String> getMessage(String user) {
        ArrayList<String> allMessages = new ArrayList<>();
        ArrayList<String> allSenders = new ArrayList<>();
        try {
            Connection conn = DriverManager
                    .getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM messages WHERE recipient = '" + user + "' ORDER BY msgID DESC;";

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                allMessages.add(resultSet.getString("msg"));
                allMessages.add(resultSet.getString("sender"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allMessages;
    }

    public boolean logAction(String featureAccessed, String user) {
        // Connects to the database and then prepares a statement to store the user logs
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO userlogs (Email, LogDate, LogTime, FeatureAccessed)"
                        + "VALUES (?, ?, ?, ?)"
                )) {
    
            // The following then populates the prepared statement with all the necessary information
            //preparedStatement.setString(1, "1");                                            // Not sure what LogID is so 1 is a placeholder
            preparedStatement.setString(1, user);
            preparedStatement.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            preparedStatement.setTime(3, java.sql.Time.valueOf(java.time.LocalTime.now()));
            preparedStatement.setString(4, featureAccessed);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
     * Takes the user back to the Login page
     */
    public void back() {
        Login login = new Login();
        dispose();
    }

    /*
     * Takes the user to the View System Logs page and logs the action
     */
    public void viewSystemLogs() {
        logAction("viewSystemLogs", currentUser);
        Authorisation auth = new Authorisation(currentUser);
        dispose();
    }

    /*
     * Takes the user to the Change Doctor page and logs the action
     */
    public void changeDoctor() {
        logAction("changeDoctor", currentUser);
        DoctorsList dl = new DoctorsList(currentUser);
        dispose();
    }

    /*
     * Takes the user to the Arrange Bookings page and logs the action
     */
    public void arrangeBookings() {
        logAction("arrangeBookings", currentUser);
        arrangeBookings booking = new arrangeBookings(currentUser);
        dispose();
    }

    /*
     * Takes the user to the View Bookings page and logs the action
     */
    public void viewBookings(String currentUser) {
        logAction("viewBookings", currentUser);
        ViewBooking booking = new ViewBooking(currentUser);
        dispose();
    }

    /*
     * Takes the user to the Reschedule Bookings page and logs the action
     */
    public void rescheduleBookings() {
        logAction("rescheduleBookings", currentUser);
        rescheduleBooking rb = new rescheduleBooking(currentUser);
        dispose();
    }

    public String getCurrentUser(){
        return currentUser;
    }

    /*
     * Takes the user to the Visit Details page and logs the action
     */
    public void visitDetails() {
        logAction("visitDetails", currentUser);
        VisitDetails vd = new VisitDetails(currentUser);
        dispose();
    }
}
