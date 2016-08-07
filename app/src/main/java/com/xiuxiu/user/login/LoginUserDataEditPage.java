package com.xiuxiu.user.login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.UserEditDetailUploadManager;
import com.xiuxiu.user.UserTxtEditActivity;
import com.xiuxiu.utils.FileUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.widget.city.CityDialog;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 首次登录后的第一次修改页面
 * Created by huzhi on 16-5-30.
 */
public class LoginUserDataEditPage extends BaseActivity implements View.OnClickListener{

    private static String TAG = "LoginUserDataEditPage";

    public static final int REQUEST_CODE = 1001;

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,LoginUserDataEditPage.class);
        intent.putExtra(KEY_NICK_NAME,"");
        intent.putExtra(KEY_HEAD_IMG_PATH,"");
        intent.putExtra(KEY_CITY,"");
        intent.putExtra(KEY_SEX,"male");
        ac.startActivityForResult(intent, REQUEST_CODE);
    }


    public static void startActivity(FragmentActivity ac,String nickname,String headimgpath,String city,String sex){
        Intent intent = new Intent(ac,LoginUserDataEditPage.class);
        intent.putExtra(KEY_NICK_NAME,nickname);
        intent.putExtra(KEY_HEAD_IMG_PATH,headimgpath);
        intent.putExtra(KEY_CITY,city);
        intent.putExtra(KEY_SEX, sex);
        ac.startActivityForResult(intent, REQUEST_CODE);
    }

    public static String KEY_NICK_NAME = "nickname";

    public static String KEY_HEAD_IMG_PATH = "headimgpath";

    public static String KEY_CITY = "city";

    public static String KEY_SEX = "sex";

    private static int RESULT_LOAD_IMAGE = 101;

    private ImageView mHeadView;

    private EditText mNickName;

    private TextView mCityTextView;

    private String mUploadFilePath;

    private TextView mBrithDayView;

    private RadioGroup mSexRadioGroup;

    private boolean isImgFromLocal = false;

    private String mSex = "male";

    private View mCommitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_data_layout);
        setupViews();
        initData();
    }

    private void setupViews(){
        mHeadView = (ImageView) findViewById(R.id.head_img);
        mHeadView.setOnClickListener(this);
        findViewById(R.id.head_img_txt).setOnClickListener(this);
        mNickName = (EditText) findViewById(R.id.nick_name_edit);
        mBrithDayView = (TextView) findViewById(R.id.brithday_value);
        findViewById(R.id.brithday_layout).setOnClickListener(this);
        mCommitView = findViewById(R.id.commit);
        mCommitView.setOnClickListener(this);
        mSexRadioGroup = (RadioGroup) findViewById(R.id.sex_radio_group);
        mSexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bt_male) {
                    mSex = "male";
                } else if (checkedId == R.id.bt_female) {
                    mSex = "female";
                } else {
                    mSex = "unknow";
                }
                checkCommitBtStatus();
            }
        });
        mCityTextView = (TextView) findViewById(R.id.city_value);
        findViewById(R.id.city_layout).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        mNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCommitBtStatus();
            }
        });
        mBrithDayView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                checkCommitBtStatus();
            }
        });
        mCityTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                checkCommitBtStatus();
            }
        });
        checkCommitBtStatus();
    }

    private void checkCommitBtStatus(){
        mCommitView.setBackgroundResource(R.drawable.wallet_bt_bg);
        if(TextUtils.isEmpty(mNickName.getText().toString())){
            mCommitView.setBackgroundResource(R.drawable.wallet_bt_bg_disable);
        }
        if(TextUtils.isEmpty(mBrithDayView.getText().toString())){
            mCommitView.setBackgroundResource(R.drawable.wallet_bt_bg_disable);
        }
        if(TextUtils.isEmpty(mCityTextView.getText().toString())){
            mCommitView.setBackgroundResource(R.drawable.wallet_bt_bg_disable);
        }
        if(TextUtils.isEmpty(mUploadFilePath)){
            mCommitView.setBackgroundResource(R.drawable.wallet_bt_bg_disable);
        }
        if(TextUtils.isEmpty(mSex)){
            mCommitView.setBackgroundResource(R.drawable.wallet_bt_bg_disable);
        }
        android.util.Log.d("123456","mUploadFilePath = " + mUploadFilePath);
    }


    private void initData(){
        String nickname = getIntent().getStringExtra(KEY_NICK_NAME);
        if(!TextUtils.isEmpty(nickname)){
            mNickName.setText(nickname);
            mNickName.setSelection(nickname.length());
        }
        String city = getIntent().getStringExtra(KEY_CITY);
        if(!TextUtils.isEmpty(city)) {
            mCityTextView.setText(city);
        }
        String headurl = getIntent().getStringExtra(KEY_HEAD_IMG_PATH);
        ImageLoader.getInstance().displayImage(headurl,mHeadView);
        mUploadFilePath = headurl;
        String sex = getIntent().getStringExtra(KEY_SEX);
        android.util.Log.d(TAG,"sex = " + sex);
        if("1".equals(sex)){
            ((RadioButton)(mSexRadioGroup.getChildAt(0))).performClick();
            mSex = "male";
        }else{
            ((RadioButton)(mSexRadioGroup.getChildAt(1))).performClick();
            mSex = "female";
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.head_img_txt:
            case R.id.head_img:
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.brithday_layout:
                showAgeDialog();
                break;
            case R.id.city_layout:
                showCityDialog();
                break;
            case R.id.commit:
                commit();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void commit(){
        if(TextUtils.isEmpty(mNickName.getText().toString())){
            ToastUtil.showMessage(LoginUserDataEditPage.this,"昵称不能为空！");
            return;
        }
        if(TextUtils.isEmpty(mCityTextView.getText().toString())){
            ToastUtil.showMessage(LoginUserDataEditPage.this,"所在城市不能为空！");
            return;
        }
        if(TextUtils.isEmpty(mSex)||"unknow".equals(mSex)){
            ToastUtil.showMessage(LoginUserDataEditPage.this,"必须选择性别！");
            return;
        }
        if(TextUtils.isEmpty(mUploadFilePath)){
            ToastUtil.showMessage(LoginUserDataEditPage.this,"必须选择头像！");
            return;
        }
        if(mAge==-1){
            ToastUtil.showMessage(LoginUserDataEditPage.this,"请选择出生日期！");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginUserDataEditPage.this);
        String sex = "女性";
        if(mSex.equals("male")){
            sex = "男性";
        }
        builder.setMessage("性别选择为" + sex + "，性别选择后不可修改，请确定选择正确");

                builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showProgressDialog();
                updateUserData();
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
            mUploadFilePath = picturePath;
            ImageLoader.getInstance().displayImage("file:/" + mUploadFilePath, mHeadView);
            isImgFromLocal = true;
            checkCommitBtStatus();
        }

    }

    // ===========================================================================================
    // 年纪
    // ===========================================================================================
    private String mYear = "1990";
    private String mMonth = "1";
    private String mDay = "1";
    private Calendar calendar = Calendar.getInstance();
    private int mAge = -1;

    private DatePickerDialog mDatePickerDialog;
    private void showAgeDialog(){
        if(mDatePickerDialog == null) {
            mDatePickerDialog = new DatePickerDialog(LoginUserDataEditPage.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    month = month +1;
                    mYear = year + "";
                    mMonth = month  + "";
                    mDay = day + "";
                    android.util.Log.d(TAG,"mYear = " + mYear + ",mMonth = " + mMonth + ",mDay = " + mDay);
                    if (mMonth.length() == 1) {
                        mMonth = "0" + mMonth;
                    }
                    if (mDay.length() == 1) {
                        mDay = "0" + mDay;
                    } else if (String.valueOf(mDay).length() == 0) {
                        mDay = "01";
                    }
                    mBrithDayView.setText(mYear + "-" + mMonth + "-" + mDay);
                    mAge = calendar.get(Calendar.YEAR) - year;
                    if (mAge < 0) {
                        mAge = 0;
                    }
                }
            }, 1990, 0,
                    1);
        }
        mDatePickerDialog.show();
    }

    // ===========================================================================================
    // 城市
    // ===========================================================================================
    private void showCityDialog(){
        new CityDialog(LoginUserDataEditPage.this,new CityDialog.OnDateSetListener(){
            @Override
            public void onDateSet(String city) {
                mCityTextView.setText(city);
            }
        }).show();
    }

    // ============================================================================================
    // 提交数据
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

    private List<UserEditDetailUploadManager.FileBean> mFileBeans = null;
    private void updateUserData() {
        mFileBeans = new ArrayList<UserEditDetailUploadManager.FileBean>();
        if(!isImgFromLocal) {
            android.util.Log.d(TAG,"mUploadFilePath = " + mUploadFilePath);
            ImageLoader.getInstance().loadImage(mUploadFilePath, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    dismisslProgressDialog();
                }
                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    dismisslProgressDialog();
                    ToastUtil.showMessage(LoginUserDataEditPage.this,"修改失败!");
                }
                @Override
                public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(bitmap == null){
                                return;
                            }
                            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/" + XiuxiuLoginResult.getInstance().getXiuxiu_id() + ".jpg";
                            if(FileUtils.saveBitmap2file(bitmap, filePath)){
                                UserEditDetailUploadManager.FileBean fileBean = new UserEditDetailUploadManager.FileBean();
                                fileBean.key = XiuxiuLoginResult.getInstance().getXiuxiu_id() + "00001" + ".jpg";
                                fileBean.localPath = filePath;
                                mFileBeans.add(fileBean);

                                UserEditDetailUploadManager.getInstance().excute(mFileBeans,null,new UserEditDetailUploadManager.CallBack(){
                                    @Override
                                    public void onSuccess() {
                                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                XiuxiuApplication.getInstance().getQueue()
                                                        .add(new StringRequest(getUpdateUrl(), mRefreshListener, mRefreshErroListener));
                                            }
                                        });
                                    }
                                    @Override
                                    public void onFailure() {
                                        android.util.Log.d(TAG,"onFailure()");
                                        android.util.Log.d("AAAAA","00002");
                                        ToastUtil.showMessage(LoginUserDataEditPage.this,"修改失败!");
                                        dismisslProgressDialog();
                                    }
                                });

                            }else{
                            }
                        }
                    }).start();
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }else{
            if(!TextUtils.isEmpty(mUploadFilePath)){
                UserEditDetailUploadManager.FileBean bean = new UserEditDetailUploadManager.FileBean();
                bean.key = XiuxiuLoginResult.getInstance().getXiuxiu_id() + "00001" + "." + FileUtils.getFileSuffix(mUploadFilePath);
                bean.localPath = mUploadFilePath;
                android.util.Log.d(TAG,"bean.key = " + bean.key );
                android.util.Log.d(TAG,"bean.localPath = " + bean.localPath);
                mFileBeans.add(bean);
                UserEditDetailUploadManager.getInstance().excute(mFileBeans, null, new UserEditDetailUploadManager.CallBack() {
                    @Override
                    public void onSuccess() {
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuApplication.getInstance().getQueue()
                                        .add(new StringRequest(getUpdateUrl(), mRefreshListener, mRefreshErroListener));
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.showMessage(LoginUserDataEditPage.this, "修改失败!");
                        dismisslProgressDialog();
                    }
                });
            }
        }
    }


    private String getUpdateUrl() {
        Uri.Builder builder = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.UPDATE_USER_INFO)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter(XiuxiuUserInfoResult.XIUXIU_ID, XiuxiuLoginResult.getInstance().getXiuxiu_id());
        if(!TextUtils.isEmpty(mNickName.getText().toString())){
            builder.appendQueryParameter(XiuxiuUserInfoResult.XIUXIU_NAME, mNickName.getText().toString());
        }
        if(!TextUtils.isEmpty(mCityTextView.getText().toString())){
            builder.appendQueryParameter(XiuxiuUserInfoResult.CITY, mCityTextView.getText().toString());
        }
        if(!TextUtils.isEmpty(mFileBeans.get(0).key)){
            builder.appendQueryParameter(XiuxiuUserInfoResult.PIC, mFileBeans.get(0).key);
        }
        if(!TextUtils.isEmpty(mAge+"")){
            builder.appendQueryParameter(XiuxiuUserInfoResult.AGE, mAge+"");
        }
        if(!TextUtils.isEmpty(mBrithDayView.getText().toString())){
            builder.appendQueryParameter(XiuxiuUserInfoResult.BIRTHDAY, mBrithDayView.getText().toString().replaceAll("-", ""));
        }
        if(!TextUtils.isEmpty(mSex)){
            builder.appendQueryParameter(XiuxiuUserInfoResult.SEX, mSex);
        }
        String url = builder.toString();
        android.util.Log.d(TAG,"url = " + url);
        return url;
    }


    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            android.util.Log.d(TAG,"response = " + response);
            if(res.isSuccess()){
                XiuxiuUserInfoResult info = new XiuxiuUserInfoResult();

                info.setXiuxiu_id(XiuxiuLoginResult.getInstance().getXiuxiu_id());
                if(!TextUtils.isEmpty(mNickName.getText().toString())) {
                    info.setXiuxiu_name(mNickName.getText().toString());
                }
                if(mFileBeans!=null &&mFileBeans.get(0)!=null&&!TextUtils.isEmpty(mFileBeans.get(0).key)) {
                    info.setPic(mFileBeans.get(0).key);
                }

                if(!TextUtils.isEmpty(mBrithDayView.getText().toString())) {
                    info.setBirthday(mBrithDayView.getText().toString().replaceAll("-",""));
                }
                if(!TextUtils.isEmpty(mCityTextView.getText().toString())) {
                    info.setCity(mCityTextView.getText().toString());
                }
                if(!TextUtils.isEmpty(mAge + "")){
                    info.setAge(mAge + "");
                }
                if(!TextUtils.isEmpty(mSex)) {
                    info.setSex(mSex);
                }
                XiuxiuUserInfoResult.save(info);
                dismisslProgressDialog();
                ToastUtil.showMessage(LoginUserDataEditPage.this, "资料更新成功!");
                setResult(RESULT_OK);
                finish();
            }else{
                dismisslProgressDialog();
                ToastUtil.showMessage(LoginUserDataEditPage.this, "修改失败!");
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismisslProgressDialog();
            ToastUtil.showMessage(LoginUserDataEditPage.this, "修改失败!");
        }
    };
}