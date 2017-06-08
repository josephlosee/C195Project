package c195_jlosee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 6/7/2017.
 */
public class SQLReports {
    private String queryConsultantSchedule = "Select start, end from appointment where createdBy=? and start between ? and ?";
    private String queryApptTypeByMont = "Select description, count(description) from appointment where ?"; //start is within the passed month/year
    private String queryCustomerLastActive = "Select max(start) where customerId = ? ";
    PreparedStatement pstCustomerLastActive, pstApptTypeByMonth, pstConsultantSchedule;
    //List<?> monthlyApptTypes

    public SQLReports(){
        try{
            Connection sqlConn = SQLManager.getInstance().getSQLConnection();
            pstApptTypeByMonth = sqlConn.prepareStatement(queryApptTypeByMont);
            pstCustomerLastActive = sqlConn.prepareStatement(queryCustomerLastActive);
            pstConsultantSchedule = sqlConn.prepareStatement(queryConsultantSchedule);
        }catch (SQLException sqle){

        }


    }

    //TODO: Generate reports from SQL

    /**
     * Gets a list and count of appointments by description
     * @param month
     * @param year
     * @return an empty list if there is no appointment.
     */
    public List<String> getAppointmentTypesByMonth(Month month, Year year){
        //TODO: STUB
        List<String> apptsByMonth = new ArrayList<>();
        pstApptTypeByMonth.asdf;

        try{
            pstApptTypeByMonth.setTime();
            pstApptTypeByMonth.setTime();
        }catch (SQLException sqle){

        }

        return apptsByMonth;
    }

    /**
     * Generates a list of the specified consultant(user) appointments for the given date
     * @param userId
     * @param scheduleForDay
     * @return empty list if no appointments for the given day.
     */
    public List<SQLAppointment> getConsultantSchedule(int userId, LocalDate scheduleForDay){
        List<SQLAppointment> consultantSchedule = new ArrayList<>();

        pstConsultantSchedule.asdf;

        try{
            pstConsultantSchedule.setInt();
            pstConsultantSchedule.setTime();
        }catch (SQLException sqle){

        }

        return consultantSchedule;
    }

    public LocalDate customerLastActive(int customerID){
        LocalDate lastActive;

        pstCustomerLastActive.asdf;

        try{
            pstCustomerLastActive.setInt(1, customerID);
        }catch (SQLException sql){

        }

        return lastActive;
    }

    public List<String> mostActiveCustomers(){
        return null;
        //TODO
    }
}//END OF CLASS
