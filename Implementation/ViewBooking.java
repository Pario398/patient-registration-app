import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ViewBooking extends JFrame {
    
    private JButton backButton, emptyButton, confirmButton;
    public String currentUser;
    private JTextArea chooseTime, year, months;

    public static void main(String[] args) {
        //ViewBooking booking = new ViewBooking("alice.smith@example.com");
        ViewBooking booking = new ViewBooking("u1@gmail.com");
    }
    /*
     * This first constructer constructs a form where the user can input a month and year
     * this will then be sent to the next class in the variable "inputDate"
     * if left blank inputDate = "all"
     */
    public ViewBooking(String userEmail) {
        currentUser = userEmail;

        setTitle("View Bookings for specific Month/Year");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(2, 3));


        chooseTime = new JTextArea("Enter a month and a year to view bookings. \n Leave blank to view all:");

        //Textboxes to enter the date
        year = new JTextArea("YYYY");
        months = new JTextArea("MM");

        year.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        months.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        chooseTime.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Font font = chooseTime.getFont();
        chooseTime.setFont(font.deriveFont(Font.BOLD));
        chooseTime.setEditable(false);
        chooseTime.setLineWrap(true);

        panel.add(chooseTime);
        panel.add(months);
        panel.add(year);


        // Create the back button and add it to the panel
        backButton = new JButton("Back");
        backButton.addActionListener(ev -> back(currentUser));
        panel.add(backButton);

        // Create the empty button and add it to the panel
        emptyButton = new JButton();
        panel.add(emptyButton);

        confirmButton = new JButton("confirm");
        confirmButton.addActionListener(ev -> confirm());
        panel.add(confirmButton);


        add(panel);
        setVisible(true);
    }

    public void back(String currentUser) {
        MainMenu menu = new MainMenu(currentUser);
        dispose();
    }

    public ViewBookingResults confirm(){
        String month = months.getText().strip();
        String inputDate = year.getText() + "-" + month;

        if (month.equals("MM") && year.getText().strip().equals("YYYY")){
            ViewBookingResults vbr  = new ViewBookingResults(currentUser, "all", this);
            //dispose();
            return vbr;
        } else if (month.strip().length() == 2 && year.getText().strip().length() == 4){
            ViewBookingResults vbr  = new ViewBookingResults(currentUser, inputDate, this);
            //dispose();
            return vbr;
        }   
        else{
            JOptionPane.showMessageDialog(this, "Invalid input, please try again.","Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}


class ViewBookingResults extends JFrame{
    private JTable table;
    private JButton backButton;
    // change password to work for your db
    private String dbRootPassword = "";
    public String currentUser;
    public DefaultTableModel bookingtable;

    /*
     * This constructs a form with table to display the users bookings
     * The inputDate is the date given by the user on the previous form.
     * This form will be filled with the bookings that are in that month and year.
     * If the previous form was left blank, that means the user wants to see all there bookings
     * so then the form will load all there bookings.
     */
    public ViewBookingResults(String userEmail, String inputDate, ViewBooking vb){
        vb.dispose();
        //System.out.println(inputDate);
        currentUser =userEmail;
        setTitle("View Bookings for: " + userEmail);
        setSize(800, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        String[] columns = { "Booking ID", "Forename", " Surname", "Date", "Time", "Notes" };
        bookingtable = new DefaultTableModel(columns, 0);
        table = new JTable(bookingtable);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        backButton = new JButton("Main Menu");
        backButton.addActionListener(ev -> back(userEmail));
        add(backButton, BorderLayout.WEST);
        loadBookings(inputDate);

        setVisible(true);
    }
    /*
     * This method querys the database a gets all the bookings for the current user
     * Either after the given date
     * or all the bookings 
     * depending on what the user wanted.
     */
    public boolean loadBookings(String inputDate){
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query;
            if (!inputDate.equals("all")){
                query =  "SELECT * FROM booking JOIN doctors ON booking.DoctorID = doctors.DoctorID WHERE Email = '" + currentUser + "' AND Date LIKE '%" +  inputDate  + "%'";
            
            }else{
                query =  "SELECT * FROM booking JOIN doctors ON booking.DoctorID = doctors.DoctorID WHERE Email = '" + currentUser  + "'";
            
            }
            ResultSet result;
            result = statement.executeQuery(query);
            
            while (result.next()) {
                int bookingID = result.getInt("BookingID");
                String doctorForeName = result.getString("Forename");
                String doctorsurName = result.getString("Surname");
                String date = result.getString("Date");
                String time = result.getString("Time");
                String notes = result.getString("Notes");
                Object[] row = { bookingID, doctorForeName, doctorsurName, date, time, notes };
                bookingtable.addRow(row);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /*
     * This method returns the user back to the main menu
     * and closes this form.
     */
    public void back(String currentUser) {
        MainMenu menu = new MainMenu(currentUser);
        dispose();
    }
}