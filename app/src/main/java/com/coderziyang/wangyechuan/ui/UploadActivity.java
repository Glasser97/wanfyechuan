package com.coderziyang.wangyechuan.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderziyang.wangyechuan.Common.BaseActivity;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.core_func.utils.ToastUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.coderziyang.wangyechuan.micro_server.UploadService;
import com.coderziyang.wangyechuan.ui.view.UploadDialog;
import com.coderziyang.wangyechuan.ui.view.UploadFileFragment;
import com.coderziyang.wangyechuan.utils.WifiMgr;

public class UploadActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = UploadActivity.class.getSimpleName();

    /**
     * 获取文件的请求码
     */
    public static final int REQUEST_CODE_GET_FILE_INFOS = 200;

    /**
     * activity中的控件们
     */
    TextView tv_back;
    ImageView iv_search;
    TextView tv_title;
    TabLayout tab_layout;
    ViewPager view_pager;
    FloatingActionButton fab;

    /**
     * 用于Viewpager的fragments
     * 应用，图片，音频，视频，文件Fragment
     */
    UploadFileFragment mCurrentFragment;
    UploadFileFragment mApkInfoFragment;
    UploadFileFragment mJpgInfoFragment;
    UploadFileFragment mMp3InfoFragment;
    UploadFileFragment mMp4InfoFragment;

    UploadDialog mUploadDialog = new UploadDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        tv_back = findViewById(R.id.tv_back);
        iv_search = findViewById(R.id.iv_search);
        tv_title = findViewById(R.id.tv_title);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);
        fab = findViewById(R.id.fab);
        tv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        fab.setOnClickListener(this);
        init();

    }


    /**
     * 初始化
     */
    private void init(){
        // TODO
        tv_title.setText(getResources().getString(R.string.title_file_receiver));
        tv_title.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.INVISIBLE);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GET_FILE_INFOS);
        }else{
            initData();
        }


    }

    private void initData(){
        String ipAddr = WifiMgr.getInstance(getContext()).getLocalIpAddr();
        Log.d(TAG,"ipAddr===>>>"+ipAddr);
        mUploadDialog.ShowUploadDialog(getContext(),ipAddr);

        mApkInfoFragment = UploadFileFragment.newInstance(FileInfo.TYPE_APK);
        mJpgInfoFragment = UploadFileFragment.newInstance(FileInfo.TYPE_JPG);
        mMp3InfoFragment = UploadFileFragment.newInstance(FileInfo.TYPE_MP3);
        mMp4InfoFragment = UploadFileFragment.newInstance(FileInfo.TYPE_MP4);
        mCurrentFragment = mApkInfoFragment;

        String[] titles = getResources().getStringArray(R.array.array_res);
        view_pager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(),titles));
        view_pager.setOffscreenPageLimit(4);
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        if(requestCode == REQUEST_CODE_GET_FILE_INFOS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initData();
            }else{
                ToastUtils.show(this,getResources().getString(R.string.tip_permission_denied_and_not_get_file_info_list));
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }




    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_back:{
                this.finish();
                break;
            }
            case R.id.fab:{
                UploadService.start(getContext());
                mUploadDialog.show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /**
     * 资源的PagerAdapter
     */
    class ResPagerAdapter extends FragmentPagerAdapter {
        String[] sTitleArray;

        public ResPagerAdapter(FragmentManager fm){
            super(fm);
        }

        public ResPagerAdapter(FragmentManager fm, String[] sTitleArray){
            this(fm);
            this.sTitleArray = sTitleArray;
        }

        @Override
        public Fragment getItem(int position){
            if(position == 0){
                mCurrentFragment = mApkInfoFragment;
            }else if(position == 1){
                mCurrentFragment = mJpgInfoFragment;
            }else if(position == 2){
                mCurrentFragment = mMp3InfoFragment;
            }else if(position == 3){
                mCurrentFragment = mMp4InfoFragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount(){
            return sTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return sTitleArray[position];
        }
    }

    @Override
    public void onDestroy(){
        this.mUploadDialog = null;
        super.onDestroy();
    }

}
