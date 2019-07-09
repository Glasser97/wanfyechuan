package com.coderziyang.wangyechuan.micro_server;


import android.app.Activity;
import android.media.Image;
import android.util.Log;

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

public class ImageResUriHandler implements ResUriHandler {

    private static final String TAG = ImageResUriHandler.class.getSimpleName();

    /**
     * default app logo png name
     */
    public static final String DEFAULT_LOGO = "logo.png";

    public static final String IMAGE_PREFIX = "/image/";

    private Activity mActivity;

    public ImageResUriHandler(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public boolean matches(String uri){
        return uri.startsWith(IMAGE_PREFIX);
    }

    @Override
    public void handler(Request request){
        //通过uri获得图像文件的名字
        String uri = request.getUri();
        String fileName = uri.substring(uri.lastIndexOf("/")+1,uri.length());

        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        FileInfo fileInfo = FileUtils.getFileInfo(this.mActivity,fileName);
        //检查本地存储有没有这个文件
        Socket socket = request.getUnderSocket();
        OutputStream os = null;
        PrintStream printStream =null;
        try{
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
        }catch(IOException e){
            e.printStackTrace();
        }

        if(fileInfo==null){
            printStream.println("HTTP/1.1 404 NotFound");
            printStream.println();
        }else{
            //检查缩略图文件有没有，有就返回缩略图，没有就生成缩略图
            try {
                FileUtils.autoCreateScreenShot(mActivity, fileInfo.getFilePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "------>>>Auto create screen shot failure : " + e.getMessage());
            }
            File file = null;
            FileInputStream fis = null;
            try {
                if(fileName.trim().equals(DEFAULT_LOGO)){
                    file = new File(FileUtils.getScreenShotDirPath() + DEFAULT_LOGO);
                }else{
                    file = new File(FileUtils.getScreenShotFilePath(fileName));
                }
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            printStream.println("HTTP/1.1 200 OK");
//            image/jpeg
            printStream.println("Content-Length:" + file.length());
//            printStream.println("Content-Type:image/png");
//            printStream.println("Content-Type:application/octet-stream");
            printStream.println("Content-Type:multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*");
            printStream.println();

            //

            //send the file to the client
            try{
                int len = 0;
                byte[] bytes = new byte[2048];
                while((len=fis.read(bytes)) != -1){
                    printStream.write(bytes,0,len);
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally{
                if(fis!=null){
                    try{
                        fis.close();
                        fis=null;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        printStream.flush();
        printStream.close();
    }

    @Override
    public void destroy(){
        this.mActivity=null;
    }

}
