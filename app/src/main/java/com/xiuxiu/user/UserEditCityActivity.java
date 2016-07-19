package com.xiuxiu.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.wheel.widget.WheelView;
import com.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;

/**
 * 城市编辑页面
 * Created by huzhi on 16-5-24.
 */
public class UserEditCityActivity extends BaseActivity{

    private WheelView mWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_layout);
        setupViews();
    }

    private void setupViews(){
        mWheelView = (WheelView) findViewById(R.id.city);
        mWheelView.setWheelBackground(R.drawable.wheel_bg_holo);
        mWheelView.setWheelForeground(R.drawable.wheel_val_holo);
        mWheelView.setShadowColor(0xFF000000, 0x88000000, 0x00000000);
        mWheelView.setViewAdapter(new CountryAdapter(this));
        mWheelView.setVisibleItems(5);

    }


    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String ages[] =
                new String[] {"1", "2", "3", "4","5","6","7","8","9","10",
                    "11","12","13","14","15","16","17","18","19","20",
                        "21","22","23","24","25","26","27","28","29","30",
                        "31","32","33","34","35","36","37","38","39","40",
                        "41","42","43","44","45","46","47","48","49","50",
                        "51","52","53","54","55","56","57","58","59","60",
                        "61","62","63","64","65","66","67","68","69","70",
                        "71","72","73","74","75","76","77","78","79","80",
                        "81","82","83","84","85","86","87","88","89","90",
                        "91","92","93","94","95","96","97","98","99","100",};

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.activity_user_city_edit_layout, NO_RESOURCE);

            setItemTextResource(R.id.user_age);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return ages.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return ages[index];
        }
    }
}
