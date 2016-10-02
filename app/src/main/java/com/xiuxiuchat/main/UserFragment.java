package com.xiuxiuchat.main;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.fb.FeedbackAgent;
import com.xiuxiuchat.R;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.api.XiuxiuWalletCoinResult;
import com.xiuxiuchat.easeim.ImHelper;
import com.xiuxiuchat.main.discover.OnLineListManager;
import com.xiuxiuchat.qupai.QuPaiManager;
import com.xiuxiuchat.server.UpdateActiveUserManager;
import com.xiuxiuchat.user.CharmLevelActivity;
import com.xiuxiuchat.user.SetupPage;
import com.xiuxiuchat.user.UserDetailActivity;
import com.xiuxiuchat.user.WalletActivity;
import com.xiuxiuchat.user.WealthLevelActivity;
import com.xiuxiuchat.user.XiuxiuSettingsPage;
import com.xiuxiuchat.user.invitation.InvitationPage;
import com.xiuxiuchat.user.login.LoginPage;
import com.xiuxiuchat.utils.ToastUtil;
import com.xiuxiuchat.utils.UiUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-3-21.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

    private static String TAG = "UserFragment";

    private ViewGroup mRootView;

    private ViewGroup mCharmValueLayout;

    private ViewGroup mWealthValueLayout;

    private ViewGroup mXiuxiuSettingsLayout;

    private ViewGroup mWalletLayout;

    private ViewGroup mInviteFriendsLayout;

    private ViewGroup mFeedbackLayout;

    private ViewGroup mSetUpLayout;

    private ViewGroup mLoginoutLayout;


    private TextView mNameTv;

    private TextView mSignTv;

    private ImageView mHeadIv;


    /**性别设置*/
    private boolean isFemale = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView ==null){
            android.util.Log.d(TAG,"mRootView ==null");
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, null);
            setUpView();
            refreshData();
        } else {
            android.util.Log.d(TAG,"mRootView !=null");
            try {
                ((ViewGroup)mRootView.getParent()).removeView(mRootView);
            } catch (Exception e) {
            }
        }
        return mRootView;
    }

    private void setUpView(){
        //魅力值
        mCharmValueLayout = (ViewGroup) mRootView.findViewById(R.id.charm_value);
        UiUtil.findTextViewById(mCharmValueLayout, R.id.item_name).setText("魅力值");
        UiUtil.findImageViewById(mCharmValueLayout, R.id.img).setImageResource(R.drawable.user_icon_charm);
        UiUtil.findTextViewById(mCharmValueLayout, R.id.tag_txt).setText("等级 2");
        UiUtil.findTextViewById(mCharmValueLayout, R.id.tag_txt).setTextColor(Color.parseColor("#00b8d0"));
        mCharmValueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharmLevelActivity.startActivity(getActivity());
            }
        });

        //财富值
        mWealthValueLayout = (ViewGroup) mRootView.findViewById(R.id.wealth_value);
        UiUtil.findTextViewById(mWealthValueLayout, R.id.item_name).setText("财富值");
        UiUtil.findImageViewById(mWealthValueLayout, R.id.img).setImageResource(R.drawable.user_icon_wealth);
        UiUtil.findTextViewById(mWealthValueLayout, R.id.tag_txt).setText("等级 2");
        UiUtil.findTextViewById(mWealthValueLayout, R.id.tag_txt).setTextColor(Color.parseColor("#00b8d0"));
        mWealthValueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WealthLevelActivity.startActivity(getActivity());
            }
        });

        //咻羞设置
        mXiuxiuSettingsLayout = (ViewGroup) mRootView.findViewById(R.id.xiuxiu_settings);
        UiUtil.findTextViewById(mXiuxiuSettingsLayout, R.id.item_name).setText("咻羞设置");
        UiUtil.findImageViewById(mXiuxiuSettingsLayout, R.id.img).setImageResource(R.drawable.user_icon_wallet);
        mXiuxiuSettingsLayout.setOnClickListener(this);

        //钱包
        mWalletLayout = (ViewGroup) mRootView.findViewById(R.id.wallet);
        UiUtil.findImageViewById(mWalletLayout, R.id.img).setImageResource(R.drawable.user_icon_wallet);
        mWalletLayout.setOnClickListener(this);
        UiUtil.findTextViewById(mWalletLayout, R.id.tag_txt).setTextColor(Color.parseColor("#00b8d0"));

        //邀请朋友
        mInviteFriendsLayout = (ViewGroup) mRootView.findViewById(R.id.invite_friends);
        UiUtil.findTextViewById(mInviteFriendsLayout, R.id.item_name).setText("邀请朋友");
        UiUtil.findImageViewById(mInviteFriendsLayout, R.id.img).setImageResource(R.drawable.user_icon_add_friends);
        UiUtil.findTextViewById(mInviteFriendsLayout, R.id.tag_txt).setText("邀请返现金");
        mInviteFriendsLayout.setOnClickListener(this);
        //邀请朋友去掉了
        mInviteFriendsLayout.setVisibility(View.GONE);
        mRootView.findViewById(R.id.invite_friends_line);

        //意见反馈
        mSetUpLayout = (ViewGroup) mRootView.findViewById(R.id.feedback);
        UiUtil.findTextViewById(mSetUpLayout, R.id.item_name).setText("意见反馈");
        UiUtil.findImageViewById(mSetUpLayout, R.id.img).setImageResource(R.drawable.user_icon_feedback);
        mSetUpLayout.setOnClickListener(this);

        //设置
        mFeedbackLayout = (ViewGroup) mRootView.findViewById(R.id.setup);
        UiUtil.findTextViewById(mFeedbackLayout, R.id.item_name).setText("设置");
        UiUtil.findImageViewById(mFeedbackLayout, R.id.img).setImageResource(R.drawable.user_icon_settings);
        mFeedbackLayout.setOnClickListener(this);

        //退出
        mLoginoutLayout = (ViewGroup) mRootView.findViewById(R.id.login_out);
        UiUtil.findTextViewById(mLoginoutLayout, R.id.item_name).setText("退出当前账号");
        mLoginoutLayout.setOnClickListener(this);


        UiUtil.findImageViewById(mRootView, R.id.user_detail).setOnClickListener(this);
        UiUtil.findViewById(mRootView,R.id.login_out).setOnClickListener(this);


        if(isFemale){//女性用户
            mWealthValueLayout.setVisibility(View.GONE);
            UiUtil.findViewById(mRootView, R.id.wealth_value_line).setVisibility(View.GONE);
        }else{//男性用户
            mCharmValueLayout.setVisibility(View.GONE);
            mXiuxiuSettingsLayout.setVisibility(View.GONE);
            UiUtil.findViewById(mRootView, R.id.charm_value_line).setVisibility(View.GONE);
            UiUtil.findViewById(mRootView, R.id.xiuxiu_settings_line).setVisibility(View.GONE);
        }

        mNameTv = UiUtil.findTextViewById(mRootView, R.id.user_name);
        mSignTv = UiUtil.findTextViewById(mRootView, R.id.description);
        mHeadIv = UiUtil.findImageViewById(mRootView, R.id.head_img);
        mRootView.findViewById(R.id.top_layout).setOnClickListener(this);
    }

    private void refreshData(){
        if(!TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getXiuxiu_name())) {
            mNameTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        }else{
            mNameTv.setText(XiuxiuLoginResult.getInstance().getXiuxiu_id());
        }
        if(!TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getSign())) {
            mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
        }
        try {
            String headUrl = HttpUrlManager.QI_NIU_HOST + XiuxiuUserInfoResult.getInstance().getPics().get(0);
            android.util.Log.d(TAG,"headUrl = " + headUrl);
            mHeadIv.setImageResource(R.drawable.head_default);
            ImageLoader.getInstance().displayImage(headUrl, mHeadIv);
        }catch (Exception e){
        }

        isFemale = !XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex());
        android.util.Log.d(TAG,"isFemale = " + isFemale);
        if(isFemale){//女性用户
            mWealthValueLayout.setVisibility(View.GONE);
            UiUtil.findViewById(mRootView, R.id.wealth_value_line).setVisibility(View.GONE);
            mCharmValueLayout.setVisibility(View.VISIBLE);
            UiUtil.findViewById(mRootView, R.id.charm_value_line).setVisibility(View.VISIBLE);
            UiUtil.findTextViewById(mCharmValueLayout, R.id.tag_txt).setText("等级 " + XiuxiuUserInfoResult.getInstance().getCharmValue());
        }else{//男性用户
            mCharmValueLayout.setVisibility(View.GONE);
            UiUtil.findViewById(mRootView, R.id.charm_value_line).setVisibility(View.GONE);
            mWealthValueLayout.setVisibility(View.VISIBLE);
            UiUtil.findViewById(mRootView, R.id.wealth_value_line).setVisibility(View.VISIBLE);
            UiUtil.findTextViewById(mWealthValueLayout, R.id.tag_txt).setText("等级 " + XiuxiuUserInfoResult.getInstance().getFortuneValue());
            mXiuxiuSettingsLayout.setVisibility(View.GONE);
        }
        UiUtil.findTextViewById(mWalletLayout, R.id.item_name).setText(isFemale ? "钱包" : "充值");
        XiuxiuWalletCoinResult xiuxiu = XiuxiuWalletCoinResult.getFromShareP();
        int size = xiuxiu.getRecharge_coin() + xiuxiu.getEarn_coin();
        UiUtil.findTextViewById(mWalletLayout, R.id.tag_txt).setText(isFemale ? "余额: "+ size + "羞币" : "一元购100咻羞币,限购1次");
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.top_layout:
            case R.id.user_detail:
                UserDetailActivity.startActivity(getActivity());
                break;
            case R.id.invite_friends:
                InvitationPage.startActivity(getActivity());
                break;
            case R.id.setup:
                SetupPage.startActivity(getActivity());
                break;
            case R.id.login_out:
                logout();
                break;
            case R.id.wallet:
                WalletActivity.startActivity(getActivity());
                break;
            case R.id.xiuxiu_settings:
                if(isFemale){
                    if(XiuxiuUserInfoResult.getInstance().getCharmValue()>=3){
                        XiuxiuSettingsPage.startActivity(getActivity());
                    }else{
                        ToastUtil.showMessage(getActivity(),"魅力等级达到３级才可以设置.");
                    }
                }
                break;
            case R.id.feedback:
                FeedbackAgent agent = new FeedbackAgent(getActivity());
                agent.startFeedbackActivity();
                break;
        }
    }


    void logout() {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st = getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		ImHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        //清除数据
                        XiuxiuLoginResult.clear();
                        XiuxiuUserInfoResult.clear();
                        OnLineListManager.getInstance(getActivity()).destory();
                        TabsFragmentManager.getInstance().clear();
                        QuPaiManager.getInstance().clearAccessToken();
                        ((MainActivity) getActivity()).finish();
                        LoginPage.startActivity(getActivity());
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        UpdateActiveUserManager.getInstance().stop();
	}
}
