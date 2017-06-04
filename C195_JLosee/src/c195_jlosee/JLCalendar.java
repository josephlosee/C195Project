package c195_jlosee;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

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
        calendar.setMinHeight(550);
        baseDate = LocalDate.now();
        banner = new HBox(10);
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
        banner.getChildren().add(nextMonth);
        banner.getChildren().add(currentYear);

        calendar.add(banner, 0 ,0 );
        constructCalendar(baseDate);
//        calendar.add(gpMonth, 0, 1);
    }

    public static JLCalendar getInstance(){
        return instance;
    }

    public GridPane getCalendar(){
        return this.calendar;
    }

    public void constructCalendar(LocalDate initialFocus){
        final int DAYS_IN_WEEK = 7;

        GridPane constructedMonth = new GridPane();
        constructedMonth.setMinHeight(450);
        constructedMonth.setPadding(new Insets(5.0));
        constructedMonth.gridLinesVisibleProperty().setValue(true);
        baseDate=initialFocus;

        currentMonth.setText(initialFocus.getMonth().toString());
        currentYear.setText(String.valueOf(initialFocus.getYear()));

        Month initialMonth = initialFocus.getMonth();
        int initialYear = initialFocus.getYear();

        int rowIndex = 0;
        for (int dayOfMonth = 1; dayOfMonth <= initialFocus.getMonth().length(initialFocus.isLeapYear()); dayOfMonth++){

            LocalDate date = LocalDate.of(initialYear, initialMonth, dayOfMonth);
            if(date.getDayOfWeek().getValue()==7){rowIndex++;}

            VBox dateBox = new VBox();
            dateBox.setPadding(new Insets(5.0));
            dateBox.setMinWidth(75);
            dateBox.setMinHeight(75);

            //TODO: PLACEHOLDER, get
            long numAppts = SQLManager.getInstance().getActiveUser().getUserAppts()
                    .stream()
                    .filter(a->a.getStartDateTime().toLocalDate().isEqual(date))
                    .count();
            //int numAppts = (int)(dayOfMonth*Math.random()*.3);
            dateBox.getChildren().addAll(new Label(String.valueOf(dayOfMonth)), new Label("# Appts: "+numAppts));

            if (dayOfMonth==initialFocus.getDayOfMonth()){
                dateBox.setStyle("-fx-background-color:AZURE");
            }
            constructedMonth.add(dateBox, date.getDayOfWeek().getValue()%DAYS_IN_WEEK, rowIndex);

        }

        this.calendar.getChildren().remove(gpMonth);
        this.gpMonth=constructedMonth;
        this.calendar.add(gpMonth, 0, 1);

    }
}//END OF CLASS

