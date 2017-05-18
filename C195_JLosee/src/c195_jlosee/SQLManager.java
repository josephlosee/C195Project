package c195_jlosee;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/8/2017.
 */
public class SQLManager {

    //TODO: Add Prepared Statements for SQL Queries up here along with strings for grouped references
    //SQL Connection information in one convenient package!
    private static String driver   = "com.mysql.jdbc.Driver"; ;
    private static String schema = "U04bqK";
    private static String url = "jdbc:mysql://52.206.157.109/"+schema;
    private static String user = "U04bqK", pass="53688195806";
    private static Connection sqlConnection = null;
    private static final SQLManager instance = new SQLManager();

    ObservableList <SQLCustomer> customerList;
    ObservableList <SQLAppointment> apptList;

    private static SQLUser activeUser = null;

    private SQLManager(){
        sqlConnection = getSQLConnection();
    }

    public static SQLManager getInstance(){
        return instance;
    }

    //Returns the sql connection object
    public static Connection getSQLConnection(){

        //Would use a data source instead, but the SQL server seems to be set up for this.
        if (sqlConnection==null){
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException cnfe){
                System.out.println("Class not found exception encountered: " + cnfe.getMessage());
            }

            try {
                sqlConnection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException sqlE){
                System.out.println("SQL Exception Encountered: "+sqlE.getMessage());
            }
        }

        return sqlConnection;

    }

    public boolean login(String user, String pass){
        //Sanitize
        PreparedStatement prepStatement;
        ResultSet res = null;
        boolean success = false;

        //LOGIN QUERY
        String loginSQLString = "select * from user where userName = ? and password = ?";

        try {
            //use prepare statement to prevent sql injection
            prepStatement = getSQLConnection().prepareStatement(loginSQLString);
            prepStatement.setString(1, user);
            prepStatement.setString(2, pass);
            res = prepStatement.executeQuery();

            //password/usesrname should match a single result
            if (res.next()){
                int userID = res.getInt(1);
                String userName = res.getString(2);

                //TODO: Make this update the log file for logins
                System.out.println("User login successful!");
                System.out.println("UserID: " + userID);
                System.out.println("UserName: " + userName);
                activeUser = new SQLUser(userID, userName);

                success = true;
            }

        }catch (SQLException sqlE){
            System.out.println("Error creating statement: "+ sqlE.getMessage());
        }catch (Exception e){
            System.out.println("An generic exception occurred during the prepared statement of login. "+e.getMessage());
        }

        return success;

    }

    public void logout(){
        activeUser = null;
    }

    public boolean addCustomer(SQLCustomer inCustomer){
        boolean addSucceed = false;

        String addCustString = "Insert into customer (customerID, customerName, addressId, active, createDate, createdBy) " +
                                            "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement prepStatement = getSQLConnection().prepareStatement(addCustString)){
            prepStatement.setInt(1, inCustomer.getCustomerID());
            prepStatement.setString(2, inCustomer.getCustomerName());
            prepStatement.setInt(3, inCustomer.getAddressID());
            prepStatement.setInt(4, inCustomer.getActive());//not sure what active is for, but I'll probably just let this be set as a checkbox?
            prepStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            prepStatement.setString(6, activeUser.getUserName());



            //TODO: Add "active" checkbox to customer form
            prepStatement.executeQuery();

            //After all is said and done, if no exceptions are encountered, add the customer to the list.
            customerList.add(inCustomer);
            addSucceed=true;
        }catch(SQLException sqlE){
            System.out.println("Error creating statement: "+ sqlE.getMessage());
        }

        return addSucceed;
    }

    /**
     *
     * @param countryName
     * @return the new countryId or -1 if something went wrong,
     */
    public int addCountry(String countryName){
        int countryID = -1;
        //Quick Debug test:
        //try
        String selectCountry = "Select * from country where country=?";
        String addCountry= "Insert into country (countryId, country, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?, ?)";
        try (PreparedStatement pstCountryExistQuery = getSQLConnection().prepareStatement(selectCountry)){
            pstCountryExistQuery.setString(1, countryName);
            ResultSet rs = pstCountryExistQuery.executeQuery();
            if (rs.next()){
                countryID = rs.getInt(1);
            }else{
                //find the highest country ID and increment it, because I can't turn on auto-inc in the DB
                rs = getSQLConnection().createStatement().executeQuery("Select Max(countryId) from country");
                if (rs.next()){
                    countryID=rs.getInt(1);
                }

                //
                PreparedStatement pstAddCountry = getSQLConnection().prepareStatement(addCountry);
                pstAddCountry.setInt(1, ++countryID);
                pstAddCountry.setString(2, countryName);
                pstAddCountry.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstAddCountry.setString(4, activeUser.getUserName());
                pstAddCountry.setString(5, activeUser.getUserName());
                int i = pstAddCountry.executeUpdate();

                //getSQLConnection().commit();
            }

        }catch (SQLException e){
            //Handle the sql exception

            System.out.println("SQLException in addCountry: "+ e.getMessage());
        }

        return countryID;
    }

    public int addCity(String cityName, int countryID){
        int cityID = -1;
        String selectCity = "Select * from city where city=? and countryID=?";
        String addCity = "INSERT INTO city (cityID, city, countryID, createDate, createdBy, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement pstCityExists = sqlConnection.prepareStatement(selectCity)){
            pstCityExists.setString(1,cityName);
            pstCityExists.setInt(2, countryID);
            ResultSet cityPresent = pstCityExists.executeQuery();
            if (cityPresent.next()){
                cityID=cityPresent.getInt(1);
            }
            else{
                ResultSet nextID = getSQLConnection().createStatement().executeQuery("Select MAX(cityID) from city");
                if (nextID.next()){
                    cityID = nextID.getInt(1);
                }

                PreparedStatement pstAddCity = sqlConnection.prepareStatement(addCity);
                pstAddCity.setInt(1, ++cityID);
                pstAddCity.setString(2, cityName);
                pstAddCity.setInt(3, countryID);
                pstAddCity.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
                pstAddCity.setString(5, activeUser.getUserName());
                pstAddCity.setString(6, activeUser.getUserName());
                pstAddCity.executeUpdate();
            }
        }catch (SQLException e){
            System.out.println("SQLException in addCity: "+e.getMessage());
        }

        return cityID;
    }

    public int addAddress(String addressLine1, String addressLine2, String postCode, String phone, int cityID){
        int addressID = -1;

        String selectAddress = "Select * from address where address=? and address2=? and cityID=? and postalCode=? and phone=?";
        String addAddress="INSERT INTO address (addressID, address, address2, cityID, postalCode, phone, createDate, createdBy, lastUpdateBy)"+
                " VALUES (?, ? , ?, ?, ?, ?, ?, ?, ?)";


        try(PreparedStatement pstAddrExists = sqlConnection.prepareStatement(selectAddress)){
            pstAddrExists.setString(1,addressLine1);
            pstAddrExists.setString(2, addressLine2);
            pstAddrExists.setInt(3, cityID);
            pstAddrExists.setString(4, postCode);
            pstAddrExists.setString(5, phone);


            ResultSet rs = pstAddrExists.executeQuery();
            if (rs.next()){
                addressID=rs.getInt(1);
                //If the address is present.
            }else{
                rs=getSQLConnection().createStatement().executeQuery("SELECT MAX(addressID) from address");
                if (rs.next()) {
                    addressID = rs.getInt(1);
                }
                    PreparedStatement pstAddAddress = sqlConnection.prepareStatement(addAddress);
                    pstAddAddress.setInt(1, ++addressID);
                    pstAddAddress.setString(2, addressLine1);
                    pstAddAddress.setString(3, addressLine2);
                    pstAddAddress.setInt(4, cityID);
                    pstAddAddress.setString(5, postCode);
                    pstAddAddress.setString(6, phone);
                    pstAddAddress.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                    pstAddAddress.setString(8, activeUser.getUserName());
                    pstAddAddress.setString(9, activeUser.getUserName());
                    pstAddAddress.executeUpdate();
            }
        }catch (SQLException e){
            //TODO: Handle
            System.out.println("SQLException in addAddres: "+e.getMessage());
        }

        return addressID;
    }


    private void populateCustomerList(){
        //TODO: Select * from customer Join address Using(addressId)  Join city Using (cityId) Join country using (countryId);
        String allCustQuery = "SELECT * FROM CUSTOMER";
        SQLCustomer current;
        try (ResultSet rs = this.sqlConnection.createStatement().executeQuery(allCustQuery)) {
            try{
                if(rs.next()){
                    //TODO: May need to update these to note specific tables, this is UNTESTED as of 5/15
                    int custId = rs.getInt("customerId");
                    String custName = rs.getString("customerName");
                    int addressID = rs.getInt("addressId");
                    int active = rs.getInt("active");
                    String address1 = rs.getString("address");
                    String address2 = rs.getString("address2");
                    int cityId = rs.getInt("cityId");
                    String cityName = rs.getString ("city");
                    int countryId = rs.getInt("countryId");
                    String country = rs.getString("country");

                }
            }catch (SQLException e){
                System.out.println("Error in SQLManager.populateCustomerList() resultSet : "+e.getMessage());
            }
        } catch (SQLException e){
            System.out.println("Error in SQLManager.populateCustomerList() query : "+e.getMessage());
        }
    }

    //Probably don't need this after all, just use a join
    private void parseCustomerAddressInfo(SQLCustomer in){
        String addrQuery = "Select * FROM address where addressId = ?";
        String cityQuery = "Select * FROM city where cityId = ?";
        String countryQuery = "Select * FROM country where cityId = ?";
        try{
            sqlConnection.prepareStatement(addrQuery);

        }catch (SQLException e){
            System.out.println("Error in parseCustomerAddr");
        }
    }

    //TODO: Populate appointment list
    private void populateAppointmentList(){
        String apptQuery = "Select * from appointment";
    }

    public ArrayList<SQLAppointment> getCustomersAppointments(SQLCustomer in){
        String apptQuery = "Select * from appointment";
        int custID = in.getCustomerID();
        try{
            PreparedStatement st = getSQLConnection().prepareStatement(apptQuery);
            st.setInt(1, custID);

            ResultSet rs = st.executeQuery();
            while (rs.next()){
                SQLAppointment appt = new SQLAppointment();
                appt.setApptID(rs.getInt(1));
                appt.setCustomerID(custID);
                appt.setTitle(rs.getString("title"));
                appt.setDescription(rs.getString("description"));
                appt.setLocation(rs.getString("location"));
                appt.setContact(rs.getString("contact"));
                appt.setUrl(rs.getString("url"));
                //TODO: Add another method for localTime, fix this
                appt.setStartTime(rs.getTime("start").toString());
                //TODO: END TIME?
            }
        }catch (SQLException e){
            //TODO
        }



        //TODO: STUB
        return null;
    }

    public LocalDateTime canSchedule(int customerId, LocalDateTime start, LocalDateTime end){
        String scheduleQuery = "Select * from appointment where customerId = ? AND (? > start or ? < end)";
        LocalDateTime dt = null;
        try{
            PreparedStatement st = sqlConnection.prepareStatement(scheduleQuery);
            //I have no idea if this will work, and it will probably need to be changed.
            st.setInt(1, customerId);
            st.setTimestamp(2, new Timestamp(end.toEpochSecond((ZoneOffset) ZoneOffset.systemDefault())));
            st.setTimestamp(3, new Timestamp(start.toEpochSecond((ZoneOffset) ZoneOffset.systemDefault())));
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                System.out.println((rs.getTime("start").toLocalTime()));
                System.out.println(rs.getTime("end").toLocalTime());

                //LocalTime start = LocalTime.parse(startTime.toString().toCharArray())

            }

        }catch (SQLException e){
            System.out.println("SQLException in SQLManager.canSchedule: "+e.getMessage());
        }

        return dt; //TODO STUB
    }

    //NOTE: Appointment scheduling should be doable by lambda expressions using ForEach. Populate a list of appointments for the selected customer,
    //Then use a predicate to ensure that the target start dateTime is not before any currently scheduled end dateTimes, and the target dateTime
    // //is not scheduled for any scheudled starttimes


}
