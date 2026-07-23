package lk.saumiz.banking.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lk.saumiz.banking.util.SessionManager;

public class MainController {

    @FXML private javafx.scene.control.Label lblLoggedUser;
    @FXML private StackPane contentArea;

    @FXML
    private void initialize() {
        var user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            lblLoggedUser.setText("Signed in as " + user.getUsername() + " (" + user.getRole() + ")");
        }
        showDashboard();
    }

    @FXML private void showDashboard() { load("/view/DashboardView.fxml"); }
    @FXML private void showCustomers() { load("/view/CustomerView.fxml"); }
    @FXML private void showAccounts() { load("/view/AccountView.fxml"); }
    @FXML private void showTransactions() { load("/view/TransactionView.fxml"); }
    @FXML private void showReports() { load("/view/ReportView.fxml"); }

    private void load(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene loginScene = new Scene(root, 900, 600);
            loginScene.getStylesheets().add(getClass().getResource("/view/bank-theme.css").toExternalForm());
            stage.setScene(loginScene);
            stage.setTitle("Smart Banking Management System");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
