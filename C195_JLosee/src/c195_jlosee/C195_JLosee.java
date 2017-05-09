/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195_jlosee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

/**
 *
 * @author Joe
 */
public class C195_JLosee extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        SQLManager.getSQLConnection();
        /*Connection conn = null;

        String driver   = "com.mysql.jdbc.Driver"; ;
        String schema = "U04bqK";
        String url = "jdbc:mysql://52.206.157.109/"+schema;
        String user = "U04bqK", pass="53688195806";

        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to: "+schema);

            System.out.println(conn.getSchema());
            Statement st = conn.prepareStatement("CREATE TABLE JLTEST");
            try {
                st.execute("CREATE TABLE Customer(CustomerID INT NOT NULL UNIQUE AUTO_INCREMENT,LastName	VARCHAR(20), FirstName 	VARCHAR(20), StreetAddress VARCHAR(64), AptNum 	VARCHAR(5), City 		VARCHAR(30), State 		CHAR (2), Zip		CHAR (5), HomePhone CHAR(14), CellPhone CHAR(14), OtherPhone CHAR(14), PRIMARY KEY (CustomerID));");
            }catch (Exception e){

            }
            st.execute("INSERT INTO Customer (LastName, FirstName, StreetAddress, City, State, Zip, HomePhone) VALUES ('Smith', 'John', '12445 Main St', 'Seattle', 'WA', '98101', '(888) 555-5555'); ");
            ResultSet results = st.executeQuery("Select * from Customer");

            conn.close();

        }catch (SQLException sqle){
            System.out.println("SQLException: " +sqle.getMessage());
            System.out.println("SQLState: "+sqle.getSQLState());
            System.out.println("Vendor Error: "+sqle.getErrorCode());
        }catch (ClassNotFoundException cnfe){
            System.out.println(cnfe.getMessage());
        }*/


        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Test Title");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

}
