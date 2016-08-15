package com.xiuxiu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiuxiu.easeim.XiuxiuSayHelloManager;

/**
 * Created by huzhi on 16-8-15.
 */
public class DateTimeReceiver extends BroadcastReceiver{

    private static final String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_DATE_CHANGED.equals(action)) {
            XiuxiuSayHelloManager.getInstance().clear();
        }
    }
}
