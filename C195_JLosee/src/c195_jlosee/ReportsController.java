package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Created by Joe on 6/10/2017.
 */
public class ReportsController implements Initializable{

    @FXML TextField apptTypeYearField, activeCustomerYearField;
    @FXML DatePicker startDatePicker, endDatePicker;
    @FXML ChoiceBox<String> apptTypesMonthCB = new ChoiceBox<>(), activeCustomersMonthCB = new ChoiceBox<>();
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

        apptTypesMonthCB.setItems(FXCollections.observableArrayList(monthsList));
        activeCustomersMonthCB.setItems(FXCollections.observableArrayList(monthsList));
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
            }catch (Exception e){
                System.out.println("An exception occurred in ReportsController.generateAppointments: "+e);
                new Alert(Alert.AlertType.ERROR, "Enter a valid year.").showAndWait();
            }
        }

    }

    @FXML public void generateConsultantScheduleClicked(){

    }

    @FXML public void generateActiveCustomersClicked(){
        //List<String> customerActivity = reports.mostActiveCustomers(10, )
        //activeCustomerList.setItems((FXCollections.observableList()));

        if (startDatePicker.getValue()==null){
            new Alert(Alert.AlertType.ERROR, "Select a start date.").showAndWait();
        }
        if (endDatePicker.getValue()==null){
            new Alert(Alert.AlertType.ERROR, "Select an end date.").showAndWait();
        }

        List<String> activeCustomers = reports.mostActiveCustomers(10, startDatePicker.getValue(), endDatePicker.getValue());

        activeCustomerList.setItems(FXCollections.observableList(activeCustomers));
    }

    private void setGridPaneVisibility(GridPane visible){
        apptTypeGrid.setVisible(false);
        consSchedGrid.setVisible(false);
        activeCustomersGrid.setVisible(false);
        visible.setVisible(true);
    }
}//END OF CLASS
