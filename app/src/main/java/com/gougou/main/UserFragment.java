package com.gougou.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gougou.R;
import com.gougou.user.CharmLevelActivity;
import com.gougou.user.SetupPage;
import com.gougou.user.UserDetailActivity;
import com.gougou.user.WalletActivity;
import com.gougou.user.invitation.InvitationPage;
import com.gougou.utils.UiUtil;

/**
 * Created by huzhi on 16-3-21.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

    private ViewGroup mRootView;

    private ViewGroup mCharmValueLayout;

    private ViewGroup mWalletLayout;

    private ViewGroup mInviteFriendsLayout;

    private ViewGroup mFeedbackLayout;

    private ViewGroup mSetUpLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView ==null){
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, null);
            setUpView();
            initData();
        } else {
            try {
                ((ViewGroup)mRootView.getParent()).removeView(mRootView);
            } catch (Exception e) {
            }
        }
        return mRootView;
    }

    private void setUpView(){
        /*
        mWealthLevelLayout = (ViewGroup) mRootView.findViewById(R.id.wealth_level);
        mWalletLayout = (ViewGroup) mRootView.findViewById(R.id.wallet);
        mInvitationCodeLayout = (ViewGroup) mRootView.findViewById(R.id.invitation_code);
        mSetUpLayout = (ViewGroup) mRootView.findViewById(R.id.setup);
        mHelpLayout = (ViewGroup) mRootView.findViewById(R.id.help);

        UiUtil.findTextViewById(mWealthLevelLayout,R.id.item_name).setText("财富等级");
        UiUtil.findTextViewById(mWalletLayout,R.id.item_name).setText("钱包");
        UiUtil.findTextViewById(mInvitationCodeLayout,R.id.item_name).setText("邀请码");
        UiUtil.findTextViewById(mSetUpLayout,R.id.item_name).setText("设置");
        UiUtil.findTextViewById(mSetUpLayout,R.id.item_name).setText("帮助与反馈");

        mWalletLayout.setOnClickListener(this);
        */

        mCharmValueLayout = (ViewGroup) mRootView.findViewById(R.id.charm_value);
        UiUtil.findTextViewById(mCharmValueLayout, R.id.item_name).setText("魅力值");
        UiUtil.findImageViewById(mCharmValueLayout, R.id.img).setImageResource(R.drawable.charm_icon);
        UiUtil.findImageViewById(mCharmValueLayout, R.id.itme_icon).setImageResource(R.drawable.vip_grade_1);
        UiUtil.findTextViewById(mCharmValueLayout, R.id.tag_txt).setText("等级 2");
        mCharmValueLayout.setOnClickListener(this);

        mWalletLayout = (ViewGroup) mRootView.findViewById(R.id.wallet);
        UiUtil.findTextViewById(mWalletLayout, R.id.item_name).setText("钱包");
        UiUtil.findImageViewById(mWalletLayout, R.id.img).setImageResource(R.drawable.wallet_icon);
        UiUtil.findTextViewById(mWalletLayout, R.id.tag_txt).setText("23114 羞币");
        mWalletLayout.setOnClickListener(this);

        mInviteFriendsLayout = (ViewGroup) mRootView.findViewById(R.id.invite_friends);
        UiUtil.findTextViewById(mInviteFriendsLayout, R.id.item_name).setText("邀请朋友");
        UiUtil.findImageViewById(mInviteFriendsLayout, R.id.img).setImageResource(R.drawable.add_friends_icon);
        UiUtil.findTextViewById(mInviteFriendsLayout, R.id.tag_txt).setText("邀请返现金");
        mInviteFriendsLayout.setOnClickListener(this);

        mSetUpLayout = (ViewGroup) mRootView.findViewById(R.id.feedback);
        UiUtil.findTextViewById(mSetUpLayout, R.id.item_name).setText("意见反馈");
        UiUtil.findImageViewById(mSetUpLayout, R.id.img).setImageResource(R.drawable.feedback_icon);
        mSetUpLayout.setOnClickListener(this);


        mFeedbackLayout = (ViewGroup) mRootView.findViewById(R.id.setup);
        UiUtil.findTextViewById(mFeedbackLayout, R.id.item_name).setText("设置");
        UiUtil.findImageViewById(mFeedbackLayout, R.id.img).setImageResource(R.drawable.settings_icon);
        mFeedbackLayout.setOnClickListener(this);

        UiUtil.findImageViewById(mRootView,R.id.user_detail).setOnClickListener(this);

    }

    private void initData(){

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
        }
    }
}
