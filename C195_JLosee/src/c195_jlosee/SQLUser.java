package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/8/2017.
 */
public class SQLUser {
    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    private int userID;
    private String userName;
    private ObservableList<SQLAppointment> userAppts;

    public SQLUser (int id, String name){
        this.userName = name;
        this.userID = id;
        userAppts = FXCollections.observableArrayList();
    }

    public void setApptList(ObservableList<SQLAppointment> apptList){
        this.userAppts=apptList;
    }

    /**
     * Attempts to add the appointment to the user
     * @param appt
     * @throws ConflictingAppointmentException
     */
    public void addAppointment(SQLAppointment appt) throws ConflictingAppointmentException {
        if (userAppts.size() ==0){
            userAppts.add(appt);
            //SQLManager.getInstance().addAppointment(appt);
        }else if(userAppts.stream()
                //if startDateTime of existing appointments is >= desiredStartDateTime and <= desiredEndDateTime
                //OR if endDateTime of existing appointment is >= desiredStartDateTime and <= desiredEndDateTime
                .filter(a->a.getStartDateTime().compareTo(appt.getStartDateTime())>=0&
                        a.getStartDateTime().compareTo(appt.getEndDateTime())<=0 ||
                        a.getEndDateTime().compareTo(appt.getStartDateTime())>=0 &
                                a.getEndDateTime().compareTo(appt.getEndDateTime())<=0)
                .count()>0){
            throw new ConflictingAppointmentException("One of your existing appointments conflicts with the requested appointment.");

        }else {
            userAppts.add(appt);
            //SQLManager.getInstance().addAppointment(appt);
        }
    }

    public ObservableList<SQLAppointment> getUserAppts(){
        return userAppts;
    }
}
