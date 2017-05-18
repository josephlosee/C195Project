package c195_jlosee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/12/2017.
 */
public class MainViewController implements Initializable{
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: Nothing at the moment.
    }

    @FXML public void addCustClicked(){
        System.out.println("Add Customer Button Clicked");
    }

    @FXML public void editCustClicked(){
        System.out.println("Edit Customer Button Clicked");
    }

    @FXML public void addApptClicked(){
        System.out.println("Add Appt button clicked");
    }

    @FXML public void editApptClicked(){
        System.out.println("Edit Appt button clicked");
    }

    @FXML public void logoutClicked(ActionEvent e){
        if (ViewManager.showConfirmationView("Logout?")){
            ViewManager.closeWindowFromEvent(e);
            SQLManager.getInstance().logout();

            Stage stage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("resources/LoginView.fxml"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        }
    }

    @FXML public void reportsClicked(){
        System.out.println("Reports button clicked");

    }


}
