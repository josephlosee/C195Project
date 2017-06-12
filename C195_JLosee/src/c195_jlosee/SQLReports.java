package c195_jlosee;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 6/7/2017.
 */
public class SQLReports {
    private String queryConsultantSchedule = "Select * from appointment where createdBy=? and start between ? and ?";
    private String queryApptTypeByMont = "Select DISTINCT description, count(*) AS ApptCount from appointment where start between ? and ? GROUP BY description"; //start is within the passed month/year
    private String queryCustomerLastActive = "Select max(start) where customerId = ? ";
    private String queryMostActiveCustomers = "Select customerId, count(customerId) AS ApptCount from appointment where start between ? and ? GROUP BY customerId ORDER BY count(customerId) DESC";
    private PreparedStatement pstCustomerLastActive, pstApptTypeByMonth, pstConsultantSchedule, pstMostActiveCustomer;

    public SQLReports(){
        try{
            Connection sqlConn = SQLManager.getInstance().getSQLConnection();
            pstApptTypeByMonth = sqlConn.prepareStatement(queryApptTypeByMont);
            pstCustomerLastActive = sqlConn.prepareStatement(queryCustomerLastActive);
            pstConsultantSchedule = sqlConn.prepareStatement(queryConsultantSchedule);
            pstMostActiveCustomer = sqlConn.prepareStatement(queryMostActiveCustomers);
        }catch (SQLException sqle){

        }
    }

    /**
     * Gets a list and count of appointments by description. Completed code
     * @param month
     * @param year
     * @return an empty list if there is no appointment.
     */
    public List<String> getAppointmentTypesByMonth(int month, int year){
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
     * @param userName the name of the consultant
     * @param startDate, endDate
     * @return empty list if no appointments for the given day.
     */
    public List<SQLAppointment> getConsultantSchedule(String userName, LocalDate startDate, LocalDate endDate){
        List<SQLAppointment> consultantSchedule = new ArrayList<>();

        SQLUser consultant = SQLManager.getInstance().getUserList()
                    .parallelStream()
                    .filter(user -> user.getUserName().equalsIgnoreCase(userName))
                    .findFirst()
                    .get();

        consultant.getUserAppts().parallelStream()
                .filter(appt->appt.getStartDateTime().isAfter(startDate.atStartOfDay(ZoneOffset.systemDefault()))
                        & appt.getStartDateTime().isBefore(endDate.atTime(23,59,59).atZone(ZoneOffset.systemDefault())))
                .forEach(appt->consultantSchedule.add(appt));

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

        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = end.atTime(23,59, 59);

        try {
            pstMostActiveCustomer.setTimestamp(1, Timestamp.valueOf(startDT));
            pstMostActiveCustomer.setTimestamp(2, Timestamp.valueOf(endDT));

            ResultSet rs = pstMostActiveCustomer.executeQuery();

            int i=0;
            while (i < numCustomers & rs.next()){
                i++;
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