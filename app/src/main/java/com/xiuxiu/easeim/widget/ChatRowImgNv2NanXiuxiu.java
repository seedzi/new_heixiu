package com.xiuxiu.easeim.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.chat.im.ChatFragment;
import com.xiuxiu.easeim.ChatRowUtils;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgTable;
import com.xiuxiu.utils.ImageUtils;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;
import com.xiuxiu.xiuxiutask.XiuxiuTaskBean;

import java.io.File;

/**
 * Created by huzhi on 16-7-14.
 * 咻咻图片
 */
public class ChatRowImgNv2NanXiuxiu extends EaseChatRowFile{

    private static final String TAG = "ChatRowImgNv2NanXiuxiu";

    protected ImageView imageView;
    private EMImageMessageBody imgBody;

    private TextView mClickLookTv;
    private TextView mCostXiuxiuBTv;
    private TextView mXiuxiuContentTv;

    private TextView mStatusTv;

    public ChatRowImgNv2NanXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_img_nv_2_nan_received_xiuxiu :
                R.layout.ease_row_img_nv_2_nan_sent_xiuxiu, this);

        setXiuxiuBubbleSize();

        android.util.Log.d("77777","System.currentTimeMillis() = " + System.currentTimeMillis()
            + ",message.getMsgTime() = " + message.getMsgTime());
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(com.hyphenate.easeui.R.id.percentage);
        imageView = (ImageView) findViewById(com.hyphenate.easeui.R.id.image);
        mClickLookTv = (TextView) findViewById(R.id.click_look);
        mCostXiuxiuBTv = (TextView) findViewById(R.id.cost_xiuxiu_b);
        mXiuxiuContentTv = (TextView) findViewById(R.id.xiuxiu_content);
        mStatusTv = (TextView) findViewById(R.id.status);

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
        mClickLookTv.setVisibility(View.VISIBLE);
        mCostXiuxiuBTv.setVisibility(View.VISIBLE);
        mXiuxiuContentTv.setVisibility(View.VISIBLE);
        mXiuxiuContentTv.setText(xiuxiuContent);
        mCostXiuxiuBTv.setText("需支付" + xiuxiuB + "咻币");

        imgBody = (EMImageMessageBody) message.getBody();

        // 接收方向的消息
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(com.hyphenate.easeui.R.drawable.ease_default_image);
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                imageView.setImageResource(com.hyphenate.easeui.R.drawable.ease_default_image);
                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                    // 兼容旧版SDK收到的thumbnail
                    thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                }
                showImageView(thumbPath, imageView, imgBody.getLocalUrl(), message);
            }
            //设置状态
            setStatus();
            return;
        }

        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageView(thumbPath, imageView, filePath, message);
        handleSendMessage();

        //设置状态
        setStatus();
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
            rl.setMargins(0, 0,(int) getResources().getDimension(R.dimen.margin_chat_activity),0);
        }
        bubbleLayout.setLayoutParams(rl);
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {
        if(message.direct() == EMMessage.Direct.RECEIVE && !ChatRowUtils.isPaid(message) &&
                ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)) {
            ToastUtil.showMessage(getContext(),"已经过期");
            return;
        }
        if(message.direct() == EMMessage.Direct.RECEIVE && !ChatRowUtils.isPaid(message)){//如果接收的没有付费
            showDialog();
            return;
        }
        if(message.direct()==EMMessage.Direct.RECEIVE) {
            int count = ChatRowUtils.getLookCount(message);
            if(count>=3){
                ToastUtil.showMessage(getContext(),"图片已经销毁");
                return;
            }
        }

        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(imgBody.getLocalUrl());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("secret", imgBody.getSecret());
            intent.putExtra("remotepath", imgBody.getRemoteUrl());
            intent.putExtra("localUrl", imgBody.getLocalUrl());
        }
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        context.startActivity(intent);


        //将查看次数+1
        if(message.direct() == EMMessage.Direct.RECEIVE) {
            try {
                String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                XiuxiuActionMsgTable.updateLookCountById(askId);
                setStatus();
            } catch (Exception e) {
            }
        }
    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @param position
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            /*
            if( message.direct() == EMMessage.Direct.SEND){
                iv.setImageBitmap(ImageUtils.blurImages(bitmap,context));
            }else if(ChatRowUtils.isPaid(message)){
                iv.setImageBitmap(bitmap);
            }else{
                iv.setImageBitmap(ImageUtils.blurImages(bitmap,context));
            }*/
            iv.setImageBitmap(ImageUtils.blurImages(bitmap,context));
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return EaseImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                        return EaseImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), 160, 160);
                    }
                    else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return EaseImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        /*
                        if( message.direct() == EMMessage.Direct.SEND){
                            iv.setImageBitmap(ImageUtils.blurImages(image,context));
                        }else if(ChatRowUtils.isPaid(message)){
                            iv.setImageBitmap(image);
                        }else{
                            iv.setImageBitmap(ImageUtils.blurImages(image,context));
                        }*/
                        iv.setImageBitmap(ImageUtils.blurImages(image,context));
                        EaseImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (EaseCommonUtils.isNetWorkConnected(activity)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                                    }
                                }).start();
                            }
                        }

                    }
                }
            }.execute();

            return true;
        }
    }


    private void setStatus(){
        android.util.Log.d(TAG,"setStatus");
        if( message.direct() == EMMessage.Direct.RECEIVE) {
            if (!ChatRowUtils.isPaid(message) && ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)) {
                mStatusTv.setText("已过期");
                return;
            }
            if (ChatRowUtils.isPaid(message)){
                android.util.Log.d(TAG, "is pad");
                int count = 3-ChatRowUtils.getLookCount(message);
                if(count>0){
                    mStatusTv.setText("还可查看" + count);
                }else{
                    mStatusTv.setText("已销毁" );
                }
            }else{
                mStatusTv.setText("未接受");
            }
        } else {
            if (!ChatRowUtils.isPaid(message) && ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)) {
                mStatusTv.setText("已过期");
            }else if (ChatRowUtils.isPaid(message)){
                mStatusTv.setText("已支付");
            }else{
                mStatusTv.setText("未接受");
            }
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
                XiuxiuUtils.transferCoinAsyn(message.getFrom(),xiuxiuB, new XiuxiuUtils.CallBack() {
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
