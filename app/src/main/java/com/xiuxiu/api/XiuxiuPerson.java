package com.xiuxiu.api;

import android.widget.ImageView;

import com.xiuxiu.R;

/**
 * Created by huzhi on 16-5-15.
 */
public class XiuxiuPerson {

    private String xiuxiu_id;

    private String xiuxiu_name;

    private String weixin_token;

    private String sign;

    private int charm;

    public String getXiuxiu_id(){
        return xiuxiu_id;
    }

    public String getXiuxiu_name(){
        return xiuxiu_name;
    }

    public String getWeixin_token(){
        return weixin_token;
    }

    public String getSign() {
        return sign;
    }

    public int getCharm(){
        return charm;
    }

    public static void setCharmValue(ImageView iv,int charm){
        switch (charm){
            case 1:
                iv.setImageResource(R.drawable.charm_1);
                break;
            case 2:
                iv.setImageResource(R.drawable.charm_2);
                break;
            case 3:
                iv.setImageResource(R.drawable.charm_3);
                break;
            case 4:
                iv.setImageResource(R.drawable.charm_4);
                break;
            case 5:
                iv.setImageResource(R.drawable.charm_5);
                break;
            case 6:
                iv.setImageResource(R.drawable.charm_6);
                break;
            case 7:
                iv.setImageResource(R.drawable.charm_7);
                break;
            default:
                iv.setImageResource(R.drawable.charm_1);
                break;
        }
    }

    public static void setWealthValue(ImageView iv,int charm){
        switch (charm){
            case 1:
                iv.setImageResource(R.drawable.wealth_1);
                break;
            case 2:
                iv.setImageResource(R.drawable.wealth_2);
                break;
            case 3:
                iv.setImageResource(R.drawable.wealth_3);
                break;
            case 4:
                iv.setImageResource(R.drawable.wealth_4);
                break;
            case 5:
                iv.setImageResource(R.drawable.wealth_5);
                break;
            case 6:
                iv.setImageResource(R.drawable.wealth_6);
                break;
            case 7:
                iv.setImageResource(R.drawable.wealth_7);
                break;
            default:
                iv.setImageResource(R.drawable.wealth_1);
                break;
        }
    }
}
