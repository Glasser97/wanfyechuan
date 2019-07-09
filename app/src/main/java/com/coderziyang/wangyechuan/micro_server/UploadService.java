package com.coderziyang.wangyechuan.micro_server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.coderziyang.wangyechuan.Constant;
import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.core_func.utils.TextUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class UploadService extends Service {

    public static final String TAG = UploadService.class.getSimpleName();
    public static final String ACTION_START_UPLOAD_SERVICE = "ACTION_START_UPLOAD_SERVICE";
    public static final String ACTION_STOP_UPLOAD_SERVICE = "ACTION_STOP_UPLOAD_SERVICE";

    private AsyncHttpServer httpServer = new AsyncHttpServer();
    private AsyncServer asyncServer = new AsyncServer();
    FileInfo fileInfo = new FileInfo();

    public UploadService() {
    }

    public static void start(Context context){
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_START_UPLOAD_SERVICE);
        context.startService(intent);
    }

    public static void stop(Context context){
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_STOP_UPLOAD_SERVICE);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String action = (intent != null) ? intent.getAction():null;
        if(ACTION_START_UPLOAD_SERVICE.equals(action)){
            startServer();
        }else if(ACTION_STOP_UPLOAD_SERVICE.equals(action)){
            stopSelf();
        }
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(asyncServer != null){
            asyncServer.stop();
        }
        if(httpServer != null){
            httpServer.stop();
        }
    }

    private void startServer(){
        //send image and js/css resource
        httpServer.get("/images/.*", this::sendResource);
        httpServer.get("/scripts/.*",this::sendResource);
        httpServer.get("/css/.*",this::sendResource);

        //send The Index Page.
        httpServer.get("/",this::sendIndexHtml);

        //Upload files.
        httpServer.post("/files",this::uploadFiles);

        //Get Progress
        httpServer.get("/progress/*",this::getProgress);

        //Set Port Listener
        httpServer.listen(asyncServer, Constant.DEFAULT_UPLOAD_SERVER_PORT);
    }

    /**
     * Get Progress Callback function
     */
    private void getProgress(final AsyncHttpServerRequest request,final AsyncHttpServerResponse response){
        JSONObject jsonObject = new JSONObject();

        String path = request.getPath().replaceAll("/progress/","");

        if(path.equals(fileInfo.getName())){
            try{
                jsonObject.put("fileName",fileInfo.getName());
                jsonObject.put("size",fileInfo.getFileSize());
                jsonObject.put("progress",fileInfo.getBufferedOutputStream() == null ? 1 : 0.1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        response.send(jsonObject);

    }

    /**
     * Upload Files Callback Function
     */
    private void uploadFiles(AsyncHttpServerRequest request, AsyncHttpServerResponse response){
        final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
        body.setMultipartCallback((Part part)->{
            if(part.isFile()){
                body.setDataCallback((DataEmitter dataEmitter, ByteBufferList byteBufferList)->{
                    fileInfo.write(byteBufferList.getAllByteArray());
                    byteBufferList.recycle();
                });
            }else{
                if (body.getDataCallback() ==  null){
                    body.setDataCallback((DataEmitter dataEmitter, ByteBufferList byteBufferList)->{
                        try{
                            String fileName = URLDecoder.decode(new String(byteBufferList.getAllByteArray()), "UTF-8");
                            fileInfo.createFileInfo(fileName);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        byteBufferList.recycle();
                    });
                }
            }
        });
        request.setEndCallback((Exception e)->{
            fileInfo.clearBufferedStream();
            response.end();
        });
    }

    private void sendIndexHtml(AsyncHttpServerRequest request, AsyncHttpServerResponse response){
        try{
            InputStream is = getAssets().open("wifi/index.html");
            String indexHtml = IOStreamUtil.inputStreamToString(is);
            response.send(indexHtml);
        } catch (IOException e) {
            e.printStackTrace();
            response.code(500).end();
        }
    }


    private void sendResource(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response){
        try{
            String fullPath = request.getPath();
            fullPath = fullPath.replace("%20"," ");
            String resourceName = fullPath;
            if(resourceName.startsWith("/")){
                resourceName = resourceName.substring(1);
            }
            if(resourceName.indexOf("?") > 0){
                resourceName = resourceName.substring(0,resourceName.indexOf("?"));
            }
            if(!TextUtils.isNullOrBlank(FileUtils.getContentTypeByResourceName(resourceName))){
                response.setContentType(FileUtils.getContentTypeByResourceName(resourceName));
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(getAssets().open("wifi/"+resourceName));
            response.sendStream(bufferedInputStream,bufferedInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            response.code(404).end();
        }
    }

}
