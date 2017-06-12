C195 Software II
Decision Making Notes
-Default User Name/Password: test/test
-Made a check on launch to ensure all id columns are set to auto_increment, as this was noted as an option in the course chatter.

Rubric 

Requirement A:
-Done, this is the initial loaded form. This defaults to the user's system setting, but the user can select the language from the combobox for supported languages.

Requirement B:
-Form Created, todo: Validate all input, add customer to DB
-Made a combobox for the country selection to avoid duplicates in the database.
-TODO: make sure to set active in the customer table, currently do not have this working
-Todo: edit customer code, view customer code (reuse same view, edit loads in existing, view sets all fields to not editable)

Requirement C:
-TODO: ALL

Requirement D:
-TODO: All, 

Requirement E:
-TODO: All, but this is a simple conversion from the SQL dateTime to a zoned datetime based on user system defaults

Requirement F (Exception Controls):
-In progress: overlapping appointments, nonexistent or invalid customer data (See Requirement B, completed validation)
-Done: UN/Password

Requirement G:
-TODO: Lambda expressions for alerts and popups, this will be done at the end probably

Requirement H:
-Reminders at login, todo: get appointments at login, parse for appointsmnets and display a popup

Rubric I:
TODO: All
Appt types by month: Select description as 'Appt Type', count(description) where datetime in ?(given month)
Schedule for each consultant: Select * from appts where createdBy = current user
Additional report: 
Rubric J:
-Completed, this is output to /log/log.txt