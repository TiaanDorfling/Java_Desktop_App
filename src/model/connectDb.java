/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author rjjou
 */
public class connectDb {
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL = "jdbc:derby:posgres2;create=true";
    
    Connection con;
    
    public connectDb(){}
    
    public void connect() throws ClassNotFoundException{
        try{
            Class.forName(DRIVER);
            this.con = DriverManager.getConnection(JDBC_URL);
            if (this.con != null){
                System.out.println("connected to db");
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public void createTable(){
        try{
            String query = "CREATE TABLE Appointments ("
                + "student VARCHAR(20), "
                + "counselor VARCHAR(20), "
                + "appointment_date VARCHAR(10),"
                + "appointment_time VARCHAR(5), " 
                + "status VARCHAR(20))";
            this.con.createStatement().execute(query);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        try{
            String query = "CREATE TABLE Feedback ("
                + "student VARCHAR(20), "
                + "rating SMALLINT, " 
                + "comments VARCHAR(255))"; 
            this.con.createStatement().execute(query);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        try{
            String query = "CREATE TABLE Counselors ("
                + "name VARCHAR(20), "
                + "specialisation VARCHAR(20), "
                + "availability BOOLEAN)";
            this.con.createStatement().execute(query);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    public void rebuildFeedbackTable() {
        try (Statement st = con.createStatement()) {
            // drop if it already exists
            try {
                st.execute("DROP TABLE Feedback");
            } catch (SQLException e) {
                // ignore if the table wasn't there
            }

            // now create it with an auto‑generated ID
            String ddl =
                "CREATE TABLE Feedback (" +
                "  id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                "  student VARCHAR(20)," +
                "  counselor VARCHAR(20)," +
                "  rating SMALLINT," +
                "  comments VARCHAR(255)" +
                ")";
            st.execute(ddl);
            System.out.println("Feedback table rebuilt.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
        // --- NEW FUNCTION TO DROP TABLES ---
    public void dropTables() {
        if (this.con == null) {
            System.err.println("Cannot drop tables: Database connection is not established.");
            return;
        }

        // Use try-with-resources to ensure the Statement is closed automatically
        try (Statement stmt = this.con.createStatement()) {
            // Drop tables in reverse order of potential dependencies if any existed
            // (e.g., if Feedback referenced Counselors, drop Feedback first)
            // For simple tables like yours, order doesn't strictly matter unless you add FOREIGN KEYS later.
            
            executeDerbyDropTable(stmt, "Appointments");
            executeDerbyDropTable(stmt, "Feedback");
            executeDerbyDropTable(stmt, "Counselors");

        } catch (SQLException ex) {
            System.err.println("Error acquiring statement or during table deletion: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
        // Helper method for idempotent table dropping in Derby
    private void executeDerbyDropTable(Statement stmt, String tableName) {
        String dropQuery = "DROP TABLE " + tableName;
        try {
            stmt.execute(dropQuery);
            System.out.println("Table '" + tableName + "' dropped successfully.");
        } catch (SQLException e) {
            // SQLState 42Y55 indicates "table not found" in Derby for DROP TABLE
            if (e.getSQLState().equals("42Y55")) { // Corresponds to "table does not exist"
                System.out.println("Table '" + tableName + "' does not exist. Skipping drop.");
            } else {
                System.err.println("Error dropping table '" + tableName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void addCounselor(String fullName, String spec, Boolean available){
        try{
            String query = "INSERT INTO Counselors VALUES('"+fullName+"','"+spec+"','"+available+"')";
            this.con.createStatement().execute(query);
            System.out.println("Data Added");
        } catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("Data Not Added");
        }
    }
    
    public Object[] addBooking(String name, String date, String time, String counselor){
        PreparedStatement pstmt = null;
        String status = null;
        ArrayList<Object[]> counselors = new ArrayList<Object[]>();
        try{
            String query = "INSERT INTO Appointments VALUES (?,?,?,?,?)";
            
            pstmt = this.con.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setString(4, counselor);
            
            LocalDate dateNow = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate scheduledDate = LocalDate.parse(date, formatter);
            if (dateNow.isBefore(scheduledDate)){
                status = "Coming Up";
            } else if (dateNow.isAfter(scheduledDate)) {
                status = "Done";
            } else if (dateNow.isEqual(scheduledDate)){
                status = "Today";
            }
            
            pstmt.setString(5,status);
            
            int rowsAffected = pstmt.executeUpdate(); 
        
            if (rowsAffected > 0) {
                System.out.println("Booking for '" + name + "' Added successfully.");
            } else {
                System.out.println("Booking for '" + name + "' not added.");
            }
            
        } catch(SQLException ex){
            ex.printStackTrace();
        }finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        if (status != null){
            counselors = viewCounselors();
            for (Object[] person : counselors){
                String counselorName = (String) person[0];
                String spec = (String) person[1];
                
                if (counselorName.equalsIgnoreCase(counselor)){
                    return new Object[]{counselor,spec,status,date,time, name};
                }
            }
        }
            return null;   
    }
    
    public ArrayList<Object[]> viewCounselors(){
        ArrayList<Object[]> dataList = new ArrayList<Object[]>();
        
        try{
            String query = "Select * FROM Counselors";
            ResultSet table = this.con.createStatement().executeQuery(query);
            
            while (table.next()){
                String fullName = table.getString("name");
                String spec = table.getString("specialisation");
                Boolean avail = table.getBoolean("availability");
                
                Object[] row = {fullName, spec, avail};
                dataList.add(row);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return dataList;
    }
    
    public ArrayList<Object[]> viewBookings(){
        ArrayList<Object[]> counselors = new ArrayList<Object[]>();
        ArrayList<Object[]> dataList = new ArrayList<Object[]>();
        String spec = null;
        
        try{
            String query = "Select * FROM Appointments";
            ResultSet table = this.con.createStatement().executeQuery(query);
            counselors = viewCounselors();
            
            while (table.next()){
                String student = table.getString("student");
                String counselor = table.getString("counselor");
                String date = table.getString("appointment_date");
                String time = table.getString("appointment_time");
                String status = table.getString("status");
                
                for (Object[] person : counselors){
                    
                    String counselorName = (String) person[0];
                    if (counselorName.equalsIgnoreCase(counselor)){
                        spec = (String) person[1]; 
                        Object[] row = {counselor,spec,status,date,time, student};
                        dataList.add(row);
                    }
                }
               
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return dataList;
    }
    
    public void deleteCounselor(String fullName){
        PreparedStatement pstmt = null;
        try{
            String query = "DELETE FROM Counselors WHERE name = ?";
            pstmt = this.con.prepareStatement(query);
            pstmt.setString(1, fullName);
            
            int rowsAffected = pstmt.executeUpdate(); 
        
            if (rowsAffected > 0) {
                System.out.println("Counselor '" + fullName + "' deleted successfully.");
            } else {
                System.out.println("Counselor '" + fullName + "' not found or no rows deleted.");
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        } finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public void deleteBooking(String studentName){
        PreparedStatement pstmt = null;
        try{
            String query = "DELETE FROM Appointments WHERE name = ?";
            pstmt = this.con.prepareStatement(query);
            pstmt.setString(1, studentName);
            
            int rowsAffected = pstmt.executeUpdate(); 
        
            if (rowsAffected > 0) {
                System.out.println("Student '" + studentName + "' deleted successfully.");
            } else {
                System.out.println("Student '" + studentName + "' not found or no rows deleted.");
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        } finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public Object[] updateCounselor(String oldName, String newName, String spec, Boolean avail ){
        
        PreparedStatement pstmt = null;
        
        try{
            String query = "UPDATE Counselors SET name = ?, specialisation = ?, availability = ? WHERE name = ?";
            
            pstmt = this.con.prepareStatement(query);
            pstmt.setString(1, newName);
            pstmt.setString(2, spec);
            pstmt.setBoolean(3, avail);
            pstmt.setString(4, oldName);
            
            int rowsAffected = pstmt.executeUpdate(); 
        
            if (rowsAffected > 0) {
                System.out.println("Counselor '" + newName + "' updated successfully.");
                return new Object[]{newName, spec, avail};
            } else {
                System.out.println("Counselor '" + oldName + "' not found or no rows deleted.");
            }
            
        } catch(SQLException ex){
            ex.printStackTrace();
        }finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        
        return null;
    }
    
    public Object[] UpdateBooking(String oldStudent, String newStudent, String spec, String status, String date, String time, String counselor){
        
        PreparedStatement pstmt = null;
        
        try{
            String query = "UPDATE Appointments SET student = ?, counselor = ?, appointment_date = ?, appointment_time = ?, status = ? WHERE student = ?";
            
            pstmt = this.con.prepareStatement(query);
            pstmt.setString(1, newStudent);
            pstmt.setString(2, counselor);
            pstmt.setString(3, date);
            pstmt.setString(4, time);
            pstmt.setString(5, status);
            pstmt.setString(6, oldStudent);
            
            int rowsAffected = pstmt.executeUpdate(); 
        
            if (rowsAffected > 0) {
                System.out.println("Counselor '" + newStudent + "' updated successfully.");
                return new Object[]{counselor, spec, status, date, time, newStudent};
            } else {
                System.out.println("Counselor '" + oldStudent + "' not found or no rows deleted.");
            }
            
        } catch(SQLException ex){
            ex.printStackTrace();
        }finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        
        return null;
    }
    public void addFeedback(String student, String counselor, int rating, String comments) {
        try {
            String sql = "INSERT INTO Feedback(student, counselor, rating, comments) VALUES(?,?,?,?)";
            try ( PreparedStatement ps = this.con.prepareStatement(sql) ) {
                ps.setString(1, student);
                ps.setString(2, counselor);
                ps.setInt(3, rating);
                ps.setString(4, comments);
                ps.executeUpdate();
            }
            System.out.println("Feedback added.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add feedback: " + ex.getMessage());
        }
    }

    public List<Object[]> viewFeedback() {
        List<Object[]> list = new ArrayList<>();
        try {
            String sql = "SELECT id, student, counselor, rating, comments FROM Feedback";
            try ( Statement st = this.con.createStatement();
                  ResultSet rs = st.executeQuery(sql) ) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("student"),
                        rs.getString("counselor"),
                        rs.getInt("rating"),
                        rs.getString("comments")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch feedback: " + ex.getMessage());
        }
        return list;
    }

    public void updateFeedback(int id, String student, String counselor, int rating, String comments) {
        try {
            String sql = "UPDATE Feedback SET student=?, counselor=?, rating=?, comments=? WHERE id=?";
            try ( PreparedStatement ps = this.con.prepareStatement(sql) ) {
                ps.setString(1, student);
                ps.setString(2, counselor);
                ps.setInt(3, rating);
                ps.setString(4, comments);
                ps.setInt(5, id);
                ps.executeUpdate();
            }
            System.out.println("Feedback updated (ID=" + id + ").");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to update feedback: " + ex.getMessage());
        }
    }

    public void deleteFeedback(int id) {
        try {
            String sql = "DELETE FROM Feedback WHERE id=?";
            try ( PreparedStatement ps = this.con.prepareStatement(sql) ) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            System.out.println("Feedback deleted (ID=" + id + ").");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to delete feedback: " + ex.getMessage());
        }
    }

}

