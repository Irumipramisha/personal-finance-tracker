package backend; 

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/personal_finance_tracker";
    private static final String USER = "root"; 
    private static final String PASSWORD = "enter your password here"; // your password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        if (getConnection() != null) {
            System.out.println("✅ Successfully Connected to Database!");
        }
    }
} 

