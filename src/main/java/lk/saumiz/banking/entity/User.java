package lk.saumiz.banking.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private String userId;
    private String username;
    private String password; // bcrypt hash
    private String role;     // ADMIN / STAFF
    private String status;   // ACTIVE / INACTIVE
    private LocalDateTime createdDate;

    public User() {}

    public User(String userId, String username, String password, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
