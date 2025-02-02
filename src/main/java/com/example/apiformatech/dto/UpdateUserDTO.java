package com.example.apiformatech.dto;

import com.example.apiformatech.model.User;
import com.example.apiformatech.model.UserInfo;

public class UpdateUserDTO {
    private User user;
    private UserInfo userInfo;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
