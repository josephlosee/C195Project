package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/12/2017.
 */
public class MainViewController implements Initializable{

    @FXML
    private TableView customerTable, appointmentTable;
    @FXML private GridPane gpMain;
    private JLCalendar calendar = JLCalendar.getInstance();
    private JLWeeklyAppointments weeklyAppts = new JLWeeklyAppointments();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerTable.setItems(SQLManager.getInstance().getCustomerList());

        calendar.getVBoxList().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                setCalendarActions();
            }
        });
        this.setCalendarActions();
        this.weeklyAppts.getWeekScrollPane().setOnMouseClicked(event->{
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate endOfWeek = LocalDate.now().plusDays(1);
            SQLManager.getInstance().getActiveUser().getUserAppts()
                    .parallelStream()
                    .filter(a->a.getStartDateTime().toLocalDate().isAfter(yesterday) &&
                            a.getStartDateTime().toLocalDate().isBefore(endOfWeek))
                    .collect(Collectors.toList());
        });
        gpMain.add(calendar.getCalendar(), 0,0);
        gpMain.add(weeklyAppts.getWeekScrollPane(), 1, 0);
        gpMain.setAlignment(Pos.TOP_CENTER);
    }

    @FXML public void addCustClicked(){
        System.out.println("Add Customer Button Clicked");
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("resources/CustomerView.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Add Customer");
        stage.showAndWait();
    }

    @FXML public void editCustClicked() {
        SQLCustomer updateCustomer =(SQLCustomer)customerTable.getSelectionModel().getSelectedItem();

        if (updateCustomer!=null){
            Parent modProdPane = new Region();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/CustomerView.fxml"));
            try {
                //Setup the parent
                modProdPane = (Parent) loader.load();
                //Get the reference to the controller class so
                CustomerViewController controller = loader.<CustomerViewController>getController();
                //We can populate the view with the part to be modified.
                controller.editCustomer(updateCustomer);

            } catch (IOException ioExc) {
                ioExc.printStackTrace();
            }
            //Resume setting up
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(modProdPane));

            //Show and Wait to take away input from the main window
            secondaryStage.showAndWait();
        }
    }

    @FXML public void viewCustClicked(){
        int selectionIndex = customerTable.getSelectionModel().getSelectedIndex();
        if (selectionIndex>-1){
            SQLCustomer updateCustomer = SQLManager.getInstance().getCustomerList().get(selectionIndex);

            Parent modProdPane = new Region();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/CustomerView.fxml"));
            try {
                //Setup the parent
                modProdPane = (Parent)loader.load();
                //Get the reference to the controller class so
                CustomerViewController controller =loader.<CustomerViewController>getController();
                //We can populate the view with the part to be modified.
                controller.viewCustomer(updateCustomer);

            }catch (IOException ioExc){
                ioExc.printStackTrace();
            }
            //Resume setting up
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(modProdPane));
            //Show and Wait to take away input from the main window
            secondaryStage.showAndWait();
        }
    }

    /**
     * Handler for Add Appointment clicked
     */
    @FXML public void addApptClicked(){
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("resources/AppointmentView.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Add Appointment");
        stage.showAndWait();
        this.refreshCalendars();
    }

    /**
     * On clicking a customer, the table is populated with their appointments
     */
    @FXML public void customerSelected(){
        int i = customerTable.getSelectionModel().getSelectedIndex();
        if (i>-1) {
            SQLCustomer selectedCustomer = (SQLCustomer) customerTable.getItems().get(i);

            if (selectedCustomer.getCustomerAppointments().size()>0){
                appointmentTable.setItems(FXCollections.observableArrayList(selectedCustomer.getCustomerAppointments()));
                appointmentTable.getSortOrder().setAll(appointmentTable.getColumns());
                appointmentTable.getSortOrder().clear();
            }
        }
    }

    @FXML public void editApptClicked(){
        int selectionIndex = appointmentTable.getSelectionModel().getSelectedIndex();

        if (selectionIndex>-1){
            SQLAppointment selectedAppointment = (SQLAppointment)appointmentTable.getItems().get(selectionIndex);
            if(selectedAppointment.getStartDateTime().compareTo(ZonedDateTime.now())<0){
                String strPastAppointment = "You may not edit past appointments.";
                new Alert(Alert.AlertType.ERROR, strPastAppointment)
                        .showAndWait();
            } else{
                Parent modProdPane = new Region();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/AppointmentView.fxml"));
                try {
                    //Setup the parent
                    modProdPane = (Parent)loader.load();
                    //Get the reference to the controller class so
                    AppointmentViewController controller =loader.<AppointmentViewController>getController();
                    //We can populate the view with the part to be modified.
                    controller.editAppointment(selectedAppointment);

                }catch (IOException ioExc){
                    ioExc.printStackTrace();
                }
                //Resume setting up
                Stage secondaryStage = new Stage();
                secondaryStage.setScene(new Scene(modProdPane));

                //Show and Wait to take away input from the main window
                secondaryStage.showAndWait();
                //TODO - nothing?
            }
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Select an appointment to edit.")
                    .showAndWait();
        }
        this.refreshCalendars();
    }

    @FXML public void viewApptClicked(){

        int selectionIndex = appointmentTable.getSelectionModel().getSelectedIndex();

        if (selectionIndex>-1){
            SQLAppointment viewAppt = (SQLAppointment)appointmentTable.getItems().get(selectionIndex);

            Parent modProdPane = new Region();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/AppointmentView.fxml"));
            try {
                //Setup the parent
                modProdPane = (Parent)loader.load();
                //Get the reference to the controller class so
                AppointmentViewController controller =loader.<AppointmentViewController>getController();
                //We can populate the view with the part to be modified.
                controller.viewAppointment(viewAppt);

            }catch (IOException ioExc){
                ioExc.printStackTrace();
            }
            //Resume setting up
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(modProdPane));

            //Show and Wait to take away input from the main window
            secondaryStage.showAndWait();
        }
    }

    @FXML public void logoutClicked(ActionEvent e){
        String logoutConfMsg = "Logout?";
        boolean bLogout = new Alert(Alert.AlertType.CONFIRMATION, logoutConfMsg)
                .showAndWait()
                .filter(response->response== ButtonType.OK)
                .isPresent();
        if (bLogout){
            (((Node)e.getSource()).getScene().getWindow()).hide();
            SQLManager.getInstance().logout();

            try {//Open the login fxml and show it
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("resources/LoginView.fxml"));
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setTitle("Login");
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @FXML public void reportsClicked(){
        System.out.println("Reports button clicked");
    }

    public void refreshCalendars(){
        this.calendar.refresh();
        this.weeklyAppts.refresh();

        Instant time1 = Instant.now();
        setCalendarActions();
        System.out.println(Instant.now().minusMillis(time1.toEpochMilli()).toEpochMilli());
    }

    public void setCalendarActions(){
        calendar.getVBoxList().parallelStream()
                .filter((Node n)->(n.getId()!=null && n.getId().contains("dbox_")))
                .forEach((Node n)->n.setOnMouseClicked(e->{
                    LocalDate date = LocalDate.parse(n.getId().substring(5));
                    List dateToFilter = SQLManager.getInstance().getActiveUser().getUserAppts()
                            .parallelStream()
                            .filter(appt->appt.getStartDateTime().toLocalDate().isEqual(date))
                            .collect(Collectors.toList());
                    appointmentTable.getItems().clear();
                    appointmentTable.setItems(FXCollections.observableList(dateToFilter));
                    n.setStyle("-fx-background-color:");
                }));
    }
}//END OF CLASS