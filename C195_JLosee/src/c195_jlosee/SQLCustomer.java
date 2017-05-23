package c195_jlosee;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/11/2017.
 */

//TODO: Make this throw exceptions to customer for excessively long variable lengths. Refer to ERD.
    //Note: Can I poll SQL for the lengths?

public class SQLCustomer {

    //Constants, no good way to limit these and
    private static int MAX_NAME_LENGTH = 45, MAX_ADDRESS1_LENGTH=50,
            MAX_ADDRESS2_LENGTH=50, MAX_CITY_LENGTH=50, MAX_COUNTRY_LENGTH=50,
            MAX_POSTALCODE_LENGTH=10, MAX_PHONE_LENGTH=20;

    private SimpleStringProperty customerName = new SimpleStringProperty(),address1=new SimpleStringProperty(),
            address2=new SimpleStringProperty(), city=new SimpleStringProperty(), postalCode=new SimpleStringProperty(), phone=new SimpleStringProperty(),
            country=new SimpleStringProperty();
    private SimpleIntegerProperty customerID = new SimpleIntegerProperty();
    private SimpleIntegerProperty addressID = new SimpleIntegerProperty();
    private SimpleIntegerProperty cityID = new SimpleIntegerProperty();
    private SimpleIntegerProperty countryID = new SimpleIntegerProperty();
    private SimpleIntegerProperty active = new SimpleIntegerProperty();

    public static void GetMaxValues(){
        SQLManager.getSQLConnection();

    }

    /**
     * Default constructor, debug code is present to determine feasibility of setting the max values with the latest from the DB.
     * Result of test: Doable, but may be time inefficient
     */
    public SQLCustomer(){
        /*try {
            int testNameLength = SQLManager.getSQLConnection()
                    .createStatement()
                    .executeQuery("Select * from customer")
                    .getMetaData().
            System.out.println("testing column display size for customerName. Max length: "+testNameLength);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //SQLManager.getInstance().addCustomer(this);

    }

    public SQLCustomer(String customerName, String address1, String address2, String city, String postCode, String phone, String country) throws Exception {

        this.setCustomerName(customerName);
        this.setCountry(country);
        this.setCity(city);
        this.setFullAddress(address1, address2, postCode, phone);
    }

    public boolean setFullAddress(String address1, String address2, String postCode, String phone) throws Exception {
        boolean ret = false;

        this.setAddress1(address1);
        this.setAddress2(address2);
        this.setPostalCode(postCode);
        this.setPhone(phone);

        setAddressID(SQLManager.getInstance().addAddress(address1, address2, postCode, phone, this.getCityID()));
        if (this.getAddressID()>0){
            ret = true;
        }
        return ret;
    }

    public int getActive() {        return active.get();    }
    public SimpleIntegerProperty activeProperty() {        return active;    }
    public void setActive(int active) {        this.active.set(active);    }
//555-888-5431
    //
    //Customer Name getter/setter
    public String getCustomerName() {        return customerName.get();    }
    public SimpleStringProperty customerNameProperty() {        return customerName;    }
    public void setCustomerName(String customerName) throws Exception {
        String varName = "Customer Name";
        if (isStringNullOrEmpty(customerName)) {
            throw new Exception(notNullFieldEmptyMessageFactory(varName));
        }else if (customerName.length() > MAX_NAME_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory(varName));
        }
        this.customerName.set(customerName);
    }

    //
    // Address Line 1 getter/Setter
    public String getAddress1() {
        return address1.get();
    }
    public SimpleStringProperty address1Property() {
        return address1;
    }
    public void setAddress1(String address1) throws Exception {
        String varName = "Address Line 1";

        if (isStringNullOrEmpty(address1)){
            throw new Exception((notNullFieldEmptyMessageFactory(varName)));
        }else if (address1.length() > MAX_ADDRESS1_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory(varName));
        }

        this.address1.set(address1);
    }

    /**
     *
     * @param toCheck
     * @return true if the string is empty, null, or contains only whitespace
     */
    private boolean isStringNullOrEmpty(String toCheck){
        boolean isEmpty = false;
        if (toCheck != null){
            isEmpty = toCheck.trim().isEmpty();
        }
        return isEmpty;
    }

    private String notNullFieldEmptyMessageFactory(String fieldName){
        return fieldName+" cannot be empty or only whitespace. Please enter a value.";
    }

    private String maxLengthExceptionMessageFactory(String varName){
        return varName+" exceeds the maximum length supported by the database and would result in truncation. Please shorten and try again.";
    }

    //
    // Address Line 2 getter/setter
    public String getAddress2() {
        return address2.get();
    }
    public SimpleStringProperty address2Property() {
        return address2;
    }
    public void setAddress2(String address2) throws Exception {

        String varName = "Address Line 2";
        if (address2.length() > MAX_ADDRESS2_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory(varName));
        }
        this.address2.set(address2);
    }

    //
    // City getter/setter
    public String getCity() {
        return city.get();
    }
    public SimpleStringProperty cityProperty() {
        return city;
    }
    public void setCity(String city) throws Exception {

        if (isStringNullOrEmpty(city)){
            throw new Exception(notNullFieldEmptyMessageFactory("City name"));
        } else if(city.length() > MAX_CITY_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory("City name"));
        }
        int cityID = SQLManager.getInstance().addCity(city, this.getCountryID());

        if (cityID>0){
            this.city.set(city);
            this.setCityID(cityID);
        }
    }

    public String getPostalCode() {
        return postalCode.get();
    }
    public SimpleStringProperty postalCodeProperty() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) throws Exception {
        if (isStringNullOrEmpty(postalCode)){
            throw new Exception(notNullFieldEmptyMessageFactory("Postal Code"));
        } else if(postalCode.length() > MAX_POSTALCODE_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory("Postal Code"));
        }
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }
    public SimpleStringProperty phoneProperty() {
        return phone;
    }
    public void setPhone(String phone) throws Exception {
        String phoneRegex = "[xX#0-9\\(\\)\\-*+]{1,20}[^a-wA-wy-zY-Z]";

        if (isStringNullOrEmpty(phone)){
            throw new Exception(notNullFieldEmptyMessageFactory("Phone Number"));
        } else if(phone.length() > MAX_PHONE_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory("Phone Number"));
        }else if (!phone.matches(phoneRegex)){
            throw new Exception(new Exception("Phone number may contain only numbers 0-9, (), -, #, x, + and *\n"+phone));
        }
        this.phone.set(phone);
    }

    public String getCountry() {
        return country.get();
    }
    public SimpleStringProperty countryProperty() {
        return country;
    }
    public void setCountry(String country) throws Exception {

        if (isStringNullOrEmpty(country)){
            throw new Exception(notNullFieldEmptyMessageFactory("Country name"));
        } else if(country.length() > MAX_COUNTRY_LENGTH){
            throw new Exception(maxLengthExceptionMessageFactory("Country name"));
        }

        int countryID = SQLManager.getInstance().addCountry(country);
        if (countryID >0){
            this.country.set(country);
            this.setCountryID(countryID);
        }

    }

    public int getCustomerID() {
        return customerID.get();
    }
    public SimpleIntegerProperty customerIDProperty() {
        return customerID;
    }
    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    public int getAddressID() {
        return addressID.get();
    }
    public SimpleIntegerProperty addressIDProperty() {
        return addressID;
    }
    public void setAddressID(int addressID) {
        this.addressID.set(addressID);
    }

    public int getCityID() {
        return cityID.get();
    }
    public SimpleIntegerProperty cityIDProperty() {
        return cityID;
    }
    public void setCityID(int cityID) {
        this.cityID.set(cityID);
    }

    public int getCountryID() {
        return countryID.get();
    }
    public SimpleIntegerProperty countryIDProperty() {
        return countryID;
    }
    public void setCountryID(int countryID) {
        this.countryID.set(countryID);
    }

    @Override
    public String toString(){
        return this.getCustomerName()+" "+this.getCity()+" "+this.getCountry();
    }

}
