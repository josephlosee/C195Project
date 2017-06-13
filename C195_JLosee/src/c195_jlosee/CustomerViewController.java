package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

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
    }

    @FXML void saveClicked(ActionEvent e){
        //if customer is not being edited
        if (customerData==null){
            try {
                customerData=new SQLCustomer(custNameField.getText(), AddressLine1Field.getText(), AddressLine2Field.getText(),
                        CityField.getText(), PostalCodeField.getText(),  PhoneField.getText(),countryField.getSelectionModel().getSelectedItem());
                int active = 0;
                if (isCBChecked){
                    active =1;
                }
                customerData.setActive(active);
                SQLManager.getInstance().addCustomer(customerData);
                custIdLabel.setText(String.valueOf(customerData.getCustomerID()));

                //Close the window
                (((Node)e.getSource()).getScene().getWindow()).hide();
            }catch (Exception exc){
                new Alert(Alert.AlertType.ERROR, exc.getMessage())
                        .showAndWait();
            }
        }else{
            //Update customer information
            try {
                customerData.setCustomerName(custNameField.getText());
                customerData.setCountry(countryField.getSelectionModel().getSelectedItem());
                customerData.setCity(CityField.getText());
                customerData.setFullAddress(AddressLine1Field.getText(), AddressLine2Field.getText(),
                        PostalCodeField.getText(), PhoneField.getText());
                int active = 0;
                if (activeCB.isSelected()){
                    active =1;
                }
                customerData.setActive(active);
                SQLManager.getInstance().updateCustomer(customerData);

                //Close the window
                ((Node)e.getSource()).getScene().getWindow().hide();
            } catch (Exception e1) {
                new Alert(Alert.AlertType.ERROR,e1.getMessage())
                        .showAndWait();
            }
        }
    }

    @FXML void cancelClicked(ActionEvent e){
        String confMessage = "Discard changes?";
        boolean bCancel = new Alert(Alert.AlertType.CONFIRMATION, confMessage)
                .showAndWait()
                .filter(response->response==ButtonType.OK)
                .isPresent();
        if (bCancel){
            //Close the window, discarding changes
            (((Node)e.getSource()).getScene().getWindow()).hide();
        }
    }

    /**
     * Click handling for the checkbox
     */
    @FXML void testClick(){
        activeCB.setSelected(!activeCB.isSelected());
        activeCB.fire();
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
                (((Node)event.getSource()).getScene().getWindow()).hide());
        disableControls(true);
    }

    private void setCustomerFields(){
        custNameField.setText(customerData.getCustomerName());
        AddressLine1Field.setText(customerData.getAddress1());
        AddressLine2Field.setText(customerData.getAddress2());
        CityField.setText(customerData.getCity());
        PostalCodeField.setText(customerData.getPostalCode());
        PhoneField.setText(customerData.getPhone());
        custIdLabel.setText(String.valueOf(customerData.getCustomerID()));
        setCountrySelection(customerData.getCountry());
        activeCB.setSelected(customerData.getActive()>0);
    }

    //Disables everything for viewing the customer data without chance of altering the data
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

}// END OF CLASS
