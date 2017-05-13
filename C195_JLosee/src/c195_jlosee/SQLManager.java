package c195_jlosee;

import java.sql.*;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/8/2017.
 */
public class SQLManager {

    //SQL Connection information in one convenient package!
    private static String driver   = "com.mysql.jdbc.Driver"; ;
    private static String schema = "U04bqK";
    private static String url = "jdbc:mysql://52.206.157.109/"+schema;
    private static String user = "U04bqK", pass="53688195806";
    private static Connection sqlConnection = null;

    private static SQLUser activeUser = null;

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

    public static boolean login(String user, String pass){
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

    public static void logout(){
        activeUser = null;

        //TODO: Call View Manager and close all open windows, then show login window.
    }

    public static boolean addCustomer(SQLCustomer inCustomer){
        boolean addSucceed = false;

        String addCustString = "Insert into customer (customerID, customerName, addressId, active, createDate, createdBy) " +
                                            "VALUES (?, ?, ?, ?, NOW(), ?)";
        try (PreparedStatement prepStatement = getSQLConnection().prepareStatement(addCustString)){
            prepStatement.setInt(1, inCustomer.getCustomerID());
            prepStatement.setString(2, inCustomer.getCustomerName());
            prepStatement.setInt(3, inCustomer.getAddressID());
            prepStatement.setInt(4, inCustomer.getActive());//not sure what active is for, but I'll probably just let this be set as a checkbox?
            prepStatement.setString(5, activeUser.getUserName());


            //TODO: Add "active" checkbox to customer form
            prepStatement.executeQuery();
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
    private static int addCountry(String countryName){
        int countryID = -1;
        String selectCountry = "Select * from country where country=?";
        String addCountry= "Insert into country (countryId, country, createDate, createdBy) values (?, ?, NOW(), ?)";
        try (PreparedStatement pstCountryExistQuery = getSQLConnection().prepareStatement(selectCountry)){
            pstCountryExistQuery.setString(1, countryName);
            ResultSet rs = pstCountryExistQuery.executeQuery();
            if (rs.next()){
                countryID = rs.getInt(1);
            }else{
                //find the highest country ID and increment it, because I can't turn on auto-inc in the DB
                rs = getSQLConnection().createStatement().executeQuery("Select Max(countryId) from Country");
                if (rs.next()){
                    countryID=rs.getInt(1);
                }

                //
                PreparedStatement pstAddCountry = getSQLConnection().prepareStatement(addCountry);
                pstAddCountry.setInt(1, ++countryID);
                pstAddCountry.setString(2, countryName);
                pstAddCountry.setString(3, activeUser.getUserName());
                pstAddCountry.executeQuery();
            }

        }catch (SQLException e){
            //Handle the sql exception
        }

        return countryID;
    }

    private static int addCity(String cityName, int countryID){
        int cityID = -1;
        String selectCity = "Select * from city where city=? and countryID=?";
        String addCity = "INSERT INTO city (cityID, city, countryID, createDate, createdBy VALUES (?, ?, ?, NOW(),"+activeUser.getUserName()+" )";

        try(PreparedStatement pstCityExists = sqlConnection.prepareStatement(selectCity)){
            pstCityExists.setString(1,cityName);
            pstCityExists.setInt(2, countryID);
            ResultSet cityPresent = pstCityExists.executeQuery();
            if (cityPresent.next()){
                cityID=cityPresent.getInt(1);
            }
            else{
                ResultSet nextID = getSQLConnection().createStatement().executeQuery("Select MAX(cityID) from City");
                if (nextID.next()){
                    cityID = nextID.getInt(1);
                }

                PreparedStatement pstAddCity = sqlConnection.prepareStatement(addCity);
                pstAddCity.setInt(1, ++cityID);
                pstAddCity.setString(2, cityName);
                pstAddCity.setInt(3, countryID);
                pstAddCity.executeQuery();
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return cityID;
    }

    private static int addAddress(String addressLine1, String addressLine2, String postCode, String phone, int cityID){
        int addressID = -1;

        String selectAddress = "Select * from address where address=? and address2=? and cityID=? and postCode=? and phone=?";
        String addAddress="INSERT INTO address (addressID, address, address2, cityID, postCode, phone, createDate, createdBy)"+
                "VALUES (?, ? , ?, ?, ?, ?, NOW(), "+activeUser.getUserName()+")";

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
            }
        }catch (SQLException e){
            //TODO: Handle
        }

        return addressID;
    }

}
