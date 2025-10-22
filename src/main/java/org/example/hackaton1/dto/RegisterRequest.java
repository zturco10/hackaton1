package org.example.hackaton1.dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private String branch;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}