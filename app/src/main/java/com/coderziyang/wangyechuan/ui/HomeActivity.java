package com.coderziyang.wangyechuan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.coderziyang.wangyechuan.Common.BaseActivity;
import com.coderziyang.wangyechuan.R;

public class HomeActivity extends BaseActivity implements View.OnClickListener{

    Button send_button;
    Button upload_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        send_button = findViewById(R.id.btn_send);
        upload_button = findViewById(R.id.btn_upload);
        send_button.setOnClickListener(this);
        upload_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId()==R.id.btn_send){
            Intent intent = new Intent(HomeActivity.this,ChooseFileActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.btn_upload){
            Intent intent = new Intent(HomeActivity.this,UploadActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
