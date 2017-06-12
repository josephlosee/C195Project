C195 Software II
Decision Making Notes
-Default User Name/Password: test/test
-Additional User Name/Passwords: jlosee/password, review/review
-Made a check on launch to ensure all id columns are set to auto_increment, as
	this was noted as an option in the course chatter.

Rubric 

Requirement A:
This is the initial loaded form. This defaults to the user's system setting, 
but the user can select the language from the combobox for supported languages.

Requirement B:
Customer information can be viewed in the bottom left table view of the main 
application view.

Requirement C:
Lamda expressions are used in AppointmentViewController to filter appointment 
time input, SQLCustomer and SQLUser(in canUpdateAppointmentTime and addAppointment)

Requirement D:
Interactive calendar is present in the main view after login, as is the
consultant's calendar for the week.

Requirement E:
Implemented.

Requirement F (Exception Controls):
Done, try with resources is used in LoginController to log successful and 
failed login attempts.

Requirement G:
All alerts and popups use lambdas

Requirement H:
On login, the user will receive an alert if they have an appointment in the 
next 15 minutes.

Rubric I:
Reports can be accessed by clicking the "Reports" button at the bottom of the 
main view.
The additional report created is a list of the most active customers in the
selected period.

Rubric J:
The log is output to /log/log.txt