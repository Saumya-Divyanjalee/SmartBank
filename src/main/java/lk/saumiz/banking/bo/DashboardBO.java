package lk.saumiz.banking.bo;

import java.math.BigDecimal;

public interface DashboardBO {
    int getTotalCustomers() throws Exception;
    int getTotalAccounts() throws Exception;
    BigDecimal getTotalBankBalance() throws Exception;
    BigDecimal getTodayDeposits() throws Exception;
    BigDecimal getTodayWithdrawals() throws Exception;
    BigDecimal getTodayTransfers() throws Exception;
}
