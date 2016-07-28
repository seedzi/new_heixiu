package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzhi on 16-4-29.
 */
public class EaseChatExtendMenu extends RelativeLayout{

    protected Context context;

    public EaseChatExtendMenu(Context context) {
        super(context);
        init(context);
    }

    public EaseChatExtendMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseChatExtendMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.ease_widget_chat_extend_menu2, this);
    }

//    private List<ChatMenuItemModel> itemModels = new ArrayList<ChatMenuItemModel>();

    /**
     * 初始化
     */
    /*
    public void init(){
        for(int i=0;i<itemModels.size();i++){
            final ChatMenuItemModel chatMenuItemModel = itemModels.get(i);
            ImageView iv = (ImageView)getChildAt(i);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatMenuItemModel.clickListener.onClick(chatMenuItemModel.id,v);
                }
            });
        }
    }*/

    /**
     * 注册menu item
     *
     * @param name
     *            item名字
     * @param drawableRes
     *            item背景
     * @param itemId
     *             id
     * @param listener
     *            item点击事件
     */
    /*
    public void registerMenuItem(String name, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        ChatMenuItemModel item = new ChatMenuItemModel();
        item.name = name;
        item.image = drawableRes;
        item.id = itemId;
        item.clickListener = listener;
        itemModels.add(item);
    }*/

    /**
     * 注册menu item
     *
     * @param nameRes
     *            item名字的resource id
     * @param drawableRes
     *            item背景
     * @param itemId
     *             id
     * @param listener
     *            item点击事件
     */
    /*
    public void registerMenuItem(int nameRes, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        registerMenuItem(context.getString(nameRes), drawableRes, itemId, listener);
    }*/
        /*
    class ChatMenuItemModel{
        String name;
        int image;
        int id;
        EaseChatExtendMenuItemClickListener clickListener;
    }
    public interface EaseChatExtendMenuItemClickListener{
        void onClick(int itemId, View view);
    }*/
}
