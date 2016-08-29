package com.xiuxiuchat.xiuxiutask;

import android.text.TextUtils;

import com.xiuxiuchat.api.XiuxiuLoginResult;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by huzhi on 16-7-16.
 * 咻咻任务Task bean
 */
public class XiuxiuTaskBean implements Serializable{

    public static final String TAG = "XiuxiuTaskBean";

    public static String TITLE_KEY = "title";

    public static String CONTENT_KEY = "content";

    public static String XIUXIUB_KEY = "xiuxiub";

    public static String TYPE_KEY = "type";

    public static String IS_NAN_2_NV_KEY = "nan_2_nv";

    public static final String IMAGE_PATH_KEY = "image_path";

    public static final String VIDEO_PATH_KEY = "video_path";

    public static final String THUMB_PATH_KEY = "thumb_path";

    public static final String VIDEO_LENGTH_KEY = "video_length";

    //=================================================================================
    // 三种咻咻任务的type
    //=================================================================================
    public static final int TYPE_IMAGE_XIUXIU = 101;
    public static final int TYPE_VIDEO_XIUXIU = 102;
    public static final int TYPE_VOICE_XIUXIU = 103;

    //咻咻任务标题
    public String title;
    //咻咻任务描述
    public String content;
    //多少咻咻b
    public String xiuxiub;
    //咻咻任务类型
    public int type;
    //是否是男发给女的
    public boolean isNan2Nv;
    //图片路径
    public String imgPath;
    //视频路径　(可选)
    public String videoPath;
    //缩略图路径　(可选)
    public String thumbPath;
    //视频大小　(可选)
    public String videoLength;

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

            jsonObject.put(TYPE_KEY,xiuxiuTaskBean.type);
            jsonObject.put(XIUXIUB_KEY,xiuxiuTaskBean.xiuxiub);
            jsonObject.put(IS_NAN_2_NV_KEY,xiuxiuTaskBean.isNan2Nv);
            jsonObject.put(IMAGE_PATH_KEY,xiuxiuTaskBean.imgPath);
            jsonObject.put(VIDEO_PATH_KEY,xiuxiuTaskBean.videoPath);
            jsonObject.put(THUMB_PATH_KEY,xiuxiuTaskBean.thumbPath);
            jsonObject.put(VIDEO_LENGTH_KEY,xiuxiuTaskBean.videoLength);

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
            xiuxiuTaskBean.type = jsonObject.optInt(TYPE_KEY);
            xiuxiuTaskBean.isNan2Nv = jsonObject.optBoolean(IS_NAN_2_NV_KEY);
            xiuxiuTaskBean.imgPath = jsonObject.optString(IMAGE_PATH_KEY);
            xiuxiuTaskBean.videoLength =  jsonObject.optString(VIDEO_LENGTH_KEY);
            xiuxiuTaskBean.videoPath =  jsonObject.optString(VIDEO_PATH_KEY);
            xiuxiuTaskBean.thumbPath =  jsonObject.optString(THUMB_PATH_KEY);
            return xiuxiuTaskBean;
        } catch (Exception e){
            android.util.Log.d("12345","e = " + e.getMessage());
        }
        return null;
    }
    /*
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
        xiuxiuTaskBean.imgPath = data.getStringExtra(XiuxiuTaskBean.IMAGE_PATH_KEY);
        xiuxiuTaskBean.videoPath = data.getStringExtra(XiuxiuTaskBean.VIDEO_PATH_KEY);
        xiuxiuTaskBean.thumbPath = data.getStringExtra(XiuxiuTaskBean.THUMB_PATH_KEY);
        xiuxiuTaskBean.videoLength = data.getStringExtra(XiuxiuTaskBean.VIDEO_LENGTH_KEY);
        return xiuxiuTaskBean;
    }
    */


    /**
     * 生成xiuxiu 请求id :当前用户id + "_"　+ 当前时间mills
     * @return
     */
    public static String createXiuxiuMsgId(){
        String askId = XiuxiuLoginResult.getInstance().getXiuxiu_id() + "_" + System.currentTimeMillis();
        return askId;
    }

    // ===================================================================================================
    // 创建咻咻任务
    // ===================================================================================================
    /**
     * 创建咻咻语聊任务
     * @param isNan2Nv
     * @param xiuxiuTitle
     * @param content
     * @param xiuxiub
     * @param imgPath
     * @param type
     * @return
     */
    public static XiuxiuTaskBean createXiuxiuTaskBean(boolean isNan2Nv,String xiuxiuTitle,String content,String xiuxiub){
        return createXiuxiuTaskBean(isNan2Nv,xiuxiuTitle,content,xiuxiub,"","","","",XiuxiuTaskBean.TYPE_VOICE_XIUXIU);
    }
    /**
     * 创建咻咻图片任务
     * @param isNan2Nv
     * @param xiuxiuTitle
     * @param content
     * @param xiuxiub
     * @param imgPath
     * @param type
     * @return
     */
    public static XiuxiuTaskBean createXiuxiuTaskBean(boolean isNan2Nv,String xiuxiuTitle,String content,String xiuxiub,
                                                      String imgPath){
        return createXiuxiuTaskBean(isNan2Nv,xiuxiuTitle,content,xiuxiub,imgPath,"","","",XiuxiuTaskBean.TYPE_IMAGE_XIUXIU);
    }
    /**
     * 创建咻咻任务
     * @param isNan2Nv　
     * @param xiuxiuTitle
     * @param content
     * @param xiuxiub
     * @param imgPath
     * @param videoFile
     * @param thum
     * @param duration
     * @param type
     * @return
     */
    public static XiuxiuTaskBean createXiuxiuTaskBean(boolean isNan2Nv,String xiuxiuTitle,String content,String xiuxiub,
                                                      String imgPath,
                                                      String videoFile,String thum,String duration,
                                                      int type){
        XiuxiuTaskBean taskBean = new XiuxiuTaskBean();
        taskBean.title = xiuxiuTitle;
        taskBean.content = content;
        taskBean.xiuxiub = xiuxiub;
        taskBean.isNan2Nv = isNan2Nv;
        taskBean.type = type;

        switch (taskBean.type){
            case XiuxiuTaskBean.TYPE_IMAGE_XIUXIU:
                taskBean.imgPath = imgPath;
                break;
            case XiuxiuTaskBean.TYPE_VIDEO_XIUXIU:
                //视频相关数据
                taskBean.videoPath = videoFile;
                taskBean.thumbPath = thum;
                taskBean.videoLength = duration;
                break;
        }
        return taskBean;
    }

}
