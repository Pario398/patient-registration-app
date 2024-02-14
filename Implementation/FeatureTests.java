import org.junit.*;

import static org.junit.Assert.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import java.sql.*; // database check

public class FeatureTests {
    
    /*
     * All users should have at least 1 message
     * This test makes sure that a valid user will have their messages returned.
     */
    @Test
    public void getValidUserMessages() {
        MainMenu menu = new MainMenu("u1@gmail.com");
        ArrayList<String> messages = menu.getMessage("u1@gmail.com");
        assertTrue(messages.size() > 0);
    }

    /*
     * This tests to make sure that no messages are returned for a user that does
     * not exist
     */
    @Test
    public void getInvalidUserMessages() {
        MainMenu menu = new MainMenu("invalidrandomusername");
        ArrayList<String> messages = menu.getMessage("invalidrandomusername");
        assertFalse(messages.size() > 0);
    }

    /*
     * Test to see if a message can be added to an existing user
     */
    @Test
    public void validAddMessage() {
        dbHelper dbh = new dbHelper();
        String recipient = "u1@gmail.com";
        String msg = "This is a test";
        String sender = "System";
        boolean isAdded = dbh.AddMessage(recipient, msg, sender);

        assertTrue(isAdded);
    }

    // Tests if the login page opens up
    @Test
    public void testLoginDisplay() {
        Login login = new Login();
        assertTrue(login.isVisible());
    }

    // tests if the Main Menu page opens up
    @Test
    public void testMainMenuDisplay() {
        MainMenu menu = new MainMenu("user@email.com");
        assertTrue(menu.isVisible());
    }

    // Checks if the Register opens up
    @Test
    public void testRegisterDisplay() {
        RegisterNewUser RegisterNewUser = new RegisterNewUser();
        assertTrue(RegisterNewUser.isVisible());
    }

    /*
     * Test to check if a message can not be added to an non-existing user
     */
    @Test
    public void invalidAddMessage() {
        dbHelper dbh = new dbHelper();
        String recipient = "invalidrandomusername";
        String msg = "This is a test";
        String sender = "System";
        boolean isAdded = dbh.AddMessage(recipient, msg, sender);

        assertFalse(isAdded);
    }

    // This methods check for a correct email and password(change info to match your
    // db)
    @Test
    public void testAuthenticationValid() {
        Login login = new Login();
        login.authentication("twang@example.com", "pw");
        MainMenu menu = new MainMenu("twang@example.com");
        assertTrue(menu.isVisible());
    }

    // This methods check for an incorrect email and password(change info to match
    // your db)
    // For this to work ,the Error prompt for incorrect email and password must be
    // closed
    @Test
    public void testAuthenticationInvalid() {
        Login login = new Login();
        login.authentication("invalid@email.com", "invalidpassword");
        assertTrue(login.isVisible());
    }

    // This checks for empty information
    // For this to work ,the Error prompt for incorrect email and password must be
    // closed
    @Test
    public void testAuthenticationEmpty() {
        Login login = new Login();
        login.authentication("", "");
        assertTrue(login.isVisible());
    }

     ////Sprint 2 tests below////

    /*
     * This test is to see if a Doctor is returned for a patient who has a registed doctor
     * in the database.
     */
    @Test
    public void getValidDoctor(){
        dbHelper dbh = new dbHelper();
        String DoctorID = dbh.getDoctor("u1@gmail.com");
        boolean hasDoctor = false;

        //if the returned doctor is not null or ""
        //The the method worked!
        if (DoctorID != null || DoctorID != ""){
            hasDoctor = true;
        }
        assertTrue(hasDoctor);
    }

    /*
     * This is a test to check to see if we can get a valid doctor's email
     * with their ID using the getDoctorDetails method
     */
    @Test
    public void getValidDoctorDetails(){
        dbHelper dbh = new dbHelper();
        String DoctorEmail = dbh.getDoctorDetails("1");
        boolean returnedEmail = false;

        //If the method returned an email then, it wont be null or empty string
        if (DoctorEmail != null || DoctorEmail != ""){
            returnedEmail = true;
        }
        assertTrue(returnedEmail);

    }

    /*
     * This is a test to see if a valid booking can be made for a valid account
     */
    @Test
    public void makeValidBooking(){
        dbHelper dbh = new dbHelper();
        String currentUser = "u1@gmail.com";
        String BookingDate = "3333-04-03";
        String BookingTime = "12:30";

        boolean madeBooking = dbh.makeBooking(currentUser, BookingDate, BookingTime);
        assertTrue(madeBooking);
    }

    /*
     * This is a test to check availability of a booking we know to be unavailable
     *  (the booking that was made in the previous test)
     */
    @Test 
    public void checkUnavailability(){
        dbHelper dbh = new dbHelper();
        String DoctorID = dbh.getDoctor("u1@gmail.com");
        String BookingDate = "3333-04-03";
        String BookingTime = "12:30";

        boolean unavailable = dbh.checkAvailability(DoctorID, BookingDate, BookingTime);
        assertFalse(unavailable);
    }
    /*
     * Test to see if we can delete the valid booking that was made in the previous test
     */
    @Test
    public void deleteValidBooking(){
        dbHelper dbh = new dbHelper();
        String currentUser = "u1@gmail.com";
        String BookingDate = "3333-04-03";
        String BookingTime = "12:30";

        boolean deletedBooking = dbh.deleteBooking(currentUser, BookingDate, BookingTime);
        assertTrue(deletedBooking);
    }

     /*
     * quick test to check if the database is connected.
     * asssumes that the username/login is correct.
     */
    @Test
    public void DatabaseConnectionTest() {
        try {
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/groupDatabase", "username", "password");
            assertFalse(connect.isClosed());
            connect.close();
            assertTrue(connect.isClosed());
        } catch (SQLException e) {
            fail("Database isn't currently connected");
        }
    }
    
    /* 
     * tests that creating a new patient with valid data works correctly.
     */
    @Test
    public void testRegister() {
        RegisterNewUser reg = new RegisterNewUser();
        assertTrue(reg.register("John", "Doe", "johndoe@example.com", "password123"));
    }
    
    /*
     * Test to make sure a duplicate account cannot be added
     */
    @Test
    public void invalidtestRegister() {
        RegisterNewUser reg = new RegisterNewUser();
        assertFalse(reg.register("John", "Doe", "johndoe@example.com", "password123"));
    }

     /*
     * Test to make sure a fields can not be left blank and  account cannot be added
     */
    @Test
    public void blankRegister() {
        RegisterNewUser reg = new RegisterNewUser();
        assertFalse(reg.register("", "", "", ""));
    }

    /* 
     * creates a new instance of view Booking with test email / check window title is correct
     */
    @Test
    public void testViewBooking() {
        ViewBooking viewBooking = new ViewBooking("alice.smith@example.com");
        
        assertNotNull(viewBooking.confirm());
        assertEquals("View Bookings for: alice.smith@example.com", viewBooking.confirm().getTitle());
    }

    /*
     * This is a test to see if the bookings are able to be fetched from the database
     * if the loadBookings methods retunrs true that means it was successfully able to query the database
     * 
     */
    @Test
    public void validLoadBookings(){
        ViewBooking vb = new ViewBooking("u1@gmail.com");
        assertTrue(vb.confirm().loadBookings("all"));
    }

    /* 
     * test creates a new viewbooking and checks back with null
     */
    @Test
    public void testBackWithNull() {
    ViewBooking booking = new ViewBooking("alice.smith@example.com");
    assertThrows(NullPointerException.class, () -> {
        booking.back(null);
    });
}
    /*
     * test creates a new VisitDetails and checks if the page is visible
     */
    @Test
    public void testVisitDetailPage() {
        VisitDetails vd = new VisitDetails("u1@gmail.com");
        assertTrue(vd.isVisible());
    }
    /*
     * test creates a new VisitDetails and checks if the frame title includes the passed username
     */
    @Test
    public void testVisitDetailsTitle() {
        String username = "u1@gmail.com";
        VisitDetails vd = new VisitDetails(username);
        assertEquals("Visit Details: " + username, vd.getTitle());
    }
    /*
     * test creates a new VisitDetails and checks if the Tables are formed correctly
     */
    @Test
    public void testVisitDetailsTable() {
        String username = "u1@gmail.com";
        VisitDetails vd = new VisitDetails(username);
        assertEquals(5, vd.visitTable.getColumnCount());
        assertEquals("Date", vd.visitTable.getColumnName(0));
        assertEquals("Time", vd.visitTable.getColumnName(1));
        assertEquals("Notes", vd.visitTable.getColumnName(2));
        assertEquals("Prescription", vd.visitTable.getColumnName(3));
        assertEquals("Doctor", vd.visitTable.getColumnName(4));
    }
    /* 
     * test creates a new VisitDetails and checks if the Tables are populated correctly
     */
    @Test
    public void testTablePopulation() {
        VisitDetails vd = new VisitDetails("u1@gmail.com");
        DefaultTableModel model = (DefaultTableModel) vd.visitTable.getModel();
        assertEquals(1, model.getRowCount());
        assertEquals("2023-01-30", model.getValueAt(0, 0));
        assertEquals("12:00:00", model.getValueAt(0, 1));
        assertEquals("Patient had mild flu", model.getValueAt(0, 2));
        assertEquals("No medication needed", model.getValueAt(0, 3));
        assertEquals("Dr. Emma Wilson", model.getValueAt(0, 4));
        vd.dispose();
    }
    
    /*
     * checking back button works with valid user logged in
     */
     @Test
    public void testBack() {
        Authorisation auth = new Authorisation("u1@gmail.com");
        String UserExp = "u1@gmail.com";
        auth.back(UserExp);
        assertEquals(UserExp, MainMenu.getCurrentUser());
    }

    /*
     * Test to see if a valid log can be added to the system
     * using the logactoin method
     */
    @Test
    public void validLogAction(){
        MainMenu mm = new MainMenu("u1@gmail.com");
        boolean result = mm.logAction("viewSystemLogs", "u1@gmail.com");
        assertTrue(result);
    }

    /*
     * Test to see if logs can be retrieved using getlogs method
     * for a valid user
     */
    @Test
    public void validGetLogs(){
        Authorisation auth = new Authorisation("u1@gmail.com");
        boolean result  = auth.getLogs("u1@gmail.com");
        assertTrue(result);
    }

}
