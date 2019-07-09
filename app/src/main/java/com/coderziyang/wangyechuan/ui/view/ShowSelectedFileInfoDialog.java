package com.coderziyang.wangyechuan.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.coderziyang.wangyechuan.AppContext;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.core_func.utils.SelectedFileListChangedBroadcastReceiver;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.coderziyang.wangyechuan.ui.adapter.FileInfoSelectedAdapter;

import java.util.Map;
import java.util.Set;

public class ShowSelectedFileInfoDialog implements View.OnClickListener{

    /**
     * UI控件
     */
    Button btn_operation;
    TextView tv_title;
    ListView lv_result;

    Context mContext;
    AlertDialog mAlertDialog;
    FileInfoSelectedAdapter mFileInfoSelectedAdapter;

    public ShowSelectedFileInfoDialog(Context context){
        this.mContext = context;

        View contentView = View.inflate(mContext,R.layout.view_show_selected_file_info_dialog,null);
        btn_operation = contentView.findViewById(R.id.btn_operation);
        tv_title = contentView.findViewById(R.id.tv_title);
        lv_result = contentView.findViewById(R.id.lv_result);

        String title = getAllSelectedDes();
        tv_title.setText(title);

        mFileInfoSelectedAdapter = new FileInfoSelectedAdapter(mContext);
        mFileInfoSelectedAdapter.setOnDataListChangedListener(new FileInfoSelectedAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                if (mFileInfoSelectedAdapter.getCount() == 0){
                    hide();
                }
                tv_title.setText(getAllSelectedDes());
                sendUpdateSelectedFileBR();
                //发送更新选择文件的广播
            }
        });

        lv_result.setAdapter(mFileInfoSelectedAdapter);
        btn_operation.setOnClickListener(this);


        this.mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(contentView).create();
    }

    /**
     * 更新选择文件的广播
     */
    private void sendUpdateSelectedFileBR(){
        mContext.sendBroadcast(new Intent(SelectedFileListChangedBroadcastReceiver.ACTION_CHOOSE_FILE_LIST_CHANGED));
        Log.d(ShowSelectedFileInfoDialog.class.getSimpleName(),"NOT FINISHED YET");
    }


    /**
     * 获取选中文件的对话框的Title
     * @return
     */
    private String getAllSelectedDes(){
        String title = "";
        long totalSize = 0;
        Set<Map.Entry<String, FileInfo>> entrySet = AppContext.getAppContext().getFileInfoMap().entrySet();
        for(Map.Entry<String, FileInfo> entry:entrySet){
            FileInfo fileInfo = entry.getValue();
            totalSize += fileInfo.getFileSize();
        }

        title = mContext.getResources().getString(R.string.str_selected_file_info_detail)
                .replace("{count}",String.valueOf(entrySet.size()))
                .replace("{size}", FileUtils.getFileSize(totalSize));
        return title;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_operation:{
                clearAllSelectedFiles();
                sendUpdateSelectedFileBR();
                //tv_title.setText(getAllSelectedDes());
                break;
            }
        }
    }
    /**
     * 清除所有的文件
     */
    public void clearAllSelectedFiles(){
        AppContext.getAppContext().getFileInfoMap().clear();
        if(mFileInfoSelectedAdapter != null){

        }
        this.hide();
    }

    /**
     * 显示
     */
    public void show(){
        if(this.mAlertDialog != null){
            notifyDataSetChanged();
            tv_title.setText(getAllSelectedDes());
            this.mAlertDialog.show();
        }
    }

    /**
     * 隐藏
     */
    public void hide(){
        if(this.mAlertDialog != null ){
            this.mAlertDialog.hide();
        }
    }

    /**
     * 通知列表发生变化
     */
    public void notifyDataSetChanged(){
        if(mFileInfoSelectedAdapter != null){
            mFileInfoSelectedAdapter.notifyDataSetChanged();
        }
    }



}
