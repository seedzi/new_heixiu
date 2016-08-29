package com.xiuxiuchat.bean;

/**
 * Created by huzhi on 16-5-22.
 */
public class ChatNickNameAndAvatarBean {

    private String nickName;

    private String avatar;

    private String xiuxiu_id;

    public String getNickName() {
        return nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getXiuxiu_id() {
        return xiuxiu_id;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setXiuxiu_id(String xiuxiu_id) {
        this.xiuxiu_id = xiuxiu_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatNickNameAndAvatarBean other = (ChatNickNameAndAvatarBean) obj;

        if (xiuxiu_id == null) {
            if (other.xiuxiu_id != null)
                return false;
        } else if (!xiuxiu_id.equals(other.xiuxiu_id))
            return false;

        if(avatar == null){
            if(other.avatar != null)
                return false;
        } else if (!avatar.equals(other.avatar))
            return false;

        if(nickName == null){
            if(other.nickName != null)
                return false;
        }else if (!nickName.equals(other.nickName))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "xiu_xiu_id = " + xiuxiu_id + ",avatar = " + avatar + ",nickName = " + nickName;
    }
}
