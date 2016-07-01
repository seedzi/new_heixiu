package com.xiuxiu.main.discover;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.bean.ChatNickNameAndAvatarBean;
import com.xiuxiu.call.CallManager;
import com.xiuxiu.call.voice.CallVoicePage;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.easeim.ChatNickNameAndAvatarCacheManager;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.PersonDetailActivity;
import com.xiuxiu.user.voice.VoicePlayManager;
import com.xiuxiu.utils.DateUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.UiUtil;

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

        holder.headImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CallManager.getInstance(MainActivity.getInstance()).call2Person(getItem(position).getXiuxiu_id());
                CallVoicePage.startActivity(MainActivity.getInstance(), "", "", true);
            }
        });

        return convertView;
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
