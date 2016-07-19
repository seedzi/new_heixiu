package com.xiuxiu.main.discover;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuPerson;

import java.util.List;

/**
 * Created by huzhi on 16-5-26.
 */
public class WealthHeadLayout extends RelativeLayout implements View.OnClickListener{

    public WealthHeadLayout(Context context) {
        super(context);
        init(context);
    }

    public WealthHeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WealthHeadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private FragmentActivity mAc;

    public void setActivity(FragmentActivity ac){
        mAc = ac;
    }

    private void init(Context context){

        inflate(context, R.layout.discover_wealth_head_layout, this);

        userName1 = (TextView) findViewById(R.id.user_name_1);
        userName2 = (TextView) findViewById(R.id.user_name_2);
        userName3 = (TextView) findViewById(R.id.user_name_3);

        headImg1 = (ImageView) findViewById(R.id.head_img_1);
        headImg2 = (ImageView) findViewById(R.id.head_img_2);
        headImg3 = (ImageView) findViewById(R.id.head_img_3);

        wealth1 = (ImageView) findViewById(R.id.wealth_1);
        wealth2 = (ImageView) findViewById(R.id.wealth_2);
        wealth3 = (ImageView) findViewById(R.id.wealth_3);

        headImg1.setOnClickListener(this);
        headImg2.setOnClickListener(this);
        headImg3.setOnClickListener(this);
    }

    private TextView userName1;

    private ImageView headImg1;

    private ImageView wealth1;

    private TextView userName2;

    private ImageView headImg2;

    private ImageView wealth2;

    private TextView userName3;

    private ImageView headImg3;

    private ImageView wealth3;

    private List<XiuxiuPerson> mData;

    public void setData(List<XiuxiuPerson> list){
        mData = list;
        int currentPosition = 0;
        for(XiuxiuPerson xiuxiuperson:list){
            setItem(xiuxiuperson,currentPosition);
            currentPosition ++;
        }
    }

    private void setItem(XiuxiuPerson xiuxiuPerson,int position){
        switch (position){
            case 0:
                userName1.setText(xiuxiuPerson.getXiuxiu_name());
                XiuxiuPerson.setWealthValue(wealth1, xiuxiuPerson.getCharm());
                break;
            case 1:
                userName2.setText(xiuxiuPerson.getXiuxiu_name());
                XiuxiuPerson.setWealthValue(wealth2, xiuxiuPerson.getCharm());
                break;
            case 2:
                userName3.setText(xiuxiuPerson.getXiuxiu_name());
                XiuxiuPerson.setWealthValue(wealth3, xiuxiuPerson.getCharm());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int position = 0;
        if(v == headImg1){
            position = 0;
        }else if(v == headImg2){
            position = 1;
        }else if(v == headImg3){
            position = 2;
        }
        /*
        try {
            XiuxiuPerson xiuxiuUser = mData.get(position);
            ChatPage.startActivity(mAc, xiuxiuUser.getXiuxiu_id(), xiuxiuUser.getXiuxiu_name());
            ChatNickNameAndAvatarBean info = new ChatNickNameAndAvatarBean();
            info.setAvatar("");
            info.setXiuxiu_id(xiuxiuUser.getXiuxiu_id());
            info.setNickName(xiuxiuUser.getXiuxiu_name());
            ChatNickNameAndAvatarCacheManager.getInstance().add(info);
        } catch (Exception e){
        }
        */
    }
}
