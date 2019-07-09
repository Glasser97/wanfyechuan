package com.coderziyang.wangyechuan.micro_server;

import com.coderziyang.wangyechuan.Constant;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

public class UploadReceiveHandler {

    private AsyncServer asyncServer = new AsyncServer();
    private AsyncHttpServer httpServer = new AsyncHttpServer();
    private FileInfo fileInfo = new FileInfo();




    void handler(Socket socket){
        httpServer.post("/files",(AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            final MultipartFormDataBody body =(MultipartFormDataBody) request.getBody();
            body.setMultipartCallback((Part part)->{
                if (part.isFile()){
                    body.setDataCallback((DataEmitter emitter, ByteBufferList byteBufferList)->{
                        fileInfo.write(byteBufferList.getAllByteArray());
                        byteBufferList.recycle();
                    });
                }else{
                    if (body.getDataCallback() != null){
                        body.setDataCallback((DataEmitter emitter, ByteBufferList byteBufferList)->{
                            try{
                                String fileName = URLDecoder.decode(new String(byteBufferList.getAllByteArray()),"UTF-8");
                                fileInfo.createFileInfo(fileName);
                            }catch (UnsupportedEncodingException e) {
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
        });

        httpServer.get("/progress/*",(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response)->{
            JSONObject jsonObject = new JSONObject();
            String path = request.getPath().replace("/progress/","");

            if(path.equals(fileInfo.getName())){
                try{
                    jsonObject.put("fileName",fileInfo.getName());
                    jsonObject.put("size",fileInfo.getFileSize());
                    jsonObject.put("progress",fileInfo.getBufferedOutputStream() == null ? 1:0.1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            response.send(jsonObject);
        });

        httpServer.listen(asyncServer, Constant.DEFAULT_UPLOAD_SERVER_PORT);
    }

}
