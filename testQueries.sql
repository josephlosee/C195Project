#insert into country (countryID, country, createDate, createdBy) values ((Select count(countryId) from country), "Swaziland", NOW(), "J.Losee");
#delete from city where cityId=1;
#Select * from country;
#Select * from city;
#Select * from address;
#@Insert into address (addressID, address, address2, cityID, postalCode, phone, createDate, createdBy) 
#VALUES( 0, "35 Tower Hill", "St Katharine's & Wapping", (select cityID from city where city="London"), "EC3N 4DR", "44 844 482 7777", NOW(), "J.Losee");
#select * from address;
#select * from address where addressId=1 join city where cityId = address.cityId Join country where countryId = city.countryId;

#Select * from customer Join address Using(addressId)  Join city Using (cityId) Join country using (countryId);
 #from customer Join Using (addressId)
SELECT * FROM customer JOIN address USING (addressId) JOIN city USING (cityId) JOIN country USING(countryId);
#alter table customer modify column customerId int(10) not null auto_increment;
#insert into user (userName, password) values ('test', 'test');
#select * from user;
select * from customer;
#delete from customer where customerId between 3 and 10;
#delete from customer where customerId=2;
