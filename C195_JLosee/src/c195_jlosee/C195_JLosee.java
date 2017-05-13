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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Joe
 */
public class C195_JLosee extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        ExecutorService startSQLConnection = null;
        try{
            startSQLConnection = Executors.newSingleThreadExecutor();
            startSQLConnection.submit(()->SQLManager.getSQLConnection());
        }finally{
            if (startSQLConnection != null) startSQLConnection.shutdown();
            //shutdown the thread
        }

        Parent root = FXMLLoader.load(getClass().getResource("resources/LoginView.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

}
