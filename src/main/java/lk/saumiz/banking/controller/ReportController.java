package lk.saumiz.banking.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lk.saumiz.banking.bo.AccountBO;
import lk.saumiz.banking.bo.CustomerBO;
import lk.saumiz.banking.bo.custom.BOFactory;
import lk.saumiz.banking.entity.Account;
import lk.saumiz.banking.entity.Customer;
import lk.saumiz.banking.util.ReportGenerator;

import java.awt.Desktop;
import java.io.File;

public class ReportController {

    @FXML private TextField txtAccountNo;
    @FXML private Label lblMessage;

    private final AccountBO accountBO = BOFactory.getInstance().getAccountBO();
    private final CustomerBO customerBO = BOFactory.getInstance().getCustomerBO();

    @FXML
    private void handleGenerateStatement() {
        lblMessage.setText("");
        try {
            String accountNo = txtAccountNo.getText();
            if (accountNo == null || accountNo.isBlank()) {
                lblMessage.setText("Enter an account number first.");
                return;
            }
            accountNo = accountNo.trim();

            Account account = accountBO.getAccount(accountNo);
            if (account == null) {
                lblMessage.setText("Account not found: " + accountNo);
                return;
            }

            var transactions = accountBO.getTransactionHistory(accountNo);

            Customer customer = customerBO.getCustomer(account.getCustomerId());
            String customerName = customer != null ? customer.getName() : account.getCustomerId();

            // Reports are saved to a folder in the user's home directory
            File outputDir = new File(System.getProperty("user.home"), "SmartBankingReports");

            File pdf = ReportGenerator.generateAccountStatementPdf(
                    accountNo,
                    customerName,
                    account.getAccountType(),
                    account.getBalance(),
                    transactions,
                    outputDir
            );

            lblMessage.setText("Statement saved to: " + pdf.getAbsolutePath());

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(pdf);
            }
        } catch (Exception e) {
            lblMessage.setText("Could not generate statement: " + e.getMessage());
        }
    }
}
