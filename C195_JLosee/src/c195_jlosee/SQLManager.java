package c195_jlosee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Alert;

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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * $SQLManager
 * Singleton for passing all SQL connection and queries.
 * Created by Joseph Losee on 5/8/2017.
 */
public class SQLManager {

    //TODO: Add Prepared Statements for SQL Queries up here along with strings for grouped references
    //SQL Connection information in one convenient package!
    private static String driver   = "com.mysql.jdbc.Driver";
    private static String schema = "U04bqK";
    private static String url = "jdbc:mysql://52.206.157.109/"+schema;
    private static String user = "U04bqK", pass="53688195806";
    private static Connection sqlConnection = null;
    private static final SQLManager instance = new SQLManager();
    private ObservableList<SQLCustomer> customerList;
    private ObservableList <SQLAppointment> activeUserApptList;
    private PreparedStatement customerAppointment;

    private Map <String, PreparedStatement> mapOfStatements = new HashMap<>();
    private Map <Integer, SQLCustomer> customerMap = new HashMap<>();
    private Map <Integer, SQLUser> userMap = new HashMap<>();

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
    public Connection getSQLConnection(){

        //Would use a data source instead, but the SQL server seems to be set up for this.
        if (sqlConnection==null){
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException cnfe){
                System.out.println("Class not found exception encountered: " + cnfe.getMessage());
            }

            try {
                //
                //Ensure all columns are set to auto_increment, takes about 500ms on launch, but worth it as this program won't have to find and calculate every addressID;
                sqlConnection = DriverManager.getConnection(url, user, pass);
                Instant timer = Instant.now();

                ResultSet rs = sqlConnection.createStatement().executeQuery("Select userId from user");

                //Programmatically determine if the database has already set the primary key columns to autoIncrement, saves about 500ms
                boolean isAutoIncrementEnabled = false;
                if (rs.next()){
                    if (rs.getMetaData().isAutoIncrement(1)){
                        isAutoIncrementEnabled=true;
                    }
                }

                //If the DB got reset:
                if (!isAutoIncrementEnabled){
                    sqlConnection.createStatement().executeUpdate("Alter Table appointment Modify Column appointmentId int(10) not null auto_increment");
                    sqlConnection.createStatement().executeUpdate("Alter Table country Modify Column countryId int(10) not null auto_increment");
                    sqlConnection.createStatement().executeUpdate("Alter Table city Modify Column cityId int(10) not null auto_increment");
                    sqlConnection.createStatement().executeUpdate("Alter Table city Modify Column cityId int(10) not null auto_increment");
                    sqlConnection.createStatement().executeUpdate("Alter Table address Modify Column addressId int(10) not null auto_increment");
                    sqlConnection.createStatement().executeUpdate("Alter Table customer Modify Column customerId int(10) not null auto_increment");
                }

                String custAppt = "Select * from appointment where customerId = ?";
                customerAppointment = sqlConnection.prepareStatement(custAppt);

            }catch(SQLException sqlE){
                new Alert(Alert.AlertType.ERROR,"SQL Exception Encountered in SQLManager.getSQLConnection: "+sqlE.getMessage()).showAndWait();
            }
        }
        return sqlConnection;
    }

    /**
     * Checks the sql database for the user information entered
     * @param user - user name
     * @param pass - user password
     * @return - if the login was successful
     */
    public boolean login(String user, String pass){
        //Sanitize
        PreparedStatement prepStatement;
        ResultSet res;
        boolean success = false;

        //LOGIN QUERY
        String loginSQLString = "select * from user where userName = ? and password = ?";

        //TODO: It would be best to not transmit these UN/PW, but the requirement isn't there and the DB isn't properly set up (pw length 50, instead of 256+).
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
                activeUser = new SQLUser(userID, userName);

                long start = System.currentTimeMillis();
                populateCustomerMap();
                System.out.println("Populating Customer List took: "+(System.currentTimeMillis()-start));
                start=System.currentTimeMillis();
                populateUserMap();
                System.out.println("Populating User Map took: " + (System.currentTimeMillis()-start));
                //This takes 3 seconds, can I speed it up?
                success = true;
            }

        }catch (SQLException sqlE){
            System.out.println("Error creating statement in : "+ sqlE.getMessage());
            System.out.println(sqlE.getStackTrace());
        }/*catch (Exception e){
            System.out.println("An generic exception occurred during the prepared statement of login. "+e.getMessage());
        }*/

        return success;
    }

    public void populateUserMap(){
        String queryUserIDAndName = "Select userId, username from user";
        try {
            PreparedStatement users = sqlConnection.prepareStatement(queryUserIDAndName);
            ResultSet rs = users.executeQuery();
            while (rs.next()){
                int id = rs.getInt("userId");
                String name = rs.getString("username");
                userMap.put(id, new SQLUser(id, name));
            }
        } catch (SQLException e) {
            System.out.println("Exception while populating the user map: "+e.getMessage());
        }
    }

    public void logout(){
        activeUser = null;
        activeUserApptList = null;
    }

    public Map<Integer, SQLUser> getUserMap(){
        return userMap;
    }

    public List<SQLUser> getUserList(){
        return new ArrayList<>(userMap.values());
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
                pstAddCustomer.setString(1, inCustomer.getCustomerName());
                pstAddCustomer.setInt(2, inCustomer.getAddressID());
                pstAddCustomer.setInt(3, inCustomer.getActive());

                ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
                pstAddCustomer.setTimestamp(4, Timestamp.from(utcTime.toInstant()));
                pstAddCustomer.setString(5, activeUser.getUserName());
                pstAddCustomer.setString(6, activeUser.getUserName());

                pstAddCustomer.executeUpdate();
                //After all is said and done, if no exceptions are encountered, add the customer to the list.
                rs=pstCustomerExists.executeQuery();
                if (rs.next()) {
                    inCustomer.setCustomerID(rs.getInt("customerId"));
                    customerList.add(inCustomer);
                    addSucceed=true;
                }
                rs.close();
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
                pstAddCountry.setString(1, countryName);
                pstAddCountry.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
                pstAddCountry.setString(3, activeUser.getUserName());
                pstAddCountry.setString(4, activeUser.getUserName());

                int i = pstAddCountry.executeUpdate();
                rs = pstCountryExistQuery.executeQuery();
                if(rs.next()){
                    countryID=rs.getInt(1);
                }
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

                pstAddCity.setString(1, cityName);
                pstAddCity.setInt(2, countryID);
                pstAddCity.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
                pstAddCity.setString(4, activeUser.getUserName());
                pstAddCity.setString(5, activeUser.getUserName());

                pstAddCity.executeUpdate();

                cityPresent=pstCityExists.executeQuery();
                //Assign the newly generated ID
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
            return key.getEncoded( );
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
        List<SQLCustomer> customerList = new ArrayList<SQLCustomer>(customerMap.values());//new ArrayList<>(customerMap.values().toArray());
        return FXCollections.observableList(customerList);
    }

    public Map<Integer, SQLCustomer> getCustomerMap(){
        return customerMap;
    }

    public ObservableList<SQLAppointment> getActiveUserAppointmentList(){        return activeUserApptList;    }

    public void populateUserAppointmentList(SQLUser user){
        String appointmentQuery = "Select * from appointment where createdBy=?";
        try {
            List<SQLAppointment> usersAppointments = new ArrayList<>();
            PreparedStatement pstPopulateUserAppointments = sqlConnection.prepareStatement(appointmentQuery);
            pstPopulateUserAppointments.setString(1, user.getUserName());

            ResultSet rs = pstPopulateUserAppointments.executeQuery();

            while (rs.next()){
                usersAppointments.add(new SQLAppointment(rs));
            }

            user.setApptList(FXCollections.observableList(usersAppointments));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "An exception occurred when populating the appointment list for user " + user.getUserName() + e.getMessage()).showAndWait();
        }
        user.setLastUpdatedApptsFromSQL();
    }

    private void populateCustomerMap(){
        String allCustQuery = "SELECT * FROM customer JOIN address USING (addressId) JOIN city USING (cityId) JOIN country USING(countryId)";
        SQLCustomer current;
        try{
            ResultSet rs = sqlConnection.createStatement().executeQuery(allCustQuery);
            while(rs.next()){
                //Get all the customer information
                current=new SQLCustomer();
                int customerID = rs.getInt("customerId");
                current.setCustomerID(customerID);
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
                long test = System.currentTimeMillis();
                //System.out.println("Appt list for "+current.getCustomerName()+" took: "+(System.currentTimeMillis()-test));
                //Add the customer to the lists
                customerMap.put(customerID, current);

            }
        }catch (SQLException e){
            System.out.println("Error in SQLManager.populateCustomerList() resultSet : "+e.getMessage());
        } catch (Exception e) {
            System.out.println("Change in the database parameters parsing customer records: "+e.getMessage());
            e.printStackTrace();
        }

        retrieveAllCustomerAppointments();
    }

    /**
     * Populates the customer list for the table view and other uses
     */
    private void populateCustomerList(){
        String allCustQuery = "SELECT * FROM customer JOIN address USING (addressId) JOIN city USING (cityId) JOIN country USING(countryId)";
        SQLCustomer current;
        try{
            ResultSet rs = sqlConnection.createStatement().executeQuery(allCustQuery);
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
                long test = System.currentTimeMillis();

                current.setAppointmentList((getCustomersAppointments(current)));
                System.out.println("Appt list for "+current.getCustomerName()+" took: "+(System.currentTimeMillis()-test));
                //Add the customer to the lists
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

    public void updateCustomer(SQLCustomer toUpdate){
        String strUdateQuery = "UPDATE customer SET customerName=?, addressId=?, active=?, lastUpdateBy=? WHERE customerId=?";

        try {
            PreparedStatement psUpdate=sqlConnection.prepareStatement(strUdateQuery);
            psUpdate.setString(1, toUpdate.getCustomerName());
            psUpdate.setInt(2, toUpdate.getAddressID());
            psUpdate.setInt(3, toUpdate.getActive());
            psUpdate.setString(4, activeUser.getUserName());
            psUpdate.setInt(5, toUpdate.getCustomerID());

            int i = psUpdate.executeUpdate();

        }catch (SQLException sqle){
            System.out.println("SQLException in sqlmanager.updateCustomer: " +sqle.getMessage());
        }
    }

    public void retrieveAllCustomerAppointments(){
        String apptQueryString = "Select * from appointment";

        long queryTimer = System.currentTimeMillis();
        try{
            PreparedStatement apptQuery = sqlConnection.prepareStatement(apptQueryString);

            ResultSet rs = apptQuery.executeQuery();
            System.out.println("Execute Query for  retrieveAllcustomerAppointments took " + (System.currentTimeMillis()-queryTimer)+"ms");
            int apptCount = 0;
            queryTimer = System.currentTimeMillis();

            while (rs.next()){
                //In case the appointment somehow has a null customer:
                int customerId = rs.getInt("customerId");
                SQLCustomer apptCustomer = customerMap.get(customerId);

                if (apptCustomer==null) {
                    apptCustomer = new SQLCustomer(customerId);
                    try{
                        apptCustomer.setCustomerName("PlaceholderCustomer" + customerId);
                        customerMap.put(customerId, apptCustomer);
                    }catch(Exception e){
                        assert true: "PlaceholderCustomer Name "+apptCustomer.getCustomerName() +"caused an exception. This should never be reached.";
                    }
                }
                long timer = System.currentTimeMillis();
                    SQLAppointment appt = new SQLAppointment();
                    appt.setApptID(rs.getInt("appointmentId"));
                    appt.setCustomerRef(apptCustomer);
                    appt.setTitle(rs.getString("title"));
                    appt.setDescription(rs.getString("description"));
                    appt.setLocationProperty(rs.getString("location"));
                    appt.setContact(rs.getString("contact"));
                    appt.setUrl(rs.getString("url"));
                    appt.setCreatedBy(rs.getString("createdBy"));
                    appt.setCreatedDate(rs.getTimestamp("createdate").toLocalDateTime());
                    ZonedDateTime startLocal = rs.getTimestamp("start").toInstant().atZone(ZoneId.systemDefault());
                    ZonedDateTime endLocal = rs.getTimestamp("end").toInstant().atZone(ZoneId.systemDefault());

                    try{
                        LocalTime endHolder = appt.getBusinessEnd();
                        LocalTime startHolder = appt.getBusinessStart();
                        appt.setBusinessStart(LocalTime.of(0,0));
                        appt.setBusinessEnd(LocalTime.of(23,59));
                        appt.setStartDateTime(startLocal);
                        appt.setEndDateTime(endLocal);
                        appt.setBusinessStart(startHolder);
                        appt.setBusinessEnd(endHolder);
                        //appt.setCustomerRef(in);
                    }catch (Exception e){
                        System.out.println("This is a test of the retrieveAllAppointments method, this should not be reached");//Discard this because we're pulling the information from the database so we don't really care
                    }

                    if (appt.getCreatedBy().equalsIgnoreCase(activeUser.getUserName())){
                        try {
                            activeUser.addAppointment(appt);
                        } catch (ConflictingAppointmentException cae){
                            assert true;
                        }
                    }

                    apptCustomer.getCustomerAppointments().add(appt);
                }


            }catch (SQLException e){
            System.out.println("SQLException occurred in SQLManager.getCustomersAppointments: "+e.getMessage());
        }
        System.out.println("Parsing the appointment result set took: " +(System.currentTimeMillis()-queryTimer));
    }

    public ArrayList<SQLAppointment> getCustomersAppointments(SQLCustomer in){
        ArrayList<SQLAppointment> customerAppointmentsList= new ArrayList<>();
        String apptQuery = "Select * from appointment where customerId=?";

        int custID = in.getCustomerID();
        try{

            this.customerAppointment.setInt(1, custID);
            long queryTimer = System.currentTimeMillis();
            ResultSet rs = this.customerAppointment.executeQuery();
            System.out.println("Execute Query for "+in.getCustomerName()+" took " + (System.currentTimeMillis()-queryTimer)+"ms");
            int apptCount = 0;
            while (rs.next()){
                //DEBUG testing:
                apptCount++;
                long timer = System.currentTimeMillis();

                SQLAppointment appt = new SQLAppointment();
                appt.setApptID(rs.getInt("appointmentId"));
                appt.setCustomerRef(in);
                appt.setTitle(rs.getString("title"));
                appt.setDescription(rs.getString("description"));
                appt.setLocationProperty(rs.getString("location"));
                appt.setContact(rs.getString("contact"));
                appt.setUrl(rs.getString("url"));
                appt.setCreatedBy(rs.getString("createdBy"));
                appt.setCreatedDate(rs.getTimestamp("createdate").toLocalDateTime());
                ZonedDateTime startLocal = rs.getTimestamp("start").toInstant().atZone(ZoneId.systemDefault());
                ZonedDateTime endLocal = rs.getTimestamp("end").toInstant().atZone(ZoneId.systemDefault());

                try{
                    LocalTime endHolder = appt.getBusinessEnd();
                    LocalTime startHolder = appt.getBusinessStart();
                    appt.setBusinessStart(LocalTime.of(0,0));
                    appt.setBusinessEnd(LocalTime.of(23,59));
                    appt.setStartDateTime(startLocal);
                    appt.setEndDateTime(endLocal);
                    appt.setBusinessStart(startHolder);
                    appt.setBusinessEnd(endHolder);
                    //appt.setCustomerRef(in);
                }catch (Exception e){
                    //Discard this because we're pulling the information from the database so we don't really care
                }
                customerAppointmentsList.add(appt);
                long timer2 = System.currentTimeMillis();
                System.out.print(in.getCustomerName() + "Appt #"+apptCount+" took "+(timer2-timer));
                if (appt.getCreatedBy().equalsIgnoreCase(activeUser.getUserName())){
                    try {
                        activeUser.addAppointment(appt);
                    } catch (ConflictingAppointmentException cae){
                        assert true;
                        //System.out.println("A conflicting user appointment was generated on loading from the database. "+cae.getMessage());
                    }
                }
                System.out.print(" adding to user took " + (System.currentTimeMillis()-timer2)+"\n");

            }
        }catch (SQLException e){
            System.out.println("SQLException occurred in SQLManager.getCustomersAppointments: "+e.getMessage());
        }

        return customerAppointmentsList;
    }

    /**
     *
     * @return null if no appts in the next 15 minutes are found, string with Appt Details otherwise
     */
    public String checkForApptAtLogin(){
        String appointmentDetails = null;

        final int REMINDER_TIME_LIMIT = 15;


        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp nowPlus15 = Timestamp.valueOf(LocalDateTime.now().plus(15, ChronoUnit.MINUTES));

        try{
            String usersAppts = "Select * from appointment where createdBy= ? and start BETWEEN ? AND ?";
            PreparedStatement userApptSt = sqlConnection.prepareStatement(usersAppts);
            userApptSt.setString(1, activeUser.getUserName());
            userApptSt.setTimestamp(2, now);
            userApptSt.setTimestamp(3, nowPlus15);

            ResultSet rs = userApptSt.executeQuery();

            if (rs.next()){
                Timestamp tsApptStart = rs.getTimestamp("start");
                Timestamp tsApptEnd = rs.getTimestamp("end");

                ZoneId test = ZoneId.systemDefault();
                //////IMPORTANT THING
                ZonedDateTime zdtStart = ZonedDateTime.ofInstant(tsApptStart.toInstant(), ZoneId.systemDefault());
                ZonedDateTime zdtEnd = ZonedDateTime.ofInstant(tsApptEnd.toInstant(), ZoneId.systemDefault());

                String title = rs.getString("title");
                String location = rs.getString("location");
                String description = rs.getString("description");
                String contact = rs.getString("contact");
                String url = rs.getString("url");
                int apptCustId = rs.getInt("customerId");
                int apptId = rs.getInt("appointmentId");

                SQLCustomer customerOfAppt = customerList.filtered(a->a.getCustomerID()==apptCustId).get(0);

                appointmentDetails="Appointment Alert:\n" +title+"\n" +description+"\nCustomer: "
                        +customerOfAppt.getCustomerName()+"\n"+zdtStart.toLocalTime()+" - "+ zdtEnd.toLocalTime()+
                        " \nLocation: "+location;
            }
        }catch (SQLException e){
            System.out.println("Exception encountered in SQLManager.checkForApptAtLogin: "+ e.getMessage());
        }
        return appointmentDetails;
    }

    /**
     * Adds an appointment to the database.
     * @param appt
     * @return
     */
    public boolean addAppointment(SQLAppointment appt){

        boolean success = false;
        String appointmentQuery = "INSERT INTO appointment (customerId, title, description, location, contact, URL, start, end, createDate, createdBy, lastUpdateBy)"+
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Timestamp tsStart = Timestamp.from(appt.getStartDateTime().toInstant());
        Timestamp tsEnd = Timestamp.from(appt.getEndDateTime().toInstant());

        try{
            PreparedStatement addAppt = sqlConnection.prepareStatement(appointmentQuery);
            addAppt.setInt(1, appt.getCustomerID());
            addAppt.setString(2, appt.getTitle());
            addAppt.setString(3, appt.getDescription());
            addAppt.setString(4, appt.getLocationProperty());
            addAppt.setString(5, appt.getContact());
            addAppt.setString(6, appt.getUrl());
            addAppt.setTimestamp(7, tsStart);
            addAppt.setTimestamp(8, tsEnd);
            addAppt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            addAppt.setString(10, activeUser.getUserName());
            addAppt.setString(11, activeUser.getUserName());

            addAppt.executeUpdate();

            try {
                String apptId = "Select appointmentId from appointment where customerId=? and start=?";
                PreparedStatement st = sqlConnection.prepareStatement(apptId);
                st.setInt(1, appt.getCustomerID());
                st.setTimestamp(2,tsStart);
                ResultSet rs = st.executeQuery();
                if (rs.next()){
                    int id = rs.getInt(1);
                    appt.setApptID(id);
                }
            }catch (SQLException e){
                System.out.println("SQLException in SQLManager.addAppointment while attempting to retrieve the appointmentID");
            }

            //activeUser.addAppointment(appt);
            success = true;

        }catch(SQLException sqle){
            System.out.println("SQL Error adding appointment: "+ sqle.getMessage());
        }
        return success;
    }

    public boolean updateAppointment(SQLAppointment updateAppt){
        //TODO: Finish this and make sure its workable.
        boolean success = false;

        String queryUpdate = "Update appointment SET customerId=?, start=?, end=?, title=?, description=?, location=?, contact=?, url=?, lastUpdateBy=? WHERE appointmentId=?";

        Timestamp tsStart = Timestamp.from(updateAppt.getStartDateTime().toInstant());
        Timestamp tsEnd = Timestamp.from(updateAppt.getEndDateTime().toInstant());

        try {
            PreparedStatement pstUpdateAppt = sqlConnection.prepareStatement(queryUpdate);
            pstUpdateAppt.setInt(1, updateAppt.getCustomerID());
            pstUpdateAppt.setTimestamp(2, tsStart);
            pstUpdateAppt.setTimestamp(3, tsEnd);
            pstUpdateAppt.setString(4, updateAppt.getTitle());
            pstUpdateAppt.setString(5, updateAppt.getDescription());
            pstUpdateAppt.setString(6, updateAppt.getLocationProperty());
            pstUpdateAppt.setString(7, updateAppt.getContact());
            pstUpdateAppt.setString(8, updateAppt.getUrl());
            pstUpdateAppt.setString(9, activeUser.getUserName());
            pstUpdateAppt.setInt(10, updateAppt.getApptID());

            pstUpdateAppt.executeUpdate();
            success = true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "An exception occurred in SQLManager.updateAppointment(): " +e.getMessage()).showAndWait();
        }
        return success;
    }

    public SQLUser getActiveUser(){
        return activeUser;
    }
}//END OF CLASS
