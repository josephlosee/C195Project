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
    boolean isCBChecked = false;
    SQLCustomer customerData;

    public void initialize(URL urlInit, ResourceBundle resourceBundle){
        //TODO: add any init calls here

        //Debug:
        custNameField.setText("Alan Smithee");
        AddressLine1Field.setText("123 Main St.");
        CityField.setText("Anytown");
        countryField.setText("United States");
        PostalCodeField.setText("11111");
        PhoneField.setText("888-555-5555");
    }

    @FXML void saveClicked(ActionEvent e){
        /// public SQLCustomer(String customerName, String address1, String address2, String city, String postCode, String phone, String country){

        try {
            customerData=new SQLCustomer(custNameField.getText(), AddressLine1Field.getText(), AddressLine2Field.getText(),
                    CityField.getText(), PostalCodeField.getText(),  PhoneField.getText(),countryField.getText());
        }catch (Exception exc){
            ViewManager.showErrorMessage(exc.getMessage());
        }
    }

    @FXML void cancelClicked(ActionEvent e){

    }

    @FXML void activeToggled(ActionEvent e){
        if (!(isCBChecked)) isCBChecked = true;
        else isCBChecked = false;
        activeCB.setSelected(isCBChecked);
    }
}
