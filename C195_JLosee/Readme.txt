C195 Software II
Decision Making Notes
-Default User Name/Password: test/test
-As setting columns to autoincrement being allowed was not noted, when adding a new tuple to the DB, I instead checked the highest ID, the pre-incremented it as the new id for the given tuple.

Rubric 

Requirement A:
-Done, this is the initial loaded form. This defaults to the user's system setting, but the user can select the language from the combobox for supported languages.

Requirement B:
-Form Created, todo: Validate all input, add customer to DB
-Made a combobox for the country selection to avoid duplicates in the database.

Requirement C:
-TODO: ALL

Requirement D:
-TODO: All, 

Requirement E:
-TODO: All, but this is a simple conversion from the SQL dateTime to a zoned datetime based on user system defaults

Requirement F (Exception Controls):
-In progress: overlapping appointments, nonexistent or invalid customer data (See Requirement B)
-Done: UN/Password

Requirement G:
-TODO: Lambda expressions for alerts and popups, this will be done at the end probably

Requirement H:
-Reminders at login, todo: get appointments at login, parse for appointsmnets and display a popup

Rubric I:
TODO: All
Appt types by month: Select count(description) where datetime in (given month)
Schedule for each consultant: Select * from appts where createdBy = current user
Additional report: 
Rubric J:
-Completed, this is output to /log/log.txt