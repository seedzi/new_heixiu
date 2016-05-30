package com.xiuxiu.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.wheel.widget.WheelView;
import com.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.xiuxiu.R;

/**
 * Created by huzhi on 16-5-25.
 */
public class CityDialog extends AlertDialog implements DialogInterface.OnClickListener{

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        void onDateSet(String city);
    }


    private OnDateSetListener mCallBack;
    private WheelView mWheelView;
    private ViewGroup mLayout;

    public CityDialog(Context context,OnDateSetListener callBack) {
        super(context,0);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, (CharSequence)"设置", this);
        setButton(BUTTON_NEGATIVE, (CharSequence)"取消", this);
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (ViewGroup)inflater.inflate(R.layout.city_picker_dialog, null);
        setView(mLayout);
        setTitle("请选择城市");
        setupViews();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == BUTTON_POSITIVE){
            tryNotifyDateSet();
        }
    }

    private void setupViews(){
        mWheelView = (WheelView) mLayout.findViewById(R.id.city);
//        mWheelView.setWheelBackground(R.drawable.wheel_bg_holo);
        mWheelView.setWheelForeground(R.drawable.wheel_val_holo);
//        mWheelView.setShadowColor(0xFF000000, 0x88000000, 0x00000000);
        mWheelView.setShadowColor(0x88FFFFFF, 0x88FFFFFF, 0x88FFFFFF);
        mWheelView.setViewAdapter(new CountryAdapter(getContext()));
        mWheelView.setVisibleItems(5);

    }

    private void tryNotifyDateSet(){
        if (mCallBack != null) {
            mCallBack.onDateSet(cities[mWheelView.getCurrentItem()]);
        }
    }

    // Countries names
    private String cities[] =
            new String[] {"长沙","北京","上海","深圳","南京","回龙观"};

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {


        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.activity_user_city_edit_layout, NO_RESOURCE);

            setItemTextResource(R.id.age);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return cities.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return cities[index];
        }
    }
}
