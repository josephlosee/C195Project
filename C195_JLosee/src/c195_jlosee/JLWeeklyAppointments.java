package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Joe on 6/3/2017.
 */
public class JLWeeklyAppointments {
    private ScrollPane container = new ScrollPane();
    private VBox weekContainer = new VBox(5.0);
    private LocalDate startOfWeek = LocalDate.now();
    String formatterPattern = "ccc MM-dd";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterPattern);
    //private static JLWeeklyAppointments wkAppts = new JLWeeklyAppointments();

    public JLWeeklyAppointments(){
        weekContainer.setPadding(new Insets(5));
        populateWeekVBox();
        container.setContent(weekContainer);
    }

    public ScrollPane getWeekScrollPane(){
        return container;
    }

    public JLWeeklyAppointments(LocalDate startOfWeek){
        setStartOfWeek(startOfWeek);
    }

    /*public VBox getWeekVBox(){
        return weekContainer;
    }*/

    /**
     * Sets the local date that is the basis of the week container
     * @param dt
     * @return returns the instance for method chaining
     */
    public JLWeeklyAppointments setStartOfWeek(LocalDate dt){
        if (!this.startOfWeek.equals(dt)){
            this.startOfWeek=dt;
            weekContainer.getChildren().clear();
            populateWeekVBox();
        }
        return this;
    }

    /**
     * Populates the VBox including appointment information
     */
    private void populateWeekVBox(){
        String title = "This week's appointments: ";
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size:12pt");
        weekContainer.getChildren().add(lblTitle);
        for (int day = 0; day < 7; day++){
            LocalDate dayOfWeek = startOfWeek.plusDays(day);
            VBox dayVBox = new VBox();
            Label dayName = new Label(dayOfWeek.format(formatter));
            dayVBox.getChildren().add(dayName);
            //ArrayList<SQLAppointment> appts = null;
            //ObservableList<SQLAppointment> appts = FXCollections.observableArrayList(new ArrayList<>());
            List<SQLAppointment> collect = SQLManager.getInstance().getActiveUser().getUserAppts()
                    .stream()
                    .filter(a -> a.getStartDateTime().toLocalDate().isEqual(dayOfWeek))
                    .collect(Collectors.toList());
            collect.forEach(a-> dayVBox.getChildren().add(new Label(a.toString())));
            //dayVBox.getChildren().add();
            //TODO dayVBox.getChildren().add(ObservableList);


            weekContainer.getChildren().add(dayVBox);
        }
    }

    public void refresh(){
        weekContainer.getChildren().clear();
        this.populateWeekVBox();
    }
}//End of class
