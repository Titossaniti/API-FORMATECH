package com.example.apiformatech.dto;

import com.example.apiformatech.model.UserInfo;

public class CreateTrainerDTO {
    private String email;
    private String password;
    private UserInfo userInfo;

    public CreateTrainerDTO() {}

    public CreateTrainerDTO(String email, String password, UserInfo userInfo) {
        this.email = email;
        this.password = password;
        this.userInfo = userInfo;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }
}
