package c195_jlosee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/13/2017.
 */
public class CustomerViewController implements Initializable{

    @FXML CheckBox activeCB;
    @FXML Button saveCust, cancelCust;
    @FXML TextArea custNameField, AddressLine1Field, AddressLine2Field, CityField, countryField, PostalCodeField, PhoneField;



    public void initialize(URL urlInit, ResourceBundle resourceBundle){
        //TODO: add any init calls here
    }

    @FXML void saveClicked(ActionEvent e){

    }

    @FXML void cancelClicked(ActionEvent e){

    }

    @FXML void activeToggled(ActionEvent e){

    }
}
