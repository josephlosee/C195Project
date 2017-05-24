package c195_jlosee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/12/2017.
 */
public class MainViewController implements Initializable{

    @FXML
    TableView customerTable;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerTable.setItems(SQLManager.getInstance().getCustomerList());
        //TODO: Nothing at the moment.
    }

    @FXML public void addCustClicked(){
        System.out.println("Add Customer Button Clicked");
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("resources/CustomerView.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Add Customer");
        stage.show();
    }

    @FXML public void editCustClicked() {
        int selectionIndex =customerTable.getSelectionModel().getSelectedIndex();

        if (selectionIndex>-1){
            SQLCustomer updateCustomer = SQLManager.getInstance().getCustomerList().get(selectionIndex);

            Parent modProdPane = new Region();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/CustomerView.fxml"));
            try {
                //Setup the parent
                modProdPane = (Parent) loader.load();
                //Get the reference to the controller class so
                CustomerViewController controller = loader.<CustomerViewController>getController();
                //We can populate the view with the part to be modified.
                controller.editCustomer(updateCustomer);

            } catch (IOException ioExc) {
                ioExc.printStackTrace();
            }
            //Resume setting up
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(modProdPane));

            //Show and Wait to take away input from the main window
            secondaryStage.showAndWait();
        }

    }

    @FXML public void viewCustClicked(){
        int selectionIndex = customerTable.getSelectionModel().getSelectedIndex();
        if (selectionIndex>-1){
            SQLCustomer updateCustomer = SQLManager.getInstance().getCustomerList().get(selectionIndex);

            Parent modProdPane = new Region();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/CustomerView.fxml"));
            try {
                //Setup the parent
                modProdPane = (Parent)loader.load();
                //Get the reference to the controller class so
                CustomerViewController controller =loader.<CustomerViewController>getController();
                //We can populate the view with the part to be modified.
                controller.viewCustomer(updateCustomer);

            }catch (IOException ioExc){
                ioExc.printStackTrace();
            }
            //Resume setting up
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(modProdPane));

            //Show and Wait to take away input from the main window
            secondaryStage.showAndWait();
        }

    }

    @FXML public void addApptClicked(){
        System.out.println("Add Appt button clicked");

        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("resources/AppointmentView.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Add Appointment");
        stage.show();
    }

    @FXML public void editApptClicked(){
        System.out.println("Edit Appt button clicked");
    }

    @FXML public void viewApptClicked(){
        System.out.println("View Appt button clicked");
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
