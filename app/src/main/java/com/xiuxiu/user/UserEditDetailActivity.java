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
import com.xiuxiu.user.voice.VoiceIntroductionActivity;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.UiUtil;
import com.xiuxiu.widget.CityDialog;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by huzhi on 16-5-15.
 */
public class UserEditDetailActivity extends FragmentActivity implements View.OnClickListener{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,UserEditDetailActivity.class);
        context.startActivity(intent);
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

    private PhotoAdpater mPhotoAdpater;

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


        mTitleTv = (TextView) findViewById(R.id.title);
        mAgeTv = (TextView) findViewById(R.id.age_value);
        mCityTv = (TextView)findViewById(R.id.city_value);
    }

    private void initData(){
        mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
        mTitleTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mAgeTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getAge()));
        mCityTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getCity()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.voice_layout:
                VoiceIntroductionActivity.startActivity(UserEditDetailActivity.this);
                break;
            case R.id.signature_layout:
                UserTxtEditActivity.startActivity(UserEditDetailActivity.this);
                break;
            case R.id.city_layout:
                showCityDialog();
                break;
            case R.id.age_layout:
                showAgeDialog();
                break;
            case R.id.ok:
                showProgressDialog();
                updateUserData();
                break;
        }
    }

    // ===========================================================================================
    // 年纪
    // ===========================================================================================
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar calendar = Calendar.getInstance();
    private void showAgeDialog(){
        new DatePickerDialog(UserEditDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;

                mAgeTv.setText("" + (calendar.get(Calendar.YEAR) - mYear));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
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
            return mData.size() + 1;
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
            if(position == mData.size()){//最后一个item
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
                convertView.findViewById(R.id.img).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        showDeleteItemDialog((Integer)v.getTag());
                    }
                });
                ImageLoader.getInstance().displayImage("file:/" + mData.get(position), (ImageView) convertView.findViewById(R.id.img));
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

    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res.isSuccess()){
                XiuxiuUserInfoResult.getInstance().setSign(mSignTv.getText().toString());
                XiuxiuUserInfoResult.getInstance().setAge(mAgeTv.getText().toString());
                XiuxiuUserInfoResult.getInstance().setCity(mCityTv.getText().toString());
            }
            dismisslProgressDialog();
            ToastUtil.showMessage(UserEditDetailActivity.this, "资料更新成功!");
            finish();
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismisslProgressDialog();
            ToastUtil.showMessage(UserEditDetailActivity.this, "修改失败!");
        }
    };

    /**
     * 登录
     */
    private void updateUserData() {
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getUpdateUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getUpdateUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.UPDATE_USER_INFO)
                .appendQueryParameter(XiuxiuUserInfoResult.XIUXIU_ID,XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter(XiuxiuUserInfoResult.SIGN,mSignTv.getText().toString())
                .appendQueryParameter(XiuxiuUserInfoResult.AGE,mAgeTv.getText().toString())
                .appendQueryParameter(XiuxiuUserInfoResult.CITY,mCityTv.getText().toString())
                .build().toString();
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
            mData.add(picturePath);
            android.util.Log.d("ccc", "picturePath = " + picturePath);
            mPhotoAdpater.notifyDataSetChanged();
            // String picturePath contains the path of selected Image
        }else if(resultCode==RESULT_OK){
            Bundle bundle =data.getExtras();
            if(bundle.getString(XiuxiuUserInfoResult.SIGN)!=null){
                mSignTv.setText(bundle.getString(XiuxiuUserInfoResult.SIGN));
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
                mData.remove(position);
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
