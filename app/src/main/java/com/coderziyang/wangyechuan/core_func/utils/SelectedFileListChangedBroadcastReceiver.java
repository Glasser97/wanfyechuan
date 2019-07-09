package com.coderziyang.wangyechuan.core_func.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class SelectedFileListChangedBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = SelectedFileListChangedBroadcastReceiver.class.getSimpleName();

    public static final String ACTION_CHOOSE_FILE_LIST_CHANGED = "ACTION_CHOOSE_FILE_LIST_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if(action.equals(ACTION_CHOOSE_FILE_LIST_CHANGED)){
            Log.i(TAG,"ACTION_CHOOSE_FILE_LIST_CHANGED--->>>");
            onSelectedFileListChanged();
        }
    }

    /**
     * 选中传送的文件改变
     */
    public abstract void onSelectedFileListChanged();
}
