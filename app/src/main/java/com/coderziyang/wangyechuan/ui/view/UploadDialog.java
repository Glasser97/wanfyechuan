package com.coderziyang.wangyechuan.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.micro_server.UploadService;

public class UploadDialog implements View.OnClickListener{


    Context mContext;
    AlertDialog mAlertDialog;
    String ipAddr;

    /**
     *UI控件
     */
     Button btn_set_network;
     Button btn_cancel;
     TextView address_tip;

     public void ShowUploadDialog(Context context,String ipaddr){
         this.mContext = context;
         this.ipAddr = ipaddr;
         View contentView = View.inflate(mContext, R.layout.dialog_upload,null);
         btn_set_network = contentView.findViewById(R.id.shared_wifi_settings);
         btn_cancel = contentView.findViewById(R.id.shared_wifi_cancel);
         address_tip = contentView.findViewById(R.id.popup_menu_title);
         btn_set_network.setOnClickListener(this);
         btn_cancel.setOnClickListener(this);

         address_tip.setText(mContext.getResources().getString(R.string.tip_upload_please_link).replaceAll("\\{IpAddress\\}",ipAddr));

         this.mAlertDialog = new AlertDialog.Builder(mContext)
                 .setView(contentView).create();
     }

     public void show(){
         if (mAlertDialog != null){
             this.mAlertDialog.show();
         }
     }

     public void hide(){
         if(this.mAlertDialog != null ){
             this.mAlertDialog.hide();
         }
     }




    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.shared_wifi_cancel:{
                 hide();
                 UploadService.stop(mContext);
                 break;
             }
             case R.id.shared_wifi_settings:{
                 mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                 break;
             }
         }

    }
}
