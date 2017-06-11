package c195_jlosee;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Collectors;

/**
 * Created by Joe on 6/3/2017.
 */
public class JLCalendar {
    private GridPane calendar = new GridPane();
    private GridPane gpMonth = new GridPane();
    private Button nextMonth, prevMonth;
    private Label currentMonth, currentYear;
    private HBox banner;
    private LocalDate baseDate;
    private static JLCalendar instance = new JLCalendar();

    private JLCalendar(){
        calendar.setMinHeight(385);

        baseDate = LocalDate.now();
        banner = new HBox(10);
        banner.setMinHeight(35);
        banner.setPadding(new Insets(5.0));
        nextMonth = new Button(" > ");
        nextMonth.setMinWidth(25);
        nextMonth.setMinHeight(25);
        prevMonth = new Button(" < ");
        prevMonth.setMinHeight(25);
        prevMonth.setMinWidth(25);

        currentMonth = new Label(baseDate.getMonth().toString());
        currentMonth.setMinWidth(75);
        currentMonth.setMinHeight(25);
        currentMonth.setTextAlignment(TextAlignment.CENTER);
        currentYear = new Label(String.valueOf(baseDate.getYear()));
        currentYear.setMinWidth(50);
        currentYear.setMinHeight(25);
        currentMonth.setTextAlignment(TextAlignment.CENTER);

        prevMonth.setOnAction(event -> this.constructCalendar(baseDate=baseDate.minusMonths(1)));
        nextMonth.setOnAction(event->this.constructCalendar(baseDate=baseDate.plusMonths(1)));

        banner.getChildren().add(prevMonth);
        banner.getChildren().add(currentMonth);
        banner.getChildren().add(currentYear);
        banner.getChildren().add(nextMonth);

        gpMonth.setMinHeight(360);
        gpMonth.setPadding(new Insets(5.0));
        gpMonth.gridLinesVisibleProperty().setValue(true);

        calendar.add(banner, 0 ,0 );
        constructCalendar(baseDate);
        calendar.add(gpMonth, 0, 1);
    }

    public static JLCalendar getInstance(){
        return instance;
    }

    public GridPane getCalendar(){
        return this.calendar;
    }

    public ObservableList<Node> getVBoxList(){
        return gpMonth.getChildren();
    }

    public void constructCalendar(LocalDate initialFocus){
        final int DAYS_IN_WEEK = 7;
        gpMonth.getChildren().clear();

        //GridPane constructedMonth = new GridPane();
        //constructedMonth.setMinHeight(360);
        //constructedMonth.setPadding(new Insets(5.0));
        //constructedMonth.gridLinesVisibleProperty().setValue(true);

        baseDate=initialFocus;

        currentMonth.setText(initialFocus.getMonth().toString());
        currentYear.setText(String.valueOf(initialFocus.getYear()));

        Month initialMonth = initialFocus.getMonth();
        int initialYear = initialFocus.getYear();

        int rowIndex = 0;
        Instant test = Instant.now();
        for (int dayOfMonth = 1; dayOfMonth <= initialFocus.getMonth().length(initialFocus.isLeapYear()); dayOfMonth++){

            LocalDate date = LocalDate.of(initialYear, initialMonth, dayOfMonth);
            if(date.getDayOfWeek().getValue()==7){rowIndex++;}

            VBox dateBox = new VBox();
            dateBox.setPadding(new Insets(5.0));
            dateBox.setMinWidth(65);
            dateBox.setMinHeight(60);

            //get the count of appts for this day
            long numAppts = SQLManager.getInstance().getActiveUser().getUserAppts()
                    .stream()
                    .filter(a->a.getStartDateTime().toLocalDate().isEqual(date))
                    .count();

            dateBox.getChildren().addAll(new Label(String.valueOf(dayOfMonth)), new Label("# Appts: "+numAppts));

            //TODO: Make the click set the
            //dateBox.setOnMouseClicked(()->);
            if (date.isEqual(LocalDate.now())){
                dateBox.setStyle("-fx-background-color:lawngreen");
            }
            dateBox.setId("dbox_"+date);
            gpMonth.add(dateBox, date.getDayOfWeek().getValue()%DAYS_IN_WEEK, rowIndex);
        }
        System.out.println(Instant.now().minusMillis(test.toEpochMilli()).toEpochMilli());

        //this.calendar.getChildren().remove(gpMonth);
    }

    public void refresh(){
        constructCalendar(this.baseDate);
    }
}//END OF CLASS

