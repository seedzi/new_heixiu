package com.hyphenate.easeui.widget.gift;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

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
        int width = screenWidth - EaseCommonUtils.dip2px(getContext(),14);
        mGiftItemWidth = (width - EaseCommonUtils.dip2px(getContext(),12))/4;
        mGiftItemHeight = mGiftItemWidth;

        GridView giftGrid = (GridView) findViewById(R.id.gift_wall);
        giftGrid.setAdapter(new GiftAdpater());
        giftGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(GiftManager.getInstance().getListener()!=null){
                    GiftManager.getInstance().getListener().onItemClick(position);
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
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_item_gift,null);
                gl = new GridView.LayoutParams(mGiftItemWidth, mGiftItemHeight);
                convertView.setLayoutParams(gl);
            }
            ((ImageView)convertView).setImageDrawable(new ColorDrawable(Color.parseColor("#a6a6a6")));
            /*
            if(position == mImgFiles.size()){//最后一个item
                convertView.findViewById(R.id.img).setVisibility(View.GONE);
                convertView.findViewById(R.id.bt).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                });
            }else{
                convertView.findViewById(R.id.img).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.bt).setVisibility(View.GONE);
                convertView.findViewById(R.id.img).setTag(position);
                convertView.findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteItemDialog((Integer) v.getTag());
                    }
                });
                if(TextUtils.isEmpty(mImgFiles.get(position).localPath)){
                    ImageLoader.getInstance().displayImage(
                            HttpUrlManager.QI_NIU_HOST + mImgFiles.get(position).key,
                            (ImageView) convertView.findViewById(R.id.img));
                }else{
                    ImageLoader.getInstance().displayImage(
                            "file:/" + mImgFiles.get(position).localPath,
                            (ImageView) convertView.findViewById(R.id.img));
                }

            }
            convertView.setBackgroundColor(Color.parseColor("#a6a6a6"));
            */
            return convertView;
        }
    }
}
