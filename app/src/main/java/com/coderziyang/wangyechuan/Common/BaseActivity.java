package com.coderziyang.wangyechuan.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.coderziyang.wangyechuan.utils.StatusBarUtils;

public class BaseActivity extends AppCompatActivity {
    /**写入文件的请求码**/
    public static final int REQUEST_CODE_WRITE_FILE=200;
    /**读取文件的请求码**/
    public static final int REQUEST_CODE_READ_FILE=201;
    /**打开GPS的请求码**/
    public static final int REQUEAT_CODE_OPEN_GPS=205;

    Context mContext;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        mContext=this;
        StatusBarUtils.setStatusBarAndBottomBarTranslucent(this);
        super.onCreate(savedInstanceState);
    }
    /**
     * 获取上下文信息
     **/
    public Context getContext(){
        return mContext;
    }

    protected void showProgressBar(){
        if (mProgressBar==null){
            mProgressBar=new ProgressBar(mContext);
        }
        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    protected void hideProgressBar(){
        if(mProgressBar!=null && mProgressBar.getVisibility()==View.VISIBLE){
            mProgressBar.setVisibility(View.GONE);
            mProgressBar=null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
