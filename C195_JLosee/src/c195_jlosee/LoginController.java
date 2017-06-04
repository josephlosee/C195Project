/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195_jlosee;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 *
 * @author Joe
 */
public class LoginController implements Initializable  {
    @FXML private Button loginButton, exitButton;
    @FXML private TextField loginUN;
    @FXML private PasswordField loginPW;
    @FXML private Label  userNameLabel, passwordLabel;
    @FXML private ComboBox langSelect;
    private Locale userLocale = Locale.getDefault();
    private ResourceBundle rb;
    Path path = Paths.get("./log/login.txt");
    BufferedWriter logFile;

    ObservableList<String> supportedLangList = FXCollections.observableList(new ArrayList<String>());

    @Override public void initialize(URL url, ResourceBundle rb){
        supportedLangList.add("English");
        supportedLangList.add("Francois");
        supportedLangList.add("Deutch");

        langSelect.setItems(supportedLangList);

        switch (userLocale.getLanguage()){
            case("fr"):
                langSelect.getSelectionModel().select(1);
                break;
            case("de"):
                langSelect.getSelectionModel().select(2);
                break;
            default:
                langSelect.getSelectionModel().select(0);
        }

        changeLanguage(userLocale);
    }

    /**
     * Changes the locale of the login view
     * @param changeToLocale
     */
    private void changeLanguage(Locale changeToLocale){
        this.rb = ResourceBundle.getBundle("c195_jlosee/resources/Login", changeToLocale);
        userNameLabel.setText(this.rb.getString("userName"));
        passwordLabel.setText(this.rb.getString("password"));
        loginButton.setText(this.rb.getString("login"));
        exitButton.setText(this.rb.getString("exit"));
    }
    
    @FXML public void loginClicked(ActionEvent e){
        //Quick check for blank fields:
        String userName = loginUN.getText();
        String pw = loginPW.getText();

        if (userName.isEmpty()){
            Alert emptyString = new Alert(Alert.AlertType.ERROR, rb.getString("emptyUN"));
            emptyString.showAndWait();
        }else if (pw.isEmpty()){
            Alert emptyString = new Alert(Alert.AlertType.ERROR, rb.getString("emptyPW"));
            emptyString.showAndWait();
        } else{
            if (SQLManager.getInstance().login(userName, pw)) {

                //Write to the log file
                logAction(true);

                Alert welcome = new Alert(Alert.AlertType.INFORMATION, rb.getString("loginSuccess"));
                welcome.showAndWait();

                String appointmentReminder = SQLManager.getInstance().checkForApptAtLogin();
                if (appointmentReminder!=null){
                    new Alert(Alert.AlertType.WARNING, appointmentReminder).showAndWait();
                }

                Stage custStage = new Stage();
                try {
                    custStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("resources/MainView.fxml"))));
                    custStage.show();
                    (((Node)e.getSource()).getScene().getWindow()).hide();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } else {
                logAction(false);
                new Alert(Alert.AlertType.ERROR, rb.getString("loginFailed"))
                        .showAndWait();
            }
        }
    }
    
    @FXML public void exitClicked(ActionEvent e){
        //Lambda
        new Alert(Alert.AlertType.CONFIRMATION, rb.getString("exitConfirmation"))
                .showAndWait()
                .filter(response->response==ButtonType.OK)
                .ifPresent(response->System.exit(0));
    }

    @FXML public void langChanged(ActionEvent e){
        int i = langSelect.getSelectionModel().getSelectedIndex();

        Locale changeTo = new Locale(supportedLangList.get(i).substring(0,2));
        this.changeLanguage(changeTo);
    }

    /**
     * Writes to the log file for login attempts
     * @param loginSucceeded
     */
    private void logAction(boolean loginSucceeded){
        //Paths.
        try{
            //Create the file if not present
            Files.createDirectories(Paths.get("./log/"));
            File filePresent = path.toFile();
            filePresent.createNewFile();
        }catch (IOException createExc){
            System.out.println("Error in creating file: "+createExc.getMessage());
        }

        String loginString;
        if (loginSucceeded) {
            loginString = "" + LocalDateTime.now() + " Login successful for " + loginUN.getText();
        }else{
            loginString = "" + LocalDateTime.now() + " Login failed for " + loginUN.getText();
        }

        //Try with resources
        try (BufferedWriter loginBuff = Files.newBufferedWriter(path, Charset.forName("UTF-16"), StandardOpenOption.APPEND)) {
            loginBuff.write(loginString);
            loginBuff.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
