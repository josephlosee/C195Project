package c195_jlosee;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/13/2017.
 */
public class SQLAppointment {

    private SimpleStringProperty date, startTime, title, description, location, contact, url, createdBy, createdDate;
    private LocalDate scheduledDate;
    private int apptID, customerID;

    //ASSUMPTION: business hours are 8am-6pm
    private LocalTime businessStart = LocalTime.of(8,0), businessEnd = LocalTime.of(18,0);

    public SQLAppointment(){

    }
    public SQLAppointment(LocalDateTime dateTime, String title, String descrip, String location, String contact, String URL){

        //TODO stub
        System.out.println("SQLApptConstructor Stub 1");
    }

    public SQLAppointment(LocalDateTime dateTime, String title, String descrip, String location, String contact, String URL, String createdBy, String createdDate){

    }

    public SQLAppointment(Map<String, String> values){

    }

    //todo: start tiem, end time

    public String getDate() {
        return date.get();
    }
    public SimpleStringProperty dateProperty() {
        return date;
    }
    public void setDate(String date) {
        this.date.set(date);
    }

    public String getStartTime() {
        return startTime.get();
    }

    public SimpleStringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getLocation() {
        return location.get();
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getContact() {
        return contact.get();
    }

    public SimpleStringProperty contactProperty() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public SimpleStringProperty createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    public String getCreatedDate() {
        return createdDate.get();
    }

    public SimpleStringProperty createdDateProperty() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate.set(createdDate);
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public int getApptID() {
        return apptID;
    }

    public void setApptID(int apptID) {
        this.apptID = apptID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }


}
