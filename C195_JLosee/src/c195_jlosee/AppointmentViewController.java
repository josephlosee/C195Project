package c195_jlosee;

import com.sun.istack.internal.NotNull;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;



/**
 * $AppointmentViewController, handles the logic and basic validation
 * Created by Joseph Losee on 5/22/2017.
 */
public class AppointmentViewController implements Initializable, InvalidationListener {
    @FXML
    private TextField titleField, descriptionField, locationField, contactField, urlField, startTimeField, endTimeField;
    @FXML
    private ComboBox cbCustomer;
    @FXML
    private DatePicker startDate;
    @FXML
    private MenuButton startTimeSpecifier, endTimeSpecifier;
    @FXML Button saveButton;

    private SQLAppointment current;// = new SQLAppointment();

    private final String REGEX_24H ="([01]?[0-9]|2[0-3]):[0-5][0-9]";
    private final String REGEX_24HTEST = "^([01]\\d|2[0-3]):?([0-5]\\d)$";
    private final String REGEX_12H ="([01]?[0-9]|2[0-3]):[0-5][0-9]";
    private final String EXCLUDE_ALL_NOT_TIME=".*[^0-9:]";

    /**
     * used for validating time input
     * @param observable
     */
    @Override
    public void invalidated(Observable observable){
        StringProperty timeString = (StringProperty)observable;
        String input = timeString.get();
        if (!input.isEmpty()) {
            if (input.matches(EXCLUDE_ALL_NOT_TIME)){
                timeString.set(input.substring(0,input.length()-1));
            }
            if ( input.length()==5) {
                if (!input.matches(REGEX_24HTEST) & !input.matches(REGEX_12H)) {
                    new Alert(Alert.AlertType.ERROR, "Enter a valid time").showAndWait();
                }
            }else if(input.length()>5){
                timeString.set(input.substring(0,5));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //zones = FXCollections.observableList(new ArrayList<>(ZoneId.getAvailableZoneIds()));
        cbCustomer.setItems(SQLManager.getInstance().getCustomerList());
        startTimeField.textProperty().addListener(this::invalidated);
        startTimeField.setText(LocalTime.now().plus(15, ChronoUnit.MINUTES).toString());
        endTimeField.textProperty().addListener(this::invalidated);
        endTimeField.setText(LocalTime.now().plus(30, ChronoUnit.MINUTES).toString());
        startDate.setValue(LocalDate.now());
    }

    @FXML public void saveClicked(ActionEvent e){
        try{
            ZonedDateTime start = constructStartDateTime();
            ZonedDateTime end = constructEndDateTime();

            if (start.compareTo(end)>-1){
                new Alert(Alert.AlertType.ERROR,"Appointment start time cannot be after the end. ")
                        .showAndWait();
            }
            int customerIndex = cbCustomer.getSelectionModel().getSelectedIndex();
            if (customerIndex <0){
                throw new Exception("No customer selected to schedule the appointment with.");
            }

            //Ensures all the fields have information
            String title = titleField.getText();
            if (title== null || title.isEmpty()) throw new Exception("Enter a title.");
            String description = descriptionField.getText();
            if (description== null || description.isEmpty()) throw new Exception("Enter a description.");
            String location = locationField.getText();
            if (location==null||location.isEmpty()) throw new Exception("Enter a location for the appointment.");
            String contact = contactField.getText();
            if (contact==null||contact.isEmpty()) throw new Exception("Enter contact information.");
            String url = urlField.getText();
            if (url==null||url.isEmpty()) throw new Exception("Enter a URL");


            SQLCustomer apptCustomer = SQLManager.getInstance().getCustomerList().get(customerIndex);

            //If this is a new appointment:
            if (current==null) {
                current = new SQLAppointment(start, end, title, description, location, contact, url, apptCustomer);

                SQLManager.getInstance().getActiveUser().addAppointment(current);
                apptCustomer.addAppointment(current);
                /*Else: Edit the existing customer that's been set*/
            } else {
                //TODO:
                //Check there's no conflict with existing customer appointments then update
                if (!apptCustomer.canUpdateAppointmentTime(current, start, end)) {
                    throw new ConflictingAppointmentException("An existing customer appointment conflicts with the requested times.");
                }
                //Check there's no conflict with the user that created the appointment
                SQLUser appointmentContact;
                 Optional<SQLUser> userOptional= SQLManager.getInstance().getUserList()
                        .parallelStream()
                        .filter(user->user.getUserName().equalsIgnoreCase(current.getCreatedBy()))
                        .findFirst();
                 if (userOptional.isPresent()){
                     appointmentContact = userOptional.get();
                     if (!appointmentContact.canUpdateAppointment(current, start, end)) {
                         throw new ConflictingAppointmentException("An existing user appointment conflicts with the requested times");
                     }
                     else {
                         //System.out.println("No conflicts for the appointment found. ");

                         current.setTitle(title);
                         current.setDescription(description);
                         current.setLocationProperty(location);
                         current.setContact(contact);
                         current.setUrl(url);
                         current.setStartDateTime(start);
                         current.setEndDateTime(end);

                         current.getCustomerRef().getCustomerAppointments().remove(current);
                         current.setCustomerRef(apptCustomer);
                         apptCustomer.getCustomerAppointments().add(current);
                         SQLManager.getInstance().updateAppointment(current);
                     }
                 } else{
                     new Alert(Alert.AlertType.ERROR, "createdBy user was not found in the user list.");
                 }

                //Select appointmentId where createdBy = current.getCreatedBy() and start between start and end
                //Then update the database
            }
            //Close the window after everything is done
            (((Node)e.getSource()).getScene().getWindow()).hide();

        }catch (ConflictingAppointmentException cae){
            //Call this if there is an appointment already scheduled for this customer
            new Alert(Alert.AlertType.ERROR, cae.getMessage())
                    .showAndWait();
        } catch(Exception exc){
            new Alert(Alert.AlertType.ERROR, exc.getMessage())
                    .showAndWait();
        }
    }

    /**
     * Discard changes if accepted
     * @param e
     */
    @FXML public void cancelClicked(ActionEvent e){
        String confirmation = "Discard changes?";
        boolean bCancel = new Alert(Alert.AlertType.CONFIRMATION, confirmation)
                .showAndWait()
                .filter(response->response==ButtonType.OK)
                .isPresent();
        if (bCancel){
            (((Node)e.getSource()).getScene().getWindow()).hide();
        }
    }

    /**
     * Constructs the start date time from the date picker and
     * @return
     */
    public ZonedDateTime constructStartDateTime(){

        ZonedDateTime startDT = null;
        try {
            LocalDate date = getDate();
            LocalTime time = LocalTime.parse(startTimeField.getCharacters());
            startDT = ZonedDateTime.of(date, time, ZoneId.systemDefault());
        } catch (DateTimeParseException dtpe){
            new Alert(Alert.AlertType.ERROR,"Please enter a valid date and start time for the appointment.")
                    .showAndWait();
        } catch (Exception e){
            System.out.println("Something went wrong in constructing the appointment start date time.");
        }
        return startDT;
    }

    public ZonedDateTime constructEndDateTime(){
        ZonedDateTime endDT = null;

        try{
            LocalDate date = getDate();
            LocalTime time = LocalTime.parse(endTimeField.getCharacters());
            endDT=ZonedDateTime.of(date, time, ZoneId.systemDefault());
        } catch (DateTimeParseException dtpe){
            new Alert(Alert.AlertType.ERROR,"Please enter a valid date and end time for the appointment.")
                    .showAndWait();
        } catch (Exception e){
            System.out.println("Something went wrong in constructing the appointment end date time.");
        }

        return endDT;
    }

    public LocalDate getDate(){
        LocalDate ldValue = null;
        try {
            ldValue = startDate.getValue();
            if (ldValue.compareTo(LocalDate.now())<0){
                new Alert(Alert.AlertType.ERROR, "Cannot schedule an appointment in the past.")
                        .showAndWait();
            }
        }catch (NullPointerException npe){
            new Alert(Alert.AlertType.INFORMATION, "Select a date")
                    .showAndWait();
        }

        return ldValue;
    }

    public void viewAppointment(@NotNull SQLAppointment appt){
        this.current = appt;
        setFields();
        enableControls(false);
        saveButton.setText("OK");
        saveButton.setOnAction(event->
                (((Node)event.getSource()).getScene().getWindow()).hide());
    }

    public void editAppointment(SQLAppointment appt){
        this.current = appt;
        setFields();
    }

    private void setFields(){
        titleField.setText(current.getTitle());
        descriptionField.setText(current.getDescription());
        contactField.setText(current.getContact());
        urlField.setText(current.getUrl());
        locationField.setText(current.getLocationProperty());

        startTimeField.setText(current.getStartDateTime().toLocalTime().toString());
        endTimeField.setText(current.getEndDateTime().toLocalTime().toString());

        startDate.setValue(current.getStartDateTime().toLocalDate());

        cbCustomer.getSelectionModel().select(cbCustomer.getItems().indexOf(current.getCustomerRef()));
    }

    private void enableControls(boolean enabled){
        titleField.setEditable(enabled);
        descriptionField.setEditable(enabled);
        contactField.setEditable(enabled);
        urlField.setEditable(enabled);
        locationField.setEditable(enabled);

        startTimeField.setEditable(enabled);
        endTimeField.setEditable(enabled);

        startDate.setDisable(!enabled);
        cbCustomer.setDisable(!enabled);
    }
}//END OF CLASS


