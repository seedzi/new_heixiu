package com.xiuxiu.user.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.user.UserTxtEditActivity;

import java.util.Calendar;

/**
 * Created by huzhi on 16-5-30.
 */
public class LoginUserDataPage extends FragmentActivity implements View.OnClickListener{

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,LoginUserDataPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    private static int RESULT_LOAD_IMAGE = 101;

    private ImageView mHeadView;

    private TextView mNickName;

    private String mFilePath;

    private TextView mBrithDayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_data_layout);
        setupViews();
    }

    private void setupViews(){
        mHeadView = (ImageView) findViewById(R.id.head_img);
        mHeadView.setOnClickListener(this);
        mNickName = (TextView) findViewById(R.id.nick_name_value);
        findViewById(R.id.nick_name_layout).setOnClickListener(this);
        mBrithDayView = (TextView) findViewById(R.id.brithday_value);
        findViewById(R.id.brithday_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.head_img:
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.nick_name_layout:
                UserTxtEditActivity.startActivity(LoginUserDataPage.this,mNickName.getText().toString());
                break;
            case R.id.brithday_layout:
                showAgeDialog();
                break;
        }
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
            mFilePath = picturePath;
            ImageLoader.getInstance().displayImage("file:/" + mFilePath, mHeadView);
        }else if(resultCode==RESULT_OK){
            Bundle bundle =data.getExtras();
            if(bundle.getString(XiuxiuUserInfoResult.SIGN)!=null){
                mNickName.setText(bundle.getString(XiuxiuUserInfoResult.SIGN));
            }
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
        new DatePickerDialog(LoginUserDataPage.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                mBrithDayView.setText("" + (calendar.get(Calendar.YEAR) - mYear));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
