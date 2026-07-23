package lk.saumiz.banking.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Customer implements Serializable {
    private String customerId;
    private String name;
    private String nic;
    private String phone;
    private String address;
    private String email;
    private LocalDateTime createdDate;

    public Customer() {}

    public Customer(String customerId, String name, String nic, String phone, String address, String email) {
        this.customerId = customerId;
        this.name = name;
        this.nic = nic;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
