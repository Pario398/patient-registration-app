import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VisitDetails extends JFrame {
    public String currentUser = "";
    public JTable visitTable;
    public String dbRootPassword = "";
    public JButton backBtn;
    public DefaultTableModel modelTable;

    public static void main(String[] args){
        // Creates an instance of the VisitDetails class, do allow the user to access all logs of all access
        VisitDetails vd = new VisitDetails("u1@gmail.com");
    }

    public VisitDetails(String username){
        // Some definitions for the JFrame and formatting
        currentUser = username;
        setTitle("Visit Details: " + username);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);

        // Initiates the table
        String[] columns = {"Date", "Time", "Notes", "Prescription", "Doctor"};
        modelTable = new DefaultTableModel(columns, 0);
        visitTable = new JTable(modelTable);
        add(new JScrollPane(visitTable), BorderLayout.CENTER);

        //This adds the column to the table
        visitTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        visitTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        visitTable.getColumnModel().getColumn(2).setPreferredWidth(303);
        visitTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        visitTable.getColumnModel().getColumn(4).setPreferredWidth(30);

        // Adds a back button to return to the main menu
        backBtn = new JButton("Back");
        backBtn.addActionListener(ev -> back());
        add(backBtn, BorderLayout.SOUTH);

        // Connects to the database and retrives the necessary data
        // Retrieves the booking Date, Time, Notes, Prescription and the Doctor's first and last name
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT b.Date, b.Time, b.Notes, b.Prescriptions, d.Forename, d.Surname " +
                                "FROM booking b " +
                                "INNER JOIN doctors d ON b.DoctorID = d.DoctorID " +
                                "WHERE b.Email = ? " +
                                "AND b.Date <= CURRENT_DATE " +
                                "ORDER BY Date ASC, Time ASC  ;")) {

            // The following then populates the table with the retrieved information from the table
            preparedStatement.setString(1, currentUser);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                //This adds the results to table
                String date = result.getString("Date");
                String time = result.getString("Time");
                String notes = result.getString("Notes");
                String prescriptions = result.getString("Prescriptions");
                String doctor = result.getString("Forename") + " " + result.getString("Surname");
                Object[] row = {date, time, notes, prescriptions, doctor};
                modelTable.addRow(row);
            }
        } 
        
        // Just a necessary error message in case anything went wrong with the database connection
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    // Runs the MainMenu class when the back button is pressed
    public void back(){
        MainMenu mm = new MainMenu(currentUser);
        dispose();
    }
}
