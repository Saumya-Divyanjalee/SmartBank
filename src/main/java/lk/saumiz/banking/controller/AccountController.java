package lk.saumiz.banking.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.saumiz.banking.bo.AccountBO;
import lk.saumiz.banking.bo.custom.BOFactory;
import lk.saumiz.banking.dto.AccountDTO;
import lk.saumiz.banking.entity.Account;

import java.math.BigDecimal;

public class AccountController {

    @FXML private TextField txtCustomerId;
    @FXML private TextField txtBranchId;
    @FXML private ChoiceBox<String> cbAccountType;
    @FXML private TextField txtOpeningBalance;
    @FXML private TextField txtFilterCustomerId;
    @FXML private Label lblMessage;

    @FXML private TableView<Account> tblAccounts;
    @FXML private TableColumn<Account, String> colAccNo;
    @FXML private TableColumn<Account, String> colCustomerId;
    @FXML private TableColumn<Account, String> colBranchId;
    @FXML private TableColumn<Account, String> colType;
    @FXML private TableColumn<Account, BigDecimal> colBalance;
    @FXML private TableColumn<Account, String> colStatus;

    private final AccountBO accountBO = BOFactory.getInstance().getAccountBO();

    @FXML
    private void initialize() {
        cbAccountType.setItems(FXCollections.observableArrayList("SAVINGS", "CURRENT", "FIXED_DEPOSIT"));
        cbAccountType.getSelectionModel().selectFirst();

        colAccNo.setCellValueFactory(new PropertyValueFactory<>("accountNo"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        handleRefresh();
    }

    @FXML
    private void handleCreateAccount() {
        try {
            String customerId = txtCustomerId.getText();
            if (customerId == null || customerId.isBlank()) {
                lblMessage.setText("Customer ID is required.");
                return;
            }
            BigDecimal opening = BigDecimal.ZERO;
            if (txtOpeningBalance.getText() != null && !txtOpeningBalance.getText().isBlank()) {
                opening = new BigDecimal(txtOpeningBalance.getText().trim());
            }
            AccountDTO dto = new AccountDTO(null, customerId.trim(),
                    txtBranchId.getText() == null ? null : txtBranchId.getText().trim(),
                    cbAccountType.getValue(), opening, "ACTIVE");
            String newAcc = accountBO.createAccount(dto);
            if (newAcc != null) {
                lblMessage.setText("Account created: " + newAcc);
                handleClear();
                handleRefresh();
            } else {
                lblMessage.setText("Failed to create account. Check the Customer ID exists.");
            }
        } catch (NumberFormatException nfe) {
            lblMessage.setText("Opening balance must be a valid number.");
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        txtCustomerId.clear();
        txtBranchId.clear();
        txtOpeningBalance.clear();
        cbAccountType.getSelectionModel().selectFirst();
        lblMessage.setText("");
    }

    @FXML
    private void handleFilterByCustomer() {
        try {
            String cid = txtFilterCustomerId.getText();
            if (cid == null || cid.isBlank()) {
                handleRefresh();
                return;
            }
            ObservableList<Account> list = FXCollections.observableArrayList(accountBO.getAccountsForCustomer(cid.trim()));
            tblAccounts.setItems(list);
        } catch (Exception e) {
            lblMessage.setText("Filter failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            ObservableList<Account> all = FXCollections.observableArrayList(accountBO.getAllAccounts());
            tblAccounts.setItems(all);
        } catch (Exception e) {
            lblMessage.setText("Could not load accounts: " + e.getMessage());
        }
    }
}
