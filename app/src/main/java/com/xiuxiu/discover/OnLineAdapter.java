package com.xiuxiu.discover;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.utils.UiUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-4-6.
 */
public class OnLineAdapter extends ArrayAdapter<XiuxiuPerson> implements View.OnClickListener{


    public OnLineAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView != null ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.discover_friend_item, parent, false);

        if(getItem(position)!=null){
            if(!TextUtils.isEmpty(getItem(position).getXiuxiu_name())&&
                    UiUtil.findTextViewById(itemView, R.id.user_name)!=null ) {
                UiUtil.findTextViewById(itemView, R.id.user_name).setText(URLDecoder.decode(getItem(position).getXiuxiu_name()));
            }
            if(!TextUtils.isEmpty(getItem(position).getSign())&&
                    UiUtil.findTextViewById(itemView, R.id.description)!=null){
                UiUtil.findTextViewById(itemView, R.id.description).setText(URLDecoder.decode(getItem(position).getSign()));
            }
        }


        itemView.setTag(new String[]{getItem(position).getXiuxiu_id(),getItem(position).getXiuxiu_name()});
//        itemView.setOnClickListener(this);

        return itemView;
    }

    FragmentActivity mAc;

    public void setActivity(FragmentActivity ac){
        mAc = ac;
    }

    @Override
    public void onClick(View v) {
        ChatPage.startActivity(mAc,((String[])v.getTag())[0],((String[])v.getTag())[1]);
    }
}
