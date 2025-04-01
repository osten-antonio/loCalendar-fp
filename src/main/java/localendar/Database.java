package localendar;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;


public class Database {
    private Connection conn;
    public Database() {
        String url = "jdbc:sqlite:entries.db"; // Path to your SQLite database file
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
    public Connection getConnection() {
        return conn;
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public ResultSet getTasks() {
        /*
         To use, for each data structure, iterate through the result set and add a new
         task instance

         * The result of getCategory has to be stored first in a
           HashMap<Integer, Category> instance

         data structure -> add new task(rs.getString(COLUMN_NAME),...)

         Column names: title, body, status, due_date, time, rrule, category_id

         Task object constructor:
         Task(String title,
              String body,
              boolean status,
              LocalDate dueDate,
              LocalTime dueTime,
              int priority,
              String rrule,
              Category category)

         For due time use res.getBoolean("status")
         For LocalDate dueDate, use
            LocalDate.parse(rs.getString("due_date"))
         For LocalTime time, use
            LocalTime.parse(rs.getString("time_column"))
         For category, use
            CATEGORY_HASHMAP.get(rs.getInt("category_id"))

            where, CATEGORY_HASHMAP is the hashmap created from getCategories
         For priority, use
            rs.getInt("priority")
         */
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT * FROM tasks");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public HashMap<Integer, Category> getCategories(){
        HashMap<Integer, Category> res = new HashMap<>();
        try(
            Statement stmt = conn.createStatement();
            ResultSet sqlRes = stmt.executeQuery("SELECT * FROM categories")
            ){
            while(sqlRes.next()){
                res.put(sqlRes.getInt("category_id"),
                        new Category(sqlRes.getString("name"),
                                sqlRes.getString("color"),
                                sqlRes.getString("text_color"))
                        );
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public static PriorityQueue<>
//    public static ArrayList<String> test(){
//        Connection db = connect();
//        ArrayList<String> res = new ArrayList<>();
//        if(db == null){
//            res.add("failed");
//            return res;
//        }
//        try(Statement stmt = db.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT name FROM categories");
//        ){
//            while(rs.next()){
//                res.add(rs.getString("name"));
//            }
//        } catch(SQLException e){
//            System.out.println(e);
//            res.add(e.getMessage());
//            return res;
//        }
//        return res;
//    }

}
