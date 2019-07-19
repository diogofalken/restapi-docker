package main.java;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class Database {
    // JDBC driver name and database URL
    private static String DB_Url = "jdbc:mysql://mysql:3306/restapi";
    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Database Credentials
    private static String DB_Username = "root";
    private static String DB_Password = "root";

    public Database() {
        Connection conn = null;
        Statement stmt = null;
        try{
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Password);
            stmt = conn.createStatement();

            // Create Table
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users (id int NOT NULL AUTO_INCREMENT, name TEXT, birthDate DATE, city TEXT, PRIMARY KEY(id));";
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
    }//end main


    public static void insertUser(User user, int flag) {
        Connection conn = null;
        Statement stmt = null;
        try{
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username,DB_Password);

            // Execute a query
            System.out.println("Inserting users into table.");
            stmt = conn.createStatement();
            String sql;
            if(flag == 0) {
                sql = "INSERT INTO users (name, birthDate, city) VALUES (\"" + user.getName() + "\",\"" + user.getBirthDate() + "\",\"" + user.getCity() + "\")";
                System.out.println(sql);
            }
            else {
                sql = "INSERT INTO users VALUES (" + Integer.toString(flag) + ",\"" + user.getName() + "\",\"" + user.getBirthDate() + "\",\"" + user.getCity() + "\")";
                System.out.println(sql);
            }
            stmt.executeUpdate(sql);

            // Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            // Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            // finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            } // nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }// end finally try
        }// end try
    }

    public static JSONArray getUsers() {
        Connection conn = null;
        Statement stmt = null;
        JSONArray users = new JSONArray();
        try{
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Username);

            // Execute a query
            stmt = conn.createStatement();
            System.out.println("Getting all users from database.");
            String sql = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                int id  = rs.getInt("id");
                JSONObject user = new JSONObject();
                user.put("id", Integer.toString(rs.getInt("id")));
                user.put("name", rs.getString("name"));
                user.put("birthDate", rs.getDate("birthDate").toString());
                user.put("city", rs.getString("city"));

                users.put(user);
            }
            rs.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        return users;
    }

    public static JSONArray getUser(Integer id) {
        Connection conn = null;
        Statement stmt = null;
        JSONArray users = new JSONArray();
        try{
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Username);

            // Execute a query
            stmt = conn.createStatement();
            System.out.println("Get user with id = " + id.toString());
            String sql = "SELECT * FROM users WHERE id=" + id.toString();
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);

            // See if id was valid
            if(!rs.next()) {
                return null;
            }
            else {
                JSONObject user = new JSONObject();
                user.put("id", Integer.toString(rs.getInt("id")));
                user.put("name", rs.getString("name"));
                user.put("birthDate", rs.getDate("birthDate").toString());
                user.put("city", rs.getString("city"));
                System.out.println(user.toString());
                users.put(user);
            }
            rs.close();
            return users;
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        return users;
    }

    public static boolean deleteUser(Integer id) {
        Connection conn = null;
        Statement stmt = null;
        try{
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Password);

            // Execute a query
            System.out.println("Deleting user with ID = " + Integer.toString(id));
            stmt = conn.createStatement();
            String sql = "DELETE FROM users " +
                    "WHERE id = " + Integer.toString(id);
            int delete = stmt.executeUpdate(sql);

            if(delete == 0) {
                return false;
            }
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        return true;
    }
}
