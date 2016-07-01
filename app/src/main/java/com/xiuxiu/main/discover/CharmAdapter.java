package com.xiuxiu.main.discover;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.utils.UiUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-4-7.
 */
public class CharmAdapter extends ArrayAdapter<XiuxiuPerson> implements View.OnClickListener{

    public CharmAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View itemView = convertView != null ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.discover_friend_item, null);

        if(getItem(position)!=null){
            if(!TextUtils.isEmpty(getItem(position).getXiuxiu_name())&&
                    UiUtil.findTextViewById(itemView, R.id.user_name)!=null ) {
                UiUtil.findTextViewById(itemView, R.id.user_name).setText(URLDecoder.decode(getItem(position).getXiuxiu_name()));
            }
            if(!TextUtils.isEmpty(getItem(position).getSign())&&
                    UiUtil.findTextViewById(itemView, R.id.description)!=null){
                UiUtil.findTextViewById(itemView, R.id.description).setText(URLDecoder.decode(getItem(position).getSign()));
            }
            if(UiUtil.findImageViewById(itemView,R.id.vip_grade)!=null) {
                XiuxiuPerson.setCharmValue(UiUtil.findImageViewById(itemView, R.id.vip_grade), getItem(position).getCharm());
            }

            if(UiUtil.findImageViewById(itemView,R.id.head_img)!=null){
                ImageLoader.getInstance().displayImage(getItem(position).getPic(), UiUtil.findImageViewById(itemView, R.id.head_img));
            }

            if(UiUtil.findImageViewById(itemView,R.id.head_img)!=null){
                ImageLoader.getInstance().displayImage(getItem(position).getPic(),UiUtil.findImageViewById(itemView,R.id.head_img));
            }

            /*
            if(UiUtil.findTextViewById(itemView,R.id.user_age)!=null){
                UiUtil.findTextViewById(itemView,R.id.user_age).setText(getItem(position).getAge());
            }*/
        }

        itemView.setTag(new String[]{getItem(position).getXiuxiu_id(), getItem(position).getXiuxiu_name()});

        return itemView;
    }

    @Override
    public void onClick(View v) {
//        ChatPage.startActivity(mAc, ((String[]) v.getTag())[0], ((String[]) v.getTag())[1]);
    }
}
