package com.talentstream.dto;

public class RefreshTokenDTO {
    private String token;
    private String username;
 
    // Getters and Setters
    public String getToken() {
        return token;
    }
 
    public void setToken(String token) {
        this.token = token;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
}