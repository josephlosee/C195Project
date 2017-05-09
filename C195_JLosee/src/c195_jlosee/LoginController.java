/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195_jlosee;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Joe
 */
public class LoginController implements Initializable  {
    @FXML Button loginButton, exitButton;
    @FXML TextField loginUN;
    @FXML PasswordField loginPW;
    
    @Override public void initialize(URL url, ResourceBundle rb){
        //TODO: Add any init here
    }
    
    @FXML public void loginClicked(ActionEvent e){
        //Attempt login, check against 
        System.out.println("UN: "+loginUN.getText()+"\nPW: "+loginPW.getText());
        SQLManager.login(loginUN.getText(), loginPW.getText());
    }
    
    @FXML public void exitClicked(ActionEvent e){
        System.exit(0);
    }
    
}
