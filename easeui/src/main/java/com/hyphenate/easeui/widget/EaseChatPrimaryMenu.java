package com.hyphenate.easeui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天输入栏主菜单栏
 *
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
    private EditText editText;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private ImageView faceNormal;
    private ImageView faceChecked;
//    private Button buttonMore;
    private RelativeLayout faceLayout;
    private Context context;
    private EaseVoiceRecorderView voiceRecorderView;

    /*需要加点击时间的view集合*/
    private List<View> mItemViews = new ArrayList<View>();

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatPrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
        editText = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.ease_chat_voice_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.ease_chat_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        faceNormal = (ImageView) findViewById(R.id.ease_chat_expression);
        faceChecked = (ImageView) findViewById(R.id.ease_chat_expression_checked);
        faceLayout = (RelativeLayout) findViewById(R.id.rl_face);
//        buttonMore = (Button) findViewById(R.id.btn_more);
//        edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
        
        buttonSend.setOnClickListener(this);
        buttonSend.setEnabled(false);
        //huzhi
//        buttonSetModeKeyboard.setOnClickListener(this);
//        buttonSetModeVoice.setOnClickListener(this);
        findViewById(R.id.rl_voice).setOnClickListener(this);
//        buttonMore.setOnClickListener(this);
        faceLayout.setOnClickListener(this);
        editText.setOnClickListener(this);
        editText.requestFocus();
        
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
                }*/

            }
        });
        // 监听文字框
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
//                    buttonMore.setVisibility(View.GONE);
//                    buttonSend.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.VISIBLE);
                    buttonSend.setEnabled(true);
                } else {
//                    buttonMore.setVisibility(View.VISIBLE);
//                    buttonSend.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                    buttonSend.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        
        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(listener != null){
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // =============== 视频 图片 礼物  拍照  注册点击事件 huzhi ===================//
        mItemViews.add(findViewById(R.id.ease_chat_video));//视频
        mItemViews.add(findViewById(R.id.ease_chat_pic));//图片
        mItemViews.add(findViewById(R.id.ease_chat_gift));//礼物
    }
    
    /**
     * 设置长按说话录制控件
     * @param voiceRecorderView
     */
    public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView){
        this.voiceRecorderView = voiceRecorderView;
    }

    /**
     * 表情输入
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent){
        editText.append(emojiContent);
    }
    
    /**
     * 表情删除
     */
    public void onEmojiconDeleteEvent(){
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }
    
    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view){
        int id = view.getId();
        if (id == R.id.btn_send) {
            if(listener != null){
                String s = editText.getText().toString();
                editText.setText("");
                listener.onSendBtnClicked(s);
            }
        } else if (id == R.id.ease_chat_voice) {
//            setModeVoice(); //huzhi
            toggleVoiceImage();
            showNormalFaceImage();
            if(listener != null)
                listener.onToggleVoiceBtnClicked();
        } else if (id == R.id.ease_chat_voice_keyboard) {
//            setModeKeyboard(); //huzhi
            showNormalFaceImage();
            if(listener != null)
                listener.onToggleVoiceBtnClicked();
        } /*else if (id == R.id.btn_more) {
            buttonSetModeVoice.setVisibility(View.VISIBLE);
            buttonSetModeKeyboard.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.GONE);
            showNormalFaceImage();
            if(listener != null)
                listener.onToggleExtendClicked();
        }*/
        else if (id == R.id.et_sendmessage) {
//            edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
//            faceNormal.setVisibility(View.VISIBLE);
//            faceChecked.setVisibility(View.INVISIBLE);
            if(listener != null)
                listener.onEditTextClicked();
        } else if (id == R.id.rl_voice){
            toggleVoiceImage();
            if(listener != null)
                listener.onToggleVoiceBtnClicked();
        } else if (id == R.id.rl_face) {
            toggleFaceImage();
            if(listener != null){
                listener.onToggleEmojiconClicked();
            }
        } else {
        }
    }
    
    
    /**
     * 显示语音图标按钮
     * 
     */
    protected void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
//        buttonMore.setVisibility(View.VISIBLE); //huzhi
        buttonPressToSpeak.setVisibility(View.VISIBLE);
//        faceNormal.setVisibility(View.VISIBLE);
//        faceChecked.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示键盘图标
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        /*
        if (TextUtils.isEmpty(editText.getText())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }*/
    }
    
    
    protected void toggleFaceImage(){
        android.util.Log.d("aaaa","toggleFaceImage");
        if(faceNormal.getVisibility() == View.VISIBLE){
            showSelectedFaceImage();
        }else{
            showNormalFaceImage();
        }
        showNormalVoiceImage();
    }
    
    private void showNormalFaceImage(){
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }
    
    private void showSelectedFaceImage(){
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }

    protected void toggleVoiceImage(){
        if(buttonSetModeKeyboard.getVisibility() == View.VISIBLE){
            showNormalVoiceImage();
        }else{
            showSelectedVoiceImage();
        }
        showNormalFaceImage();
    }

    private void showNormalVoiceImage(){
        android.util.Log.d("aaaa","showNormalVoiceImage");
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.INVISIBLE);
    }

    private void showSelectedVoiceImage(){
        android.util.Log.d("aaaa","showSelectedVoiceImage");
        buttonSetModeVoice.setVisibility(View.INVISIBLE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
    }
    

    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }


    // ====================================== huzhi ==================================================//\

    public void registerMenuItems(final int id, final EaseChatPrimaryMenu.EaseChatExtendItemClickListener listener) {
        mItemViews.get(id).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                listener.onClick(id,v);
            }
        });
    }

    public interface EaseChatExtendItemClickListener{
        void onClick(int itemId, View view);
    }

}
