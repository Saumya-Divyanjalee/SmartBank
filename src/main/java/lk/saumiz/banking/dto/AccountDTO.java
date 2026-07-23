package lk.saumiz.banking.dto;

import java.math.BigDecimal;

public class AccountDTO {
    private String accountNo;
    private String customerId;
    private String branchId;
    private String accountType;
    private BigDecimal balance;
    private String status;

    public AccountDTO() {}

    public AccountDTO(String accountNo, String customerId, String branchId,
                       String accountType, BigDecimal balance, String status) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.branchId = branchId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
