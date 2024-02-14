// all imports
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;



public class DoctorsList {
    public String dbRootPassword = "";
    public String currentUser = "";

    public static void main(String[] args) {
       // DoctorsList dl = new DoctorsList("u1@gmail.com");


    }

    public DoctorsList(String username){
        // SQL section starting null
        Connection connect = null;
        Statement state = null;
        ResultSet result = null;
        currentUser = username;
    

        try { 

            // standard connection setup to java/SQL database
            connect = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase", "root", dbRootPassword);

            state = connect.createStatement();

            // select from database
            String sql = "SELECT DoctorID, Forename, Surname FROM doctors";
            result = state.executeQuery(sql);

            // collect data from database
            ResultSetMetaData metaD = result.getMetaData();
            // find column amount from database
            int columns = metaD.getColumnCount();


            // Vector is array that can get far larger
            Vector<String> columnName = new Vector<String>();

            // add column names from database
            for (int i = 1; i <= columns; i++) {
                columnName.add(metaD.getColumnName(i));
            }

            // holds the data vector cause holds data type.
            Vector<Vector<Object>> dataset = new Vector<Vector<Object>>();
            while(result.next()) {
                // adds rows with info from db
                Vector<Object> row = new Vector<Object>();
                for (int i = 1; i <= columns; i++) {
                    row.add(result.getObject(i));
                }
                dataset.add(row);
            }
            
            /*
             * Adding the table for the databse in JFrame
             */
            JTable table = new JTable(dataset, columnName);
            JFrame frame = new JFrame("Doctors List");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(table));
            frame.pack();
            frame.setVisible(true);

            // create combo box with doctor names
            JComboBox<String> comboBox = new JComboBox<String>();
            for (Vector<Object> row : dataset) {
                comboBox.addItem(row.get(1).toString() + " ID: " + row.get(0).toString());
            }

            // create label and add combo box to panel
            JLabel label = new JLabel("Please select a doctor from the drop down list:");
            JPanel panel = new JPanel();
            panel.add(label);
            panel.add(comboBox);

            // create button to confirm doctor selections
            JButton confirmButton = new JButton("Confirm Selection");
            confirmButton.addActionListener(ev -> AssignedDoctor(frame, comboBox));
            panel.add(confirmButton);


            // create new frame and add panel
            JFrame frame2 = new JFrame("Select Doctor");
            frame2.add(panel);
            frame2.pack();
            frame2.setVisible(true);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (result != null) result.close(); } 
            catch (SQLException e) { e.printStackTrace(); }

            try { if (state != null) state.close(); } 
            catch (SQLException e) { e.printStackTrace(); }

            try { if (connect != null) connect.close(); } 
            catch (SQLException e) { e.printStackTrace(); }
            // ends connection
        }

    }

    /*
     * This method first adds the confirmation message to the messages table
     * And then Assigns the selected doctor to the patient
     * by putting the doctor ID in the "DoctorID" Column for the correct patient
     */
    public void AssignedDoctor(JFrame frame, JComboBox comboBox){
        String doctorSelected = (String) comboBox.getSelectedItem();
        dbHelper dbh = new dbHelper();

        String[] selected = doctorSelected.split(" ID: ");
        
        //Confirmation message for patient
        dbh.AddMessage(currentUser, "You have chosen doctor " + selected[0], "System");

        //Confirmation message for doctor
        String doctorsEmail = dbh.getDoctorDetails(selected[1]);
        dbh.AddMessage(doctorsEmail, "You have a new patient: " + currentUser, "System");

        //Assign the doctor to the patient in the database.
        try{
            String query = "UPDATE accounts set DoctorID = '" + selected[1] + "' WHERE Email = '" + currentUser + "'";
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/groupdatabase", "root", dbRootPassword);
            Statement stat = conn.createStatement();
            stat.executeUpdate(query);
        } catch(SQLException z) {
            z.printStackTrace();
        }

                    
        MainMenu mm = new MainMenu(currentUser);
        frame.dispose();
    }

    
    
}
