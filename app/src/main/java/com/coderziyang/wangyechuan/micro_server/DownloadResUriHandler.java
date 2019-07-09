package com.coderziyang.wangyechuan.micro_server;


import android.app.Activity;

import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

/**
 * the image resource uri handler
 * the matched uri format is http://hostname:port/image/xxxx.xx
 */
public class DownloadResUriHandler implements ResUriHandler {

    public static final String DOWNLOAD_PREFIX="/download/";
    private Activity mActivity;

    public DownloadResUriHandler(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public boolean matches(String uri){
        return uri.startsWith(DOWNLOAD_PREFIX);
    }

    @Override
    public void handler(Request request){
        //1.get Image file from the uri
        String uri = request.getUri();
        String fileName = uri.substring(uri.lastIndexOf("/")+1,uri.length());
        try{
            fileName = URLDecoder.decode(fileName,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        FileInfo fileInfo = FileUtils.getFileInfo(this.mActivity, fileName);

        //查看本地还有没有这个文件，如果有的话，就返回这个图片文件，如果没有的话就返回404给访问端
        Socket socket = request.getUnderSocket();
        OutputStream os = null;
        PrintStream printStream = null;
        try{
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
        }catch (IOException e) {
            e.printStackTrace();
        }

        if(fileInfo == null){
            printStream.println("HTTP/1.1 404 NotFound");
            printStream.println();
        }else{
            printStream.println("HTTP/1.1 200 OK");
            printStream.println("Content-Length:" + fileInfo.getFileSize());
            printStream.println("Content-Type:application/octet-stream");
            printStream.println();
        }

        File file = null;
        FileInputStream fis = null;
        try{
            if(fileName.trim().equals("")||fileName.trim().equals("/")){
                file = new File(mActivity.getPackageCodePath());
            }else{
                file = new File(fileInfo.getFilePath());
            }
            fis = new FileInputStream(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        //发送文件给客户端
        try{
            int len = 0;
            byte[] bytes = new byte[2048];
            while((len = fis.read(bytes))!= -1){
                printStream.write(bytes,0,len);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(fis!=null){
                try{
                    fis.close();
                    fis=null;
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destroy(){
        this.mActivity=null;
    }
}
