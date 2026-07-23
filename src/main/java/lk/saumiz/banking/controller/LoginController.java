package lk.saumiz.banking.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.saumiz.banking.bo.UserBO;
import lk.saumiz.banking.bo.custom.BOFactory;
import lk.saumiz.banking.entity.User;
import lk.saumiz.banking.util.SessionManager;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblMessage;

    private final UserBO userBO = BOFactory.getInstance().getUserBO();

    @FXML
    private void handleLogin() {
        lblMessage.setText("");
        String username = txtUsername.getText() == null ? "" : txtUsername.getText().trim();
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Username and password are required.");
            return;
        }

        btnLogin.setDisable(true);
        try {
            User user = userBO.login(username, password);
            if (user == null) {
                lblMessage.setText("Invalid username or password, or account inactive.");
                return;
            }
            SessionManager.getInstance().setLoggedInUser(user);
            loadMainView();
        } catch (Exception e) {
            lblMessage.setText("Login failed: " + e.getMessage());
        } finally {
            btnLogin.setDisable(false);
        }
    }

    private void loadMainView() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.setTitle("Smart Banking Management System - Dashboard");
        stage.centerOnScreen();
    }
}
