package com.hyphenate.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EaseUserUtils {

    private static String TAG = EaseUserUtils.class.getSimpleName();

    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * 根据username获取相应user
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
        android.util.Log.d(TAG,"setUserAvatar");
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            ImageLoader.getInstance().displayImage(user.getAvatar(),imageView);
        }
        /*
        if(user != null && user.getAvatar() != null){
            android.util.Log.d(TAG,"user.getAvatar() = " + user.getAvatar());
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //正常的string路径
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
        */
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }

    /**
     * 设置用户签名
     */
    public static void setUserSignature(String username,TextView textView){
        if(textView != null){
            EaseUser user = getUserInfo(username);
            if(user != null && user.getSign() != null){
                textView.setText(user.getSign());
            }
        }
    }

    /**
     * 设置用户年纪
     */
    public static void setUserAge(String username,TextView textView){
        if(textView != null){
            EaseUser user = getUserInfo(username);
            if(user != null ){
                textView.setText(user.getAge());
            }
        }
    }

}
