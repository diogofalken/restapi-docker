package main.java;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class Database {
    // JDBC driver name and database URL
    private static final String DB_Url = "jdbc:mysql://mysql:3306/restapi";

    // Database Credentials
    private static final String DB_Username = "root";
    private static final String DB_Password = "root";

    public static void insertUser(User user, int flag) {
        Connection conn = null;
        try {
            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Password);

            // Execute a query
            System.out.println("Inserting users into table.");
            PreparedStatement sql;
            if (flag == 0) {
                sql = conn.prepareStatement("INSERT INTO users (name, birthDate, city) VALUES (?,?,?)");
                sql.setString(1, user.getName());
                sql.setString(2, user.getBirthDate());
                sql.setString(3, user.getCity());
                System.out.println(sql);
            } else {
                sql = conn.prepareStatement("INSERT INTO users VALUES (?,?,?,?)");
                sql.setString(1, Integer.toString(flag));
                sql.setString(2, user.getName());
                sql.setString(3, user.getBirthDate());
                sql.setString(4, user.getCity());
                System.out.println(sql);
            }
            sql.executeUpdate();
            // Clean-up environment
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }// end finally try
        }// end try
    }

    public static JSONArray getUsers() {
        Connection conn = null;
        Statement stmt = null;
        JSONArray users = new JSONArray();
        try {
            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Username);

            // Execute a query
            stmt = conn.createStatement();
            System.out.println("Getting all users from database.");
            String sql = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                JSONObject user = new JSONObject();
                user.put("id", Integer.toString(rs.getInt("id")));
                user.put("name", rs.getString("name"));
                user.put("birthDate", rs.getDate("birthDate").toString());
                user.put("city", rs.getString("city"));

                users.put(user);
            }
            rs.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException ignored) {

            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return users;
    }

    public static JSONArray getUser(Integer id) {
        Connection conn = null;
        JSONArray users = new JSONArray();
        try {
            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Username);

            // Execute a query
            System.out.println("Get user with id = " + id.toString());
            PreparedStatement sql = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            sql.setString(1, Integer.toString(id));
            ResultSet rs = sql.executeQuery();


            // See if id was valid
            if (!rs.next()) {
                return null;
            }

            JSONObject user = new JSONObject();
            user.put("id", Integer.toString(rs.getInt("id")));
            user.put("name", rs.getString("name"));
            user.put("birthDate", rs.getDate("birthDate").toString());
            user.put("city", rs.getString("city"));
            System.out.println(user.toString());
            users.put(user);
            rs.close();
            return users;
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return users;
    }

    public static boolean deleteUser(Integer id) {
        Connection conn = null;
        try {
            // Open a connection
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Password);

            // Execute a query
            System.out.println("Deleting user with ID = " + id);
            PreparedStatement sql = conn.prepareStatement("DELETE FROM users WhERE id = ?");
            sql.setString(1, Integer.toString(id));

            int delete = sql.executeUpdate();

            if (delete == 0) {
                return false;
            }
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return true;
    }

    public static boolean putUser(Integer id, User user) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_Url, DB_Username, DB_Password);

            System.out.println("Updating the info of the user with ID = " + id);
            PreparedStatement sql = conn.prepareStatement("UPDATE users SET name=?, birthDate=?, city=? WHERE id=?");
            sql.setString(1, user.getName());
            sql.setString(2, user.getBirthDate());
            sql.setString(3, user.getCity());
            sql.setString(4, Integer.toString(id));

            int update = sql.executeUpdate();

            if(update == 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
