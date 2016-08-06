package com.xiuxiu.api;

import android.text.TextUtils;
import android.widget.ImageView;

import com.hyphenate.easeui.domain.EaseUser;
import com.xiuxiu.R;
import com.xiuxiu.SharePreferenceWrap;
import com.xiuxiu.utils.DateUtils;
import com.xiuxiu.utils.FileUtils;

import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huzhi on 16-5-25.
 */
public class XiuxiuUserInfoResult {

    private static String TAG = XiuxiuUserInfoResult.class.getSimpleName();

    public static final String SHARE_PREFERENCE_NAME = "xiuxiuuerinforesult";

    public static final String XIUXIU_ID = "xiuxiu_id";

    public static final String XIUXIU_NAME = "xiuxiu_name";

    public static final String SIGN = "sign";

    public static final String AGE = "age";

    public static final String CITY = "city";

    public static final String CHARM = "charm";

    public static final String BIRTHDAY = "birthday";

    public static final String PIC = "pic";

    public static final String SEX = "sex";

    public static final String VOICE = "voice";

    public static final String GET_GIFT = "get_gift";

    public static final String SPAM = "spam";

    private static XiuxiuUserInfoResult mInstance;

    public XiuxiuUserInfoResult(){}

    public static XiuxiuUserInfoResult getInstance(){
        if(mInstance == null ){
            mInstance = getFromShareP();
        }
        return mInstance;
    }

    private String sign;

    private String age;

    private String sex;

    private String city;

    private String xiuxiu_id;

    private String xiuxiu_name;

    private String weixin_token;

    private String birthday;

    private String pic;

    private int charm = -1;

    private int fortune;

    private String voice;

    private long active_time;

    private String get_gift;

    private String spam;

    public String getSpam() {
        return spam;
    }

    public void setSpam(String spam) {
        this.spam = spam;
    }

    public int getCharm() {
        return charm;
    }

    public int getFortune() {
        return fortune;
    }

    public int getFortuneValue() {
        if(fortune<=0){
            return 1;
        }
        return fortune;
    }

    public void setCharm(int charm) {
        this.charm = charm;
    }

    public int getCharmValue() {
        if(charm<=0){
            return 1;
        }
        return charm;
    }

    public void setFortune(int fortune) {
        this.fortune = fortune;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setXiuxiu_id(String xiuxiu_id) {
        this.xiuxiu_id = xiuxiu_id;
    }

    public void setXiuxiu_name(String xiuxiu_name) {
        this.xiuxiu_name = xiuxiu_name;
    }

    public void setWeixin_token(String weixin_token) {
        this.weixin_token = weixin_token;
    }

    public String getSign() {
        return sign;
    }

    public String getAge() {
        return age;
    }

    public String getAgeVale() {
        if(TextUtils.isEmpty(age)){
            return "0";
        }
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getCity() {
        return city;
    }

    public String getXiuxiu_id() {
        return xiuxiu_id;
    }

    public String getXiuxiu_name() {
        return xiuxiu_name;
    }

    public String getWeixin_token() {
        return weixin_token;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getVoice(){
        return voice;
    }

    public void setActive_time(long active_time) {
        this.active_time = active_time;
    }

    public long getActive_time() {
        return active_time;
    }

    public String getGet_gift() {
        return get_gift;
    }

    public void setGet_gift(String get_gift) {
        this.get_gift = get_gift;
    }

    /**
     * 是否是男性
     * @return
     */
    public static boolean isMale(String sex){
        if("male".equals(sex)){
            return true;
        }
        return false;
    }


    public static String getUrlVoice4Qiniu(String voice) {
        if(!TextUtils.isEmpty(voice) && !"null".equals(voice) && !voice.contains("http")){
            return HttpUrlManager.QI_NIU_HOST + voice;
        }else if("null".equals(voice)){
            return null;
        }
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }


    public List<String> getPics(){
        List<String> picList = new ArrayList<String>();
        if(TextUtils.isEmpty(pic)){
            return null;
        }
        String[] pics = pic.split(",");
        for (String pic : pics) {
            if ("null".equals(pic)||TextUtils.isEmpty(pic)) {
            } else {
                picList.add(pic);
            }
        }
        return picList;
    }

    public List<Gift> getGiftList(){
        try {
            List<Gift> list = new ArrayList<>();
            if(TextUtils.isEmpty(get_gift)){
                return null;
            }else{
                android.util.Log.d("hehe","get_gift  = " + get_gift);
                String[] strs = get_gift.split(",");
                int i = 0;
                for(String str: strs){
                    Gift gift = new Gift();
                    String[] cStrs = null;
                    String s = "";
                    if( i==0 && i==strs.length-1){
                        s = str.substring(1,str.length()-1);
                    }else if(i==strs.length-1){
                        s = str.substring(0,str.length()-1);
                    }else if(i==0){
                        s = str.substring(1,str.length());
                    }else{
                        s = str;
                    }
                    cStrs = s.split(":");
                    gift.type = Integer.valueOf(cStrs[0].replaceAll("\"", ""));
                    gift.size = Integer.valueOf(cStrs[1].replaceAll("\"", ""));
                    i++;
                    list.add(gift);
                }
                return list;
            }
        }catch (Exception e){
            return null;
        }

    }

    /**
     * @param user
     */
    public static void save(XiuxiuUserInfoResult user){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        if(!TextUtils.isEmpty(user.getXiuxiu_id())) {
            sharePreferenceWrap.putString(XIUXIU_ID, user.getXiuxiu_id());
        }
        if(!TextUtils.isEmpty(user.getXiuxiu_name())) {
            sharePreferenceWrap.putString(XIUXIU_NAME, user.getXiuxiu_name());
        }
        if(!TextUtils.isEmpty(user.getSign())) {
            sharePreferenceWrap.putString(SIGN, user.getSign());
        }
        if(!TextUtils.isEmpty(user.getAge())) {
            sharePreferenceWrap.putString(AGE, user.getAge());
        }
        if(!TextUtils.isEmpty(user.getCity())) {
            sharePreferenceWrap.putString(CITY, user.getCity());
        }
        if(!TextUtils.isEmpty(user.getPic())) {
            sharePreferenceWrap.putString(PIC, user.getPic());
        }
        if(user.getCharm()!=-1) {
            sharePreferenceWrap.putInt(CHARM, user.getCharm());
        }
        if(!TextUtils.isEmpty(user.getBirthday())) {
            sharePreferenceWrap.putString(BIRTHDAY, user.getBirthday());
//            sharePreferenceWrap.putString(BIRTHDAY, DateUtils.time2Date(user.getBirthday()));
        }
        if(!TextUtils.isEmpty(user.getVoice())){
            sharePreferenceWrap.putString(VOICE, user.getVoice());
        }
        if(!TextUtils.isEmpty(user.getSex())){
            sharePreferenceWrap.putString(SEX, user.getSex());
        }
        if(!TextUtils.isEmpty(user.getGet_gift())){
            sharePreferenceWrap.putString(GET_GIFT, user.getGet_gift());
        }
        if(!TextUtils.isEmpty(user.getSpam())){
            sharePreferenceWrap.putString(SPAM, user.getSpam());
        }
        reset();
    }

    /**
     * @return
     */
    private static XiuxiuUserInfoResult getFromShareP(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        XiuxiuUserInfoResult user = new XiuxiuUserInfoResult();
        user.setXiuxiu_id(sharePreferenceWrap.getString(XIUXIU_ID, ""));
        user.setXiuxiu_name(sharePreferenceWrap.getString(XIUXIU_NAME, ""));
        user.setSign(sharePreferenceWrap.getString(SIGN, ""));
        user.setCity(sharePreferenceWrap.getString(CITY, ""));
        user.setAge(sharePreferenceWrap.getString(AGE, ""));
        user.setCharm(sharePreferenceWrap.getInt(CHARM, 1));
        user.setPic(sharePreferenceWrap.getString(PIC, ""));
        user.setBirthday(sharePreferenceWrap.getString(BIRTHDAY, ""));
        user.setVoice(sharePreferenceWrap.getString(VOICE, ""));
        user.setSex(sharePreferenceWrap.getString(SEX, ""));
        user.setGet_gift(sharePreferenceWrap.getString(GET_GIFT, ""));
        user.setSpam(sharePreferenceWrap.getString(SPAM,""));
        return user;
    }

    public static void reset(){
        mInstance = getFromShareP();
    }

    public static void clear(){
        mInstance = null;
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, "");
        sharePreferenceWrap.putString(XIUXIU_NAME, "");
        sharePreferenceWrap.putString(SIGN, "");
        sharePreferenceWrap.putString(AGE, "");
        sharePreferenceWrap.putString(CITY, "");
        sharePreferenceWrap.putInt(CHARM, 1);
        sharePreferenceWrap.putString(VOICE, "");
        sharePreferenceWrap.putString(PIC, "");
        sharePreferenceWrap.putString(BIRTHDAY, "");
        sharePreferenceWrap.putString(SEX, "");
        sharePreferenceWrap.putString(GET_GIFT, "");
        sharePreferenceWrap.putString(SPAM, "");
    }

    public static String getKeyByPic(String pic){
        if(TextUtils.isEmpty(pic)){
            return pic;
        }else{
            return FileUtils.getFileName(pic);
        }
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



    /**
     * XiuxiuUserInfoResult转换成EaseUser
     */
    public static EaseUser toEaseUser(XiuxiuUserInfoResult result){
        if(result == null){
            return null;
        }
        android.util.Log.d(TAG, "result.pic= " + result.getPic());
        EaseUser easeUser = new EaseUser(result.getXiuxiu_id());
        easeUser.setNick(result.getXiuxiu_name());
        easeUser.setAge(result.getAge());
        easeUser.setCharm(result.getCharm());
        easeUser.setFortune(result.getFortune());
        easeUser.setSign(result.getSign());
        easeUser.setVoice(result.getVoice());
        easeUser.setSex(result.getSex());
        easeUser.setOnlineTime(result.getActive_time());
        try {
            easeUser.setAvatar(HttpUrlManager.QI_NIU_HOST + result.getPics().get(0));
        } catch (Exception e){}
        return easeUser;
    }

    @Override
    public String toString() {
        return "XiuxiuUserInfoResult{" +
                "sign='" + sign + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", city='" + city + '\'' +
                ", xiuxiu_id='" + xiuxiu_id + '\'' +
                ", xiuxiu_name='" + xiuxiu_name + '\'' +
                ", weixin_token='" + weixin_token + '\'' +
                ", birthday='" + birthday + '\'' +
                ", pic='" + pic + '\'' +
                ", charm=" + charm +
                ", fortune=" + fortune +
                ", voice='" + voice + '\'' +
                ", active_time=" + active_time +
                ", get_gift=" + get_gift +
                ", spam=" + spam +
                '}';
    }

    public static class Gift{
        public int type;
        public int size;
    }
}