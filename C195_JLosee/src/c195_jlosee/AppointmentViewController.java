package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.net.URL;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/22/2017.
 */
public class AppointmentViewController implements Initializable {
    @FXML
    TextField title, desc, location, contact, url, startTimeField, endTime;
    @FXML
    ComboBox startZone, cbCustomer;
    ObservableList<String> zones;
    @FXML
    DatePicker startDate, endDate;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set test = ZoneId.getAvailableZoneIds();
        //test.
        zones = FXCollections.observableList(new ArrayList<>(ZoneId.getAvailableZoneIds()));
        zones.stream().forEach(System.out::println);
        cbCustomer.setItems(SQLManager.getInstance().getCustomerList());

    }

    public void saveAppt(ActionEvent e){
        //SQLManager.addAppt();
        //TODO: SQLAppointment.setAll
        System.out.println(startDate.getValue());
        endDate.getValue();
    }

    public void cancel(ActionEvent e){
        String confirmation = "Discard changes?";
        if (ViewManager.showConfirmationView(confirmation)){
            ViewManager.closeWindowFromEvent(e);
        }
    }


}
