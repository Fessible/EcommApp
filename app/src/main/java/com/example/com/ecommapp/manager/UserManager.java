package com.example.com.ecommapp.manager;

import com.example.com.ecommapp.module.user.User;

/**
 * 用于管理用户登录信息
 * Created by rhm on 2018/1/20.
 */

public class UserManager {
    private static UserManager userManager = null;
    private User user;

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (userManager == null) {
            synchronized (UserManager.class) {
                if (userManager == null) {
                    userManager = new UserManager();
                }
            }
        }
        return userManager;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void removeUser() {
        this.user = null;
    }

    public boolean hasLogined() {
        return user == null ? false : true;
    }
}
