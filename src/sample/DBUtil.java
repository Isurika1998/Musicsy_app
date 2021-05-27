package sample;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
public final class DBUtil {
    private static boolean isDriverLoaded = false;
    static{
        try{
            Class.forName("com.mysql.jdbc.Driver");
            //System.out.println("Driver Loaded");
            isDriverLoaded = true;
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException{
        Connection con = null;
        if(isDriverLoaded){
            con  = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicsy?autoReconnect=true&useSSL=false","root","isurika");
          //  System.out.println("Connection established");
        }
        return con;
    }


    public static void closeConnection(Connection con) throws SQLException{
        if(con!=null){
            con.close();
           // System.out.println("connection closed");
        }
    }
}
