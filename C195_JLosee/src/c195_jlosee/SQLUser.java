package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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
    private Instant lastUpdatedFromSQL = null;

    public SQLUser (int id, String name){
        this.userName = name;
        this.userID = id;
        userAppts = FXCollections.observableArrayList();
    }

    @Override public String toString(){
        return userName+" "+userID;
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
        if (lastUpdatedFromSQL==null || lastUpdatedFromSQL.plus(15, ChronoUnit.MINUTES).compareTo(Instant.now())<0)
        if (userAppts.size() ==0){
            userAppts.add(appt);

            //Check there are no conflicts
        }else if(userAppts.parallelStream()
                .filter(a->a.getStartDateTime().compareTo(appt.getStartDateTime())>=0&
                        a.getStartDateTime().compareTo(appt.getEndDateTime())<=0 ||
                        a.getEndDateTime().compareTo(appt.getStartDateTime())>=0 &
                                a.getEndDateTime().compareTo(appt.getEndDateTime())<=0)
                .count()>0){
            throw new ConflictingAppointmentException("One of your existing appointments conflicts with the requested appointment.");

        }else {
            userAppts.add(appt);
        }
    }

    /**
     * Used for checking the appointment can be schedule for this user.
     */
    public boolean canUpdateAppointment(SQLAppointment updateAppt, ZonedDateTime start, ZonedDateTime end){
        boolean canUpdate = false;

        if (userAppts.isEmpty()){
            SQLManager.getInstance().populateUserAppointmentList(this);
        }

        List<SQLAppointment> countOfConflictingAppts = userAppts.parallelStream()
                .filter(a->!(a.getApptID() == updateAppt.getApptID()))
                .filter(a->a.getStartDateTime().compareTo(start)>=0&
                        a.getStartDateTime().compareTo(end)<=0||
                        a.getEndDateTime().compareTo(start)>=0 &
                                a.getEndDateTime().compareTo(end)<=0)
                .collect(Collectors.toList());

        canUpdate = countOfConflictingAppts.size()==0;
        return canUpdate;
    }

    public ObservableList<SQLAppointment> getUserAppts(){
        //If it's been more than 15 minutes, or the appt list has never been populated, do so now
        if (lastUpdatedFromSQL == null ||
                Instant.now().compareTo(lastUpdatedFromSQL.plus(15, ChronoUnit.MINUTES))>0){
            SQLManager.getInstance().populateUserAppointmentList(this);
            setLastUpdatedApptsFromSQL();
        }
        return userAppts;
    }

    /**
     * Sets the time when the user's appt list was last checked against the SQL
     */
    public void setLastUpdatedApptsFromSQL(){
        lastUpdatedFromSQL=Instant.now();
    }
}
