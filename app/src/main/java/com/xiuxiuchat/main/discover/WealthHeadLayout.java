package com.xiuxiuchat.main.discover;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiuchat.R;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuPerson;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.main.MainActivity;
import com.xiuxiuchat.user.PersonDetailActivity;

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

    private List<XiuxiuUserInfoResult> mData;

    public void setData(List<XiuxiuUserInfoResult> list){
        mData = list;
        int currentPosition = 0;
        for(XiuxiuUserInfoResult xiuxiuperson:list){
            setItem(xiuxiuperson,currentPosition);
            currentPosition ++;
        }
    }

    private XiuxiuUserInfoResult mFirstData,mSecondData,mThirdData;

    public void setItem(XiuxiuUserInfoResult xiuxiuPerson,int position){
        switch (position){
            case 0:
                mFirstData = xiuxiuPerson;
                userName1.setText(xiuxiuPerson.getXiuxiu_name());
                XiuxiuPerson.setWealthValue(wealth1, xiuxiuPerson.getCharm());
                ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + xiuxiuPerson.getPic(), headImg1);
                findViewById(R.id.first_viewgroup).setVisibility(View.VISIBLE);
                break;
            case 1:
                mSecondData = xiuxiuPerson;
                userName2.setText(xiuxiuPerson.getXiuxiu_name());
                XiuxiuPerson.setWealthValue(wealth2, xiuxiuPerson.getCharm());
                ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + xiuxiuPerson.getPic(), headImg2);
                findViewById(R.id.second_viewgroup).setVisibility(View.VISIBLE);
                break;
            case 2:
                mThirdData = xiuxiuPerson;
                userName3.setText(xiuxiuPerson.getXiuxiu_name());
                XiuxiuPerson.setWealthValue(wealth3, xiuxiuPerson.getCharm());
                ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + xiuxiuPerson.getPic(), headImg3);
                findViewById(R.id.thrid_viewgroup).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        XiuxiuUserInfoResult xiuxiuUser = null;
        if(v == headImg1){
            xiuxiuUser = mFirstData;
        }else if(v == headImg2){
            xiuxiuUser = mSecondData;
        }else if(v == headImg3){
            xiuxiuUser = mThirdData;
        }
        if(xiuxiuUser==null){
            return;
        }
        try{
            PersonDetailActivity.startActivity(MainActivity.getInstance(), xiuxiuUser.getXiuxiu_id(), false);
        }catch (Exception e){}
    }
}
