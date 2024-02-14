import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class arrangeBookings extends JFrame {

    private JPanel panel;
    private JLabel titleLabel, dateLabel, timeLabel;
    private JTextField dateTextField, timeTextField;
    private JButton submitButton;
    private JButton backButton;
    private String currentUser = "";
    private String dbRootPassword = "";
    private dbHelper dbh = new dbHelper();

    public static void main(String[] args) {
        arrangeBookings booking =new arrangeBookings( "u1@gmail.com");
    }

    /*
     * This form is used by the patient to make a booking by giving a date and time
     * This form contains two textboxes for the date and time respectively.
     * It also contains a back buttona and a submit button.
     * The program automatically knows who is logged in and who their doctor is.
     * If they are a new account and dont have a doctor they wont be able to make a booking even if they click the submit button.
     */
    public arrangeBookings(String username) {
        currentUser = username;
        setTitle("Arrange Booking: "+ username);
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        titleLabel = new JLabel("Enter booking details:", JLabel.CENTER);
        dateLabel = new JLabel("Date (yyyy-mm-dd):", JLabel.RIGHT);
        
        
        timeLabel = new JLabel("Time (hh:mm):", JLabel.RIGHT);
        dateTextField = new JTextField();
        timeTextField = new JTextField();
        
        //when the submit button is clicked it runs the make booking method in the dbhelper class
        //using the data given by the user in this form.
        submitButton = new JButton("Submit");
        submitButton.addActionListener(ev -> {boolean mB = dbh.makeBooking(currentUser,dateTextField.getText(), timeTextField.getText());
            if (mB == false){
                JOptionPane.showMessageDialog(null, "Error! Booking Unavailable.");
            } else{
                JOptionPane.showMessageDialog(null, "Success! Booking Complete");
                MainMenu mm = new MainMenu(currentUser);
                dispose();
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(ev -> back());

        panel.add(backButton);
        panel.add(titleLabel);
        panel.add(new JLabel(""));
        panel.add(dateLabel);
        panel.add(dateTextField);
        panel.add(timeLabel);
        panel.add(timeTextField);
        panel.add(new JLabel(""));
        panel.add(submitButton);
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    /*
     * This button takes the user back to the main menu and disposes of this form.
     */
    public void back() {
        MainMenu menu = new MainMenu(currentUser);
        dispose();
    }
    
}
