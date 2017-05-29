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

    private SimpleStringProperty apptTime;
    private SimpleStringProperty title;
    private SimpleStringProperty description;
    private SimpleStringProperty location;
    private SimpleStringProperty contact;
    private SimpleStringProperty url;
    private SimpleStringProperty createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime startDate, endDate;
    private int apptID, customerID;

    //ASSUMPTION: business hours are 8am-6pm
    private LocalTime businessStart = LocalTime.of(8,0), businessEnd = LocalTime.of(18,0);

    public SQLAppointment(){

    }
    public SQLAppointment(LocalDateTime startTime, LocalDateTime endTime, String title, String descrip,
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

    public void setStartDateTime(LocalDateTime start) throws OutsideBusinessHoursException{
        if (isOutsideHours(start)){
            throw new OutsideBusinessHoursException("Appointment start time is not within business hours of "+businessStart+"-"+businessEnd);
        }else{
            this.startDate=start;
            setApptTime();
        }
    }

    public void setEndDateTime(LocalDateTime end) throws OutsideBusinessHoursException{
        if (isOutsideHours(end)){
            throw new OutsideBusinessHoursException("Appointment end time is not within business hours of "+businessStart+"-"+businessEnd);
        }else{
            this.endDate=end;
            setApptTime();
        }
    }

    private void setApptTime(){
        this.apptTime.set(this.startDate.toLocalTime()+"-"+this.endDate.toLocalTime());
    }

    /**
     *
     * @param time
     * @return true if the input time falls outside the listed business hours.
     */
    private boolean isOutsideHours(LocalDateTime time){
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

    public LocalDateTime getStartDateTime() {
        return startDate;
    }

    public LocalDateTime getEndDateTime(){
        return endDate;
    }

    class OutsideBusinessHoursException extends Exception{
        OutsideBusinessHoursException(String message){
            super(message);
        }
    }
}
