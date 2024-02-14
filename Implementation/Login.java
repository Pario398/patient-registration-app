import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.event.KeyEvent;

public class Login extends JFrame { 

    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton showPasswordButton;
    private JButton exitButton;
    private JButton registerButton;
    //change password to work for your db
    private String dbRootPassword = "";

    public static void main(String[] args) {
        // Create an instance of the Login class to display the login page
        Login login = new Login();
    }

    public Login() {
        // Set up the login page
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Create a panel with a 4x2 grid layout to hold the login components
        JPanel panel = new JPanel(new GridLayout(4, 2));
        // Create the username label and text field and add them to the panel
        emailLabel = new JLabel("Email:");
        panel.add(emailLabel);
        usernameField = new JTextField();
        panel.add(usernameField);
        // When the enter key is pressed, the authenticaiton method is executed
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authentication(usernameField.getText(), new String(passwordField.getPassword()));
                }
            }
        });
        // Create the password label and password field and add them to the panel
        passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        // Set the echo character to '*' to hide the password as it is typed
        passwordField.setEchoChar('*');
        panel.add(passwordField);
        // Create the show password button and add it to the panel
        showPasswordButton = new JButton("Show Password");
        showPasswordButton.addActionListener(ev -> togglePassword());
        panel.add(showPasswordButton);
        // Create a register button and add it to the panel
        registerButton = new JButton("Register");
        registerButton.addActionListener(ev -> register());
        panel.add(registerButton);
        // Create the login button and add it to the panel
        loginButton = new JButton("Login");
        loginButton.addActionListener(
                ev -> authentication(usernameField.getText(), new String(passwordField.getPassword())));
        panel.add(loginButton);
        // When the enter key is pressed, the authenticaiton method is executed
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authentication(usernameField.getText(), new String(passwordField.getPassword()));
                }
            }
        });
        // Create the exit button and add it to the panel
        exitButton = new JButton("Exit");
        exitButton.addActionListener(ev -> System.exit(0));
        panel.add(exitButton);
        // Add the panel to the JFrame and make the JFrame visible
        add(panel);
        setVisible(true);
    }

    /*
     * authentication method with a temporary email and password
     */

    public void authentication(String email, String password) {
        //database parts repurposed from LoginSQL.java
        // Establish database connection
        // String url =
        // "jdbc:mysql://localhost/groupdatabase?user=root&password=qwerty";
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword)) {
            String selectUser = "SELECT * FROM accounts WHERE Email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(selectUser);
            statement.setString(1, email);
            statement.setString(2, password);

            // Execute query and check if the user exists
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                // User exists, so show main menu
                MainMenu menu = new MainMenu(email);
                dispose();
            } else {
                // User does not exist, show error message and once closed it shows a brand new Login page
                System.out.println("Invalid email or password");
                JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // Handle database connection errors
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Creates a new instance of Register page and disposes the Login page
    public void register() {
        RegisterNewUser run = new RegisterNewUser();
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

}
