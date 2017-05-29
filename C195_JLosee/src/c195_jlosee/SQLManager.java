package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * $SQLManager
 * Singleton for passing all SQL connection and queries.
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
    private ObservableList<SQLCustomer> customerList;
    private ObservableList <SQLAppointment> activeUserApptList;

    private static SQLUser activeUser = null;

    private SQLManager(){
        customerList = FXCollections.observableList(new ArrayList<SQLCustomer>());
        activeUserApptList = FXCollections.observableList(new ArrayList<SQLAppointment>());
        sqlConnection = getSQLConnection();
    }

    public static SQLManager getInstance(){
        return instance;
    }

    /**
     * Returns the sql connection object if available, or establishes a new connection
     * @return reference to the sqlConnection
     */
    public static Connection getSQLConnection(){

        //Would use a data source instead, but the SQL server seems to be set up for this.
        if (sqlConnection==null){
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException cnfe){
                System.out.println("Class not found exception encountered: " + cnfe.getMessage());
            }

            try {
                //Ensure all columns are set to auto_increment, takes about 500ms on launch, but worth it as this program won't have to find and calculate every addressID;
                sqlConnection = DriverManager.getConnection(url, user, pass);
                Instant timer = Instant.now();
                sqlConnection.createStatement().executeUpdate("Alter Table appointment Modify Column appointmentId int(10) not null auto_increment");
                sqlConnection.createStatement().executeUpdate("Alter Table country Modify Column countryId int(10) not null auto_increment");
                sqlConnection.createStatement().executeUpdate("Alter Table city Modify Column cityId int(10) not null auto_increment");
                sqlConnection.createStatement().executeUpdate("Alter Table city Modify Column cityId int(10) not null auto_increment");
                sqlConnection.createStatement().executeUpdate("Alter Table address Modify Column addressId int(10) not null auto_increment");
                sqlConnection.createStatement().executeUpdate("Alter Table customer Modify Column customerId int(10) not null auto_increment");
                Instant timer2 = Instant.now();
                long nanoTime = timer.toEpochMilli()-timer2.toEpochMilli();
                System.out.println(nanoTime);
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

        //TODO: It would be best to not transmit these UN/PW plaintext, but the requirement isn't there and the DB isn't properly set up (pw length 50, instead of 256+).
        // byte[] passHashed =  hashPassword(pass.toCharArray(), user.to)

        try {
            //use prepare statement to prevent sql injection
            prepStatement = getSQLConnection().prepareStatement(loginSQLString);
            prepStatement.setString(1, user);
            prepStatement.setString(2, pass);
            res = prepStatement.executeQuery();

            //Assumption: password/username matches a single result
            if (res.next()){
                int userID = res.getInt(1);
                String userName = res.getString(2);
                /*Debugging code
                System.out.println("User login successful!");
                System.out.println("UserID: " + userID);
                System.out.println("UserName: " + userName);*/
                activeUser = new SQLUser(userID, userName);

                success = true;
            }

        }catch (SQLException sqlE){
            System.out.println("Error creating statement in : "+ sqlE.getMessage());
            System.out.println(sqlE.getStackTrace());
        }catch (Exception e){
            System.out.println("An generic exception occurred during the prepared statement of login. "+e.getMessage());
        }

        ExecutorService populateCustomers = null;
        try{
            populateCustomers = Executors.newSingleThreadExecutor();
            populateCustomers.submit(()->SQLManager.getInstance().populateCustomerList());
        }finally{
            if (populateCustomers != null) populateCustomers.shutdown();
            //shutdown the thread
        }
        return success;
    }

    public void logout(){
        activeUser = null;
        activeUserApptList = null;
    }

    public boolean addCustomer(SQLCustomer inCustomer){
        boolean addSucceed = false;
        String custExists = "Select * from customer where customerName=? and addressId =?";
        String addCustString = "Insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) " +
                                            "VALUES ( ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstCustomerExists = getSQLConnection().prepareStatement(custExists);
            pstCustomerExists.setString(1, inCustomer.getCustomerName());
            pstCustomerExists.setInt(2, inCustomer.getAddressID());
            ResultSet rs = pstCustomerExists.executeQuery();
            if (rs.next()){
                inCustomer.setCustomerID(rs.getInt("customerId"));
            }else{
                PreparedStatement pstAddCustomer = getSQLConnection().prepareStatement(addCustString);
                //pstAddCustomer.setInt(1, inCustomer.getCustomerID());
                pstAddCustomer.setString(1, inCustomer.getCustomerName());
                pstAddCustomer.setInt(2, inCustomer.getAddressID());
                pstAddCustomer.setInt(3, inCustomer.getActive());//not sure what active is for, but I'll probably just let this be set as a checkbox?
                ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
                pstAddCustomer.setTimestamp(4, Timestamp.from(utcTime.toInstant()));
                pstAddCustomer.setString(5, activeUser.getUserName());
                pstAddCustomer.setString(6, activeUser.getUserName());

                //TODO: Add "active" checkbox to customer form
                pstAddCustomer.executeUpdate();
                //After all is said and done, if no exceptions are encountered, add the customer to the list.
                rs=pstCustomerExists.executeQuery();
                if (rs.next()) {
                    inCustomer.setCustomerID(rs.getInt("customerId"));
                    customerList.add(inCustomer);
                    addSucceed=true;
                }

            }
        } catch(SQLException sqlE){
            System.out.println("Error creating statement in addCustomer: "+ sqlE.getMessage());
            System.out.println(sqlE.getStackTrace());
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
        String addCountry= "Insert into country (country, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?)";
        try (PreparedStatement pstCountryExistQuery = getSQLConnection().prepareStatement(selectCountry)){
            pstCountryExistQuery.setString(1, countryName);
            ResultSet rs = pstCountryExistQuery.executeQuery();
            if (rs.next()){
                countryID = rs.getInt(1);
            }else{
                //find the highest country ID and increment it, because I can't turn on auto-inc in the DB

                PreparedStatement pstAddCountry = getSQLConnection().prepareStatement(addCountry);
                //pstAddCountry.setInt(1, ++countryID);
                pstAddCountry.setString(1, countryName);
                pstAddCountry.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
                pstAddCountry.setString(3, activeUser.getUserName());
                pstAddCountry.setString(4, activeUser.getUserName());
                int i = pstAddCountry.executeUpdate();
                rs = pstCountryExistQuery.executeQuery();
                if(rs.next()){
                    countryID=rs.getInt(1);
                }

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
        String addCity = "INSERT INTO city (city, countryID, createDate, createdBy, lastUpdateBy) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement pstCityExists = sqlConnection.prepareStatement(selectCity)){
            pstCityExists.setString(1,cityName);
            pstCityExists.setInt(2, countryID);
            ResultSet cityPresent = pstCityExists.executeQuery();
            if (cityPresent.next()){
                cityID=cityPresent.getInt(1);
            }
            else{
                PreparedStatement pstAddCity = sqlConnection.prepareStatement(addCity);
                //pstAddCity.setInt(1, ++cityID);
                pstAddCity.setString(1, cityName);
                pstAddCity.setInt(2, countryID);
                pstAddCity.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
                pstAddCity.setString(4, activeUser.getUserName());
                pstAddCity.setString(5, activeUser.getUserName());
                pstAddCity.executeUpdate();

                cityPresent=pstCityExists.executeQuery();
                if (cityPresent.next()){
                    cityID = cityPresent.getInt(1);
                }

            }
        }catch (SQLException e){
            System.out.println("SQLException in addCity: "+e.getMessage());
        }

        return cityID;
    }

    /**
     * hashPassword
     * Unused, but would be good to use if the DB were better set up
     * Source: https://www.owasp.org/index.php/Hashing_Java
     * @param password
     * @param salt
     * @param iterations
     * @param keyLength
     * @return
     */

    public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Adds the address
     * @param addressLine1
     * @param addressLine2
     * @param postCode
     * @param phone
     * @param cityID
     * @return addressId
     */
    public int addAddress(String addressLine1, String addressLine2, String postCode, String phone, int cityID){
        int addressID = -1;

        String selectAddress = "Select * from address where address=? and address2=? and cityID=? and postalCode=? and phone=?";
        String addAddress="INSERT INTO address (address, address2, cityID, postalCode, phone, createDate, createdBy, lastUpdateBy)"+
                " VALUES (? , ?, ?, ?, ?, ?, ?, ?)";


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
                PreparedStatement pstAddAddress = sqlConnection.prepareStatement(addAddress);
                //pstAddAddress.setInt(1, ++addressID);
                pstAddAddress.setString(1, addressLine1);
                pstAddAddress.setString(2, addressLine2);
                pstAddAddress.setInt(3, cityID);
                pstAddAddress.setString(4, postCode);
                pstAddAddress.setString(5, phone);
                pstAddAddress.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
                pstAddAddress.setString(7, activeUser.getUserName());
                pstAddAddress.setString(8, activeUser.getUserName());
                pstAddAddress.executeUpdate();

                rs=pstAddrExists.executeQuery();
                if (rs.next()){
                    addressID=rs.getInt(1);
                }
            }
        }catch (SQLException e){
            System.out.println("SQLException in addAddres: "+e.getMessage());
        }

        return addressID;
    }

    public ObservableList<SQLCustomer> getCustomerList(){
        return this.customerList;
    }

    public ObservableList<SQLAppointment> getAppointmentList(){        return activeUserApptList;    }

    /**
     * Populates the customer list for the table view and other uses
     */
    private void populateCustomerList(){
        String allCustQuery = "SELECT * FROM customer JOIN address USING (addressId) JOIN city USING (cityId) JOIN country USING(countryId)";
        SQLCustomer current;
        try{
            ResultSet rs = this.sqlConnection.createStatement().executeQuery(allCustQuery);
            while(rs.next()){
                //Get all the customer information
                current=new SQLCustomer();
                current.setCustomerID(rs.getInt("customerId"));
                current.setCustomerName(rs.getString("customerName"));
                current.setAddressID(rs.getInt("addressId"));
                current.setAddress1(rs.getString("address"));
                current.setAddress2(rs.getString("address2"));
                current.setCityID(rs.getInt("cityId"));
                current.setCity(rs.getString ("city"));
                current.setCountryID(rs.getInt("countryId"));
                current.setCountry(rs.getString("country"));
                current.setPhone(rs.getString("phone"));
                current.setPostalCode(rs.getString("postalCode"));
                current.setActive(rs.getInt("active"));
                //Get the customer's apointments
                current.setAppointmentList(getCustomersAppointments(current));

                //Add the custmoer to the lists
                customerList.add(current);

            }
        }catch (SQLException e){
            System.out.println("Error in SQLManager.populateCustomerList() resultSet : "+e.getMessage());
        } catch (Exception e) {
            System.out.println("Change in the database parameters parsing customer records: "+e.getMessage());
            e.printStackTrace();
        }

        //customerList.stream()
        //        .forEach(System.out::println);
    }

    //TODO: Populate appointment list
    private void populateAppointmentList(){
        String apptQuery = "Select * from appointment";
    }

    public ArrayList<SQLAppointment> getCustomersAppointments(SQLCustomer in){
        ArrayList<SQLAppointment> customerAppointments= new ArrayList<>();
        String apptQuery = "Select * from appointment";

        int custID = in.getCustomerID();
        try{
            PreparedStatement st = getSQLConnection().prepareStatement(apptQuery);
            st.setInt(1, custID);

            ResultSet rs = st.executeQuery();
            while (rs.next()){
                SQLAppointment appt = new SQLAppointment();
                appt.setApptID(rs.getInt("appointmentId"));
                appt.setCustomerID(custID);
                appt.setTitle(rs.getString("title"));
                appt.setDescription(rs.getString("description"));
                appt.setLocation(rs.getString("location"));
                appt.setContact(rs.getString("contact"));
                appt.setUrl(rs.getString("url"));
                appt.setCreatedBy(rs.getString("createdBy"));
                //createdDate may want to be set to DateTime rather than local date time
                appt.setCreatedDate(rs.getTimestamp("createdDate").toLocalDateTime());
                ZonedDateTime startLocal = rs.getTimestamp("start").toLocalDateTime().atZone(ZoneId.systemDefault());
                ZonedDateTime endLocal = rs.getTimestamp("end").toLocalDateTime().atZone(ZoneId.systemDefault());
                try{
                    appt.setStartDateTime(startLocal);
                    appt.setEndDateTime(endLocal);
                }catch (Exception e){
                    //Discard this because we're pulling the information from the database so we don't really care
                }
            }
        }catch (SQLException e){
            //TODO
        }
        //TODO: STUB
        return customerAppointments;
    }

    /**
     *
     * @return null if no appts in the next 15 minutes are found, string with Appt Details otherwise
     */
    public String checkForApptAtLogin(){
        String appointmentDetails = null;
        SQLAppointment appt = null;
        //TODO DOES THIS WORK?
        final int REMINDER_TIME_LIMIT = 15;

        ZonedDateTime zdtNow = ZonedDateTime.now(ZoneOffset.UTC);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
        Timestamp nowPlus15 = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC).plus(15, ChronoUnit.MINUTES));

        try{
            String usersAppts = "Select * from appointment where createdBy= ? and start BETWEEN ? AND ?";
            PreparedStatement userApptSt = sqlConnection.prepareStatement(usersAppts);
            userApptSt.setString(1, activeUser.getUserName());
            userApptSt.setTimestamp(2, now);
            userApptSt.setTimestamp(3, nowPlus15);

            ResultSet rs = userApptSt.executeQuery();

            if (rs.next()){
                LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                String title = rs.getString("title");
                String location = rs.getString("location");
                String description = rs.getString("description");
                String contact = rs.getString("contact");
                String url = rs.getString("url");
                int apptCustId = rs.getInt("customerId");
                int apptId = rs.getInt("appointmentId");

                SQLCustomer customerOfAppt = customerList.filtered(a->a.getCustomerID()==apptCustId).get(0);

                appointmentDetails="Appointment Alert:\n" +title+"\n" +description+"\nCustomer: "
                        +customerOfAppt.getCustomerName()+"\n"+start+" - "+end+
                        " \nLocation: "+location;
            }
        }catch (SQLException e){
            System.out.println("Exception encountered in SQLManager.checkForApptAtLogin: "+ e.getMessage());
        }
        return appointmentDetails;
    }

    public boolean addAppointment(SQLAppointment appt){
        //String addAddress="INSERT INTO address (address, address2, cityID, postalCode, phone, createDate, createdBy, lastUpdateBy)"+
        //" VALUES (? , ?, ?, ?, ?, ?, ?, ?)";
        boolean success = false;
        String appointmentQuery = "INSERT INTO appointment (customerId, title, description, location, contact, URL, start, end, createDate, createdBy, lastUpdateBy)"+
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        ZonedDateTime start = appt.getStartDateTime();
        ZonedDateTime end = appt.getEndDateTime();

        Timestamp test = Timestamp.valueOf(start.toLocalDateTime());
        
        try{
            PreparedStatement addAppt = sqlConnection.prepareStatement(appointmentQuery);
            addAppt.setInt(1, appt.getCustomerID());
            addAppt.setString(2, appt.getTitle());
            addAppt.setString(3, appt.getDescription());
            addAppt.setString(4, appt.getLocation());
            addAppt.setString(5, appt.getContact());
            addAppt.setString(6, appt.getUrl());
            //ZonedDateTime zdtAddApptTest = appt.getStartDateTime().atZone(ZoneOffset.UTC);
            addAppt.setTimestamp(7, Timestamp.valueOf(start.toLocalDateTime()));
            addAppt.setTimestamp(8, Timestamp.valueOf(end.toLocalDateTime()));
            addAppt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            addAppt.setString(10, activeUser.getUserName());
            addAppt.setString(11, activeUser.getUserName());

            //addAppt.executeUpdate();
            activeUserApptList.add(appt);
            success = true;
        }catch(SQLException sqle){
            System.out.println("SQL Error adding appointment: "+ sqle.getMessage());
        }
        return success;
    }
}//END OF CLASS
