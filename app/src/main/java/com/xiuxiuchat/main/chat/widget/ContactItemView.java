package com.xiuxiuchat.main.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiuxiuchat.R;

/**
 * Created by huzhi on 16-6-4.
 */
public class ContactItemView extends FrameLayout{

    private TextView unreadMsgView;

    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactItemView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs){
        /*
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ContactItemView);
        String name = ta.getString(R.styleable.ContactItemView_contactItemName);
        Drawable image = ta.getDrawable(R.styleable.ContactItemView_contactItemImage);
        ta.recycle();
        */

        LayoutInflater.from(context).inflate(R.layout.wiget_contact_item, this);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText("申请与通知");
        /*
        if(image != null){
            avatar.setImageDrawable(image);
        }
        nameView.setText(name);
        */
    }

    public void setUnreadCount(int unreadCount){
        unreadMsgView.setText(String.valueOf(unreadCount));
    }

    public void showUnreadMsgView(){
        unreadMsgView.setVisibility(View.VISIBLE);
    }
    public void hideUnreadMsgView(){
        unreadMsgView.setVisibility(View.INVISIBLE);
    }
}
