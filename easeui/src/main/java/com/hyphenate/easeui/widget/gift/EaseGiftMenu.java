package com.hyphenate.easeui.widget.gift;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseCommonUtils;

/**
 * Created by huzhi on 16-7-26.
 */
public class EaseGiftMenu extends FrameLayout {
    public EaseGiftMenu(Context context) {
        super(context);
        init(context);
    }

    public EaseGiftMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseGiftMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.ease_widget_gift, this);

        //设置照片墙大小
        int screenWidth = EaseCommonUtils.getScreenWidth(getContext());
        int width = screenWidth - 3;
        mGiftItemWidth = width/4;
        mGiftItemHeight = mGiftItemWidth;

        GridView giftGrid = (GridView) findViewById(R.id.gift_wall);
        giftGrid.setAdapter(new GiftAdpater());
        giftGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GiftManager.getInstance().getListener() != null) {
                    GiftManager.getInstance().getListener().onItemClick(position);
                }
            }
        });

        findViewById(R.id.chong_zhi).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(GiftManager.getInstance().getListener()!=null){
                    GiftManager.getInstance().getListener().onWalletClick();
                }
            }
        });
    }

    private int mGiftItemWidth;
    private int mGiftItemHeight;
    /**
     * 照片墙
     */
    private class GiftAdpater extends BaseAdapter {

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
            return 8;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridView.LayoutParams gl = null;
            LinearLayout.LayoutParams ll = null;
            Holder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_item_gift,null);
                gl = new GridView.LayoutParams(mGiftItemWidth, mGiftItemHeight);
                ll = new LinearLayout.LayoutParams(mGiftItemWidth/2, mGiftItemHeight/2);
                ll.gravity = Gravity.CENTER_HORIZONTAL;
                convertView.setLayoutParams(gl);
                holder = new Holder();
                holder.giftIv = (ImageView) convertView.findViewById(R.id.gift_img);
                holder.giftIv.setLayoutParams(ll);
                holder.titleTv = (TextView) convertView.findViewById(R.id.gift_title);
                holder.priceTv = (TextView) convertView.findViewById(R.id.gift_price);
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            holder.giftIv.setImageDrawable(new ColorDrawable(Color.parseColor("#a6a6a6")));
            int price = -1;
            switch (position){
                case 0:
                    holder.giftIv.setImageResource(R.drawable.gift_01);
                    holder.titleTv.setText("玫瑰");
                    holder.priceTv.setText("1咻币");
                    break;
                case 1:
                    holder.giftIv.setImageResource(R.drawable.gift_02);
                    holder.titleTv.setText("香蕉");
                    holder.priceTv.setText("1咻币");
                    break;
                case 2:
                    holder.giftIv.setImageResource(R.drawable.gift_03);
                    holder.titleTv.setText("杜蕾斯");
                    holder.priceTv.setText("1咻币");
                    break;
                case 3:
                    holder.giftIv.setImageResource(R.drawable.gift_04);
                    holder.titleTv.setText("+奶油cake");
                    holder.priceTv.setText("1咻币");
                    break;
                case 4:
                    holder.giftIv.setImageResource(R.drawable.gift_05);
                    holder.titleTv.setText("我爱你");
                    holder.priceTv.setText("3咻币");
                    break;
                case 5:
                    holder.giftIv.setImageResource(R.drawable.gift_06);
                    holder.titleTv.setText("钻戒");
                    holder.priceTv.setText("5咻币");
                    break;
                case 6:
                    holder.giftIv.setImageResource(R.drawable.gift_07);
                    holder.titleTv.setText("香水");
                    holder.priceTv.setText("5咻币");
                    break;
                case 7:
                    holder.giftIv.setImageResource(R.drawable.gift_08);
                    holder.titleTv.setText("跑车");
                    holder.priceTv.setText("8咻币");
                    break;
            }
            if(GiftManager.getInstance().getGifts().get(position)!=null){
                price = GiftManager.getInstance().getGifts().get(position);
            }
            if(price!=-1) {
                holder.priceTv.setText(price + "咻币");
            }
            return convertView;
        }

        private class Holder{
            private ImageView giftIv;
            private TextView titleTv;
            private TextView priceTv;
        }
    }
}
