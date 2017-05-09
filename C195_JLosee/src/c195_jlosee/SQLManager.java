package c195_jlosee;

import java.sql.*;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/8/2017.
 */
public class SQLManager {
    private static String driver   = "com.mysql.jdbc.Driver"; ;
    private static String schema = "U04bqK";
    private static String url = "jdbc:mysql://52.206.157.109/"+schema;
    private static String user = "U04bqK", pass="53688195806";
    private static Connection sqlConnection = null;

    //Returns the sql connection object
    public static Connection getSQLConnection(){

        //Would use a data source instead, but the SQL server seems to be set up for this.
        if (sqlConnection==null){
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException cnfe){
                System.out.println("Class not found exception encountered: " + cnfe.getMessage());
            }

            try {
                sqlConnection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException sqlE){
                System.out.println("SQL Exception Encountered: "+sqlE.getMessage());
            }
        }

        return sqlConnection;

    }

    public static boolean login(String user, String pass){
        //Sanitize
        PreparedStatement prepStatement;
        ResultSet res = null;
        boolean success = false;
        String query = /*"select * from user where userName="+user+"and password="+pass;//*/"select * from user where userName = ? and password = ?";
        try {
            prepStatement = sqlConnection.prepareStatement(query);
            prepStatement.setString(1, user);
            prepStatement.setString(2, pass);
            res = prepStatement.executeQuery();
            if (res.next()){
                System.out.println("User login successful!");
                System.out.println("UserID: " + res.getInt(1));
                System.out.println("UserName: " + res.getString(2));
                success = true;
            }

        }catch (SQLException sqlE){
            System.out.println("Error creating statement: "+ sqlE.getMessage());
        }catch (Exception e){
            System.out.println("An generic exception occurred during the prepared statement of login. "+e.getMessage());
        }

//        statement.

        //TODO: return SQL User
        return success;

    }
}
