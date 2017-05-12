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

    public int getActive() {
        return active.get();
    }

    public SimpleIntegerProperty activeProperty() {
        return active;
    }

    public void setActive(int active) {
        this.active.set(active);
    }



    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) throws Exception {
        if (customerName.length()<MAX_NAME_LENGTH){
            this.customerName.set(customerName);
        }else{
            throw new Exception("Customer Name cannot exceed "+MAX_NAME_LENGTH+" characters in length.");
        }
    }

    public String getAddress1() {
        return address1.get();
    }

    public SimpleStringProperty address1Property() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1.set(address1);
    }

    public String getAddress2() {
        return address2.get();
    }

    public SimpleStringProperty address2Property() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public String getCity() {
        return city.get();
    }

    public SimpleStringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
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
        this.country.set(country);
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
