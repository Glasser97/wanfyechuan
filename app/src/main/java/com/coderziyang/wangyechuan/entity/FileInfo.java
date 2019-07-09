package com.coderziyang.wangyechuan.entity;

import android.graphics.Bitmap;
import android.util.Log;

import com.coderziyang.wangyechuan.core_func.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import static com.coderziyang.wangyechuan.micro_server.UploadService.TAG;

public class FileInfo implements Serializable {
    /**
     * 常见文件扩展名
     */
    public static final String EXTEND_APK = ".apk";
    public static final String EXTEND_JPEG = ".jpeg";
    public static final String EXTEND_JPG = ".jpg";
    public static final String EXTEND_PNG = ".png";
    public static final String EXTEND_MP3 = ".mp3";
    public static final String EXTEND_MP4 = ".mp4";

    /**
     * 定义文件的类型
     */
    public static final int TYPE_APK = 1;
    public static final int TYPE_JPG = 2;
    public static final int TYPE_MP3 = 3;
    public static final int TYPE_MP4 = 4;

    /**
     * 必要的文件属性
     * 文件路径
     * 文件大小
     * 文件类型
     */
    private String filePath;
    private long fileSize;
    private int fileType;

    /**
     * 非必要的文件属性
     * 文件名称
     * 文件大小描述
     * 文件缩略图
     * 文件额外信息
     * 已被处理的大小
     * 文件写入流
     * 文件对象
     */
    private String name;
    private String sizeDesc;
    private Bitmap bitmap;
    private String extra;
    private long procceed;
    private File file;
    private BufferedOutputStream bufferedOutputStream;


    public FileInfo(){}

    public FileInfo(String filePath,long size){
        this.filePath=filePath;
        this.fileSize=size;
    }

    public long getFileSize(){
        return fileSize;
    }
    public void setFileSize(long size){
        this.fileSize=size;
    }

    public String getFilePath(){
        return filePath;
    }

    public void setFilePath(String filePath){
        this.filePath=filePath;
    }
    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public String getExtra(){
        return extra;
    }
    public void setExtra(String extra){
        this.extra=extra;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getSizeDesc() {
        return sizeDesc;
    }

    public void setSizeDesc(String sizeDesc) {
        this.sizeDesc = sizeDesc;
    }

    public long getProcceed() {
        return procceed;
    }

    public void setProcceed(long procceed) {
        this.procceed = procceed;
    }

    public static String toJsonStr(FileInfo fileInfo){
        //String jsonStr="";
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("filePath",fileInfo.getFilePath());
            jsonObject.put("fileType",fileInfo.getFileType());
            jsonObject.put("fileSize",fileInfo.getFileSize());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static FileInfo toObject(String jsonStr){
        FileInfo fileInfo=new FileInfo();
        try{
            JSONObject jsonObject=new JSONObject(jsonStr);
            String filePath= (String) jsonObject.get("filePath");
            long fileSize= jsonObject.getLong("fileSize");
            int fileType=jsonObject.getInt("fileType");

            fileInfo.setFilePath(filePath);
            fileInfo.setFileSize(fileSize);
            fileInfo.setFileType(fileType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileInfo;
    }
    public BufferedOutputStream getBufferedOutputStream(){
        return this.bufferedOutputStream;
    }

    public void createFileInfo(String fileName){
        this.name=fileName;
        this.filePath = FileUtils.DEFAULT_UPLOAD_PATH+File.separator+fileName;
        Log.d(TAG,filePath);
        this.fileSize = 0;
        if(!FileUtils.DEFAULT_UPLOAD_PATH.exists()){
            FileUtils.DEFAULT_UPLOAD_PATH.mkdirs();
        }
        this.file = new File(filePath);
        try{
            this.bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clearBufferedStream(){
        if (bufferedOutputStream!= null){
            try{
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bufferedOutputStream = null;
    }

    public void write(byte[] bytes){
        if(bufferedOutputStream != null){
            try{
                bufferedOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileSize += bytes.length;
        }
    }

    public static String toJsonArrayStr(List<FileInfo> fileInfoList){
        JSONArray jsonArray=new JSONArray();
        if (fileInfoList != null){
            for(FileInfo fileInfo:fileInfoList){
                if (fileInfo != null){
                    try{
                        jsonArray.put(new JSONObject(toJsonStr(fileInfo)));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return jsonArray.toString();
    }

    @Override
    public String toString(){
        return "FileInfo{" +
                "filePath='" + filePath + '\'' +
                ", fileType=" + fileType +
                ", size=" + fileSize +
                '}';
    }
}
