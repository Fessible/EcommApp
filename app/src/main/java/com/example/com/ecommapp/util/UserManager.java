package com.example.com.ecommapp.util;

/**
 * 管理用户登录信息
 * Created by rhm on 2017/12/21.
 */

public class UserManager {
    private static UserManager userManager;

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

}
