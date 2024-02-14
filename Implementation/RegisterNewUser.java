import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterNewUser extends JFrame {

    private JLabel firstnameLabel, secondnameLabel, emailLabel, passwordLabel;
    private JTextField firstnameField, secondnameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, showPasswordButton, backButton, emptyButton;
    private String dbPassword = "";// Change this in accordance with your DB's root password

    public static void main(String[] args) {
        // Create an instance of the RegisterNewUser class to display the register page
        RegisterNewUser RegisterNewUser = new RegisterNewUser();
    }

    public RegisterNewUser() {
        setTitle("Register Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 4));
        // Create the firstname label and text field and add them to the panel
        firstnameLabel = new JLabel("Firstname:");
        panel.add(firstnameLabel);
        firstnameField = new JTextField();
        panel.add(firstnameField);

        // Create the secondname label and text field and add them to the panel
        secondnameLabel = new JLabel("Secondname:");
        panel.add(secondnameLabel);
        secondnameField = new JTextField();
        panel.add(secondnameField);

        // Create the email label and text field and add them to the panel
        emailLabel = new JLabel("Email:");
        panel.add(emailLabel);
        emailField = new JTextField();
        panel.add(emailField);

        // Create the password label and password field and add them to the panel
        passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);
        passwordField = new JPasswordField();

        // Set the echo character to '*' to hide the password as it is typed
        passwordField.setEchoChar('*');
        panel.add(passwordField);

        // Create the back button and add it to the panel
        backButton = new JButton("Back");
        backButton.addActionListener(ev -> back());
        panel.add(backButton);

        // Creates an empty button for formatting
        emptyButton = new JButton("");
        panel.add(emptyButton);

        // Create the show password button and add it to the panel
        showPasswordButton = new JButton("Show Password");
        showPasswordButton.addActionListener(ev -> togglePassword());
        panel.add(showPasswordButton);

        // Create a register button and add it to the panel
        registerButton = new JButton("Register");
        registerButton.addActionListener(ev -> {
            String Fname = firstnameField.getText();
            String Lname = secondnameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            Boolean reg = register(Fname, Lname, email, password);
            if (reg == true) {
                MainMenu mm = new MainMenu(email);
                dispose();
            }

        });
        panel.add(registerButton);

        // Add the panel to the JFrame and make the JFrame visible
        add(panel);
        setVisible(true);
    }

    /*
     * Takes the user back to the Login page
     */
    public void back() {
        Login login = new Login();
        dispose();
    }

    /*
     * Toggle password visibility when the show password button is clicked
     */
    public void togglePassword() {
        if (passwordField.getEchoChar() == '*') {
            // Show the password if it is currently hidden
            passwordField.setEchoChar((char) 0);
            showPasswordButton.setText("Hide Password");
        } else {
            // Hide the password if it is currently visible
            passwordField.setEchoChar('*');
            showPasswordButton.setText("Show Password");
        }

    }

    public boolean register(String Fname, String Lname, String email, String password) {
        if (Fname.equals("") || Lname.equals("") || email.equals("") || password.equals("")){
            return false;
        }
        // Connect to database
        try (Connection conn = DriverManager
                .getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbPassword);
                // Create statement to insert user into table
                PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO accounts(Forename, Surname, Email, Password, isDoctor) VALUES (?, ?, ?, ?, 'NO')")) {
            statement.setString(1, Fname);
            statement.setString(2, Lname);
            statement.setString(3, email);
            statement.setString(4, password);

            statement.executeUpdate();

            // Execute statement and check for success
            // int rowsInserted = statement.executeUpdate();
            dbHelper dbh = new dbHelper();
            dbh.AddMessage(email, "Welcome new user!", "System");
            return true;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error! Account details are already registered.");
            return false;
        }

        // System.out.println("Register button clicked");
    }
}
