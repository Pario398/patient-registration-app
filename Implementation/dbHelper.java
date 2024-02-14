import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;

    /*
     * This class contains multiple methods that will be used by multiple forms,
     * and has been made to reduce duplicate code.
     * Methods in this class can be accessed by created an dbHelper object and envoking the neccessary methods.
     */
public class dbHelper {
    private String dbRootPassword = "";
    public dbHelper(){

    }

    /*
     * This method gets the current user's Doctor, if they have one, and returns it, else returns null.
     */
    public String getDoctor(String currentUser){
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "SELECT * FROM accounts WHERE Email = '" + currentUser + "'";
            ResultSet result;
            result = statement.executeQuery(query);
            
            String Doctor = "";
            while (result.next()) {
                Doctor = result.getString("DoctorID");
            }
            return Doctor;
        }catch(Exception z) {
            System.out.println(" get doctor failed! ");
            z.printStackTrace();
            return null;
        }
    }

    /*
     * This method returns email address of a doctor given the doctors ID.
     */
    public String getDoctorDetails(String DoctorID){
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "SELECT * FROM accounts WHERE isDoctor = 'yes' AND DoctorID = '" + DoctorID + "'";
            ResultSet result;
            result = statement.executeQuery(query);
            
            String DoctorEmail = "";
            while (result.next()) {
                DoctorEmail = result.getString("Email");
            }
            return DoctorEmail;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*
     * This method first checks to see if the recipient of the message exists.
     * Meaning are they are  user in the database?
     * IF they are then it adds the given message to the messages table. With the user as a recipient.
     * Also making note of the sender (who the message is from, could be the System).
     * Returns true if message was successfully added
     */
    public boolean AddMessage(String recipient, String msg, String sender){

        //This part checks to see if the given recipient exists
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "SELECT * FROM accounts WHERE Email = '" + recipient + "'";
            ResultSet resultSet = statement.executeQuery(query);
            boolean accountExists = false;
            while (resultSet.next()){
				accountExists = true;//If the query returns any results then the account exists.
            }

            //if the account doesn't exist  then the method returns false        
            if (accountExists == false){
                return accountExists;
            }
            		
		} catch (Exception e) {
			e.printStackTrace();
            
		}
        //This part adds a message to the message table
        // with the given recipient, message and sender
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "INSERT INTO messages (recipient, msg, sender) VALUES ('" + recipient +"', '" + msg +"', '" + sender +"')";
            

            statement.executeUpdate(query);
            return true;
            		
		} catch (Exception e) {
			e.printStackTrace();
            return false;
		}
    }



    /*
     * This method makes a booking for a given date/time/user/doctor
     * returns true when booking is successfully made
     * Only makes a booking if the doctor is available and the patient has chosen a doctor already.
     */
    public Boolean makeBooking(String currentUser, String BookingDate, String BookingTime){
        dbHelper dbh = new dbHelper();
        if(dbh.getDoctor(currentUser) == null || dbh.getDoctor(currentUser) == ""){ //This makes sure the current user has chosen a doctor.
            JOptionPane.showMessageDialog(null, "Error! You have no assigned doctor. Please choose one!");
            return false;
        } 
        String DoctorID = dbh.getDoctor(currentUser);
        if(checkAvailability(dbh.getDoctor(currentUser), BookingDate, BookingTime)){ //This only runs if the doctor is available at the given date/time
            try{

                //This part creates the booking and registers the booking in the database on the booking table
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
                Statement statement = conn.createStatement();
                
                String query =  "INSERT INTO booking (Date, Time, Email, DoctorID) VALUES ('" + BookingDate + "', '" + BookingTime + "', '" + currentUser + "', '" + DoctorID +"')";
                statement.executeUpdate(query);


                //This adds a confirmation message to the patients account
                String message = "Booking created for " + BookingDate + " at " + BookingTime;
                dbh.AddMessage(currentUser, message, "System");

                //This adds a confirmaiton message to the doctors account
                String DoctorEmail = dbh.getDoctorDetails(DoctorID);
                message = "Booking created for " + BookingDate + " at " + BookingTime + " with patient " + currentUser;
                dbh.AddMessage(DoctorEmail, message, "System");

                //returns true if all things completed successfully.
                return true;

            }catch (Exception e){
                System.out.println(" make booking failed");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /*
     * This method checks to see if the patient's doctor is available at the given date/time
     * If they are available the method returns true.
     */
    public Boolean checkAvailability(String DoctorID, String BookingDate, String BookingTime){
        
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "SELECT * FROM booking WHERE DoctorID = '" + DoctorID + "' AND Date = '" + BookingDate 
                        + "' AND Time = '" + BookingTime + "'";
           
            ResultSet resultSet = statement.executeQuery(query);
            boolean available = true;

            //If the query returns any results then a booking already exists for given date/time
            //so availability is false.
            while (resultSet.next()){
                JOptionPane.showMessageDialog(null, "Error! Doctor is unavailable at selected date/time.");
				available = false;
            }

            return available;
        }catch(Exception z) {
            System.out.println("Check availability failed.");
            z.printStackTrace();
            return false;
        }
        //return false;
    }

    /*
     * This method checks to see if the patient's doctor is available at the given date/time
     * If they are available the method returns true.
     * No warning message is displayed if unavailable in this version
     */
    public Boolean checkAvailabilityNonVerbose(String DoctorID, String BookingDate, String BookingTime){
        
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "SELECT * FROM booking WHERE DoctorID = '" + DoctorID + "' AND Date = '" + BookingDate 
                        + "' AND Time = '" + BookingTime + "'";
           
            ResultSet resultSet = statement.executeQuery(query);
            boolean available = true;

            //If the query returns any results then a booking already exists for given date/time
            //so availability is false.
            while (resultSet.next()){
				available = false;
            }

            return available;
        }catch(Exception z) {
            System.out.println("Check availability failed.");
            z.printStackTrace();
            return false;
        }
        //return false;
    }


    /*
     * This is a method to remove a booking given the date, time and patient.
     */
    public boolean deleteBooking(String patient, String BookingDate, String BookingTime){
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase?user=root&password=" + dbRootPassword);
            Statement statement = conn.createStatement();
            String query =  "DELETE FROM booking WHERE Email = '" + patient + "' AND Date = '" + BookingDate + "' AND Time = '" + BookingTime + "'";

            statement.executeUpdate(query);
            return true;
        }catch(Exception z) {
            JOptionPane.showMessageDialog(null, "Error! You do not have a booking at this time!");
            System.out.println("Check availability failed.");
            return false;
        }
        
    }

}
