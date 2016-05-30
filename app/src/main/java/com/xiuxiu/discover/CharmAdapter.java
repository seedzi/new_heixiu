package com.xiuxiu.discover;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.bean.Friend;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.provider.XiuxiuProvider;
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
        /*
        View itemView = convertView != null ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.discover_friend_item, parent, false);
        UiUtil.findTextViewById(itemView, R.id.position).setText(position+4+"");
        return itemView;
        */
        /*
        final Friend recommendShares = getItem(position);
        ImageLoader.getInstance().displayImage(recommendShares.getAvatar(), UiUtil.findImageViewById(itemView, R.id.avatar));
        UiUtil.findTextViewById(itemView, R.id.name).setText(recommendShares.getUsername());
        UiUtil.findTextViewById(itemView, R.id.createdate).setText(recommendShares.getFormatedDate());
        ImageLoader.getInstance().displayImage(recommendShares.getShareFile(), UiUtil.findImageViewById(itemView, R.id.share_img));
        UiUtil.findTextViewById(itemView, R.id.share_mark).setText(recommendShares.getShareMark());
        UiUtil.findTextViewById(itemView, R.id.share_title).setText(recommendShares.getShareTitle());

        ImageUtils.setViewPressState(UiUtil.findImageViewById(itemView, R.id.share_img));
        UiUtil.findImageViewById(itemView, R.id.share_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
//        UiUtil.findImageViewById(itemView,R.id.head_img).setImageResource(R.drawable.head_default);
//        UiUtil.findTextViewById(itemView, R.id.user_name).setText("女人");
//        UiUtil.findTextViewById(itemView, R.id.description).setText("说明");


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
            if(UiUtil.findImageViewById(itemView,R.id.vip_grade)!=null) {
                XiuxiuPerson.setCharmValue(UiUtil.findImageViewById(itemView, R.id.vip_grade), getItem(position).getCharm());
            }
        }


        itemView.setTag(new String[]{getItem(position).getXiuxiu_id(), getItem(position).getXiuxiu_name()});

        return itemView;
    }

    @Override
    public void onClick(View v) {
//        ChatPage.startActivity(mAc, ((String[]) v.getTag())[0], ((String[]) v.getTag())[1]);
    }
}
