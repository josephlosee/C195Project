package c195_jlosee;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/13/2017.
 */
public class SQLAppointment {

    private SimpleStringProperty apptTime = new SimpleStringProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty description = new SimpleStringProperty();
    private SimpleStringProperty location = new SimpleStringProperty();
    private SimpleStringProperty contact = new SimpleStringProperty();
    private SimpleStringProperty url = new SimpleStringProperty();
    private SimpleStringProperty createdBy = new SimpleStringProperty();
    private LocalDateTime createdDate;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int apptID, customerID;

    //ASSUMPTION: business hours are 8am-6pm
    private LocalTime businessStart = LocalTime.of(8,0), businessEnd = LocalTime.of(18,0);

    public SQLAppointment(){

    }

    public SQLAppointment(ZonedDateTime startTime, ZonedDateTime endTime, String title, String descrip,
                          String location, String contact, String URL, int customerID, LocalDateTime createdDate, String createdBy) throws OutsideBusinessHoursException {

        try {
            setStartDateTime(startTime);
            setEndDateTime(endTime);
            setTitle(title);
            setDescription(descrip);
            setLocation(location);
            setContact(contact);
            setUrl(URL);
            setCustomerID(customerID);
            setCreatedDate(createdDate);
            setCreatedBy(createdBy);

        }catch (Exception e){
            throw e;
        }
    }

    public SQLAppointment(ZonedDateTime startTime, ZonedDateTime endTime, String title, String descrip,
                          String location, String contact, String URL, int customerID) throws OutsideBusinessHoursException {

        try {
            this.setStartDateTime(startTime);
            this.setEndDateTime(endTime);
            this.setTitle(title);
            this.setDescription(descrip);
            this.setLocation(location);
            this.setContact(contact);
            this.setUrl(URL);
            this.setCustomerID(customerID);
        }catch (Exception e){
            throw e;
        }
    }


    public void setStartDateTime(ZonedDateTime start) throws OutsideBusinessHoursException{
        if (isOutsideHours(start)){
            throw new OutsideBusinessHoursException("Appointment start time is not within business hours of "+businessStart+"-"+businessEnd);
        }else{
            this.startDate=start;
            setApptTime();
        }
    }

    public void setEndDateTime(ZonedDateTime end) throws OutsideBusinessHoursException{
        if (isOutsideHours(end)){
            throw new OutsideBusinessHoursException("Appointment end time is not within business hours of "+businessStart+"-"+businessEnd);
        }else{
            this.endDate=end;
            setApptTime();
        }
    }

    private void setApptTime(){
        try {
            this.apptTime.set(this.startDate.toLocalTime()+"-"+this.endDate.toLocalTime());
        }catch (NullPointerException npe){

        }
    }

    /**
     *
     * @param time
     * @return true if the input time falls outside the listed business hours.
     */
    private boolean isOutsideHours(ZonedDateTime time){
        boolean outsideHours = false;
        if (time.toLocalTime().compareTo(businessEnd)>0 || time.toLocalTime().compareTo(businessStart)<0){
            outsideHours = true;
        }

        return outsideHours;
    }

    public String getApptTime() {
        return apptTime.get();
    }
    public SimpleStringProperty apptTimeProperty() {return apptTime;    }

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

    ////////////////////////////////////////////////////////////
    // URL Property Accessor/Mutator
    ////////////////////////////////////////////////////////////
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate=createdDate;
    }

    ////////////////////////////////////////////////////////////
    // Appointment ID Accessor/Mutator
    ////////////////////////////////////////////////////////////
    public int getApptID() {
        return apptID;
    }
    public void setApptID(int apptID) {
        this.apptID = apptID;
    }

    ////////////////////////////////////////////////////////////
    // CustomerID Accessor/Mutator
    ////////////////////////////////////////////////////////////
    public int getCustomerID() {
        return customerID;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public ZonedDateTime getStartDateTime() {
        return startDate;
    }

    public ZonedDateTime getEndDateTime(){
        return endDate;
    }

    class OutsideBusinessHoursException extends Exception{
        OutsideBusinessHoursException(String message){
            super(message);
        }
    }
}
