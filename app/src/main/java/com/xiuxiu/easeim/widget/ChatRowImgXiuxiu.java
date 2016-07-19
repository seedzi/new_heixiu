package com.xiuxiu.easeim.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.xiuxiu.chat.im.ChatFragment;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.utils.ImageUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;
import com.xiuxiu.xiuxiutask.XiuxiuTaskBean;

import java.io.File;

/**
 * Created by huzhi on 16-7-14.
 * 咻咻图片
 */
public class ChatRowImgXiuxiu extends EaseChatRowFile{

    protected ImageView imageView;
    private EMImageMessageBody imgBody;

    private TextView mClickLookTv;
    private TextView mCostXiuxiuBTv;

    private XiuxiuTaskBean mXiuxiuTaskBean;

    public ChatRowImgXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
        android.util.Log.d("12345", "new ChatRowImgXiuxiu()");
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture_xiuxiu :
                R.layout.ease_row_sent_picture_xiuxiu, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(com.hyphenate.easeui.R.id.percentage);
        imageView = (ImageView) findViewById(com.hyphenate.easeui.R.id.image);
    }


    @Override
    protected void onSetUpView() {
        android.util.Log.d("12345","onSetUpView()");
        mClickLookTv = (TextView) findViewById(R.id.click_look);
        mCostXiuxiuBTv = (TextView) findViewById(R.id.cost_xiuxiu_b);

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
            return;
        }

        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageView(thumbPath, imageView, filePath, message);
        handleSendMessage();

        //设置状态
        setStatus();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {
        android.util.Log.d("12345","onBubbleClick()");
        if(message.direct() == EMMessage.Direct.RECEIVE && !isPaid()){//如果接收的没有付费
            showDialog();
            return;
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
            if( message.direct() == EMMessage.Direct.SEND){
                iv.setImageBitmap(ImageUtils.blurImages(bitmap,context));
            }else if(isPaid()){
                iv.setImageBitmap(bitmap);
            }else{
                iv.setImageBitmap(ImageUtils.blurImages(bitmap,context));
            }
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
                        if( message.direct() == EMMessage.Direct.SEND){
                            iv.setImageBitmap(ImageUtils.blurImages(image,context));
                        }else if(isPaid()){
                            iv.setImageBitmap(image);
                        }else{
                            iv.setImageBitmap(ImageUtils.blurImages(image,context));
                        }
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
        android.util.Log.d("12345","setStatus()");
        if(message.direct() == EMMessage.Direct.RECEIVE){
            if(isPaid()) {
                mClickLookTv.setVisibility(View.GONE);
                mCostXiuxiuBTv.setVisibility(View.GONE);
            }else{
                mClickLookTv.setVisibility(View.VISIBLE);
                mCostXiuxiuBTv.setVisibility(View.VISIBLE);
            }
        }else{
            android.util.Log.d("12345","isPaid() = " + isPaid());
        }
    }

    /**
     * 是否付费
     */
    public boolean isPaid(){
        String status;
        try {
            String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
            status = XiuxiuActionMsgManager.getInstance().getStatusById(askId);
        }catch (Exception e){
            status = null;
        }
        if(status!=null && status.equals(EaseConstant.XIUXIU_STATUS_AGREE)){ //如果已经付费
            return true;
        }
        return false;
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
