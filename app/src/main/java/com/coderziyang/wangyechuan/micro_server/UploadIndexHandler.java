package com.coderziyang.wangyechuan.micro_server;

import android.app.Activity;
import android.provider.ContactsContract;

import com.coderziyang.wangyechuan.core_func.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class UploadIndexHandler implements ResUriHandler {

    private static final String TAG = IndexResUriHandler.class.getSimpleName();

    private static final String TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
    private static final String JS_CONTENT_TYPE = "application/javascript";
    private static final String PNG_CONTENT_TYPE = "application/x-png";
    private static final String JPG_CONTENT_TYPE = "application/jpeg";
    private static final String SWF_CONTENT_TYPE = "application/x-shockwave-flash";
    private static final String WOFF_CONTENT_TYPE = "application/x-font-woff";
    private static final String TTF_CONTENT_TYPE = "application/x-font-truetype";
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";
    private static final String EOT_CONTENT_TYPE = "image/vnd.ms-fontobject";
    private static final String MP3_CONTENT_TYPE = "audio/mp3";
    private static final String MP4_CONTENT_TYPE = "video/mpeg4";


    private Activity mActivity;

    public UploadIndexHandler(Activity activity){
        this.mActivity=activity;
    }

    @Override
    public boolean matches(String uri) {
        if(uri == null || uri.equals("") || uri.equals("/")||uri.lastIndexOf(".jpg")>=0||uri.lastIndexOf(".js")>=0||uri.lastIndexOf(".css")>=0){
            return true;
        }
        return false;
    }


    @Override
    public void handler(Request request){
        //1、取得local index.html
        String indexHtml = null;
        String contentType = TEXT_CONTENT_TYPE;
        boolean isImage =false;
        String uri = request.getUri();
        File imageFile = null;
        try{
            InputStream is = null;
            if(uri == null || uri.equals("") || uri.equals("/")){
                is = this.mActivity.getAssets().open("wifi/index.html");
            }else{
                is = this.mActivity.getAssets().open("wifi"+uri);
                contentType = getContentTypeByResourceName(uri);
            }
            if (uri.endsWith(".png")||uri.endsWith(".jpg")||uri.endsWith(".gif")){
                imageFile = new File(FileUtils.getScreenShotDirPath()+uri.substring(uri.lastIndexOf("/")+1));
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writeBytesToFile(is,imageFile);
                isImage = true;
            }
            indexHtml = IOStreamUtil.inputStreamToString(is);
        }catch(IOException e){
            e.printStackTrace();
        }
        //2.以HTTP的格式回复客户端
        if(isImage) {
            try {
                OutputStream os = request.getUnderSocket().getOutputStream();
                PrintStream printStream = new PrintStream(os);
                printStream.println("HTTP/1.1 200 OK");
//            image/jpeg
                printStream.println("Content-Length:" + imageFile.length());
//            printStream.println("Content-Type:image/png");
//            printStream.println("Content-Type:application/octet-stream");
                printStream.println("Content-Type:multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*");
                printStream.println();

                //send the file to the client
                FileInputStream fis = new FileInputStream(imageFile);
                try {
                    int len = 0;
                    byte[] bytes = new byte[2048];
                    while ((len = fis.read(bytes)) != -1) {
                        printStream.write(bytes, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                            fis = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                printStream.flush();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(request.getUnderSocket()!=null && indexHtml !=null){
                OutputStream outputStream = null;
                PrintStream printStream = null;

                try{
                    outputStream = request.getUnderSocket().getOutputStream();
                    printStream = new PrintStream(outputStream);
                    printStream.println("HTTP/1.1 200 OK");
                    printStream.println("Content-Type:"+contentType);
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
                }
            }
        }
    }
    private String getContentTypeByResourceName(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".js")) {
            return JS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".swf")) {
            return SWF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".png")) {
            return PNG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return JPG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".woff")) {
            return WOFF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".ttf")) {
            return TTF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".svg")) {
            return SVG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".eot")) {
            return EOT_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp3")) {
            return MP3_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp4")) {
            return MP4_CONTENT_TYPE;
        }
        return "";
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

    public static void writeBytesToFile(InputStream is, File file) throws IOException{
        FileOutputStream fos = null;
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while((nbread=is.read(data))>-1){
                fos.write(data,0,nbread);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally{
            if (fos!=null){
                fos.close();
            }
        }
    }


}
