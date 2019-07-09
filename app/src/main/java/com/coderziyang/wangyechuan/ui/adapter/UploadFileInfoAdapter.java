package com.coderziyang.wangyechuan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coderziyang.wangyechuan.Common.CommonAdapter;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.entity.FileInfo;

import java.util.List;

public class UploadFileInfoAdapter extends CommonAdapter<FileInfo> {
    /**
     * 文件类型的标识
     */
    private int mType = FileInfo.TYPE_APK;
    List<FileInfo> fileInfoList;

    public UploadFileInfoAdapter(Context context, List<FileInfo> dataList){
        super(context,dataList);
    }

    public UploadFileInfoAdapter(Context context, List<FileInfo> dataList, int type){
        super(context,dataList);
        this.mType = type;
        this.fileInfoList = dataList;
    }


    @Override
    public View convertView(int position, View convertView){
        FileInfo fileInfo = getmDataList().get(position);
        if(mType == FileInfo.TYPE_APK){
            FileInfoAdapter.ApkViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.item_apk,null);
                viewHolder = new FileInfoAdapter.ApkViewHolder();
                viewHolder.iv_shortcut = (ImageView)convertView.findViewById(R.id.iv_shortcut);
                viewHolder.iv_ok_tick = (ImageView)convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (FileInfoAdapter.ApkViewHolder) convertView.getTag();
            }
            if(getmDataList() != null && getmDataList().get(position) != null){
                viewHolder.iv_shortcut.setImageBitmap(fileInfo.getBitmap());
                viewHolder.tv_name.setText(fileInfo.getName() == null ? "":fileInfo.getName());
                viewHolder.tv_size.setText(fileInfo.getSizeDesc() == null? "":fileInfo.getSizeDesc());
            }
        }
        else if(mType == FileInfo.TYPE_JPG){ //JPG convertView
            FileInfoAdapter.JpgViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.item_jpg, null);
                viewHolder = new FileInfoAdapter.JpgViewHolder();
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (FileInfoAdapter.JpgViewHolder) convertView.getTag();
            }

            if(getmDataList() != null && getmDataList().get(position) != null){

//                viewHolder.iv_shortcut.setImageBitmap(fileInfo.getBitmap());
                Glide.with(getContext())
                        .load(fileInfo.getFilePath())
                        .centerCrop()
                        .placeholder(R.mipmap.icon_jpg)
                        .into(viewHolder.iv_shortcut);
            }
        }

        else if(mType == FileInfo.TYPE_MP3){ //MP3 convertView
            FileInfoAdapter.Mp3ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.item_mp3, null);
                viewHolder = new FileInfoAdapter.Mp3ViewHolder();
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (FileInfoAdapter.Mp3ViewHolder) convertView.getTag();
            }

            if(getmDataList() != null && getmDataList().get(position) != null){
                viewHolder.tv_name.setText(fileInfo.getName() == null ? "" : fileInfo.getName());
                viewHolder.tv_size.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());
            }
        }

        else if(mType == FileInfo.TYPE_MP4){ //MP4 convertView
            FileInfoAdapter.Mp4ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.item_mp4, null);
                viewHolder = new FileInfoAdapter.Mp4ViewHolder();
                //viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (FileInfoAdapter.Mp4ViewHolder) convertView.getTag();
            }

            if(getmDataList() != null && getmDataList().get(position) != null){
                //viewHolder.iv_shortcut.setImageBitmap(fileInfo.getBitmap());
                viewHolder.tv_name.setText(fileInfo.getName() == null ? "" : fileInfo.getName());
                viewHolder.tv_size.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());
            }
        }
        return convertView;
    }

    public void remove(int position){
        if(fileInfoList != null){
            fileInfoList.remove(position);
        }
        notifyDataSetChanged();
    }

}
