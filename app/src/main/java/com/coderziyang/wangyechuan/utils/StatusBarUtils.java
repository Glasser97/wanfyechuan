package com.coderziyang.wangyechuan.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.lang.annotation.Target;

public class StatusBarUtils {
    public static void setWindowStatusBarColor(Activity activity,int colorResId){
        try{
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                Window window=activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 设置StatusBar和BottomBar透明
     * 需要在 setContentView(R.layout.xx) 的布局文件设置
     *     android:fitsSystemWindows="true"
     *     android:background="@color/colorPrimary"
     * @param activity
     **/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarAndBottomBarTranslucent(Activity activity){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window=activity.getWindow();
            //Transparent StatusBar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Transparent BottomBar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
