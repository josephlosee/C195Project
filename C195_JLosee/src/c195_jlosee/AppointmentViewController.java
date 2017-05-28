package c195_jlosee;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import javafx.beans.InvalidationListener;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.ResourceBundle;



/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/22/2017.
 */
public class AppointmentViewController implements Initializable, InvalidationListener {
    @FXML
    TextField title, desc, location, contact, url, startTimeField, endTime;
    @FXML
    ComboBox startZone, cbCustomer;
    ObservableList<String> zones;
    @FXML
    DatePicker startDate, endDate;
    @FXML
    MenuButton startTimeSpecifier, endTimeSpecifier;

    private final String REGEX_24H ="([01]?[0-9]|2[0-3]):[0-5][0-9]";
    private final String REGEX_12H ="([01]?[0-9]|2[0-3]):[0-5][0-9]";
    private final String EXCLUDE_ALL_NOT_TIME=".*[^0-9:]";

    @Override
    public void invalidated(Observable observable){
        StringProperty test = (StringProperty)observable;
        System.out.println("Test value in overriden invalidated function"+test);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        zones = FXCollections.observableList(new ArrayList<>(ZoneId.getAvailableZoneIds()));
        cbCustomer.setItems(SQLManager.getInstance().getCustomerList());
        startTimeField.textProperty().addListener((observable -> {
            String input = startTimeField.getText();
            if (!input.isEmpty()) {
                if (input.matches(EXCLUDE_ALL_NOT_TIME)){
                    startTimeField.setText(input.substring(0,input.length()-1));
                }
                if ( input.length()==5) {
                    if (!startTimeField.getText().matches(REGEX_24H)) {
                        new Alert(Alert.AlertType.ERROR, "Enter a valid time").showAndWait();
                    }
                }else if(input.length()>5){
                    startTimeField.setText(input.substring(0,5));
                }
            }
        }));
        /// startTimeField.
    }

    @FXML public void saveClicked(ActionEvent e){
        //SQLManager.addAppt();
        //startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {oldValue.matches()});

        //TODO: SQLAppointment.setAll
        System.out.println(startDate.getValue());
        String stTime = startTimeField.getText();
        try{
            int hour = Integer.valueOf(stTime.substring(0,2));
            int mins = Integer.valueOf(stTime.substring(3));
            LocalTime time = LocalTime.of(hour, mins);
            LocalDateTime test = constructStartDateTime();
            System.out.println(test);
            System.out.println(LocalDateTime.of(startDate.getValue(), time));
        }catch (Exception exc){
            System.out.println("something happened in AppointmentViewController: "+exc.getMessage());
        }


        endDate.getValue();
    }

    @FXML public void cancelClicked(ActionEvent e){
        String confirmation = "Discard changes?";
        if (ViewManager.showConfirmationView(confirmation)){
            ViewManager.closeWindowFromEvent(e);
        }
    }

    public LocalDateTime constructStartDateTime(){
        //TODO: Implement, stub
        LocalDateTime startDT = LocalDateTime.of(startDate.getValue(), LocalTime.parse(startTimeField.getCharacters()));
        //Error checking: if am/pm selected, Hour must be between 1 & 12, else 0-23
        //minutes must always be between 0 and 59
        return startDT;
    }


}
