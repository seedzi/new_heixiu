package com.xiuxiuchat.api;

import java.util.List;

/**
 * Created by huzhi on 16-6-14.
 */
public class XiuxiuQueryActiveUserResult {

    public List<XiuxiuActiveUserResult> activeUsers;

    public List<XiuxiuActiveUserResult> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<XiuxiuActiveUserResult> activeUsers) {
        this.activeUsers = activeUsers;
    }
}
