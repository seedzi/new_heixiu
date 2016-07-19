package com.xiuxiu.xiuxiutask;

import android.content.Intent;
import android.text.TextUtils;

import com.xiuxiu.api.XiuxiuLoginResult;

import org.json.JSONObject;

/**
 * Created by huzhi on 16-7-16.
 * 咻咻任务Task bean
 */
public class XiuxiuTaskBean {

    public static String TITLE_KEY = "title";

    public static String CONTENT_KEY = "content";

    public static String XIUXIUB_KEY = "xiuxiub";

    public static String TYPE_KEY = "type";

    public static final String FILE_PATH_KEY = "file_path";

    public static final int TYPE_ASK_XIUXIU = 101;

    public static final int TYPE_IMAGE_XIUXIU = 102;

    public static final int TYPE_VIDEO_XIUXIU = 103;

    public static final int TYPE_VOICE_XIUXIU = 104;


    public String title;

    public String content;

    public String xiuxiub;

    public String type;

    public String path;

    public static String createJsonString(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return "";
        }
        try {
            JSONObject jsonObject = new JSONObject();
            if(TextUtils.isEmpty(xiuxiuTaskBean.title)){
                jsonObject.put(TITLE_KEY,"");
            }else{
                jsonObject.put(TITLE_KEY,xiuxiuTaskBean.title);
            }

            if(TextUtils.isEmpty(xiuxiuTaskBean.content)){
                jsonObject.put(CONTENT_KEY,"");
            }else{
                jsonObject.put(CONTENT_KEY,xiuxiuTaskBean.content);
            }

            if(TextUtils.isEmpty(xiuxiuTaskBean.type)){
                jsonObject.put(TYPE_KEY,"");
            }else{
                jsonObject.put(TYPE_KEY,xiuxiuTaskBean.type);
            }

            jsonObject.put(XIUXIUB_KEY,xiuxiuTaskBean.xiuxiub);

            return jsonObject.toString();

        } catch (Exception e){

        }
        return "";
    }

    public static XiuxiuTaskBean parse2Bean(String str){
        if(TextUtils.isEmpty(str)){
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(str);
            XiuxiuTaskBean xiuxiuTaskBean = new XiuxiuTaskBean();
            xiuxiuTaskBean.title = jsonObject.optString(TITLE_KEY);
            xiuxiuTaskBean.content = jsonObject.optString(CONTENT_KEY);
            xiuxiuTaskBean.xiuxiub = jsonObject.optString(XIUXIUB_KEY);
            xiuxiuTaskBean.type = jsonObject.optString(TYPE_KEY);
            return xiuxiuTaskBean;
        } catch (Exception e){
        }
        return null;
    }

    public static XiuxiuTaskBean createBean(String title,String content,String xiuxiub){
        XiuxiuTaskBean xiuxiuTaskBean = new XiuxiuTaskBean();
        xiuxiuTaskBean.xiuxiub = xiuxiub;
        xiuxiuTaskBean.title = title;
        xiuxiuTaskBean.content = content;
        return xiuxiuTaskBean;
    }

    public static XiuxiuTaskBean createBean(String title,String content,String xiuxiub,String type){
        XiuxiuTaskBean xiuxiuTaskBean = new XiuxiuTaskBean();
        xiuxiuTaskBean.xiuxiub = xiuxiub;
        xiuxiuTaskBean.title = title;
        xiuxiuTaskBean.content = content;
        xiuxiuTaskBean.type = type;
        return xiuxiuTaskBean;
    }


    public static XiuxiuTaskBean createBean(Intent data){
        XiuxiuTaskBean xiuxiuTaskBean = new XiuxiuTaskBean();
        xiuxiuTaskBean.xiuxiub = data.getStringExtra(XiuxiuTaskBean.XIUXIUB_KEY);
        xiuxiuTaskBean.title = data.getStringExtra(XiuxiuTaskBean.TITLE_KEY);
        xiuxiuTaskBean.content = data.getStringExtra(XiuxiuTaskBean.CONTENT_KEY);
        xiuxiuTaskBean.type = data.getStringExtra(XiuxiuTaskBean.TYPE_KEY);
        xiuxiuTaskBean.path = data.getStringExtra(XiuxiuTaskBean.FILE_PATH_KEY);
        return xiuxiuTaskBean;
    }

    /**
     * 生成xiuxiu 请求id :当前用户id + "_"　+ 当前时间mills
     * @return
     */
    public static String createXiuxiuMsgId(){
        String askId = XiuxiuLoginResult.getInstance().getXiuxiu_id() + "_" + System.currentTimeMillis();
        return askId;
    }
}
