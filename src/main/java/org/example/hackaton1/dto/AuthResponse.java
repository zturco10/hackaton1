package org.example.hackaton1.dto;

public class AuthResponse {
    private String token;
    private long expiresIn;
    private String role;
    private String branch;

    public AuthResponse() {}

    public AuthResponse(String token, long expiresIn, String role, String branch) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.role = role;
        this.branch = branch;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}