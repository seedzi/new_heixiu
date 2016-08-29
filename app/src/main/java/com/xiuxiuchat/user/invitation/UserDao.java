package com.xiuxiuchat.user.invitation;

import android.content.Context;

import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;
import java.util.Map;

/**
 * Created by huzhi on 16-6-5.
 */
public class UserDao {

    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String COLUMN_NAME_AGE = "age";
    public static final String COLUMN_NAME_CHARM = "charm";
    public static final String COLUMN_NAME_FORTUNE = "fortune";
    public static final String COLUMN_NAME_ONLINE_TIME = "online_time";
    public static final String COLUMN_NAME_VOICE = "voice";
    public static final String COLUMN_NAME_XIUXIU_ID = "xiuxiu_id";
    public static final String COLUMN_NAME_SEX = "xiuxiu_sex";
    public static final String COLUMN_NAME_ACTIVE_TIME = "active_time";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

    public UserDao(Context context) {
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<EaseUser> contactList) {
        DemoDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {

        return DemoDBManager.getInstance().getContactList();
    }

    /**
     * 删除一个联系人
     * @param username
     */
    public void deleteContact(String username){
        DemoDBManager.getInstance().deleteContact(username);
    }

    /**
     * 保存一个联系人
     * @param user
     */
    public void saveContact(EaseUser user){
        DemoDBManager.getInstance().saveContact(user);
    }

    /*
    public void setDisabledGroups(List<String> groups){
        DemoDBManager.getInstance().setDisabledGroups(groups);
    }
    */

    /*
    public List<String>  getDisabledGroups(){
        return DemoDBManager.getInstance().getDisabledGroups();
    }
    */

    /*
    public void setDisabledIds(List<String> ids){
        DemoDBManager.getInstance().setDisabledIds(ids);
    }
    */

    /*
    public List<String> getDisabledIds(){
        return DemoDBManager.getInstance().getDisabledIds();
    }
    */

    /*
    public Map<String, RobotUser> getRobotUser(){
        return DemoDBManager.getInstance().getRobotList();
    }
    */

    /*
    public void saveRobotUser(List<RobotUser> robotList){
        DemoDBManager.getInstance().saveRobotList(robotList);
    }
    */
}
