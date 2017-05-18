package c195_jlosee;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * ${FILENAME}
 * used for managing the programs views
 * Created by Joseph Losee on 5/10/2017.
 */
public class ViewManager {

    public static boolean showConfirmationView(String confMessage){
        //Show confirmation message & Yes/No
        Alert confirmationBox = new Alert(Alert.AlertType.CONFIRMATION, confMessage);

        //
        //Using lambdas to get used to them.
        return confirmationBox.showAndWait()
                .filter(response->response== ButtonType.OK)
                .isPresent();
    }

    public static void showErrorMessage(String errMessage){
        Alert errorBox = new Alert(Alert.AlertType.ERROR, errMessage);
        errorBox.showAndWait();
    }

    /**
     * Returns the window associated with an event. I hate casting this much, and this will be used frequently.
     * @param e
     * @return
     */
    public static Window getWindowFromEvent(ActionEvent e){
        return (((Node)e.getSource()).getScene().getWindow());
    }

    /**
     * closes a window associated with an event.
     * @param e
     */
    public static void closeWindowFromEvent(ActionEvent e){
        (((Node)e.getSource()).getScene().getWindow()).hide();
    }


    public static void showCalendar(){

    }

    public static void showMainView(){
        /*Stage  stage = new Stage();
        //FXMLLoader.load("LoginView.fxml");

        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        FXMLLoader.load()
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Test Title");
        stage.show();*/

    }
}
