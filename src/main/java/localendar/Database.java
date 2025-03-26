package localendar;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Database {
    public static Connection connect() {
        String url = "jdbc:sqlite:entries.db"; // Path to your SQLite database file
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }
    public static ArrayList<String> test(){
        Connection db = connect();
        ArrayList<String> res = new ArrayList<>();
        if(db == null){
            res.add("failed");
            return res;
        }
        try(Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM categories");
        ){
            while(rs.next()){
                res.add(rs.getString("name"));
            }
        } catch(SQLException e){
            System.out.println(e);
            res.add(e.getMessage());
            return res;
        }
        return res;
    }

}
