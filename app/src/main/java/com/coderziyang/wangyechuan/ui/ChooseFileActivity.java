package com.coderziyang.wangyechuan.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coderziyang.wangyechuan.AppContext;
import com.coderziyang.wangyechuan.Common.BaseActivity;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.core_func.utils.SelectedFileListChangedBroadcastReceiver;
import com.coderziyang.wangyechuan.core_func.utils.ToastUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.coderziyang.wangyechuan.ui.view.FileInfoFragment;
import com.coderziyang.wangyechuan.ui.view.ShowSelectedFileInfoDialog;

public class ChooseFileActivity extends BaseActivity implements View.OnClickListener{

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
    Button btn_selected;
    Button btn_next;
    TabLayout tab_layout;
    ViewPager view_pager;

    /**
     * 用于Viewpager的fragments
     * 应用，图片，音频，视频，文件Fragment
     */
    FileInfoFragment mCurrentFragment;
    FileInfoFragment mApkInfoFragment;
    FileInfoFragment mJpgInfoFragment;
    FileInfoFragment mMp3InfoFragment;
    FileInfoFragment mMp4InfoFragment;

    /**
     *更新文件列表的广播
     */
    SelectedFileListChangedBroadcastReceiver mSelectedFileListChangedBroadcastReceiver = null;

    /**
     *选中文件列表的对话框
     */
    ShowSelectedFileInfoDialog mShowSelectedFileInfoDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);
        tv_back = findViewById(R.id.tv_back);
        iv_search = findViewById(R.id.iv_search);
        tv_title = findViewById(R.id.tv_title);
        btn_selected = findViewById(R.id.btn_selected);
        btn_next = findViewById(R.id.btn_next);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);
        tv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        btn_selected.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        init();
    }

    @Override
    protected void onDestroy(){
        if(mSelectedFileListChangedBroadcastReceiver != null){
            unregisterReceiver(mSelectedFileListChangedBroadcastReceiver);
            mSelectedFileListChangedBroadcastReceiver = null;
        }
        super.onDestroy();
    }


    private void init(){
        tv_title.setText(getResources().getString(R.string.title_choose_file));
        tv_title.setVisibility(View.VISIBLE);

        iv_search.setVisibility(View.INVISIBLE);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GET_FILE_INFOS);
        }else{
            initData();
        }
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


    /**
     * 初始化数据
     */
    private void initData(){
        mApkInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_APK);
        mJpgInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_JPG);
        mMp3InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP3);
        mMp4InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP4);
        mCurrentFragment = mApkInfoFragment;

        String[] titles = getResources().getStringArray(R.array.array_res);
        view_pager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(),titles));
        view_pager.setOffscreenPageLimit(4);
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

        mShowSelectedFileInfoDialog =  new ShowSelectedFileInfoDialog(getContext());

        mSelectedFileListChangedBroadcastReceiver = new SelectedFileListChangedBroadcastReceiver() {
            @Override
            public void onSelectedFileListChanged() {
                update();
                Log.i(TAG, "========update file list");
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SelectedFileListChangedBroadcastReceiver.ACTION_CHOOSE_FILE_LIST_CHANGED);
        registerReceiver(mSelectedFileListChangedBroadcastReceiver,intentFilter);

    }

    /**
     * 更新选中文件列表的状态
     */
    private void update(){
        if(mApkInfoFragment != null) mApkInfoFragment.updateFileInfoAdapter();
        if(mJpgInfoFragment != null) mJpgInfoFragment.updateFileInfoAdapter();
        if(mMp3InfoFragment != null) mMp3InfoFragment.updateFileInfoAdapter();
        if(mMp3InfoFragment != null) mMp4InfoFragment.updateFileInfoAdapter();

        getSelectedView();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_back:{
                this.finish();
                break;
            }
            case R.id.btn_selected:{
                if(mShowSelectedFileInfoDialog != null){
                    mShowSelectedFileInfoDialog.show();
                }
                break;
            }
            case R.id.btn_next:{
                if(!AppContext.getAppContext().isFileInfoMapExist()){
                    ToastUtils.show(getContext(), getResources().getString(R.string.tip_please_select_your_file));
                    return;
                }

                Intent intent = new Intent(ChooseFileActivity.this,WebTransferActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.iv_search:{
                btn_selected.setEnabled(true);
                btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
                btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary,getTheme()));
                break;
            }
        }
    }

    /**
     * 获取选中的View
     */
    public View getSelectedView(){
        if(AppContext.getAppContext().getFileInfoMap() != null && AppContext.getAppContext().getFileInfoMap().size()>0){
            setSelectedViewStyle(true);
            int size = AppContext.getAppContext().getFileInfoMap().size();
            btn_selected.setText(getContext().getResources().getString(R.string.str_has_selected_detail,size));
        }else{
            setSelectedViewStyle(false);
            btn_selected.setText(getContext().getResources().getString(R.string.str_has_selected));
        }

        return btn_selected;
    }

    /**
     * 设置选中View的样式
     * @param isEnable
     */
    private void setSelectedViewStyle(boolean isEnable){
        if(isEnable){
            btn_selected.setEnabled(true);
            btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
            btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary,getTheme()));
        }else{
            btn_selected.setEnabled(false);
            btn_selected.setBackgroundResource(R.drawable.shape_bottom_text_unenable);
            btn_selected.setTextColor(getResources().getColor(R.color.darker_gray,getTheme()));
        }
    }



    /**
     * 资源的PagerAdapter
     */
    class ResPagerAdapter extends FragmentPagerAdapter{
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

}
