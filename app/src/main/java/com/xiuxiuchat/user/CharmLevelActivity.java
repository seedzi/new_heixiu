package com.xiuxiuchat.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiuxiuchat.R;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.base.BaseActivity;
import com.xiuxiuchat.widget.VipGradeView;

/**
 * Created by huzhi on 16-4-8.
 */
public class CharmLevelActivity extends BaseActivity implements View.OnClickListener{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,CharmLevelActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_charm_level_page);
        initData();
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initData(){
        initItemLayout((ViewGroup)findViewById(R.id.grade_1),0);
        initItemLayout((ViewGroup) findViewById(R.id.grade_2), 1);
        initItemLayout((ViewGroup) findViewById(R.id.grade_3), 2);
        initItemLayout((ViewGroup) findViewById(R.id.grade_4), 3);
        initItemLayout((ViewGroup) findViewById(R.id.grade_5), 4);
        initItemLayout((ViewGroup) findViewById(R.id.grade_6), 5);
        initItemLayout((ViewGroup) findViewById(R.id.grade_7), 6);

        int charm = XiuxiuUserInfoResult.getInstance().getCharmValue();
        VipGradeView vipGradeView = (VipGradeView) findViewById(R.id.vip_grade_view);
        vipGradeView.setGrade(charm);

        ((TextView)findViewById(R.id.v_grade)).setText("V"+charm);
    }

    private void initItemLayout(ViewGroup layout,int position){
        switch (position){
            case 0:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 1);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("0-99分");
                break;
            case 1:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 2);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("100-299分");
                break;
            case 2:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 3);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("300-999分");
                break;
            case 3:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 4);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("1000-9999分");
                break;
            case 4:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 5);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("10000-99999分");
                break;
            case 5:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 6);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("100000-999999分");
                break;
            case 6:
                XiuxiuUserInfoResult.setCharmValue((ImageView)layout.findViewById(R.id.icon), 7);
                ((TextView)layout.findViewById(R.id.charm_value)).setText("1000000分以上");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
        }
    }
}

