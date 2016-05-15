package com.xiuxiu.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class LayoutPagerAdapter extends PagerAdapter {

    private int layoutCount;

    public LayoutPagerAdapter(int layoutCount) {
        this.layoutCount = layoutCount;
    }

    @Override
    public int getCount() {
        return layoutCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
