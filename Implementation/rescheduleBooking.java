import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class rescheduleBooking extends JFrame {
    
    private JTextArea chooseTime, year, months, days;;
    private JComboBox minuteBox, hourBox;
    private JButton checkButton, backButton, emptyButton;
    private String hourSelected, minuteSelected;
    public String currentUser;
    //private String dbPassword = "";// Change this in accordance with your DB's root password
        public static void main(String[] args) {
        // Create an instance of the rescheduleBooking class to display the reschedule page
        rescheduleBooking rescheduleBooking = new rescheduleBooking("u1@gmail.com");
    }

    public rescheduleBooking(String username) {
        currentUser = username;
        setTitle("Reschedule Appointment: " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(3, 3));

        //creates a label to indicate where to select the user's desired time
        chooseTime = new JTextArea("Please enter the Time and Date\nof the booking you want to \nreschedule:");
        Font font = chooseTime.getFont();
        chooseTime.setFont(font.deriveFont(Font.BOLD));
        chooseTime.setEditable(false);
        chooseTime.setLineWrap(true);
        chooseTime.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(chooseTime);

        //creates a drop down box for the hours
        String[] hours = {"Hour", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
        hourBox = new JComboBox<>(hours);
        hourBox.setSelectedIndex(0);
        hourBox.setBounds(50, 50, 150, 30);
        panel.add(hourBox);

        // Creates a listener to read the selected option in hourBox and assign it to a variable
        ActionListener hourBoxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hourSelected = (String) hourBox.getSelectedItem();
            }
        };

        

        //creates a drop down box for the minutes, at 15 minute intervals
        String[] minutes = {"Minute","00", "15", "30", "45"};
        minuteBox = new JComboBox<>(minutes);
        minuteBox.setSelectedIndex(0);
        minuteBox.setBounds(50, 50, 150, 30);
        panel.add(minuteBox);
        
        //Textboxes to enter the date
        year = new JTextArea("YYYY");
        months = new JTextArea("MM");
        days = new JTextArea("DD");

        year.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        months.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        days.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(year);
        
        panel.add(months);
        panel.add(days);


        // Creates a listener to read the selected option in minuteBox and assign it to a variable
        ActionListener minuteBoxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minuteSelected = (String) minuteBox.getSelectedItem();
            }
        };

        // Initiates both listeners
        hourBox.addActionListener(hourBoxListener);
        minuteBox.addActionListener(minuteBoxListener);


        // Create the back button and add it to the panel
        backButton = new JButton("Back");
        backButton.addActionListener(ev -> back());
        panel.add(backButton);

        // Creates an empty button for formatting
        emptyButton = new JButton("");
        panel.add(emptyButton);

        // Create the check database button and add it to the panel
        checkButton = new JButton("Check Availability: ");
        checkButton.addActionListener(ev -> dbCheck());
        panel.add(checkButton);

        // Add the panel to the JFrame and make the JFrame visible
        add(panel);
        setVisible(true);
    }

    public void back() {
        System.out.println("back button pressed");
        MainMenu mainMenu = new MainMenu(currentUser);                        
        dispose();
    }

    /*
     * This method checks to see if the inputted date and time match an existing booking. 
     * If it does then the user is asked to confirm if they want to reschedule this booking
     * if they click yes it deletes the booking and takes them to the arrange bookings page.
     */
    public void dbCheck() {
      
        dbHelper dbh = new dbHelper();
        String doctorID = dbh.getDoctor(currentUser);

        String bookingDate = year.getText() + "-" + months.getText() + "-" + days.getText();

        String bookingTime = hourSelected + ":" + minuteSelected;
        String doctorEmail = dbh.getDoctorDetails(doctorID);
       
        if (hourSelected != null && minuteSelected != null && !hourSelected.equals("Hour") && !minuteSelected.equals("Minute")){
            if(!dbh.checkAvailabilityNonVerbose(doctorID, bookingDate, bookingTime)){
                String message = "Are you sure you want reschedule this booking? \n Clicking yes will delete this booking  (this can not be undone)";
                message = message + "\n and you will be taken to the next page where";
                message = message + "\n you will be able to reschedule the booking.";

                int reply = JOptionPane.showConfirmDialog(null, message, "Reschedule?",  JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    message = "Booking on " + bookingDate + " at " + bookingTime + " has been cancelled, and is being rescheduled!";
                    dbh.deleteBooking(currentUser, bookingDate, bookingTime);
                    dbh.AddMessage(currentUser,message, "System");
                    dbh.AddMessage(doctorEmail,message, "System");
                    arrangeBookings ab = new arrangeBookings(currentUser);
                    dispose();
                }
            } else{
                JOptionPane.showMessageDialog(this, "You have no booking on the given date/time","Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}