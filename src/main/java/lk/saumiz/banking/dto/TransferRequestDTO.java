package lk.saumiz.banking.dto;

import java.math.BigDecimal;

/** Carries a validated transfer request from Controller down to BO. */
public class TransferRequestDTO {
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final String remarks;

    public TransferRequestDTO(String fromAccount, String toAccount, BigDecimal amount, String remarks) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.remarks = remarks;
    }

    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getRemarks() { return remarks; }
}
