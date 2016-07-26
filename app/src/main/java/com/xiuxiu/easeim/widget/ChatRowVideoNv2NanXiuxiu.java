package com.xiuxiu.easeim.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.util.TextFormater;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.chat.im.ChatFragment;
import com.xiuxiu.easeim.ChatRowUtils;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;

import java.io.File;

/**
 * Created by huzhi on 16-7-25.
 */
public class ChatRowVideoNv2NanXiuxiu extends EaseChatRowFile {

    public ChatRowVideoNv2NanXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    private ImageView imageView;
    private TextView sizeView;
    private TextView timeLengthView;
    private ImageView playView;

    private TextView mStatusTv;
    private TextView mCostXiuxiuBTv;
    private TextView mXiuxiuContentTv;

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_video_nv_2_nan_received_xiuxiu : R.layout.ease_row_video_nv_2_nan_sent_xiuxiu, this);

        setXiuxiuBubbleSize();
    }

    @Override
    protected void onFindViewById() {
        imageView = ((ImageView) findViewById(com.hyphenate.easeui.R.id.chatting_content_iv));
        sizeView = (TextView) findViewById(com.hyphenate.easeui.R.id.chatting_size_iv);
        timeLengthView = (TextView) findViewById(com.hyphenate.easeui.R.id.chatting_length_iv);
        playView = (ImageView) findViewById(com.hyphenate.easeui.R.id.chatting_status_btn);
        percentageView = (TextView) findViewById(com.hyphenate.easeui.R.id.percentage);
        mStatusTv = (TextView) findViewById(R.id.status);
        mCostXiuxiuBTv = (TextView) findViewById(R.id.cost_xiuxiu_b);
        mXiuxiuContentTv = (TextView) findViewById(R.id.xiuxiu_content);
    }

    @Override
    protected void onSetUpView() {

        String xiuxiuB = "20";
        String xiuxiuContent = "您想要的私房照片";
        try{
            xiuxiuB = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_B_SIZE);
            xiuxiuContent = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TXT_CONTENT);
        }catch (Exception e){
        }
        mCostXiuxiuBTv.setVisibility(View.VISIBLE);
        mXiuxiuContentTv.setVisibility(View.VISIBLE);
        mXiuxiuContentTv.setText(xiuxiuContent);
        mCostXiuxiuBTv.setText("需支付" + xiuxiuB + "咻币");

        setStatus();

        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
        // final File image=new File(PathUtil.getInstance().getVideoPath(),
        // videoBody.getFileName());
        String localThumb = videoBody.getLocalThumb();

        if (localThumb != null) {

            showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
        }
        if (videoBody.getDuration() > 0) {
            String time = DateUtils.toTime(videoBody.getDuration());
            timeLengthView.setText(time);
        }
//        playView.setImageResource(R.drawable.video_play_btn_small_nor);

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
                sizeView.setText(size);
            }
        } else {
            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
                sizeView.setText(size);
            }
        }

        EMLog.d(TAG, "video thumbnailStatus:" + videoBody.thumbnailDownloadStatus());
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(com.hyphenate.easeui.R.drawable.ease_default_image);
                setMessageReceiveCallback();
            } else {
                // System.err.println("!!!! not back receive, show image directly");
                imageView.setImageResource(com.hyphenate.easeui.R.drawable.ease_default_image);
                if (localThumb != null) {
                    showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
                }

            }

            return;
        }
        //处理发送方消息
        handleSendMessage();
    }

    @Override
    protected void onBubbleClick() {

        if(message.direct() == EMMessage.Direct.RECEIVE && !ChatRowUtils.isPaid(message) &&
                ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)) {
            ToastUtil.showMessage(getContext(), "已经过期");
            return;
        }
        if(message.direct() == EMMessage.Direct.RECEIVE && !ChatRowUtils.isPaid(message)){//如果接收的没有付费
            showDialog();
            return;
        }

        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
        EMLog.d(TAG, "video view is on click");
        Intent intent = new Intent(context, EaseShowVideoActivity.class);
        intent.putExtra("localpath", videoBody.getLocalUrl());
        intent.putExtra("secret", videoBody.getSecret());
        intent.putExtra("remotepath", videoBody.getRemoteUrl());
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activity.startActivity(intent);
    }

    /**
     * 展示视频缩略图
     *
     * @param localThumb
     *            本地缩略图路径
     * @param iv
     * @param thumbnailUrl
     *            远程缩略图路径
     * @param message
     */
    private void showVideoThumbView(final String localThumb, final ImageView iv, String thumbnailUrl, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = EaseImageCache.getInstance().get(localThumb);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
//            iv.setImageBitmap(bitmap);
            iv.setImageBitmap(com.xiuxiu.utils.ImageUtils.blurImages(bitmap, context));

        } else {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    if (new File(localThumb).exists()) {
                        return ImageUtils.decodeScaleImage(localThumb, 200, 200);
                    } else {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    if (result != null) {
                        EaseImageCache.getInstance().put(localThumb, result);
//                        iv.setImageBitmap(result);
                        /*
                        if(ChatRowUtils.isPaid(message)){
                            iv.setImageBitmap(result);
                        }else{
                            iv.setImageBitmap(com.xiuxiu.utils.ImageUtils.blurImages(result, context));
                        }*/
                        iv.setImageBitmap(com.xiuxiu.utils.ImageUtils.blurImages(result, context));
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (EaseCommonUtils.isNetWorkConnected(activity)) {
                                EMClient.getInstance().chatManager().downloadThumbnail(message);
                            }
                        }

                    }
                }
            }.execute();
        }

    }

    private void setXiuxiuBubbleSize(){
        ViewGroup bubbleLayout = (ViewGroup) findViewById(R.id.bubble);
        int screenWidth = ScreenUtils.getScreenWidth(getContext());
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(screenWidth/2,screenWidth/2);
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            rl.addRule(RelativeLayout.RIGHT_OF, R.id.iv_userhead);
            rl.addRule(RelativeLayout.BELOW, R.id.tv_userid);
            rl.setMargins((int)getResources().getDimension(R.dimen.margin_chat_activity),0,0,0);
        }else{
            rl.addRule(RelativeLayout.LEFT_OF, R.id.iv_userhead);
            rl.setMargins(0, 0, (int) getResources().getDimension(R.dimen.margin_chat_activity), 0);
        }
        bubbleLayout.setLayoutParams(rl);
    }

    private void setStatus(){
        if (!ChatRowUtils.isPaid(message) && ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)) {
            mStatusTv.setText("已过期");
        }else if (ChatRowUtils.isPaid(message)){
            mStatusTv.setText("已支付");
        }else{
            mStatusTv.setText("未接受");
        }
    }

    // ============================================================================================
    // 付费对话框
    // ============================================================================================
    String xiuxiuB = "";
    private void showDialog() {
        try {
            xiuxiuB = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_B_SIZE);
        } catch (Exception e) {
            xiuxiuB = "20";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您确定付费吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                XiuxiuUtils.transferCoinAsyn(message.getFrom(), xiuxiuB, new XiuxiuUtils.CallBack() {
                    @Override
                    public void onPre() {
                        XiuxiuUtils.showProgressDialog(getContext());
                    }

                    @Override
                    public void onSccusee() {
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuUtils.dismisslProgressDialog();
                                ToastUtil.showMessage(getContext(), "支付成功!");
                                try {
                                    String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                                    ChatFragment.sendAgreeXiuxiuMessageAgree(message.getFrom(), askId);
                                    XiuxiuActionMsgManager.getInstance().add(askId, EaseConstant.XIUXIU_STATUS_AGREE);
                                } catch (Exception e) {
                                }
                                onSetUpView();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.showMessage(getContext(), "支付失败!");
                        XiuxiuUtils.dismisslProgressDialog();
                    }
                });
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
