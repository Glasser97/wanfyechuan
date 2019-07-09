package com.coderziyang.wangyechuan.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coderziyang.wangyechuan.AppContext;
import com.coderziyang.wangyechuan.Common.BaseActivity;
import com.coderziyang.wangyechuan.Constant;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.core_func.utils.TextUtils;
import com.coderziyang.wangyechuan.core_func.utils.ToastUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.coderziyang.wangyechuan.micro_server.DownloadResUriHandler;
import com.coderziyang.wangyechuan.micro_server.IOStreamUtil;
import com.coderziyang.wangyechuan.micro_server.ImageResUriHandler;
import com.coderziyang.wangyechuan.micro_server.IndexResUriHandler;
import com.coderziyang.wangyechuan.micro_server.MicroServer;
import com.coderziyang.wangyechuan.utils.ClassifyUtils;
import com.coderziyang.wangyechuan.utils.WifiMgr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WebTransferActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = WebTransferActivity.class.getSimpleName();
    public static final int REQUEST_CODE_WRITE_SCREENSHOT=200;

    /**
     * 声明UI
     */
    TextView tv_back;
    TextView tv_title;
    TextView tv_tip1;
    TextView tv_tip2;


    boolean mIsInitialized = false;
    MicroServer mMicroServer = null;
    String ssid;
    String ipAddr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_transfer);
        tv_back = findViewById(R.id.tv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_tip1 = findViewById(R.id.tv_tip_1);
        tv_tip2 = findViewById(R.id.tv_tip_2);
        tv_back.setOnClickListener(this);
        tv_title.setOnClickListener(this);
        tv_tip1.setOnClickListener(this);
        tv_tip2.setOnClickListener(this);
        init();
    }

    /**
     * 按下返回键
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        closeServer();
        AppContext.getAppContext().getFileInfoMap().clear();
        this.finish();
    }

    /**
     * 初始化
     */
    private void init(){
        //确保连接Wifi
        if (!WifiMgr.getInstance(getContext()).isWifiEnable()){
            ToastUtils.show(getContext(),getResources().getString(R.string.tip_wifi_is_not_enable));
            //onBackPressed();
        }else{
            if(!mIsInitialized){
                try{
                    ssid = WifiMgr.getInstance(getContext()).getWifiSSid();
                    Log.d(TAG,"SSid====>"+ssid);
                    ipAddr = WifiMgr.getInstance(getContext()).getLocalIpAddr();
                    Log.d(TAG,"ipAddr====>"+ipAddr);
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_WRITE_SCREENSHOT);
                    }else{
                        AppContext.MAIN_EXECUTOR.execute(createServer());
                    }
                    mIsInitialized = true;
                }catch(Exception e){
                    e.printStackTrace();
                    mIsInitialized = false;
                }
            }
            initUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        if(requestCode == REQUEST_CODE_WRITE_SCREENSHOT){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                AppContext.MAIN_EXECUTOR.execute(createServer());
            }else{
                ToastUtils.show(this,getResources().getString(R.string.tip_permission_denied_and_not_get_file_info_list));
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    /**
     * createServer
     * 创建一个服务器
     */
    public Runnable createServer(){
        return new Runnable() {
            @Override
            public void run() {
                try{
                    // TODO
                    int count = 0;
                    while(!WifiMgr.getInstance(getContext()).isWifiEnable() && count<Constant.DEFAULT_TRY_TIME){
                        ToastUtils.show(getContext(),getResources().getString(R.string.tip_wifi_is_not_enable));
                        Thread.sleep(1000);
                        count++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Log.d(TAG,"May not connected to the WIFI!!!");
                }
                mMicroServer = new MicroServer(Constant.DEFAULT_MICRO_SERVER_PORT);
                mMicroServer.registerResUriHandler(new MyIndexResUriHandler(WebTransferActivity.this,AppContext.getAppContext().getFileInfoMap(),ipAddr));
                mMicroServer.registerResUriHandler(new ImageResUriHandler(WebTransferActivity.this));
                mMicroServer.registerResUriHandler(new DownloadResUriHandler(WebTransferActivity.this));
                mMicroServer.start();
            }
        };
    }

    /**
     * 初始化UI
     */
    private void initUI(){
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_web_transfer));

        String normalColor = "#ff000000";
        String highlightColor = "#1467CD";
        //指定wifi名称，读取wifi地址
        String tip1 = getResources().getString(R.string.tip_web_transfer_first_tip).replace("{hotspot}", ssid);

        String[] tip1StringArray = tip1.split("\\n");
        Spanned tip1Spanned = Html.fromHtml("<font color='"+normalColor+"'>"+tip1StringArray[0].trim()+"</font><br>"
        +"<font color='"+normalColor+"'>"+tip1StringArray[1].trim()+"</font><br>"
        +"<font color='"+highlightColor+"'>"+tip1StringArray[2].trim()+"</font>");
        tv_tip1.setText(tip1Spanned);

        String tip2 = getResources().getString(R.string.tip_web_transfer_second_tip).replace("{IpAddress}",ipAddr);

        String[] tip2StringArray = tip2.split("\\n");
        Spanned tip2Spanned = Html.fromHtml("<font color='"+normalColor+"'>"+tip2StringArray[0].trim()+"</font><br>"
        +"<font color='"+normalColor+"'>"+tip2StringArray[1].trim()+"</font><br>"
        +"<font color='"+highlightColor+"'>"+tip2StringArray[2].trim()+"</font><br>"
                +"<font color='"+normalColor+"'>"+tip2StringArray[3].trim()+"</font><br>");
        tv_tip2.setText(tip2Spanned);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_back:{
                onBackPressed();
                break;
            }
        }
    }

    /**
     * 关闭MicroServer
     */
    private void closeServer(){
        if (mMicroServer != null){
            mMicroServer.stop();
            mMicroServer = null;
        }
    }


    static class MyIndexResUriHandler extends IndexResUriHandler{
        String DOWNLOAD_PREFIX;
        String IMAGE_PREFIX;
        String DEFAULT_IMAGE_PATH;

        Activity sActivity;
        Map<String, FileInfo> sFileInfoMap = null;

        public MyIndexResUriHandler(Activity activity,String ipAddr){
            super(activity);
            this.sActivity = activity;
            DOWNLOAD_PREFIX = "http://"+ipAddr+":3999/download/";
            IMAGE_PREFIX = "http://"+ipAddr+":3999/image/";
            DEFAULT_IMAGE_PATH = "http://"+ipAddr+":3999/image/logo.png";
        }
        public MyIndexResUriHandler(Activity activity,Map<String, FileInfo> fileInfoMap,String ipAddr){
            super(activity);
            this.sActivity = activity;
            this.sFileInfoMap = fileInfoMap;
            DOWNLOAD_PREFIX = "http://"+ipAddr+":3999/download/";
            IMAGE_PREFIX = "http://"+ipAddr+":3999/image/";
            DEFAULT_IMAGE_PATH = "http://"+ipAddr+":3999/image/logo.png";
        }

        @Override
        public String convert(String indexHtml){
            StringBuilder allFileListInfoHtmlBuilder = new StringBuilder();
            int count = this.sFileInfoMap.size();
            indexHtml = indexHtml.replaceAll("\\{app_avatar\\}", DEFAULT_IMAGE_PATH);
            indexHtml = indexHtml.replaceAll("\\{app_path\\}", DOWNLOAD_PREFIX);
            indexHtml = indexHtml.replaceAll("\\{app_name\\}", this.sActivity.getResources().getString(R.string.app_name));
            String name =TextUtils.isNullOrBlank(android.os.Build.DEVICE) ? Constant.DEFAULT_SSID : android.os.Build.DEVICE;
            indexHtml = indexHtml.replaceAll("\\{file_share\\}", name);
            indexHtml = indexHtml.replaceAll("\\{file_count\\}", String.valueOf(count));

            List<FileInfo> apkInfos = ClassifyUtils.filter(this.sFileInfoMap,FileInfo.TYPE_APK);
            List<FileInfo> jpgInfos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_JPG);
            List<FileInfo> mp3Infos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_MP3);
            List<FileInfo> mp4Infos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_MP4);

            try{
                String apkInfosHtml = getClassifyFileInfoListHtml(apkInfos,FileInfo.TYPE_APK);
                String jpgInfosHtml = getClassifyFileInfoListHtml(jpgInfos,FileInfo.TYPE_JPG);
                String mp3InfosHtml = getClassifyFileInfoListHtml(mp3Infos,FileInfo.TYPE_MP3);
                String mp4InfosHtml = getClassifyFileInfoListHtml(mp4Infos,FileInfo.TYPE_MP4);
                Log.d(TAG,"ApkHtml======>>"+apkInfosHtml);
//                Log.d(TAG,"JpgHtml======>>"+jpgInfosHtml);
//                Log.d(TAG,"Mp3Html======>>"+mp3InfosHtml);
//                Log.d(TAG,"Mp4Html======>>"+mp4InfosHtml);

                allFileListInfoHtmlBuilder.append(apkInfosHtml);
                allFileListInfoHtmlBuilder.append(jpgInfosHtml);
                allFileListInfoHtmlBuilder.append(mp3InfosHtml);
                allFileListInfoHtmlBuilder.append(mp4InfosHtml);
                Log.d(TAG,"FilesHtml===>>>"+allFileListInfoHtmlBuilder.toString());
                indexHtml = indexHtml.replaceAll("\\{file_list_template\\}", allFileListInfoHtmlBuilder.toString());
                Log.d(TAG,"indexHtml===>>>"+indexHtml);
            }catch (IOException e){
                e.printStackTrace();
            }
            return indexHtml;
        }

        /**
         * 获取指定文件类型的html字符串
         * @param fileInfos
         * @throws IOException
         */
        private String getFileInfoListHtml(List<FileInfo> fileInfos) throws IOException {
            StringBuilder sb = new StringBuilder();
            for(FileInfo fileInfo:fileInfos){
                String fileInfoHtml = IOStreamUtil.inputStreamToString(sActivity.getAssets().open(Constant.NAME_FILE_TEMPLATE));
                Log.d(TAG,"fileInfoHtml===>>>"+fileInfoHtml);
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_avatar\\}", IMAGE_PREFIX + FileUtils.getFileName(fileInfo.getFilePath()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_name\\}", FileUtils.getFileName(fileInfo.getFilePath()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_size\\}", FileUtils.getFileSize(fileInfo.getFileSize()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_path\\}", DOWNLOAD_PREFIX + FileUtils.getFileName(fileInfo.getFilePath()));
                Log.d(TAG,"fileInfoHtml2===>>>"+fileInfoHtml);

                sb.append(fileInfoHtml);
            }
            return sb.toString();
        }


        /**
         * 获取大类别的Html字符串
         */
        private String getClassifyFileInfoListHtml(List<FileInfo> fileInfos,int type) throws IOException {
            Log.d(TAG,"fileInfos.size======>"+fileInfos.size());
            if(fileInfos == null || fileInfos.size()<= 0){
                return "";
            }
            String classifyHtml = IOStreamUtil.inputStreamToString(sActivity.getAssets().open(Constant.NAME_CLASSIFY_TEMPLATE));
            Log.d(TAG,"classifyHtml===>>>"+classifyHtml);
            String className = "";
            switch(type){
                case FileInfo.TYPE_APK:{
                    className = sActivity.getResources().getString(R.string.str_apk_desc);
                    break;
                }
                case FileInfo.TYPE_JPG:{
                    className = sActivity.getResources().getString(R.string.str_jpeg_desc);
                    break;
                }
                case FileInfo.TYPE_MP3:{
                    className = sActivity.getResources().getString(R.string.str_mp3_desc);
                    break;
                }
                case FileInfo.TYPE_MP4:{
                    className = sActivity.getResources().getString(R.string.str_mp4_desc);
                    break;
                }
            }
            classifyHtml = classifyHtml.replaceAll("\\{class_name\\}", className);
            classifyHtml = classifyHtml.replaceAll("\\{class_count\\}", String.valueOf(fileInfos.size()));
            classifyHtml = classifyHtml.replaceAll("\\{file_list\\}", getFileInfoListHtml(fileInfos));
            Log.d(TAG,"classifyHtml2===>>>"+classifyHtml);
            return classifyHtml;
        }
    }


}
