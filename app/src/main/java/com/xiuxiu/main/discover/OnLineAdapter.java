package com.xiuxiu.main.discover;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.call.voice.VoiceCallActivity;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.voice.VoicePlayManager;
import com.xiuxiu.utils.DateUtils;
import com.xiuxiu.utils.ToastUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-4-6.
 */
public class OnLineAdapter extends ArrayAdapter<XiuxiuUserInfoResult> implements View.OnClickListener{

    private static String TAG = OnLineAdapter.class.getSimpleName();

    public OnLineAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.discover_friend_item, null);
            holder = new Holder();
            holder.userNameTv = (TextView) convertView.findViewById(R.id.user_name);
            holder.signTv = (TextView) convertView.findViewById(R.id.description);
            holder.vipGradeTv = (ImageView) convertView.findViewById(R.id.vip_grade);
            holder.headImg = (ImageView) convertView.findViewById(R.id.head_img);
            holder.listItemPlay = (ImageView) convertView.findViewById(R.id.list_item_play);
            holder.onlineTimeTv = (TextView) convertView.findViewById(R.id.online_time);
            holder.userAgeTv = (TextView) convertView.findViewById(R.id.user_age);
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        if(!TextUtils.isEmpty(getItem(position).getXiuxiu_name())){
            holder.userNameTv.setText(URLDecoder.decode(getItem(position).getXiuxiu_name()));
        }
        if(!TextUtils.isEmpty(getItem(position).getSign())){
            holder.signTv.setText(URLDecoder.decode(getItem(position).getSign()));
        }
        if(XiuxiuUserInfoResult.isMale(getItem(position).getSex())){
            XiuxiuUserInfoResult.setWealthValue(holder.vipGradeTv,getItem(position).getFortune());
            holder.userAgeTv.setBackgroundResource(R.drawable.male_age_bg);
        }else{
            XiuxiuUserInfoResult.setCharmValue(holder.vipGradeTv, getItem(position).getCharm());
            holder.userAgeTv.setBackgroundResource(R.drawable.female_age_bg);
        }
        ImageLoader.getInstance().displayImage(getItem(position).getPic(), holder.headImg);

        try {
            ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + getItem(position).getPics().get(0),
                    holder.headImg);
        }catch (Exception e){
        }

        holder.userAgeTv.setText(getItem(position).getAgeVale());

        //播放语音
        final String voice = getItem(position).getVoice();
        if(TextUtils.isEmpty(voice)){
            holder.listItemPlay.setVisibility(View.GONE);
        }else{
            holder.listItemPlay.setVisibility(View.VISIBLE);
            holder.listItemPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(voice)) {
                        VoicePlayManager.getInstance().release();
                        VoicePlayManager.getInstance().init(MainActivity.getInstance(), (ImageView) v,
                                R.drawable.list_item_stop, R.drawable.list_item_play);
                        VoicePlayManager.getInstance().play(HttpUrlManager.QI_NIU_HOST + voice);
                    }else{
                        ToastUtil.showMessage(MainActivity.getInstance(),"该用户没有语音信息.");
                    }
                }
            });
        }
        try {
            String timeTxt = DateUtils.getTextByTime(
                    getContext(),
                    getItem(position).getActive_time(),
                    R.string.date_fromate_anecdote);
            holder.onlineTimeTv.setText(timeTxt);
        }catch (Exception e){}
        /*
        holder.headImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startVoiceCall(getItem(position).getXiuxiu_id());
            }
        });
        */
        return convertView;
    }

    /**
     * 拨打语音电话
     */
    protected void startVoiceCall(String toChatUsername) {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(MainActivity.getInstance(), R.string.not_connect_to_server, 0).show();
        } else {
            MainActivity.getInstance().startActivity(new Intent(MainActivity.getInstance(),
                    VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
        }
    }


    FragmentActivity mAc;

    public void setActivity(FragmentActivity ac){
        mAc = ac;
    }

    @Override
    public void onClick(View v) {
        /*
        XiuxiuUserInfoResult xiuxiuUser = getItem((Integer)v.getTag());
        ChatPage.startActivity(mAc, xiuxiuUser.getXiuxiu_id(), xiuxiuUser.getXiuxiu_name());
        ChatNickNameAndAvatarBean info = new ChatNickNameAndAvatarBean();
        info.setAvatar("");
        info.setXiuxiu_id(xiuxiuUser.getXiuxiu_id());
        info.setNickName(xiuxiuUser.getXiuxiu_name());
        ChatNickNameAndAvatarCacheManager.getInstance().add(info);
        */
    }

    private class Holder{
        private TextView userNameTv;
        private TextView signTv;
        private ImageView vipGradeTv;
        private ImageView headImg;
        private TextView onlineTimeTv;
        private ImageView listItemPlay;
        private TextView userAgeTv;
    }
}
