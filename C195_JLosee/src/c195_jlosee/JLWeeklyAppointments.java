package c195_jlosee;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Joe on 6/3/2017.
 */
public class JLWeeklyAppointments {
    private VBox weekContainer = new VBox(5.0);
    private LocalDate startOfWeek = LocalDate.now();
    String formatterPattern = "ccc MM-dd";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterPattern);
    //private static JLWeeklyAppointments wkAppts = new JLWeeklyAppointments();

    public JLWeeklyAppointments(){
        populateWeekVBox();
    }

    public JLWeeklyAppointments(LocalDate startOfWeek){
        setStartOfWeek(startOfWeek);
    }

    public VBox getWeekVBox(){
        return weekContainer;
    }

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
        weekContainer.getChildren().add(new Label(title));
        for (int day = 0; day < 7; day++){
            LocalDate dayOfWeek = startOfWeek.plusDays(day);
            VBox dayVBox = new VBox();
            Label dayName = new Label(dayOfWeek.format(formatter));
            dayVBox.getChildren().add(dayName);
            //TODO dayVBox.getChildren().add(ObservableList);


            weekContainer.getChildren().add(dayVBox);
        }
    }
}//End of class
