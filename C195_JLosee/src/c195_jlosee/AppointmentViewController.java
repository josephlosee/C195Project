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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.ResourceBundle;



/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/22/2017.
 */
public class AppointmentViewController implements Initializable, InvalidationListener {
    @FXML
    TextField titleField, descriptionField, locationField, contactField, urlField, startTimeField, endTimeField;
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
        StringProperty timeString = (StringProperty)observable;
        //System.out.println("Test value in overriden invalidated function"+test);
        String input = timeString.get();
        if (!input.isEmpty()) {
            if (input.matches(EXCLUDE_ALL_NOT_TIME)){
                timeString.set(input.substring(0,input.length()-1));
            }
            if ( input.length()==5) {
                if (!input.matches(REGEX_24H)) {
                    new Alert(Alert.AlertType.ERROR, "Enter a valid time").showAndWait();
                }
            }else if(input.length()>5){
                timeString.set(input.substring(0,5));
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        zones = FXCollections.observableList(new ArrayList<>(ZoneId.getAvailableZoneIds()));
        cbCustomer.setItems(SQLManager.getInstance().getCustomerList());
        startTimeField.textProperty().addListener(this::invalidated);
        endTimeField.textProperty().addListener(this::invalidated);
        //artDate.
        /// startTimeField.
    }

    @FXML public void saveClicked(ActionEvent e){
        //SQLManager.addAppt();
        //startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {oldValue.matches()});

        //TODO: WHY ARE TWO ERROR MESSAGES THROWN? DOES IT NEED 0?
        //TODO: SQLAppointment.setAll
        try{
            LocalDateTime start = constructStartDateTime();
            LocalDateTime end = constructEndDateTime();
            String title = titleField.getText();
            String description = descriptionField.getText();
            String url = urlField.getText();
            String contact = contactField.getText();
            String location = locationField.getText();
            SQLCustomer test = (SQLCustomer)cbCustomer.getSelectionModel().getSelectedItem();
            //SQLAppointment current = new SQLAppointment(start, end, title, description, location, contact, url);

            LocalDateTime conflict = SQLManager.getInstance().canSchedule(test.getCustomerID(), start, end);

            if (start.compareTo(end)>-1){
                ViewManager.showErrorMessage("Appointment start time cannot be after the end. ");
            }
        }catch (Exception exc){
            System.out.println("something happened in AppointmentViewController: "+exc.getMessage());
        }
    }

    /**
     * Discard changes if accepted
     * @param e
     */
    @FXML public void cancelClicked(ActionEvent e){
        String confirmation = "Discard changes?";
        if (ViewManager.showConfirmationView(confirmation)){
            ViewManager.closeWindowFromEvent(e);
        }
    }

    /**
     * Constructs the start date time from the date picker and
     * @return
     */
    public LocalDateTime constructStartDateTime(){
        //TODO: Implement, stub
        LocalDateTime startDT = null;
        try {
            LocalDate date = getDate();
            LocalTime time = LocalTime.parse(startTimeField.getCharacters());
            startDT = LocalDateTime.of(date, time);
        } catch (DateTimeParseException dtpe){
            ViewManager.showErrorMessage("Please enter a valid date and start time for the appointment.");
        } catch (Exception e){
            System.out.println("Something went wrong in constructing the appointment start date time.");
        }
        return startDT;
    }

    public LocalDateTime constructEndDateTime(){
        LocalDateTime endDT = null;

        try{
            LocalDate date = getDate();
            LocalTime time = LocalTime.parse(endTimeField.getCharacters());
            endDT=LocalDateTime.of(date, time);
        } catch (DateTimeParseException dtpe){
            ViewManager.showErrorMessage("Please enter a valid date and end time for the appointment.");
        } catch (Exception e){
            System.out.println("Something went wrong in constructing the appointment end date time.");
        }

        return endDT;
    }

    public LocalDate getDate(){
        LocalDate ldValue = null;
        try {
            ldValue = startDate.getValue();
        }catch (NullPointerException npe){
            //TODO: Alert for date not picked
            System.out.println("Select a date.");
        }

        return ldValue;
    }
}
