package com.xiuxiu.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.xiuxiu.XiuxiuApplication;

/**
 * Created by huzhi on 16-5-24.
 */
public class ToastUtil {

    private static Toast sToast = null;

    public synchronized static void showMessage(final Context context,
                                                final CharSequence string, final boolean isLong) {
        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sToast == null) {
                        sToast = Toast.makeText(context, "", isLong ? Toast.LENGTH_LONG
                                : Toast.LENGTH_SHORT);
                    }
                    sToast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                    sToast.setText(TextUtils.isEmpty(string) ? "" : string);
                    sToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showMessage(final Context context, final int stringId,
                                   final boolean isLong) {
        showMessage(context, context.getResources().getString(stringId), isLong);
    }

    public static void showMessage(final Context context,
                                   final CharSequence string) {
        showMessage(context, string, false);
    }

    public static void showMessage(final Context context, final int stringId) {
        showMessage(context, stringId, false);
    }

    public synchronized static void showMessage(final Context context,
                                                final View view) {
        try {
            if (sToast == null) {
                sToast = new Toast(context);
                sToast.setDuration(Toast.LENGTH_SHORT);
            }
            sToast.setView(view);
            sToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
