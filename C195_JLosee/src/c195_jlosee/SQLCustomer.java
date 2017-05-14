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
    private final int MAX_NAME_LENGTH = 45, MAX_ADDRESS1_LENGTH=50,
            MAX_ADDRESS2_LENGTH=50, MAX_CITY_LENGTH=50, MAX_COUNTRY_LENGTH=50,
            MAX_POSTALCODE_LENGTH=10, MAX_PHONE_LENGTH=20;

    SimpleStringProperty customerName, address1, address2, city, postalCode, phone, country;
    SimpleIntegerProperty customerID;
    SimpleIntegerProperty addressID;
    SimpleIntegerProperty cityID;
    SimpleIntegerProperty countryID;
    SimpleIntegerProperty active;

    public SQLCustomer(){
        this("Doug Wasserman", "123 Boxberry Lane", null, "Greenwich", "91234", "555 555 5555", "United States");
    }

    public SQLCustomer(String customerName, String address1, String address2, String city, String postCode, String phone, String country){
        this.setCustomerName(customerName);
        this.setCountry(country);
        this.setCity(city);
        this.setFullAddress(address1, address2, postCode, phone);
    }

    public boolean setFullAddress(String address1, String address2, String postCode, String phone){
        boolean ret = false;
        setAddressID(SQLManager.getInstance().addAddress(address1, address2, postCode, phone, this.getCityID()));
        if (this.getAddressID()>=0){
            ret = true;
        }
        return ret;
    }

    public int getActive() {        return active.get();    }
    public SimpleIntegerProperty activeProperty() {        return active;    }
    public void setActive(int active) {        this.active.set(active);    }

    //
    //Customer Name getter/setter
    public String getCustomerName() {        return customerName.get();    }
    public SimpleStringProperty customerNameProperty() {        return customerName;    }
    public void setCustomerName(String customerName){
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
    public void setAddress1(String address1) {
        this.address1.set(address1);
    }

    //
    // Address Line 2 getter/setter
    public String getAddress2() {
        return address2.get();
    }
    public SimpleStringProperty address2Property() {
        return address2;
    }
    public void setAddress2(String address2) {
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
    public void setCity(String city) {
        int cityID = SQLManager.getInstance().addCity(city, this.getCountryID());

        if (cityID>=0){
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
    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }
    public SimpleStringProperty phoneProperty() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getCountry() {
        return country.get();
    }

    public SimpleStringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        int countryID = SQLManager.getInstance().addCountry(country);
        if (countryID >=0){
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

}
