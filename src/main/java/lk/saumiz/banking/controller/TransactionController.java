package lk.saumiz.banking.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.saumiz.banking.bo.AccountBO;
import lk.saumiz.banking.bo.custom.BOFactory;
import lk.saumiz.banking.dto.TransferRequestDTO;
import lk.saumiz.banking.entity.Transaction;

import java.math.BigDecimal;

public class TransactionController {

    // Deposit tab
    @FXML private TextField txtDepositAccount;
    @FXML private TextField txtDepositAmount;
    @FXML private TextField txtDepositRemarks;
    @FXML private Label lblDepositMessage;

    // Withdraw tab
    @FXML private TextField txtWithdrawAccount;
    @FXML private TextField txtWithdrawAmount;
    @FXML private TextField txtWithdrawRemarks;
    @FXML private Label lblWithdrawMessage;

    // Transfer tab
    @FXML private TextField txtTransferFrom;
    @FXML private TextField txtTransferTo;
    @FXML private TextField txtTransferAmount;
    @FXML private TextField txtTransferRemarks;
    @FXML private Label lblTransferMessage;

    // History tab
    @FXML private TextField txtHistoryAccount;
    @FXML private TableView<Transaction> tblHistory;
    @FXML private TableColumn<Transaction, String> colTxnId;
    @FXML private TableColumn<Transaction, String> colFrom;
    @FXML private TableColumn<Transaction, String> colTo;
    @FXML private TableColumn<Transaction, BigDecimal> colAmount;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, BigDecimal> colBalanceAfter;
    @FXML private TableColumn<Transaction, String> colDate;

    private final AccountBO accountBO = BOFactory.getInstance().getAccountBO();

    @FXML
    private void initialize() {
        colTxnId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("fromAccount"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("toAccount"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        colBalanceAfter.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
    }

    @FXML
    private void handleDeposit() {
        lblDepositMessage.setText("");
        try {
            String accNo = require(txtDepositAccount.getText(), "Account No");
            BigDecimal amount = parseAmount(txtDepositAmount.getText());
            accountBO.deposit(accNo, amount, emptyToNull(txtDepositRemarks.getText()));
            lblDepositMessage.setText("Deposit successful.");
            txtDepositAmount.clear();
            txtDepositRemarks.clear();
        } catch (Exception e) {
            lblDepositMessage.setText("Deposit failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleWithdraw() {
        lblWithdrawMessage.setText("");
        try {
            String accNo = require(txtWithdrawAccount.getText(), "Account No");
            BigDecimal amount = parseAmount(txtWithdrawAmount.getText());
            accountBO.withdraw(accNo, amount, emptyToNull(txtWithdrawRemarks.getText()));
            lblWithdrawMessage.setText("Withdrawal successful.");
            txtWithdrawAmount.clear();
            txtWithdrawRemarks.clear();
        } catch (Exception e) {
            lblWithdrawMessage.setText("Withdrawal failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleTransfer() {
        lblTransferMessage.setText("");
        try {
            String from = require(txtTransferFrom.getText(), "From Account");
            String to = require(txtTransferTo.getText(), "To Account");
            BigDecimal amount = parseAmount(txtTransferAmount.getText());
            TransferRequestDTO request = new TransferRequestDTO(from, to, amount,
                    emptyToNull(txtTransferRemarks.getText()));
            accountBO.transfer(request);
            lblTransferMessage.setText("Transfer successful: " + amount + " from " + from + " to " + to);
            txtTransferAmount.clear();
            txtTransferRemarks.clear();
        } catch (Exception e) {
            // Any failure here means the BO already rolled back both balance changes -
            // no partial transfer is ever left in the database.
            lblTransferMessage.setText("Transfer failed (fully rolled back): " + e.getMessage());
        }
    }

    @FXML
    private void handleLoadHistory() {
        try {
            String accNo = require(txtHistoryAccount.getText(), "Account No");
            ObservableList<Transaction> list = FXCollections.observableArrayList(accountBO.getTransactionHistory(accNo));
            tblHistory.setItems(list);
        } catch (Exception e) {
            tblHistory.setItems(FXCollections.observableArrayList());
        }
    }

    private String require(String value, String fieldName) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(fieldName + " is required");
        return value.trim();
    }

    private BigDecimal parseAmount(String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Amount is required");
        try {
            BigDecimal amount = new BigDecimal(text.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a valid number");
        }
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
