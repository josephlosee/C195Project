/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195_jlosee;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

/**
 *
 * @author Joe
 */
public class LoginController implements Initializable  {
    @FXML Button loginButton, exitButton;
    @FXML TextField loginUN;
    @FXML PasswordField loginPW;
    @FXML Label  userNameLabel, passwordLabel;
    Locale userLocale = Locale.getDefault();
    ResourceBundle rb;

    
    @Override public void initialize(URL url, ResourceBundle rb){
        //TODO: Add any init here
        /*TESTING: userLocale=

         */
        userLocale = new Locale("de");
        this.rb = ResourceBundle.getBundle("c195_jlosee/resources/Login", userLocale);

        System.out.println("Locale = "+userLocale.toString());


        userNameLabel.setText(this.rb.getString("userName"));
        passwordLabel.setText(this.rb.getString("password"));
        loginButton.setText(this.rb.getString("login"));
        exitButton.setText(this.rb.getString("exit"));
    }
    
    @FXML public void loginClicked(ActionEvent e){
        //Attempt login, check against SQL database
        //TODO: log failed/successful attempts
        System.out.println("UN: "+loginUN.getText()+"\nPW: "+loginPW.getText());
        if (SQLManager.login(loginUN.getText(), loginPW.getText())){
            Alert welcome = new Alert(Alert.AlertType.INFORMATION, rb.getString("loginSuccess"));
            welcome.showAndWait();

            ViewManager.showMainView();
        }else{
            //TODO: Log failed attempt in log file
            Alert loginFailed = new Alert(Alert.AlertType.ERROR, rb.getString("loginFailed"));
            loginFailed.showAndWait();//System.out.println(rb.getString("loginFailed"));
        }
    }
    
    @FXML public void exitClicked(ActionEvent e){
        if (ViewManager.showConfirmationView(rb.getString("exitConfirmation"))){
            System.exit(0);
        }

    }
    
}
