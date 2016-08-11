package com.xiuxiu.Xiuxiubroadcast;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.db.XiuxiuBroadcastMsgTable;
import com.xiuxiu.easeim.EaseUserCacheManager;

/**
 * Created by huzhi on 16-8-11.
 */
public class BroadcastListAdapter extends CursorAdapter{


    public BroadcastListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public BroadcastListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public BroadcastListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private LayoutInflater mLayoutInflater;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if(mLayoutInflater==null){
            mLayoutInflater = LayoutInflater.from(context);
        }
        ViewGroup convertView;
        convertView = (ViewGroup) mLayoutInflater.inflate(R.layout.xiuxiu_broadcast_item, null);
        ViewHolder holder = new ViewHolder();
        holder.nameTv = (TextView) convertView.findViewById(R.id.name);
        holder.messageTv = (TextView) convertView.findViewById(R.id.message);
        holder.timeTv = (TextView) convertView.findViewById(R.id.time);
        holder.avatarIv = (ImageView) convertView.findViewById(R.id.avatar);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String fromXiuxiuId = cursor.getString(cursor.getColumnIndex(XiuxiuBroadcastMsgTable._FROM_XIUXIU_ID));
        final long time = cursor.getLong(cursor.getColumnIndex(XiuxiuBroadcastMsgTable._UPDATE_TIME));
        final String content = cursor.getString(cursor.getColumnIndex(XiuxiuBroadcastMsgTable._CONTENT));

        XiuxiuUserInfoResult info = EaseUserCacheManager.getInstance().getBeanById(fromXiuxiuId);

        final ViewHolder holder = (ViewHolder) view.getTag();
        if(info!=null) {
            holder.nameTv.setText(info.getXiuxiu_name());
        }else{
            holder.nameTv.setText(fromXiuxiuId);
        }
        holder.avatarIv.setImageResource(R.drawable.head_default);
        if(info!=null) {
            ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + info.getPic(), holder.avatarIv);
        }
        holder.messageTv.setText(content);
        String timeTxt = com.xiuxiu.utils.DateUtils.getTextByTime(
                XiuxiuApplication.getInstance().getApplicationContext(),
                time,
                R.string.date_fromate_anecdote);
        holder.timeTv.setText(timeTxt);

    }

    private static class ViewHolder {
        TextView nameTv;
        TextView messageTv;
        TextView timeTv;
        ImageView avatarIv;
        TextView ageTv;
        TextView gradeTv;
    }
}
