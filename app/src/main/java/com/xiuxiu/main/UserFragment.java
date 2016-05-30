package com.xiuxiu.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.user.CharmLevelActivity;
import com.xiuxiu.user.SetupPage;
import com.xiuxiu.user.UserDetailActivity;
import com.xiuxiu.user.WalletActivity;
import com.xiuxiu.user.invitation.InvitationPage;
import com.xiuxiu.user.login.LoginPage;
import com.xiuxiu.utils.UiUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-3-21.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

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


    /**性别设置*/
    private boolean isFemale = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView ==null){
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, null);
            setUpView();
            refreshData();
        } else {
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
        UiUtil.findImageViewById(mCharmValueLayout, R.id.img).setImageResource(R.drawable.charm_icon);
        UiUtil.findTextViewById(mCharmValueLayout, R.id.tag_txt).setText("等级 2");
        mCharmValueLayout.setOnClickListener(this);

        //财富值
        mWealthValueLayout = (ViewGroup) mRootView.findViewById(R.id.wealth_value);
        UiUtil.findTextViewById(mWealthValueLayout, R.id.item_name).setText("财富值");
        UiUtil.findImageViewById(mWealthValueLayout, R.id.img).setImageResource(R.drawable.charm_icon);
        UiUtil.findTextViewById(mWealthValueLayout, R.id.tag_txt).setText("等级 2");
        mWealthValueLayout.setOnClickListener(this);

        //咻羞设置
        mXiuxiuSettingsLayout = (ViewGroup) mRootView.findViewById(R.id.xiuxiu_settings);
        UiUtil.findTextViewById(mXiuxiuSettingsLayout, R.id.item_name).setText("咻羞设置");
        UiUtil.findImageViewById(mXiuxiuSettingsLayout, R.id.img).setImageResource(R.drawable.charm_icon);
        mXiuxiuSettingsLayout.setOnClickListener(this);

        //钱包
        mWalletLayout = (ViewGroup) mRootView.findViewById(R.id.wallet);
        UiUtil.findTextViewById(mWalletLayout, R.id.item_name).setText(isFemale ? "钱包" : "充值");
        UiUtil.findImageViewById(mWalletLayout, R.id.img).setImageResource(R.drawable.wallet_icon);
        UiUtil.findTextViewById(mWalletLayout, R.id.tag_txt).setText(isFemale ? "余额:23114 羞币" : "一元购100咻羞币,限购1次");
        mWalletLayout.setOnClickListener(this);

        //邀请朋友
        mInviteFriendsLayout = (ViewGroup) mRootView.findViewById(R.id.invite_friends);
        UiUtil.findTextViewById(mInviteFriendsLayout, R.id.item_name).setText("邀请朋友");
        UiUtil.findImageViewById(mInviteFriendsLayout, R.id.img).setImageResource(R.drawable.add_friends_icon);
        UiUtil.findTextViewById(mInviteFriendsLayout, R.id.tag_txt).setText("邀请返现金");
        mInviteFriendsLayout.setOnClickListener(this);

        //意见反馈
        mSetUpLayout = (ViewGroup) mRootView.findViewById(R.id.feedback);
        UiUtil.findTextViewById(mSetUpLayout, R.id.item_name).setText("意见反馈");
        UiUtil.findImageViewById(mSetUpLayout, R.id.img).setImageResource(R.drawable.feedback_icon);
        mSetUpLayout.setOnClickListener(this);

        //设置
        mFeedbackLayout = (ViewGroup) mRootView.findViewById(R.id.setup);
        UiUtil.findTextViewById(mFeedbackLayout, R.id.item_name).setText("设置");
        UiUtil.findImageViewById(mFeedbackLayout, R.id.img).setImageResource(R.drawable.settings_icon);
        mFeedbackLayout.setOnClickListener(this);

        //设置
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

        mNameTv = UiUtil.findTextViewById(mRootView,R.id.user_name);
        mSignTv = UiUtil.findTextViewById(mRootView, R.id.description);
    }

    private void refreshData(){
        mNameTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));


        UiUtil.findTextViewById(mCharmValueLayout, R.id.tag_txt).setText("等级 " + XiuxiuUserInfoResult.getInstance().getCharm());
        XiuxiuPerson.setCharmValue(  UiUtil.findImageViewById(mCharmValueLayout, R.id.itme_icon),XiuxiuUserInfoResult.getInstance().getCharm());
    }

    @Override
    public void onResume() {
        super.onResume();
        mNameTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
    }

    @Override
    public void onClick(View v) {
        if(v==mWalletLayout){
            WalletActivity.startActivity(getActivity());
        } else if(v==mCharmValueLayout){
            CharmLevelActivity.startActivity(getActivity());
        }
        int id = v.getId();
        switch (id){
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
        }
    }


    void logout() {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st = getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		ImHelper.getInstance().logout(false,new EMCallBack() {

			@Override
			public void onSuccess() {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						//清除数据
                        XiuxiuLoginResult.clear();
                        XiuxiuUserInfoResult.clear();
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
	}
}
