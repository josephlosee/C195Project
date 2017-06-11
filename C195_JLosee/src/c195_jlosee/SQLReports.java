package c195_jlosee;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 6/7/2017.
 */
public class SQLReports {
    private String queryConsultantSchedule = "Select start, end from appointment where createdBy=? and start between ? and ?";
    //, count(description)
    private String queryApptTypeByMont = "Select DISTINCT description, count(*) AS ApptCount from appointment where start between ? and ? GROUP BY description"; //start is within the passed month/year
    private String queryCustomerLastActive = "Select max(start) where customerId = ? ";
    private String queryMostActiveCustomers = "Select customerId, count(customerId) AS ApptCount from appointment where start between ? and ? GROUP BY ApptCount";
    private PreparedStatement pstCustomerLastActive, pstApptTypeByMonth, pstConsultantSchedule, pstMostActiveCustomer;
    //List<?> monthlyApptTypes

    public SQLReports(){
        try{
            Connection sqlConn = SQLManager.getInstance().getSQLConnection();
            pstApptTypeByMonth = sqlConn.prepareStatement(queryApptTypeByMont);
            pstCustomerLastActive = sqlConn.prepareStatement(queryCustomerLastActive);
            pstConsultantSchedule = sqlConn.prepareStatement(queryConsultantSchedule);
            pstCustomerLastActive = sqlConn.prepareStatement(queryMostActiveCustomers);
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
    public List<String> getAppointmentTypesByMonth(int month, int year){
        //TODO: STUB
        List<String> apptsByMonth = new ArrayList<>();

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0,0);
        LocalDateTime end = LocalDateTime.of(year, month, Month.of(month).length(Year.isLeap(year)), 23, 59);

        try{
            pstApptTypeByMonth.setTimestamp(1, Timestamp.valueOf(start));
            pstApptTypeByMonth.setTimestamp(2, Timestamp.valueOf(end));

            ResultSet rs = pstApptTypeByMonth.executeQuery();

            while (rs.next()){
                String appt = rs.getString("description")+", "+rs.getInt("ApptCount");
                System.out.println(appt);
                apptsByMonth.add(appt);
            }

        }catch (SQLException sqle){
            System.out.println("SQLException in SQLReports.getAppointTypesByMonth: "+sqle.getMessage());
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

        LocalDateTime startOfDay = LocalDateTime.of(scheduleForDay, LocalTime.of(0,0));
        LocalDateTime endOfDay = LocalDateTime.of(scheduleForDay, LocalTime.of(23, 59));

        try{
            pstConsultantSchedule.setInt(1, userId);
            pstConsultantSchedule.setTimestamp(2,Timestamp.valueOf(startOfDay));
            pstConsultantSchedule.setTimestamp(3, Timestamp.valueOf(endOfDay));

            ResultSet rs = pstConsultantSchedule.executeQuery();

            while (rs.next()){
                SQLAppointment currAppt = new SQLAppointment(rs);
                consultantSchedule.add(currAppt);
            }
        }catch (SQLException sqle){
            System.out.println("SQLException in SQLReports.getConsultantSchedule: " + sqle.getMessage());
        }

        return consultantSchedule;
    }

    public LocalDate customerLastActive(int customerID){
        LocalDate lastActive = null;

        try{
            pstCustomerLastActive.setInt(1, customerID);
            ResultSet rs = pstCustomerLastActive.executeQuery();

            if (rs.next()){
                Timestamp ts = rs.getTimestamp(1);
                lastActive = ts.toLocalDateTime().toLocalDate();
            }
        }catch (SQLException sql){

        }

        return lastActive;
    }

    public List<String> mostActiveCustomers(int numCustomers, LocalDate start, LocalDate end){
        List<String> activeCustomers = new ArrayList<>();
        //TODO

        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = end.atTime(23,59, 59);

        try {
            pstMostActiveCustomer.setTimestamp(1, Timestamp.valueOf(startDT));
            pstMostActiveCustomer.setTimestamp(2, Timestamp.valueOf(endDT));

            ResultSet rs = pstMostActiveCustomer.executeQuery();

            int i=0;
            while (rs.next() && i < numCustomers){
               int custID = rs.getInt("customerId");
               int apptCount = rs.getInt("ApptCount");
               String strCustCount = SQLManager.getInstance().getCustomerMap().get(custID).getCustomerName()+", "+apptCount;
               activeCustomers.add(strCustCount);
               System.out.println("Most Active Customer #" +i + " " + strCustCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activeCustomers;
    }
}//END OF CLASS
