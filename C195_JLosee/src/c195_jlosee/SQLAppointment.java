package c195_jlosee;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.SimpleStringProperty;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/13/2017.
 */
public class SQLAppointment {

    private SimpleStringProperty apptTime = new SimpleStringProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty description = new SimpleStringProperty();
    private SimpleStringProperty locationProperty = new SimpleStringProperty();
    private SimpleStringProperty contact = new SimpleStringProperty();
    private SimpleStringProperty url = new SimpleStringProperty();
    private SimpleStringProperty customerName = new SimpleStringProperty();
    public String getDateProperty() {
        return dateProperty.get();
    }
    public SimpleStringProperty datePropertyProperty() {
        return dateProperty;
    }

    public void setDateProperty(ZonedDateTime time) {
        this.dateProperty.set(time.toLocalDate().toString());
    }

    private SimpleStringProperty dateProperty = new SimpleStringProperty();
    private SimpleStringProperty createdBy = new SimpleStringProperty();
    private LocalDateTime createdDate;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int apptID, customerID;
    private SQLCustomer customerRef;

    public LocalTime getBusinessStart() {
        return businessStart;
    }

    public void setBusinessStart(LocalTime businessStart) {
        this.businessStart = businessStart;
    }

    public LocalTime getBusinessEnd() {
        return businessEnd;
    }

    public void setBusinessEnd(LocalTime businessEnd) {
        this.businessEnd = businessEnd;
    }

    //ASSUMPTION: business hours are 8am-6pm
    private LocalTime businessStart = LocalTime.of(8,0), businessEnd = LocalTime.of(18,0);

    public SQLAppointment(){
        this.apptID=-1;
    }

    /**
     * Constructs an appointment from a resultset
     * @param rs the resultset cursor
     * @throws SQLException
     */
    public SQLAppointment(ResultSet rs) throws SQLException {
        try {
            this.setApptID(rs.getInt("appointmentId"));
            this.setCustomerID(rs.getInt("customerId"));
            this.setTitle(rs.getString("title"));
            this.setDescription(rs.getString("description"));
            this.setLocationProperty(rs.getString("location"));
            this.setContact(rs.getString("contact"));
            this.setUrl(rs.getString("url"));
            this.setCreatedBy(rs.getString("createdBy"));
            this.setCreatedDate(rs.getTimestamp("createdate").toLocalDateTime());
            ZonedDateTime startLocal = rs.getTimestamp("start").toInstant().atZone(ZoneId.systemDefault());
            ZonedDateTime endLocal = rs.getTimestamp("end").toInstant().atZone(ZoneId.systemDefault());

            try{
                LocalTime endHolder = this.getBusinessEnd();
                LocalTime startHolder = this.getBusinessStart();
                this.setBusinessStart(LocalTime.of(0,0));
                this.setBusinessEnd(LocalTime.of(23,59));
                this.setStartDateTime(startLocal);
                this.setEndDateTime(endLocal);
                this.setBusinessStart(startHolder);
                this.setBusinessEnd(endHolder);
                //appt.setCustomerRef(in);
            }catch (Exception e){
                //Discard this because we're pulling the information from the database so we don't really care
            }
        } catch (SQLException sql){
            throw sql;
        }
    }

    public SQLAppointment(ZonedDateTime startTime, ZonedDateTime endTime, String title, String descrip,
                          String locationProperty, String contact, String URL, int customerID, LocalDateTime createdDate, String createdBy) throws OutsideBusinessHoursException {

        try {
            setStartDateTime(startTime);
            setEndDateTime(endTime);
            setTitle(title);
            setDescription(descrip);
            setLocationProperty(locationProperty);
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
            this.setLocationProperty(location);
            this.setContact(contact);
            this.setUrl(URL);
            this.setCustomerID(customerID);
            this.retrieveCustomerInfo();
        }catch (Exception e){
            throw e;
        }
    }

    public SQLAppointment(ZonedDateTime startTime, ZonedDateTime endTime, String title, String descrip,
                          String location, String contact, String URL, SQLCustomer customer) throws OutsideBusinessHoursException {

        try {
            this.setStartDateTime(startTime);
            this.setEndDateTime(endTime);
            this.setTitle(title);
            this.setDescription(descrip);
            this.setLocationProperty(location);
            this.setContact(contact);
            this.setUrl(URL);
            this.setCustomerRef(customer);
        }catch (Exception e){
            throw e;
        }
    }


    public void retrieveCustomerInfo(){
        setCustomerRef(SQLManager.getInstance().getCustomerMap().get(this.customerID));
    }

    public void setCustomerRef(@NotNull SQLCustomer cust){
        this.customerRef=cust;
        if (cust!=null) {
            customerName.set(customerRef.getCustomerName());
            customerID=(customerRef.getCustomerID());
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
            throw new OutsideBusinessHoursException("Appointment end time is not within business hours of Monday through Friday, "+businessStart+"-"+businessEnd);
        }else{
            this.endDate=end;
            setApptTime();
        }
    }

    private void setApptTime(){
        try {
            this.apptTime.set(this.startDate.toLocalTime()+"-"+this.endDate.toLocalTime());
            this.setDateProperty(this.startDate);
        }catch (NullPointerException npe){
            //Discard the NPE as this method is called when one or the other of these times is null;
        }
    }

    @Override public String toString(){
        String apptTime = getApptTime();
        String customerName = customerRef.getCustomerName();
        String apptTitle = getTitle();
        return apptTime+" " +customerName+" "+apptTitle;
    }

    public SQLCustomer getCustomerRef(){
        return customerRef;
    }

     /**
     *
     * @param time
     * @return true if the input time falls outside the listed business hours.
     */
    private boolean isOutsideHours(ZonedDateTime time){
        boolean outsideHours = false;
        //check if the appointment falls outside business hours: 8am-6pm, Monday-Friday
        if (time.toLocalTime().compareTo(businessEnd)>0 || time.toLocalTime().compareTo(businessStart)<0 ||
                time.getDayOfWeek()== DayOfWeek.SATURDAY || time.getDayOfWeek() == DayOfWeek.SUNDAY){
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

    public String getLocationProperty() {
        return locationProperty.get();
    }
    public SimpleStringProperty locationPropertyProperty() {
        return locationProperty;
    }
    public void setLocationProperty(String locationProperty) {
        this.locationProperty.set(locationProperty);
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
        if (customerRef==null || customerID!=customerRef.getCustomerID()){
            this.retrieveCustomerInfo();
        }

    }

    public ZonedDateTime getStartDateTime() {
        return startDate;
    }
    public ZonedDateTime getEndDateTime(){
        return endDate;
    }

    public String getCustomerName() {
        return customerNameProperty().get();
    }

    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    class OutsideBusinessHoursException extends Exception{
        OutsideBusinessHoursException(String message){
            super(message);
        }
    }
}//END OF CLASS
