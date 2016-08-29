package com.xiuxiuchat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;


import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class CommonLib {

    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;
    private final static int BITMAP_MAX_BYTE_COUNT = 1024 * 1024 * 2;
    private final static int BITMAP_MAX_HEIGHT = 2000;
    private static Boolean sIsExcellentPhone;

    private static final int UNCONSTRAINED = -1;

    public enum ColorMode {
        LIGHTEN, DARKEN
    }

    // private static Integer SDK;
    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static boolean isScreenLand(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels < metrics.widthPixels;
    }

    public synchronized static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }



    /**
     *
     * @param context
     * @return 返回 3为中小分辨率, 返回 4为高分辨率 返回 5 超高分辨率
     *
     */
    public static int getDeviceDpiWrapValue(Context context) {
        if (context != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            final int dpi = metrics.densityDpi;
            if (dpi <= DisplayMetrics.DENSITY_MEDIUM) {
                return 3;
            } else if (dpi > DisplayMetrics.DENSITY_HIGH) {
                return 5;
            }
        }
        return 4;
    }

    /**
     * 从父view中移除自身，父view必须是view group
     *
     * @param v
     */
    public static void removeFromParent(View v) {
        if (v == null) {
            return;
        }
        ViewParent vp = v.getParent();
        if (vp == null || !(vp instanceof ViewGroup)) {
            return;
        }

        try {
            // remove view时有些系统有崩溃
            ViewGroup vG = (ViewGroup) vp;
            vG.removeView(v);
            detachView(vG, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启用/禁用 界面组.
     *
     * @param viewGroup
     *            待处理界面组
     * @param true: 启用, false: 禁用.
     * @throws IllegalAccessException
     *             if viewGroup == null
     */
    public static void setViewGroupEnable(ViewGroup viewGroup, boolean enabled) {
        if (viewGroup == null) {
            throw new IllegalArgumentException("invalid");
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                setViewGroupEnable((ViewGroup) view, enabled);
            } else if (view instanceof ListView) {
                ListView listView = (ListView) view;
                int listChildCount = listView.getChildCount();
                for (int j = 0; j < listChildCount; j++) {
                    listView.getChildAt(j).setEnabled(enabled);
                }
            }
        }
    }

    /*
     * is event in the view's area
     */
    public static boolean isInRectArea(int x, int y, View view) {
        if (view == null) {
            return false;
        }
        Rect frame = new Rect();
        view.getHitRect(frame);
        if (frame.contains(x, y)) {
            return true;
        }
        return false;
    }

    public static boolean isInRectArea(MotionEvent ev, View view) {
        if (view == null || ev == null) {
            return false;
        }

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        Rect frame = new Rect();
        view.getHitRect(frame);

        if (frame.contains(x, y)) {
            return true;
        }
        return false;
    }

    public static void expandTouchArea(final View view, final int offset) {
        expandTouchArea(view, offset, offset, offset, offset);
    }

    /**
     * 扩大view的可收到触摸事件的区域，如扩大一个button的可点击区域
     *
     * @param view
     * @param left
     *            左边扩充区域
     * @param top
     *            上边扩充区域
     * @param rigth
     *            右边扩充区域
     * @param bottom
     *            下边扩充区域
     */
    public static void expandTouchArea(final View view, final int left,
                                       final int top, final int rigth, final int bottom) {
        if (view == null) {
            return;
        }
        final View parent = (View) view.getParent();
        if (parent == null) {
            return;
        }
        parent.post(new Runnable() {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before we call getHitRect()
            @Override
            public void run() {
                final Rect r = new Rect();
                view.getHitRect(r);
                r.top -= top;
                r.left -= left;
                r.right += rigth;
                r.bottom += bottom;

                r.top = r.top < 0 ? 0 : r.top;
                r.left = r.left < 0 ? 0 : r.left;
                r.right = r.right < 0 ? 0 : r.right;
                r.bottom = r.bottom < 0 ? 0 : r.bottom;

                parent.setTouchDelegate(new TouchDelegate(r, view));
            }
        });
    }

    /**
     * @param context
     * @return Indicates whether network connectivity is possible.
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                isConnected = mNetworkInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    /**
     * indicates whether wifi connectivity is available.
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        boolean isWifiConnected = false;
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWiFiNetworkInfo != null) {
                    isWifiConnected = mWiFiNetworkInfo.isConnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isWifiConnected;
    }

    /**
     * indicates whether mobile connectivity is available.
     */
    public static boolean isMobileConnected(Context context) {
        boolean isMobileConnected = false;
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mMobileNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mMobileNetworkInfo != null) {
                    isMobileConnected = mMobileNetworkInfo.isConnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMobileConnected;
    }

    /**
     *
     * @return one of TYPE_MOBILE, TYPE_WIFI, or other types defined by
     *         ConnectivityManager
     */
    public static int getConnectedType(Context context) {
        int type = -1;
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                type = mNetworkInfo.getType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    /**
     * @return the name in one of TYPE_MOBILE, TYPE_WIFI, or other types defined
     *         by ConnectivityManager
     */
    public static String getConnectedTypeName(Context context) {
        String name = "NoNet";
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                    name = mNetworkInfo.getTypeName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }



    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = packInfo.versionName;
        return versionName;
    }

    public static int getVersionCode(Context context)
            throws NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(
                context.getPackageName(), 0);
        int versionCode = packInfo.versionCode;
        return versionCode;
    }


    public static void startNetworkSettingActivity(Context context) {
        Intent intent = null;
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(
                    android.provider.Settings.ACTION_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }

    public static Point convertPointOnScreen(View v, int x, int y) {
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        Point p = new Point();
        p.x = location[0] + x;
        p.y = location[1] + y;
        return p;
    }

    /**
     * 转换点击事件的坐标 到整个手机屏幕的坐标系上，包括通知栏
     *
     * @param v
     * @param event
     * @return
     */
    public static MotionEvent convertEventOnScreen(View v, MotionEvent event) {
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        MotionEvent motionEvent = MotionEvent.obtain(event);
        motionEvent.setLocation(location[0] + event.getX(),
                location[1] + event.getY());
        return motionEvent;
    }

    /**
     * 如果fromView的 touch event可以转换到toView的布局范围内，则返回新的touch event，该event的坐标
     * 被修改为toView的坐标系，否则返回null
     *
     * @param fromView
     * @param event
     * @param toView
     * @return
     */
    public static MotionEvent convertEventToView(View fromView,
                                                 MotionEvent event, View toView) {
        int location[] = new int[2];
        fromView.getLocationOnScreen(location);
        float newX = location[0] + event.getX();
        float newY = location[1] + event.getY();

        int toViewLocation[] = new int[2];
        toView.getLocationOnScreen(toViewLocation);
        float toX = newX - toViewLocation[0];
        float toY = newY - toViewLocation[1];

        // 不在toView的范围内
        if (toX < 0 || toY < 0) {
            return null;
        }

        MotionEvent motionEvent = MotionEvent.obtain(event);
        motionEvent.setLocation(toX, toY);
        return motionEvent;
    }

    public static void detachView(ViewGroup parent, View v) {
        if (v == null || parent == null) {
            return;
        }
        Method method;
        try {
            method = ViewGroup.class.getDeclaredMethod("detachViewFromParent",
                    View.class);
            method.setAccessible(true);
            method.invoke(parent, v);
        } catch (Exception e) {
        }
    }

    public static Bitmap createSnapshot(View view, Bitmap.Config quality) {
        int width = view.getWidth();
        int height = view.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width > 0 ? width : 1, height > 0 ? height : 1, quality);
        if (bitmap == null) {
            return null;
        }

        Resources resources = view.getResources();
        if (resources != null) {
            bitmap.setDensity(resources.getDisplayMetrics().densityDpi);
        }

        Canvas canvas;
        canvas = new Canvas(bitmap);

        view.computeScroll();
        final int restoreCount = canvas.save();
        canvas.translate(-view.getScrollX(), -view.getScrollY());

        // view.dispatchDraw(canvas);
        view.draw(canvas);

        canvas.restoreToCount(restoreCount);
        try {
            canvas.setBitmap(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static void setSoftLayerType(View view) {
        try {
            // LAYER_TYPE_SOFTWARE = 1;
            Method nativeMethod = view.getClass().getMethod("setLayerType", int.class,
                    Paint.class);
            nativeMethod.invoke(view, 1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLayerType(View view, int layerType, Paint paint) {
        try {
            Method nativeMethod = view.getClass().getMethod("setLayerType", int.class,
                    Paint.class);
            nativeMethod.invoke(view, layerType, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setOverScrollMode(View view, int mode) {
        if (view == null) {
            return;
        }
        if (getSDKVersion() >= 9) {
            view.setOverScrollMode(mode);
        }
    }

    public static Object execReflectMethod(Object object, String methodName,
                                           Class[] parameterTypes, Object... args) {
        try {
            Method nativeMethod = object.getClass().getMethod(methodName, parameterTypes);
            return nativeMethod.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ThreadPoolExecutor sExecutorService = new ThreadPoolExecutor(1, 2, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static void runInNewThread(final Runnable runnable) {
        runInNewThread(runnable, Process.THREAD_PRIORITY_BACKGROUND);
    }
    public static void runInNewThread(final Runnable runnable, final int threadPriority) {
        if (runnable == null) {
            return;
        }

//        Caused by: java.util.concurrent.RejectedExecutionException:
        try {
            sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
//                    LogUtil.d("getQueueSize:"+sExecutorService.getQueue().size());
                    Process.setThreadPriority(threadPriority);
                    runnable.run();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ========================== 图片处理相关方法 =============================== */
    /* 通过URI得到bitmap */
    public static Bitmap getBitmapByUrl(Context ctx, Uri uri, int width,
                                        int height) {
        Bitmap bitmap = null;
        InputStream input = null;
        try {
            input = ctx.getContentResolver().openInputStream(uri);
            byte[] data = readStream(input);
            bitmap = getResampleBitmap(data, width, height);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static byte[] getBitmapPixelBytes(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 取得该图片的像素数组内容
        int[] data = new int[width * height];
        byte[] bitmapPixels = new byte[width * height];
        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        // 将int数组转换为byte数组
        for (int i = 0; i < data.length; i++) {
            bitmapPixels[i] = (byte) data[i];
        }
        return bitmapPixels;
    }

    public static String readString(InputStream is) {
        return new String(readStream(is));
    }

    /* 输入流中读取byte数组 */
    public static byte[] readStream(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /* Bitmap转成byte数组 */
    public static byte[] Bitmap2Bytes(Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                return baos.toByteArray();
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


    private static int getResampleNum(int bitmapHeight) {
        if (bitmapHeight > 6000) {
            return 2;
        }
        return 1;
    }

    // Compress pictures to reduce memory consumption
    public static Bitmap decodeMap(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        }

        // Find the correct scale value. It should be the power of 2.
        BitmapFactory.Options optionsWithoutLoad = new BitmapFactory.Options();
        optionsWithoutLoad.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(b, 0, b.length, optionsWithoutLoad);

        final int REQUIRED_SIZE = 70;
        int width_tmp = optionsWithoutLoad.outWidth;
        int height_tmp = optionsWithoutLoad.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // decode with inSampleSize
        BitmapFactory.Options optionsLoad = new BitmapFactory.Options();
        optionsLoad.inSampleSize = scale;
        return BitmapFactory.decodeByteArray(b, 0, b.length, optionsLoad);
    }

    /* Drawable转成Bitmap */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getBounds().width();
        int h = drawable.getBounds().height();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /* 图片拼接 */
    public static Bitmap mosaicBitmap(Bitmap bmp1, Bitmap bmp2,
                                      boolean isVertical) {
        int width1 = bmp1.getWidth();
        int height1 = bmp1.getHeight();
        int width2 = bmp2.getWidth();
        int height2 = bmp2.getHeight();
        int width, height;
        Bitmap resultBmp;
        if (isVertical) {
            width = Math.max(width1, width2);
            height = height1 + height2;
            resultBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, height1, null);
        } else {
            height = Math.max(height1, height2);
            width = width1 + width2;
            resultBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, width1, 0, null);
        }
        return resultBmp;
    }

    /* 保存Bitmap到图片 */
    public static void saveBitmap(Bitmap bmp, String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩图片到小于size值, 用于处理上传图片有大小限制时进行压缩。
     * @param srcBp 待压缩的原图
     * @param srcSize 原图大小
     * @param targetSize 压缩到多少字节以下
     *
     * TODO 这个方法不能精确缩小到某一个值，误差比较大。
     */
    public static Bitmap zoomBitmap2SmallSize(Bitmap srcBp, double srcSize, double targetSize) {
        if (srcBp == null) {
            return null;
        }
        if (srcSize <= 0 || targetSize <= 0) {
            return null;
        }
        // 只处理缩小
        if (srcSize <= targetSize) {
            return srcBp;
        }

        float scale = (float) Math.sqrt(srcSize / targetSize);
        // 这里是按照大小比例的平方根进行缩放，而图片的大小并不是按照这个比例压缩的。
        // 比如，1000KB压缩至200KB，结果按照平方根的scale只能达到350KB
        // 这个1.5f的系数是手动试出来的，保证新浪微博分享的图片不会超过500KB
        scale = scale * 1.5f;
        Matrix matrix = new Matrix();
        matrix.setScale(1 / scale, 1 / scale);
        Bitmap targetBp = null;
        try {
            targetBp = Bitmap.createBitmap(srcBp, 0, 0, srcBp.getWidth(), srcBp.getHeight(), matrix, true);
        } catch (Exception e) {
            return null;
        }
        return targetBp;
    }

    /* 图片缩放 */
    public static Bitmap zoomBitmap(Bitmap oldbmp, int targetWidth,
                                    int targetHeight) {
        if (oldbmp == null) {
            return null;
        }
        if (targetHeight <= 0 || targetWidth <= 0) {
            return oldbmp;
        }
        int oldWidth = oldbmp.getWidth();
        int oldHeight = oldbmp.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) targetWidth / oldWidth);
        float scaleHeight = ((float) targetHeight / oldHeight);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = null;
        try {
            newbmp = Bitmap.createBitmap(oldbmp, 0, 0, oldWidth, oldHeight,
                    matrix, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
        return newbmp;
    }

    /* 图片等比例压缩，按倍数小的边裁剪以保证图片不变形 */
    public static Bitmap zoomBitmapWithCut(Bitmap oldbmp, int targetWidth,
                                           int targetHeight) {
        if (oldbmp == null) {
            return null;
        }
        int oldWidth = oldbmp.getWidth();
        int oldHeight = oldbmp.getHeight();
        float widthRate = ((float) oldWidth / targetWidth);
        float heightRate = ((float) oldHeight / targetHeight);
        int srcWidth = oldWidth;
        int srcHeight = oldHeight;
        Matrix matrix = new Matrix();
        float scaleWH;
        int x;
        int y;
        if (widthRate >= heightRate) {
            srcWidth = (int) (targetWidth * heightRate);
            scaleWH = (float) (1.0 / heightRate);
            y = 0;
            x = (oldWidth - srcWidth) / 2;
        } else {
            srcHeight = (int) (targetHeight * widthRate);
            scaleWH = (float) (1.0 / widthRate);
            x = 0;
            y = (oldHeight - srcHeight) / 2;
        }
        matrix.postScale(scaleWH, scaleWH);
        try {
            return Bitmap.createBitmap(oldbmp, x, y, srcWidth, srcHeight,
                    matrix, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static Bitmap zoomBitmapWithJoke(Bitmap oldbmp, int targetWidth,
                                            int targetHeight) {
        if (oldbmp == null) {
            return null;
        }
        int oldWidth = oldbmp.getWidth();
        int oldHeight = oldbmp.getHeight();
        float widthRate = ((float) oldWidth / targetWidth);
        float heightRate = ((float) oldHeight / targetHeight);
        int srcWidth = oldWidth;
        int srcHeight = oldHeight;
        Matrix matrix = new Matrix();
        float scaleWH;
        if (widthRate >= heightRate) {
            srcWidth = (int) (targetWidth * heightRate);
            scaleWH = (float) (1.0 / heightRate);
        } else {
            srcHeight = (int) (targetHeight * widthRate);
            scaleWH = (float) (1.0 / widthRate);
        }
        matrix.postScale(scaleWH, scaleWH);
        try {
            return Bitmap.createBitmap(oldbmp, 0, 0, srcWidth, srcHeight,
                    matrix, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static Bitmap cutBitmapToSquare(Bitmap oldbmp) {
        if (oldbmp == null) {
            return null;
        }
        int oldWidth = oldbmp.getWidth();
        int oldHeight = oldbmp.getHeight();
        if (oldWidth == oldHeight) {
            return oldbmp;
        }
        Bitmap newbmp = null;
        int offset = 0;
        Matrix matrix = new Matrix();
        if (oldWidth > oldHeight) {
            offset = (oldWidth - oldHeight) / 2;
            newbmp = Bitmap.createBitmap(oldbmp, offset, 0, oldHeight, oldHeight,
                    matrix, true);
        } else {
            offset = (oldHeight - oldWidth) / 2;
            newbmp = Bitmap.createBitmap(oldbmp, 0, offset, oldWidth, oldWidth,
                    matrix, true);
        }
        return newbmp;
    }

    /* 图片圆角处理 */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap,
                                                float roundPx) {
        if (bitmap == null) {
            return null;
        }
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        } catch (OutOfMemoryError e) {
            return null;
        } catch (Exception e){
            return null;
        }
    }

    /* 图片重采样 */
    public static Bitmap getResampleBitmap(byte[] data, int width, int height) {
        Bitmap bmp = null;
        if (data != null) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                // 计算图片缩放比
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }

    public static byte[] getByteArrayFromDrawableResource(Context context, int resId) {
        Bitmap bp = BitmapFactory.decodeResource(context.getResources(), resId);
        return CommonLib.Bitmap2Bytes(bp);
    }

    /*
     * Compute the sample size as a function of minSideLength and
     * maxNumOfPixels. minSideLength is used to specify that minimal width or
     * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
     * pixels that is tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints. Both size
     * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
     * indicates no care of the corresponding constraint. The functions prefers
     * returning a sample size that generates a smaller bitmap, unless
     * minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way. For example,
     * BitmapFactory downsamples an image by 2 even though the request is 3. So
     * we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1
                : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128
                : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static String getForrmatedCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static String twoNumOfStringDoSubtractor(String arg1, String arg2) {
        String result = "-1";
        if (TextUtils.isEmpty(arg2) || TextUtils.isEmpty(arg2)) {
            return result;
        }
        if (isNumeric(arg1) && isNumeric(arg2)) {
            long first = Long.parseLong(arg1);
            long sec = Long.parseLong(arg2);
            return String.valueOf(first - sec);
        }
        return result;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static Pattern getPattern(ArrayList<String> strings) {
        StringBuilder patternString = new StringBuilder();
        patternString.append('(');
        for (String s : strings) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        // Replace the extra '|' with a ')'
        patternString.replace(patternString.length() - 1, patternString.length(), ")");

        return Pattern.compile(patternString.toString());
    }

    public static boolean unzipFile(File zipFile, String toFolder) {
        if (zipFile == null || TextUtils.isEmpty(toFolder)) {
            return false;
        }
        int BUFFER_SIZE = 2048;
        try {
            ZipFile zippedFile = new ZipFile(zipFile);
            new File(toFolder).mkdir();
            Enumeration zipFileEntries = zippedFile.entries();
            // process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(toFolder, currentEntry);
                File destFileParent = destFile.getParentFile();
                destFileParent.mkdirs();
                if (!entry.isDirectory()) {
                    BufferedInputStream bis = null;
                    BufferedOutputStream bos = null;
                    try {
                        bis = new BufferedInputStream(zippedFile.getInputStream(entry));
                        int count;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        bos = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
                        while ((count = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                            bos.write(buffer, 0, count);
                        }
                        bos.flush();
                    } finally {
                        if (bos != null) {
                            bos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void zipFiles(File[] srcFiles, File zipFile) {
        if (zipFile == null || srcFiles == null) {
            return;
        }
        int BUFFER_SIZE = 1024;
        byte[] bytes = new byte[BUFFER_SIZE];
        ZipOutputStream zos = null;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fileOutputStream);

            for (int i = 0; i < srcFiles.length; i++) {
                File srcFile = srcFiles[i];

                if (srcFile == null) {
                    continue;
                }

                FileInputStream fileInputStream = new FileInputStream(srcFile);
                ZipEntry entry = new ZipEntry(srcFile.getName());
                zos.putNextEntry(entry);

                int count;
                while ((count = fileInputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
                    zos.write(bytes, 0, count);
                }
                zos.closeEntry();
                srcFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] convertIOToByte(InputStream io) throws IOException {
        if (io == null) {
            return null;
        }
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(io);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int readBytes = 0;
        while ((readBytes = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, readBytes);
        }
        buffer = null;
        baos.close();
        bis.close();
        bis = null;
        return baos.toByteArray();
    }

    public static void setScrollX(View view, int scrollX) {
        if (view == null) {
            return;
        }

        try {
            Field field = View.class.getDeclaredField("mScrollX");
            field.setAccessible(true);
            field.set(view, scrollX);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setScrollY(View view, int scrollY) {
        if (view == null) {
            return;
        }

        try {
            Field field = View.class.getDeclaredField("mScrollY");
            field.setAccessible(true);
            field.set(view, scrollY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 手机串号，与adb devices显示的sn一致
    public static String getDeviceSerialNo() {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String serial = (String) get.invoke(c, "ro.serialno");
            return serial;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String getDeviceIMEI(Context ctx) {
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getAndroidID(Context context) {
        String androidId = ""
                + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String getIMSI(Context ctx) {
        //只取系统接口返回的IMSI,即使是双卡手机
        TelephonyManager mTelephonyMgr = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = "" + mTelephonyMgr.getSubscriberId();
        return imsi;
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(Context context, long number) {
        String size = "";
        size = Formatter.formatFileSize(context, number);
        return size;
    }

    /**
     * 删除文件夹,使用递归
     */
    public static void delDir(final String filepath) {
        try {
            File f = new File(filepath);
            if (!f.exists()) {
                return;
            }
            if (f.isDirectory()) {
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        delDir(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
                f.delete();
            } else {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断第三方应用是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkAppExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isNullJsObjectString(String jsonString) {
        if (TextUtils.isEmpty(jsonString) || TextUtils.equals(jsonString, "{}")
                || TextUtils.equals(jsonString, "undefined")
                || TextUtils.equals(jsonString, "null")) {
            return true;
        }
        return false;
    }

    /**
     * 得到URL的host
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     */
    public static String getUrlHost(String urlStr) {
        String urlHost = null;
        try {
            URL url = new URL(urlStr);
            urlHost = url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlHost;
    }

    public static boolean CopyDirectory(String src, String des) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(des)) {
            return false;
        }
        File desDir = new File(des);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        File srcDir = new File(src);
        if (!srcDir.exists()) {
            return false;
        }
        File[] file = srcDir.listFiles();
        if (file == null || file.length == 0) {
            return false;
        }
        // 获取源文件夹当前下的文件或目录
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 复制文件
                copyFile(file[i], new File(des + file[i].getName()));
            }
            if (file[i].isDirectory()) {
                // 复制目录
                String sourceDir = src + File.separator + file[i].getName();
                String targetDir = des + File.separator + file[i].getName();
                copyDirectiory(sourceDir, targetDir);
            }
        }
        return true;
    }

    public static void copyFile(File sourceFile, File targetFile) {
        try {
            FileInputStream input = new FileInputStream(sourceFile);
            BufferedInputStream inBuff = new BufferedInputStream(input);
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) {
        if (TextUtils.isEmpty(sourceDir) || TextUtils.isEmpty(targetDir)) {
            return;
        }
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File sources = new File(sourceDir);
        if (!sources.exists()) {
            return;
        }
        File[] file = sources.listFiles();
        if (file == null || file.length == 0) {
            return;
        }
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(
                        new File(targetDir).getAbsolutePath() + File.separator
                                + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    // API Level >= 21 (21 is L)
    public static synchronized boolean isAndroidLOrNewer() {
        return Build.VERSION.SDK_INT >= 21;
    }


    public static void clearDisplayList(View view) {
        Method method;
        try {
            method = View.class.getDeclaredMethod("clearDisplayList", (Class[]) null);
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isApkInstalled(Context ctx, String pkgName) {
        boolean result = false;
        PackageInfo pkgInfo;
        try {
            pkgInfo = ctx.getPackageManager().getPackageInfo(pkgName, 0);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String getFileExtension(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return null;
        }
        String extension = filename.substring(dotIndex + 1).toLowerCase();
        return extension;
    }

    /**
     * 测量翻页按键原始大小值
     */
    public static void measureView(View child) {
        if (child == null) {
            return;
        }
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int childHeightSpec;
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


    public static boolean isAppVisible(Context context) {
        try {
            ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            String myPkgName = context.getPackageName();
            if (getSDKVersion() < 21) {
                RunningTaskInfo info = myManager.getRunningTasks(1).get(0);
                String packageName = info.topActivity.getPackageName();
                return TextUtils.equals(myPkgName, packageName);
            } else {
                // api 21以上，
                // getRunningTask(1)并不准确，除了在launcher为前台时返回launcher的pkg，其它情况都返回自己的包名,所以要用其它方法作判断
                final List<ActivityManager.RunningAppProcessInfo> processInfos = myManager.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                    if (!TextUtils.isEmpty(processInfo.processName) && processInfo.processName.contains(myPkgName)
                            && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 参考: https://zh.wikipedia.org/wiki/移动设备网络代码
     * @param ctx
     * @return <li>未知 Unkonw = 0</li>
     *         <li>中国联通 ChinaUnicom = 1</li>
     *         <li>中国移动 ChinaMobile = 2</li>
     *         <li>中国电信 ChinaTelecom = 3</li>
     *         <li>中国铁通 ChinaTietong = 4</li>
     */
    public static int getProviderName(Context ctx) {
        int providerCode = 0;
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        // bug : note2 无卡返回46001
        String operator = telephonyManager.getNetworkOperator();
        String name = telephonyManager.getNetworkOperatorName();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(operator)
                && TextUtils.isDigitsOnly(operator)) {
            int code = Integer.parseInt(operator);
            switch (code) {
                case 46001:
                case 460001:
                case 46006:
                    providerCode = 1;
                    break;
                case 46000:
                case 460000:
                case 46002:
                case 460002:
                case 46007:
                    providerCode = 2;
                    break;
                case 46003:
                case 460003:
                case 46005:
                case 46011:
                    providerCode = 3;
                    break;
                case 46020:
                    providerCode = 4;
                    break;
                default:
                    break;
            }
        }
        return providerCode;
    }

    public static String getNetworkType(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityMgr.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            return "NONE";
        }
        int networkType = info.getType();
        switch (networkType) {
            case ConnectivityManager.TYPE_WIFI:
                return "WIFI";
            case ConnectivityManager.TYPE_MOBILE:
                return info.getSubtypeName();
            default:
                return "UNKNOWN";
        }
    }

    public static String getWifiIpAddress(Context ctx) {
        String ipAddress = "";
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            WifiInfo wi = wm.getConnectionInfo();
            int ipAdd = wi.getIpAddress();
            ipAddress = (ipAdd & 0xFF) + "." + ((ipAdd >> 8) & 0xFF) + "."
                    + ((ipAdd >> 16) & 0xFF) + "." + (ipAdd >> 24 & 0xFF);
        }
        return ipAddress;
    }

    public static String getNetworkIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断当前版本是否为2.3版
     * @return
     */
    public static boolean isLowVersion(){
        if(CommonLib.getSDKVersion() < 11){
            return true;
        }else{
            return false;
        }
    }

    public static String getVersionRelease(){
        return  Build.VERSION.RELEASE;
    }

    /**
     * 解析色值  #FFFFFF/#FFFFFFFF -> int
     * @param colorString
     * @return
     */
    public static int parseColor(String colorString) {
        int len = colorString.length();
        if(len != 7 && len != 9) {
            return 0;
        }

        if (colorString.charAt(0) != '#') {
            return 0;
        }

        String colorData = colorString.substring(1);

        Pattern p = Pattern.compile("(^[\\da-fA-F]{6}$)|(^[\\da-fA-F]{8}$)");
        Matcher m = p.matcher(colorData);
        if(!m.find()) {
            return 0;
        }

        long color = Long.parseLong(colorData, 16);
        if (colorData.length() == 6) {
            color |= (0xffL << 24);
        }

        return (int)color;
    }

    /**
     * 比较2个版本大小（2.7.0 > 1.5）
     * @param oldVer 旧版本串
     * @param newVer 新版本串
     * @return 1: 新版本号大; 0: 相等; -1: 旧版本大
     */
    public static int compareVersion(String oldVer, String newVer) {
        if(TextUtils.equals(oldVer, newVer)) {
            return 0;
        }

        if(TextUtils.isEmpty(oldVer)) {
            return 1;
        }

        String[] oldVers = oldVer.split("[.]");
        String[] newVers = newVer.split("[.]");

        int size = Math.min(oldVers.length, newVers.length);
        for (int i = 0; i < size; i++) {
            int old = Integer.parseInt(oldVers[i]);
            int ne = Integer.parseInt(newVers[i]);
            if(old > ne) {
                return -1;
            } else if(old < ne) {
                return 1;
            }
        }

        return 0;
    }

    //重启应用;
    public static void restartApp(Activity activity) {
        if (activity == null) {
            return;
        }

        AlarmManager alm = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alm.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
                PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()), 0));
        Process.killProcess(Process.myPid());
    }

    public static boolean installApk(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return false;
        }
        File file = new File(apkPath);
        if (!file.exists()) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

    /**
     * 获取手机基站位置区域码LAC
     *
     * @param context
     * @return
     */
    public static String getCellLAC(Context context) {
        String lac = "";
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String operator = mTelephonyManager.getNetworkOperator();
            int mnc = Integer.parseInt(operator.substring(3));
            if (mnc == 2) {
                // 中国电信
                CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager
                        .getCellLocation();
                lac = String.valueOf(location1.getNetworkId());
            } else {
                // 中国移动和中国联通
                GsmCellLocation location2 = (GsmCellLocation) mTelephonyManager
                        .getCellLocation();
                lac = String.valueOf(location2.getLac());
            }
        } catch (Exception e) {
        }
        return lac;
    }

    /**
     * 获取WIFI SSID
     *
     * @param context
     * @return
     */
    public static String getWifiSSID(Context context) {
        String ssid = "";
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ssid = wifiInfo.getSSID();
        } catch (Exception e) {
        }
        return ssid;
    }

    /**
     * 从assets中读取文件，返回字符串
     */
    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open(fileName);
            byte[] buffer =  readStream(in);
            result = EncodingUtils.getString(buffer, "UTF-8");// 你的文件的编码
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean deCompressZipFile(String srcPath, String desPath, String desName) throws IOException {
        File desFile = new File(desPath);
        if(desFile.exists() && !desFile.delete()) {
            return false;
        }else{
            File parFile = desFile.getParentFile();
            if (!parFile.exists()) {
                parFile.mkdirs();
            }

            try {
                if (!desFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }


        BufferedInputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream dest = null;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(srcPath);
            ZipEntry entry = zipfile.getEntry(desName);
            if(entry == null) {
                return false;
            }
            is = new BufferedInputStream(zipfile.getInputStream(entry));
            fos = new FileOutputStream(desFile);
            dest = new BufferedOutputStream(fos);
            int count = 0;
            byte data[] = new byte[1024];
            while((count = is.read(data)) != -1) {
                dest.write(data, 0, count);
            }

            dest.close();
            is.close();

            //删除原压缩文件
            //居然删除了？？？？
//            new File(srcPath).delete();
        } catch (IOException e) {
            return false;
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if(zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                }
            }
            if(dest != null) {
                try {
                    dest.close();
                } catch (IOException e) {
                }
            }
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return true;
    }


    /**
     * 4.0以下系统String 生成 jsonobject之前需要调用此函数对String 进行处理
     * consume an optional byte order mark (BOM) if it exists
     * @param jsonString
     * @return
     */
    public static String subJSON4BOM(String jsonString){
        if(jsonString != null && jsonString.startsWith("\ufeff")){
            jsonString =  jsonString.substring(1);
        }
        return jsonString;
    }
    public static int getScreenMinInWidthAndHeight(Context context){
        int width = getScreenWidth(context);
        int height = getScreenHeight(context);
        return Math.min(width, height);
    }

    /**
     * 获取手机当前使用的输入法包名,返回可能为null
     * @param context
     * @return
     */
    public static String getCurIME(Context context) {
        if (context == null) {
            return null;
        }
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    /**
     * 判断一个方法是否存在
     * @param className 类的名称
     * @param methodName 方法名称
     * @param parameterTypes 方法需要的参数所对应的类, 如：Integer.class，String.class
     */
    public static boolean isMethodExists(String className, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = Class.forName(className).getMethod(methodName, parameterTypes);
            if (method != null) {
                return true;
            }

            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 获得当前的进程名
     */
    public static String getCurrentProcessName(Context context) {
        //fix crash 1015 5.0.6.142710 java.lang.NullPointerException  增加保护
        try {
            int pid = android.os.Process.myPid();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isSystemApp(Context context) {
        boolean isSystem = false;
        if (context != null) {
            String packageName = context.getPackageName();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    isSystem = true;
                } else {
                    isSystem = false;
                }
            } catch (NameNotFoundException e) {
                isSystem = false;
            }
        }
        return isSystem;
    }

}
