package localendar;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

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
            LocalTime.parse(rs.getString("time"))
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

    // TODO: Change to respective data structure for List
//    public Map<Integer,List<Task>> getTasks(LocalDate date, HashMap<Integer, Category> categories){
//        Map<Integer, List<Task>> res = new HashMap<>();
//        List<Task> innerRes = new ArrayList<>();
//        try {
//            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE strftime('%Y-%m', due_date) = ?");
//            stmt.setString(1,date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
//            ResultSet rs = stmt.executeQuery();
//            if(rs.next()){
//                Task tempTask = new Task(rs.getString("title"),rs.getString("body"),
//                        rs.getInt("status") != 0,
//                        LocalDate.parse(rs.getString("due_date")),
//                        LocalTime.parse(rs.getString("time")),
//                        rs.getInt("priority"), rs.getString("rrule"),
//                        categories.get(rs.getInt("category_id")));
//
//                res.computeIfAbsent(tempTask.getDueDate().getDayOfMonth(), k -> new ArrayList<>()).add(tempTask);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

    public HashMap<Integer, Category> getCategories(){
        return getCategories(false);
    }
    public HashMap<Integer, Category> getCategories(boolean forWindow){
        HashMap<Integer, Category> res = new HashMap<>();
        String query;
        if(!forWindow) query = "SELECT * FROM categories";
        else query = "SELECT * FROM categories WHERE category_id>1";
        try(
            Statement stmt = conn.createStatement();
            ResultSet sqlRes = stmt.executeQuery(query)
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

    public int writeCategory(Category category){
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM categories WHERE name=?");
            stmt.setString(1,category.getName());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                if(rs.getInt(1)>1){
                    return -1;
                }
            }
            stmt = conn.prepareStatement("INSERT INTO categories (name, color, text_color) VALUES (?, ?, ?)");

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getColor());
            stmt.setString(3, category.getTextColor());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int editCategory(Category prevCategory, Category newCategory){
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM categories WHERE name=?");
            stmt.setString(1,newCategory.getName());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                if(rs.getInt(1)>1){
                    return -1;
                }
            }

            stmt = conn.prepareStatement("UPDATE categories SET name=?, color=?, text_color=? WHERE " +
                                                                "name=? AND color=? AND text_color=?");
            stmt.setString(1,newCategory.getName());
            stmt.setString(2,newCategory.getColor());
            stmt.setString(3,newCategory.getTextColor());
            stmt.setString(4, prevCategory.getName());
            stmt.setString(5, prevCategory.getColor());
            stmt.setString(6, prevCategory.getTextColor());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteCategory(Category category){
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM categories WHERE name=? AND color=? AND text_color=?");
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getColor());
            stmt.setString(3, category.getTextColor());

            stmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getRecentCategory(){
        int recentId = -1;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT MAX(category_id) FROM categories");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                recentId = rs.getInt(1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return recentId;
    }

    public void writeTask(Task task){
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT category_id FROM categories WHERE name=?");
            stmt.setString(1,task.getCategory().getName());
            ResultSet rs = stmt.executeQuery();
            int categoryId = rs.next() ? rs.getInt("category_id"): 1;

            stmt = conn.prepareStatement("INSERT INTO tasks(title,body,status," +
                    "due_date,time,rrule,category_id,priority) VALUES (?, ?, ?, ?, ?, " +
                    "?, ?, ?)");
            stmt.setString(1,task.getTitle());
            stmt.setString(2,task.getBody());
            stmt.setInt(3,task.isStatus() ? 1:0);
            stmt.setString(4,task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            stmt.setString(5,task.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            stmt.setString(6,task.getRrule().toString());
            stmt.setInt(7,categoryId);
            stmt.setInt(8,task.getPriority().getLevel());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteTask(Task task){
        // TODO pop from data strucutre, refresh task list
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT category_id FROM categories WHERE name=?");
            stmt.setString(1,task.getCategory().getName());
            ResultSet rs = stmt.executeQuery();
            int categoryId = rs.next() ? rs.getInt("category_id"): 1;
            stmt = conn.prepareStatement("DELETE FROM tasks WHERE title = ? AND body = ? AND status = ? " +
                    "AND due_date = ? AND time = ? AND rrule = ? AND category_id = ? AND priority = ?");

            stmt.setString(1,task.getTitle());
            stmt.setString(2,task.getBody());
            stmt.setInt(3,task.isStatus() ? 1:0);
            stmt.setString(4,task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            stmt.setString(5,task.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            stmt.setString(6,task.getRrule().toString());
            stmt.setInt(7,categoryId);
            stmt.setInt(8,task.getPriority().getLevel());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task prevTask, Task task) {
        try {

            PreparedStatement stmt = conn.prepareStatement("SELECT category_id FROM categories WHERE name=?");
            stmt.setString(1, prevTask.getCategory().getName());
            ResultSet rs = stmt.executeQuery();
            int categoryId = rs.next() ? rs.getInt("category_id") : 1;

            PreparedStatement stmtCateg = conn.prepareStatement("SELECT category_id FROM categories WHERE name=?");
            stmtCateg.setString(1, task.getCategory().getName());
            rs = stmt.executeQuery();
            int categoryIdQ = rs.next() ? rs.getInt("category_id") : 1;

            stmt = conn.prepareStatement("UPDATE tasks SET title = ?, body = ?, status = ?, due_date = ?, time = ?, rrule = ?, category_id = ?, priority = ? " +
                    "WHERE title = ? AND body = ? AND status = ? AND due_date = ? AND time = ? AND rrule = ? AND category_id = ? AND priority = ?");

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getBody());
            stmt.setInt(3, task.isStatus() ? 1 : 0);
            stmt.setString(4, task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            stmt.setString(5, task.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            stmt.setString(6, task.getRrule().toString());
            stmt.setInt(7, categoryId);
            stmt.setInt(8, task.getPriority().getLevel());
            stmt.setString(9, prevTask.getTitle());
            stmt.setString(10, prevTask.getBody());
            stmt.setInt(11, prevTask.isStatus() ? 1 : 0);
            stmt.setString(12, prevTask.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            stmt.setString(13, prevTask.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            stmt.setString(14, prevTask.getRrule().toString());
            stmt.setInt(15, categoryIdQ);
            stmt.setInt(16, prevTask.getPriority().getLevel());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateCheck(Task task,boolean status){
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT category_id FROM categories WHERE name=?");
            stmt.setString(1,task.getCategory().getName());
            ResultSet rs = stmt.executeQuery();
            int categoryId = rs.next() ? rs.getInt("category_id"): 1;
            stmt = conn.prepareStatement("UPDATE tasks SET status = ? WHERE title = ? AND body = ? " +
                    "AND status = ? AND due_date = ? AND time = ? AND rrule = ? AND category_id = ? AND priority = ?");

            stmt.setInt(1,status ? 1:0);
            stmt.setString(2,task.getTitle());
            stmt.setString(3,task.getBody());
            stmt.setInt(4,task.isStatus() ? 1:0);
            stmt.setString(5,task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            stmt.setString(6,task.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            stmt.setString(7,task.getRrule().toString());
            stmt.setInt(8,categoryId);
            stmt.setInt(9,task.getPriority().getLevel());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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
