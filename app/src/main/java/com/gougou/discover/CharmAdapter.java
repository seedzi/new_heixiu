package com.gougou.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.gougou.R;
import com.gougou.bean.Friend;
import com.gougou.utils.UiUtil;

/**
 * Created by huzhi on 16-4-7.
 */
public class CharmAdapter extends ArrayAdapter<Friend> {

    public CharmAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView != null ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.discover_charm_item, parent, false);
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
        UiUtil.findTextViewById(itemView, R.id.position).setText(position+4+"");
        return itemView;
    }
}
