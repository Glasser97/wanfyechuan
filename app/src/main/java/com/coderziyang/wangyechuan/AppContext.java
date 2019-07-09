package com.coderziyang.wangyechuan;

import android.app.Application;
import com.coderziyang.wangyechuan.entity.FileInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppContext extends Application {
    /**
     * 主要的线程池
     */
    public static Executor MAIN_EXECUTOR= Executors.newFixedThreadPool(5);
    /**
     * 文件发送单线程
     */
    public static Executor FILE_SEND_EXECUTOR= Executors.newSingleThreadExecutor();
    /**
     * 全局应用的上下文
     */
    static AppContext mAppContext;

    //文件发送方
    Map<String, FileInfo> mFileInfoMap=new HashMap<String,FileInfo>();

    Map<String, FileInfo> mRecvFileInfoMap=new HashMap<String,FileInfo>();

    @Override
    public void onCreate(){
        super.onCreate();
        this.mAppContext=this;
    }
    /**
     * 全局获取AppContext
     */

    public static AppContext getAppContext(){
        return mAppContext;
    }
    /**
     * 下面是发送方的map用来存储发送文件的信息
     */
    /**
     * 添加一个FileInfo
     * @param fileInfo
     */
    public void addFileInfo(FileInfo fileInfo){
        if (!mFileInfoMap.containsKey(fileInfo.getFilePath())){
            mFileInfoMap.put(fileInfo.getFilePath(),fileInfo);
        }
    }
    /**
     * 更新一个FileInfo
     */
    public void updateFileInfo(FileInfo fileInfo){
        mFileInfoMap.put(fileInfo.getFilePath(),fileInfo);
    }
    /**
     * 删除一个FileInfo
     */
    public boolean deleteFileInfo(FileInfo fileInfo){
        if (mFileInfoMap.containsKey(fileInfo.getFilePath())){
            mFileInfoMap.remove(fileInfo.getFilePath());
            return true;
        }else{
            return false;
        }
    }
    /**
     * 是否存在FileInfo
     */
    public boolean isExist(FileInfo fileInfo){
        if (mFileInfoMap==null) return false;
        return mFileInfoMap.containsKey(fileInfo.getFilePath());
    }
    /**
     * 判断集合里面是否有元素
     */
    public boolean isFileInfoMapExist(){
        if (mFileInfoMap==null||mFileInfoMap.size()<=0) {
            return false;
        }
        return true;
    }
    /**
     * 获取全局变量的FileInfoMap
     */
    public Map<String,FileInfo> getFileInfoMap(){
        return mFileInfoMap;
    }

    /**
     * 获取传送文件的总长度
     */
    public long getAllSendFileSize(){
        long total=0;
        for (FileInfo fileInfo:mFileInfoMap.values()){
            if(fileInfo!=null){
                total+=fileInfo.getFileSize();
            }
        }
        return total;
    }

    /**
     * 下面是接收方的map
     */
    /**
     * 添加一个FileInfo
     */
    public void addRecvFileInfo(FileInfo fileInfo){
        if(!mRecvFileInfoMap.containsKey(fileInfo.getFilePath())){
            mRecvFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
        }
    }
    /**
     * 更新一个FileInfo
     */
    public void updateRecvFileInfo(FileInfo fileInfo){
        mRecvFileInfoMap.put(fileInfo.getFilePath(),fileInfo);
    }
    /**
     * 删除一个FileInfo
     */
    public boolean deleteRecvFileInfo(FileInfo fileInfo){
        if (mRecvFileInfoMap.containsKey(fileInfo.getFilePath())){
            mRecvFileInfoMap.remove(fileInfo.getFilePath());
            return true;
        }else{
            return false;
        }
    }
    /**
     * 是否存在FileInfo
     */
    public boolean isRecvExist(FileInfo fileInfo){
        if (mRecvFileInfoMap==null) return false;
        return mRecvFileInfoMap.containsKey(fileInfo.getFilePath());
    }
    /**
     * 判断集合里面是否有元素
     */
    public boolean isRecvFileInfoMapExist(){
        if (mRecvFileInfoMap==null||mRecvFileInfoMap.size()<=0) {
            return false;
        }
        return true;
    }
    /**
     * 获取全局变量的FileInfoMap
     */
    public Map<String,FileInfo> getRecvFileInfoMap(){
        return mRecvFileInfoMap;
    }

    /**
     * 获取传送文件的总长度
     */
    public long getRecvAllSendFileSize(){
        long total=0;
        for (FileInfo fileInfo:mRecvFileInfoMap.values()){
            if(fileInfo!=null){
                total+=fileInfo.getFileSize();
            }
        }
        return total;
    }


}
