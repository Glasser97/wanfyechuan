package com.coderziyang.wangyechuan.micro_server;

import android.app.Activity;

import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

public class IndexResUriHandler implements ResUriHandler {

    private static final String TAG = IndexResUriHandler.class.getSimpleName();

    private Activity mActivity;

    public IndexResUriHandler(Activity activity){
        this.mActivity=activity;
    }

    @Override
    public boolean matches(String uri) {
        if(uri == null || uri.equals("") || uri.equals("/")){
            return true;
        }
        return false;
    }

    @Override
    public void handler(Request request){
        //1、取得local index.html
        String indexHtml = null;
        try{
            InputStream is = this.mActivity.getAssets().open("index.html");
            indexHtml = IOStreamUtil.inputStreamToString(is);
        }catch(IOException e){
            e.printStackTrace();
        }

        //2.以HTTP的格式回复客户端
        if(request.getUnderSocket()!=null && indexHtml !=null){
            OutputStream outputStream = null;
            PrintStream printStream = null;

            try{
                outputStream = request.getUnderSocket().getOutputStream();
                printStream = new PrintStream(outputStream);
                printStream.println("HTTP/1.1 200 OK");
                printStream.println("Content-Type:text/html");
                printStream.println("Cache-Control:no-cache");
                printStream.println("Pragma:no-cache");
                printStream.println("Expires:0");
                printStream.println();

                indexHtml = convert(indexHtml);

                byte[] bytes = indexHtml.getBytes("UTF-8");
                printStream.write(bytes);

                printStream.flush();
                printStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }finally {

                if(outputStream != null){
                    try{
                        outputStream.close();
                        outputStream = null;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(printStream != null){
                    try{
                        printStream.close();
                        printStream = null;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void destroy(){
        this.mActivity=null;
    }

    /**
     * convert html with further processing
     * @param indexHtml
     * @return
     */
    public String convert(String indexHtml){
        return indexHtml;
    }
}
