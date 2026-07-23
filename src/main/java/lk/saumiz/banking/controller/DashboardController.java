package lk.saumiz.banking.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lk.saumiz.banking.bo.DashboardBO;
import lk.saumiz.banking.bo.custom.BOFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DashboardController {

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblTotalAccounts;
    @FXML private Label lblBankBalance;
    @FXML private Label lblTodayDeposits;
    @FXML private Label lblTodayWithdrawals;
    @FXML private Label lblTodayTransfers;
    @FXML private Label lblError;

    private final DashboardBO dashboardBO = BOFactory.getInstance().getDashboardBO();
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    @FXML
    private void initialize() {
        try {
            lblTotalCustomers.setText(String.valueOf(dashboardBO.getTotalCustomers()));
            lblTotalAccounts.setText(String.valueOf(dashboardBO.getTotalAccounts()));
            lblBankBalance.setText(format(dashboardBO.getTotalBankBalance()));
            lblTodayDeposits.setText(format(dashboardBO.getTodayDeposits()));
            lblTodayWithdrawals.setText(format(dashboardBO.getTodayWithdrawals()));
            lblTodayTransfers.setText(format(dashboardBO.getTodayTransfers()));
        } catch (Exception e) {
            lblError.setText("Could not load dashboard stats: " + e.getMessage());
        }
    }

    private String format(BigDecimal value) {
        return "Rs. " + MONEY.format(value == null ? BigDecimal.ZERO : value);
    }
}
