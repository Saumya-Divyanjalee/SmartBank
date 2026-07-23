package lk.saumiz.banking.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account implements Serializable {
    private String accountNo;
    private String customerId;
    private String branchId;
    private String accountType; // SAVINGS / CURRENT / FIXED_DEPOSIT
    private BigDecimal balance;
    private String status;      // ACTIVE / FROZEN / CLOSED
    private LocalDateTime createdDate;
    private int version;        // optimistic locking

    public Account() {}

    public Account(String accountNo, String customerId, String branchId,
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

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
