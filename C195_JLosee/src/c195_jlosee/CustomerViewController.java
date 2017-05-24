package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/13/2017.
 */
public class CustomerViewController implements Initializable{

    @FXML CheckBox activeCB;
    @FXML Button saveCust, cancelCust;
    @FXML Label custIdLabel;
    @FXML   TextArea custNameField;
    @FXML    TextArea AddressLine1Field;
    @FXML    TextArea AddressLine2Field;
    @FXML    TextArea CityField;
    @FXML    TextArea PostalCodeField;
    @FXML    TextArea PhoneField;
    @FXML ComboBox<String> countryField;
    boolean isCBChecked = false;
    SQLCustomer customerData;
    ObservableList<String> countryList;

    public void initialize(URL urlInit, ResourceBundle resourceBundle){
        //TODO: add any init calls here
        disableControls(false);
        Locale systemLocale = Locale.getDefault();
        countryList =  FXCollections.observableList(new ArrayList<>());
        Arrays.stream(Locale.getISOCountries())
                .forEach(a -> countryList.add(new Locale("",a).getDisplayCountry()));


        int indexOfDefault =0;
        for (int i =0; i<countryList.size(); i++)
        {
            //Set the initial local to the user's default
            if (countryList.get(i).equalsIgnoreCase(systemLocale.getDisplayCountry())){
                indexOfDefault=i;
                break;
            }

        }
        countryField.setItems(countryList);
        countryField.getSelectionModel().select(indexOfDefault);


        custNameField.setText("Alan Smithee");
        AddressLine1Field.setText("123 Main St.");
        CityField.setText("Anytown");
        //countryField.di("Canada");
        PostalCodeField.setText("11111");
        PhoneField.setText("888-555-5555");
        activeCB.setSelected(false);
    }

    @FXML void saveClicked(ActionEvent e){
        /// public SQLCustomer(String customerName, String address1, String address2, String city, String postCode, String phone, String country){

        //if customer is not being edited
        if (customerData==null){
            try {
                customerData=new SQLCustomer(custNameField.getText(), AddressLine1Field.getText(), AddressLine2Field.getText(),
                        CityField.getText(), PostalCodeField.getText(),  PhoneField.getText(),countryField.getSelectionModel().getSelectedItem());
                SQLManager.getInstance().addCustomer(customerData);
                custIdLabel.setText(String.valueOf(customerData.getCustomerID()));
                ViewManager.closeWindowFromEvent(e);
            }catch (Exception exc){
                ViewManager.showErrorMessage(exc.getMessage());
            }
        }else{
            //Update customer information

        }
    }

    @FXML void cancelClicked(ActionEvent e){
        String confirmation = "Discard changes?";
        if (ViewManager.showConfirmationView(confirmation)){
            ViewManager.closeWindowFromEvent(e);
        }
    }

    /*@FXML void activeToggled(ActionEvent e){
        //activeCB.fire();
        //System.out.println("Checkbox state: "+activeCB.isSelected());
    }*/

    @FXML void testClick(){
        //activeCB.fire();
        activeCB.setSelected(!activeCB.isSelected());
        activeCB.fire();
        System.out.println("Checkbox state: "+activeCB.isSelected());
    }

    public void editCustomer(SQLCustomer updateCustomer) {
        customerData=updateCustomer;
        setCustomerFields();
    }

    public void viewCustomer(SQLCustomer viewCustomer){
        customerData=viewCustomer;
        setCustomerFields();
        saveCust.setText("Close");
        cancelCust.setVisible(false);
        saveCust.setOnAction(event->
            ViewManager.closeWindowFromEvent(event));
        disableControls(true);
    }

    private void setCustomerFields(){
        custNameField.setText(customerData.getCustomerName());
        AddressLine1Field.setText(customerData.getAddress1());
        AddressLine2Field.setText(customerData.getAddress2());
        CityField.setText(customerData.getCity());
        //countryField.di("Canada");
        PostalCodeField.setText(customerData.getPostalCode());
        PhoneField.setText(customerData.getPhone());
        custIdLabel.setText(String.valueOf(customerData.getCustomerID()));
        setCountrySelection(customerData.getCountry());
        if (customerData.getActive()>0){
            activeCB.setSelected(true);
        }

    }

    private void disableControls(boolean disabled){
        activeCB.setDisable(disabled);
        boolean editable = !disabled;
        custNameField.setEditable(editable);
        AddressLine1Field.setEditable(editable);
        AddressLine2Field.setEditable(editable);
        CityField.setEditable(editable);
        PostalCodeField.setEditable(editable);
        PhoneField.setEditable(editable);
        countryField.setDisable(disabled);
    }

    private void setCountrySelection(String countryName){
        int indexOfDefault =0;
        for (int i =0; i<countryList.size(); i++)
        {
            //Set the initial local to the user's default
            if (countryList.get(i).equalsIgnoreCase(countryName)){
                indexOfDefault=i;
                break;
            }

        }
        countryField.getSelectionModel().select(indexOfDefault);
    }

}
