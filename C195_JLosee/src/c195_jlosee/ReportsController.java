package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Created by Joe on 6/10/2017.
 */
public class ReportsController implements Initializable{

    @FXML TextField apptTypeYearField, numCustomers;
    @FXML DatePicker startConsultantDatePicker, endConsultantDatePicker, startActiveCustomersDatePicker, endActiveCustomersDatePicker;
    @FXML ChoiceBox<String> apptTypesMonthCB = new ChoiceBox<>(), consultantCB = new ChoiceBox<>();
    @FXML GridPane apptTypeGrid, consSchedGrid, activeCustomersGrid;
    @FXML TableView consultScheduleTable=new TableView();
    @FXML ListView activeCustomerList=new ListView(), apptTypeCountList=new ListView();
    private SQLReports reports = new SQLReports();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //System.out.println(Month.values());

        List<String> monthsList = new ArrayList<>();
        Month[] monthArray = Month.values();
        for (int i =0; i < monthArray.length; i++){
            monthsList.add(i, monthArray[i].toString());
        }

        List<String> userList = new ArrayList<>();
        SQLManager.getInstance().getUserMap().values()
                .parallelStream()
                .forEach(user->userList.add(user.getUserName()));

        apptTypesMonthCB.setItems(FXCollections.observableArrayList(monthsList));
        consultantCB.setItems(FXCollections.observableList(userList));
    }

    @FXML public void reportMonthlyAppointmentTypesClicked(){
        setGridPaneVisibility(apptTypeGrid);
    }

    @FXML public void reportConsultantScheduleClicked(){
        setGridPaneVisibility(consSchedGrid);
    }

    @FXML public void reportActiveCustomerClicked(){
        setGridPaneVisibility(activeCustomersGrid);
    }

    @FXML public void closeClicked(ActionEvent e){
        String confirmation = "Close?";
        boolean bCancel = new Alert(Alert.AlertType.CONFIRMATION, confirmation)
                .showAndWait()
                .filter(response->response== ButtonType.OK)
                .isPresent();
        if (bCancel){
            (((Node)e.getSource()).getScene().getWindow()).hide();
        }
    }

    /**
     * Generates the appointments by type for the selected month.
     */
    @FXML public void generateAppointments(){
        int apptMonth = apptTypesMonthCB.getSelectionModel().getSelectedIndex()+1;
        if (apptMonth==-1){
            new Alert(Alert.AlertType.ERROR, "Select a month.").showAndWait();
        }
        String year = apptTypeYearField.getText();
        if (year.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Enter a year.").showAndWait();
        }else{
            try {
                int iYear = Integer.parseInt(year);
                List<String> appointmentTypesByMonth = reports.getAppointmentTypesByMonth(apptMonth, iYear);
                apptTypeCountList.setItems(FXCollections.observableList(appointmentTypesByMonth));
                apptTypeCountList.refresh();
            }catch (Exception e){
                System.out.println("An exception occurred in ReportsController.generateAppointments: "+e);
                new Alert(Alert.AlertType.ERROR, "Enter a valid year.").showAndWait();
            }
        }
    }//End of generateAppointments method

    @FXML public void generateConsultantScheduleClicked(){
        try {
            LocalDate startDate = startConsultantDatePicker.getValue();
            LocalDate endDate = endConsultantDatePicker.getValue();
            if (startDate == null) {
                throw new IllegalArgumentException("Select a start date.");
            }
            if (endDate == null) {
                throw new IllegalArgumentException("Select an end date.");
            } else if (endDate.compareTo(startDate) < 0) {
                throw new IllegalArgumentException("Select an end date after the start date.");
            }

            String selectedConsultant = consultantCB.getSelectionModel().getSelectedItem();
            if (selectedConsultant==null || selectedConsultant.isEmpty()) throw new IllegalArgumentException("Select a consultant");
            /*int userId = -1;
            */

            List<SQLAppointment> consultantSchedule;

            if (selectedConsultant.equalsIgnoreCase(SQLManager.getInstance().getActiveUser().getUserName())){
                consultantSchedule = reports.getConsultantSchedule(selectedConsultant, startDate, endConsultantDatePicker.getValue());
                //Minor optimization:
                consultantSchedule = SQLManager.getInstance().getActiveUser().getUserAppts()
                        .parallelStream()
                        .filter(appt->appt.getStartDateTime().isAfter(startDate.minusDays(1).atTime(23,59,59).atZone(ZoneOffset.systemDefault()))
                                && appt.getEndDateTime().isBefore(endDate.plusDays(1).atStartOfDay().atZone(ZoneOffset.systemDefault())))
                        .collect(Collectors.toList());
            }else{
                consultantSchedule = reports.getConsultantSchedule(selectedConsultant, startDate, endDate);
            }

            consultScheduleTable.setItems(FXCollections.observableList(consultantSchedule));

            if (consultantSchedule.isEmpty()){
                new Alert(Alert.AlertType.INFORMATION, "No appointments were found for the selected consultant between "+startDate + " and " +endDate).showAndWait();
            }
        }catch (IllegalArgumentException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }


    }

    @FXML public void generateActiveCustomersClicked(){
        if (startActiveCustomersDatePicker.getValue()==null){
            new Alert(Alert.AlertType.ERROR, "Select a start date.").showAndWait();
        }
        if (endActiveCustomersDatePicker.getValue()==null){
            new Alert(Alert.AlertType.ERROR, "Select an end date.").showAndWait();
        }

        List<String> activeCustomers = reports.mostActiveCustomers(10, startActiveCustomersDatePicker.getValue(), endActiveCustomersDatePicker.getValue());

        activeCustomerList.setItems(FXCollections.observableList(activeCustomers));
        activeCustomerList.refresh();
    }

    /**
     * Toggle grid pane visibility
     * @param visible
     */
    private void setGridPaneVisibility(GridPane visible){
        apptTypeGrid.setVisible(false);
        consSchedGrid.setVisible(false);
        activeCustomersGrid.setVisible(false);
        visible.setVisible(true);
    }
}//END OF CLASS
