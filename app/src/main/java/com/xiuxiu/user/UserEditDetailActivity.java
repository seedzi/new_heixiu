package com.xiuxiu.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.user.login.LoginUserDataEditPage;
import com.xiuxiu.user.voice.VoiceIntroductionActivity;
import com.xiuxiu.user.voice.VoicePlayManager;
import com.xiuxiu.utils.FileUtils;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.UiUtil;
import com.xiuxiu.widget.city.CityDialog;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 用户资料修改页面
 * Created by huzhi on 16-5-15.
 */
public class UserEditDetailActivity extends BaseActivity implements View.OnClickListener{

    private static String TAG = "UserEditDetailActivity";

    public static void startActivity4Result(FragmentActivity ac,int requestCode){
        Intent intent = new Intent(ac,UserEditDetailActivity.class);
        ac.startActivityForResult(intent,requestCode);
    }

    private ViewGroup mLayout;
    private GridView mPhotoWall;

    private int mScreenWidth;
    private int mPhotoItemWidth;
    private int mPhotoItemHeight;

    private TextView mSignTv;
    private TextView mTitleTv;
    private TextView mAgeTv;
    private TextView mCityTv;
    private TextView mNickNameTv;

    private PhotoAdpater mPhotoAdpater;
    /**昵称*/
    private String mNickName;
    /**语音文件的本地路径*/
    private String mVoicPath;
    /**声音的key*/
    private String mVoiceKey;
    /**图像的keys*/
    private List<UserEditDetailUploadManager.FileBean> mImgFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        mScreenWidth = ScreenUtils.getScreenWidth(getApplication());
        setContentView(R.layout.activity_user_edit_detail_page);
        setupViews();
        initData();
    }

    private void setupViews(){
        mLayout = (ViewGroup)findViewById(R.id.root_layout);
        mPhotoWall = (GridView) UiUtil.findViewById(mLayout, R.id.photo_wall);
        //设置照片墙大小
        int width = mScreenWidth - ScreenUtils.dip2px(getApplicationContext(),14);
        mPhotoItemWidth = (width - ScreenUtils.dip2px(getApplicationContext(),12))/4;
        mPhotoItemHeight = mPhotoItemWidth;
        int height = mPhotoItemHeight*2 + ScreenUtils.dip2px(getApplicationContext(),4);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width,height);
        ll.setMargins(ScreenUtils.dip2px(getApplicationContext(), 7), 0, ScreenUtils.dip2px(getApplicationContext(), 7), 0);
        mPhotoWall.setLayoutParams(ll);
        mPhotoWall.setHorizontalSpacing(4);
        mPhotoWall.setVerticalSpacing(4);

        mPhotoAdpater = new PhotoAdpater();
        mPhotoWall.setAdapter(mPhotoAdpater);

        mSignTv = (TextView) findViewById(R.id.user_sign_value);
        findViewById(R.id.signature_layout).setOnClickListener(this);

        findViewById(R.id.city_layout).setOnClickListener(this);
        findViewById(R.id.age_layout).setOnClickListener(this);
        findViewById(R.id.ok).setOnClickListener(this);
        findViewById(R.id.voice_layout).setOnClickListener(this);
        findViewById(R.id.nick_name_layout).setOnClickListener(this);
        findViewById(R.id.yuyin_bt).setOnClickListener(this);

        mTitleTv = (TextView) findViewById(R.id.title);
        mAgeTv = (TextView) findViewById(R.id.age_value);
        mCityTv = (TextView)findViewById(R.id.city_value);
        mNickNameTv = (TextView) findViewById(R.id.nickname_value);

        VoicePlayManager.getInstance().init(this,(ImageView)findViewById(R.id.yuyin_bt),
                R.drawable.list_item_stop,R.drawable.list_item_play);

        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initData(){
        mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
        mTitleTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mAgeTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getAge()));
        mCityTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getCity()));
        mNickNameTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mBrithdayS = XiuxiuUserInfoResult.getInstance().getBirthday();
        android.util.Log.d("123456", "mBrithdayS = " + mBrithdayS);
        try {
            if (!TextUtils.isEmpty(mBrithdayS)) {
                int year = Integer.valueOf(mBrithdayS.substring(0, 4));
                int month = Integer.valueOf(mBrithdayS.substring(4, 6));
                int day = Integer.valueOf(mBrithdayS.substring(6, 8));

                mYear = year;
                mMonth = month;
                mDay = day;
                android.util.Log.d("123456", "year = " + year + ",month = " + month + ",day = " + day);
                android.util.Log.d("123456", "mBrithdayS = " + mBrithdayS);
            }
        }catch (Exception e){

        }



        mImgFiles = new ArrayList<UserEditDetailUploadManager.FileBean>();
        if(XiuxiuUserInfoResult.getInstance().getPics()!=null) {
            for (String pic : XiuxiuUserInfoResult.getInstance().getPics()) {
                UserEditDetailUploadManager.FileBean bean = new UserEditDetailUploadManager.FileBean();
                bean.key = pic;
                mImgFiles.add(bean);
            }
        }
        mVoiceKey = XiuxiuUserInfoResult.getInstance().getVoice();
        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getVoice())){
            findViewById(R.id.yuyin_bt).setVisibility(View.GONE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.yuyin_txt_no)).setText("点击上传语音介绍");
        }else{
            findViewById(R.id.yuyin_bt).setVisibility(View.VISIBLE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getVoice())
                && TextUtils.isEmpty(mVoicPath)){
            findViewById(R.id.yuyin_bt).setVisibility(View.GONE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.yuyin_txt_no)).setText("点击上传语音介绍");
        }else{
            findViewById(R.id.yuyin_bt).setVisibility(View.VISIBLE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.voice_layout:
                VoiceIntroductionActivity.startActivity(UserEditDetailActivity.this);
                break;
            case R.id.signature_layout:
                UserTxtEditActivity.startActivity(UserEditDetailActivity.this,
                        URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
                break;
            case R.id.city_layout:
                showCityDialog();
                break;
            case R.id.age_layout:
                showAgeDialog();
                break;
            case R.id.ok:
                updateUserData();
                break;
            case R.id.nick_name_layout:
                UserTxtEditActivity.startActivity(UserEditDetailActivity.this,
                        mNickNameTv.getText().toString(),UserTxtEditActivity.REQUEST_CODE_2);
                break;
            case R.id.yuyin_bt:
                if(VoicePlayManager.getInstance().isPlaying()){
                    VoicePlayManager.getInstance().pause();
                }else{
                    if(TextUtils.isEmpty(mVoicPath)) {
                        VoicePlayManager.getInstance().play(XiuxiuUserInfoResult.getUrlVoice4Qiniu(XiuxiuUserInfoResult.getInstance().getVoice()));
                    }else{
                        VoicePlayManager.getInstance().play(mVoicPath);
                    }
                }
                break;
        }
    }

    // ===========================================================================================
    // 年纪
    // ===========================================================================================
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mBrithdayS = "";
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog mDatePickerDialog;
    private void showAgeDialog(){
        android.util.Log.d("123456","mYear = " + mYear + ",mMonth = " + mMonth + ",mDay = " + mDay);
        if(mDatePickerDialog==null) {
            mDatePickerDialog = new DatePickerDialog(UserEditDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    android.util.Log.d("123456", "year = " + year
                            + ",month = " + month + ",day = " + day);
                    month = month + 1;
                    mYear = year;
                    mMonth = month;
                    mDay = day;
                    mAgeTv.setText("" + (calendar.get(Calendar.YEAR) - mYear));

                    String monthS = mMonth + "";
                    String dayS = "" + mDay;
                    if (String.valueOf(mMonth).length() == 1) {
                        monthS = "0" + mMonth;
                    }
                    if (String.valueOf(mDay).length() == 1) {
                        dayS = "0" + mDay;
                    } else if (String.valueOf(mDay).length() == 0) {
                        dayS = "01";
                    }
                    mBrithdayS = String.valueOf(mYear) + monthS + dayS;
                    android.util.Log.d("123456", "mBrithdayS = " + mBrithdayS);
                }
            }, mYear, mMonth - 1, mDay);
        }
        //calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
             //   calendar.get(Calendar.DAY_OF_MONTH));
        /*
        if(mYear!=0 && mDay!=0){
            android.util.Log.d("aaaa","mYear = " + mYear + ", mMonth = " + mMonth + ", mDay = " + mDay);
            datePickerDialog.updateDate(mYear,mMonth,mDay);
        }*/
        mDatePickerDialog.show();
    }

    // ===========================================================================================
    // 城市
    // ===========================================================================================
    private void showCityDialog(){
        new CityDialog(UserEditDetailActivity.this,new CityDialog.OnDateSetListener(){
            @Override
            public void onDateSet(String city) {
                mCityTv.setText(city);
            }
        }).show();
    }

    /**
     * 照片数
     */
    private List<String> mData = new ArrayList<String>();

    private static int RESULT_LOAD_IMAGE = 101;

    /**
     * 照片墙
     */
    private class PhotoAdpater extends BaseAdapter {

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            if(mImgFiles == null){
                return 1;
            }
            if(mImgFiles.size() == 8){
                return mImgFiles.size();
            }
            return mImgFiles.size() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridView.LayoutParams gl = null;
            if(convertView == null){
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.photo_wall_layout,null);
                gl = new GridView.LayoutParams(mPhotoItemWidth,mPhotoItemHeight);
                convertView.setLayoutParams(gl);
            }
            ((ImageView)convertView.findViewById(R.id.img)).setImageDrawable(new ColorDrawable(Color.parseColor("#a6a6a6")));
            if(position == mImgFiles.size()){//最后一个item
                convertView.findViewById(R.id.img).setVisibility(View.GONE);
                convertView.findViewById(R.id.bt).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                });
            }else{
                convertView.findViewById(R.id.img).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.bt).setVisibility(View.GONE);
                convertView.findViewById(R.id.img).setTag(position);
                convertView.findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteItemDialog((Integer) v.getTag());
                    }
                });
                if(TextUtils.isEmpty(mImgFiles.get(position).localPath)){
                    ImageLoader.getInstance().displayImage(
                        HttpUrlManager.QI_NIU_HOST + mImgFiles.get(position).key,
                        (ImageView) convertView.findViewById(R.id.img));
                }else{
                    ImageLoader.getInstance().displayImage(
                            "file:/" + mImgFiles.get(position).localPath,
                            (ImageView) convertView.findViewById(R.id.img));
                }

            }
            convertView.setBackgroundColor(Color.parseColor("#a6a6a6"));
            return convertView;
        }
    }


    // ============================================================================================
    // 网络层
    // ============================================================================================
    private ProgressDialog mProgressDialog;

    private void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(this, "提示", "正在上传中...");
    }

    private void dismisslProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }


    private void updateUserData() {
        showProgressDialog();

        UserEditDetailUploadManager.FileBean bean = null;
        List<UserEditDetailUploadManager.FileBean> beanList
                = new ArrayList<UserEditDetailUploadManager.FileBean>();
        for(UserEditDetailUploadManager.FileBean fb : mImgFiles){
            android.util.Log.d(TAG,"fb.localPath = " +fb.localPath);
            android.util.Log.d(TAG,"fb.key = " +fb.key);
            if(!TextUtils.isEmpty(fb.localPath)){
                bean = new UserEditDetailUploadManager.FileBean();
                bean.key = fb.key;
                bean.localPath = fb.localPath;
                beanList.add(bean);
            }
        }

        UserEditDetailUploadManager.getInstance().excute(beanList, mVoicPath, new UserEditDetailUploadManager.CallBack() {
            @Override
            public void onSuccess() {
                android.util.Log.d(TAG, "getUpdateUrl() = " + getUpdateUrl());
                XiuxiuApplication.getInstance().getQueue()
                        .add(new StringRequest(getUpdateUrl(), mRefreshListener, mRefreshErroListener));
                android.util.Log.d(TAG, "七牛成功");
            }

            @Override
            public void onFailure() {
                dismisslProgressDialog();
                android.util.Log.d(TAG, "七牛失败");
                ToastUtil.showMessage(UserEditDetailActivity.this, "七牛修改失败!");
            }
        });

    }

    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            android.util.Log.d(TAG, "response = " + response);
            if(res.isSuccess()){
                android.util.Log.d(TAG,"mSignTv.getText().toString() = " + mSignTv.getText().toString());
                android.util.Log.d(TAG,"mAgeTv.getText().toString() = " + mAgeTv.getText().toString());
                android.util.Log.d(TAG,"mCityTv.getText().toString() = " + mCityTv.getText().toString());
                android.util.Log.d(TAG,"mBrithdayS = " + mBrithdayS);
                android.util.Log.d(TAG,"voicUrl = " + mVoiceKey);
                android.util.Log.d(TAG,"pciKey = " + UserEditDetailUploadManager.getKeys(mImgFiles));

                XiuxiuUserInfoResult.getInstance().setSign(mSignTv.getText().toString());
                XiuxiuUserInfoResult.getInstance().setAge(mAgeTv.getText().toString());
                XiuxiuUserInfoResult.getInstance().setCity(mCityTv.getText().toString());
                XiuxiuUserInfoResult.getInstance().setBirthday(mBrithdayS);
                XiuxiuUserInfoResult.getInstance().setVoice(mVoiceKey);
                XiuxiuUserInfoResult.getInstance().setXiuxiu_name(mNickName);
                XiuxiuUserInfoResult.getInstance().setPic(UserEditDetailUploadManager.getKeys(mImgFiles));
                XiuxiuUserInfoResult.save(XiuxiuUserInfoResult.getInstance());
                android.util.Log.d("123456","XiuxiuUserInfoResult.getInstance() brithday = " +
                        XiuxiuUserInfoResult.getInstance().getBirthday());
            }
            dismisslProgressDialog();
            ToastUtil.showMessage(UserEditDetailActivity.this, "服务器资料更新成功!");
            setResult(RESULT_OK);
            finish();
            android.util.Log.d(TAG, "服务器成功");
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismisslProgressDialog();
            ToastUtil.showMessage(UserEditDetailActivity.this, "服务器修改失败!");
            android.util.Log.d(TAG, "服务器失败");
        }
    };

    private String getUpdateUrl() {
        String picKey = UserEditDetailUploadManager.getKeys(mImgFiles);
        android.util.Log.d(TAG,"pic = " + picKey);
        android.util.Log.d(TAG,"voicUrl = " + mVoiceKey);
        Uri.Builder builder =  Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.UPDATE_USER_INFO)
                .appendQueryParameter("cookie",XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter(XiuxiuUserInfoResult.XIUXIU_ID, XiuxiuLoginResult.getInstance().getXiuxiu_id());
        if(!TextUtils.isEmpty(mSignTv.getText().toString())){
            builder.appendQueryParameter(XiuxiuUserInfoResult.SIGN, mSignTv.getText().toString());
        }
        if(!TextUtils.isEmpty(mAgeTv.getText().toString())){
            builder.appendQueryParameter(XiuxiuUserInfoResult.AGE, mAgeTv.getText().toString());
        }
        if(!TextUtils.isEmpty(mCityTv.getText().toString())){
            builder.appendQueryParameter(XiuxiuUserInfoResult.CITY, mCityTv.getText().toString());
        }
        if(!TextUtils.isEmpty(mBrithdayS)){
            builder.appendQueryParameter(XiuxiuUserInfoResult.BIRTHDAY, mBrithdayS);
        }
        if(!TextUtils.isEmpty(picKey)){
            builder.appendQueryParameter(XiuxiuUserInfoResult.PIC, picKey);
        }
        if(!TextUtils.isEmpty(mVoiceKey)){
            builder.appendQueryParameter(XiuxiuUserInfoResult.VOICE, mVoiceKey);
        }
        if(!TextUtils.isEmpty(mNickName)){
            builder.appendQueryParameter(XiuxiuUserInfoResult.XIUXIU_NAME, mNickName);
        }
        return builder.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            UserEditDetailUploadManager.FileBean fileBean = new UserEditDetailUploadManager.FileBean();
            if(mImgFiles!=null && mImgFiles.size()>0) {
                try {
                    android.util.Log.d(TAG,"mImgFiles.get(mImgFiles.size() - 1).key =" + mImgFiles.get(mImgFiles.size() - 1).key);
                    long lastKey = Long.valueOf(FileUtils.getFileName(mImgFiles.get(mImgFiles.size() - 1).key));
                    fileBean.key = (lastKey + 1) + "." + FileUtils.getFileSuffix(picturePath);
                }catch (Exception e){
                    fileBean.key = XiuxiuUserInfoResult.getInstance().getXiuxiu_id() + "00001" + "." + FileUtils.getFileSuffix(picturePath);
                }
            }else{
                fileBean.key = XiuxiuUserInfoResult.getInstance().getXiuxiu_id() + "00001" + "." + FileUtils.getFileSuffix(picturePath);
            }
            fileBean.localPath = picturePath;
            mImgFiles.add(fileBean);
            mPhotoAdpater.notifyDataSetChanged();

        }else if(requestCode == VoiceIntroductionActivity.REQUEST_CODE){ //语音
            if(resultCode==RESULT_OK) {
                mVoicPath = data.getStringExtra("data");
                mVoiceKey = FileUploadManager.getInstance().generateUserVoiceFileName(FileUtils.getFileSuffix(mVoicPath));
                android.util.Log.d(TAG,"mVoiceKey = " + mVoiceKey);
            }
        }else if(requestCode == UserTxtEditActivity.REQUEST_CODE_2){
            if(resultCode==RESULT_OK) {
                if(data.getStringExtra(UserTxtEditActivity.TXT_KEY)!=null){
                    mNickNameTv.setText(data.getStringExtra(UserTxtEditActivity.TXT_KEY));
                    mNickName = data.getStringExtra(UserTxtEditActivity.TXT_KEY);
                }
            }
        }if(requestCode == UserTxtEditActivity.REQUEST_CODE && resultCode==RESULT_OK){
            Bundle bundle =data.getExtras();
            if(bundle.getString(UserTxtEditActivity.TXT_KEY)!=null){
                mSignTv.setText(bundle.getString(UserTxtEditActivity.TXT_KEY));
            }
        }
    }


    // ============================================================================================
    // 删除对话框
    // ============================================================================================
    private void showDeleteItemDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserEditDetailActivity.this);
        builder.setMessage("确认删除这张照片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mImgFiles.remove(position);
                mPhotoAdpater.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
