import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Authorisation extends JFrame {
    private JTable table;
    private JButton backButton;
    private JButton logoutButton;
    public DefaultTableModel logTable;
    // change password to work for your db
    private String dbRootPassword = "";

    public static void main(String[] args) {
        Authorisation auth = new Authorisation("u1@gmail.com");
    }

    public Authorisation(String userEmail) {
        // Set up the View Logs page
        setTitle("View Logs for: " + userEmail);
        setSize(800, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Set up the table with the columns
        String[] columns = { "LogID", "Email Address:", "Log Date:", "Log Time:", "Feature Accessed:" };
        logTable = new DefaultTableModel(columns, 0);
        table = new JTable(logTable);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        // Adds two buttons to the bottom of the panel and center them
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back");
        // ActionListener to take the user back to the Main Menu page
        backButton.addActionListener(ev -> back(userEmail));
        logoutButton = new JButton("LogOut");
        // ActionListener to log the user out and return to the Login page
        logoutButton.addActionListener(e -> logout());
        // Adds the two buttons to the page
        buttonPanel.add(backButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
        // Makes the frame visible
        getLogs(userEmail);
        setVisible(true);

        
    }

    // Method to go back to the Main Menu page without logging out the user
    public void back(String currentUser) {
        MainMenu menu = new MainMenu(currentUser);
        dispose();
    }

    // Method to log out and go to the Login page
    public void logout() {
        Login login = new Login();
        dispose();
    }

    public boolean getLogs(String userEmail){
        // Connects to the database and then prepares a statement to gather the user logs for a specific email
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * "
                        + "FROM userlogs "
                        + "WHERE Email = ?"
                )) {
                    preparedStatement.setString(1, userEmail);
                    ResultSet result = preparedStatement.executeQuery();
                    
                // Loop through the results and add each row to the table model
                while (result.next()) {
                    String userID = result.getString("LogID");
                    String email = result.getString("Email");
                    String logDate = result.getString("LogDate");
                    String logTime = result.getString("LogTime");
                    String featureAccessed = result.getString("FeatureAccessed");
                    Object[] row = { userID, email, logDate, logTime, featureAccessed };
                    logTable.addRow(row);
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
    }
}
