package com.coderziyang.wangyechuan.micro_server;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadServer {

    private static final String TAG = UploadServer.class.getSimpleName();

    /**
     * Server的端口
     */
    private int mPort;

    /**
     * the Server socket
     */
    private ServerSocket mServerSocket;

    private UploadIndexHandler mUploadIndexHandler = null;

    /**
     * the ThreadPool which handle the incoming requests;
     */
    private ExecutorService mTreadPool = Executors.newCachedThreadPool();

    /**
     * the flag that microServer enable
     */
    private boolean mIsEnable = true;


    public  UploadServer(int port){this.mPort = port;}

    /**
     * start the Android Upload Server
     */
    public void start(){
        mTreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mServerSocket= new ServerSocket(mPort);
                    while(mIsEnable){
                        Socket socket=mServerSocket.accept();
                        handlerSocketAsyn(socket);
                        // TODO
                        //handle the input request
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * stop the android micro Server
     */
    public void stop(){
        if (mIsEnable){
            mIsEnable = false;
        }
        // TODO
        //release the resource

        if (mServerSocket!=null){
            try{
                mServerSocket.close();
                mServerSocket=null;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * register the resource uri handler
     */
    public void registerResUriHandler(ResUriHandler resUriHandler){
        this.mUploadIndexHandler = (UploadIndexHandler)resUriHandler;
    }

    /**
     * handler the incoming socket
     */
    private void handlerSocketAsyn(final Socket socket){
        mTreadPool.submit(new Runnable() {
            @Override
            public void run() {
                //create request object
                Request request=createRequest(socket);
                String type = request.getType();
                if(type.equals("POST")||request.getUri().startsWith("/progress")){
                    new UploadReceiveHandler().handler(socket);
                }else if(type.equals("GET")){
                    mUploadIndexHandler.handler(request);
                }


            }
        });
    }

    /**
     * used to create the request object by the specify socket
     * @param socket
     * @return
     */
    private Request createRequest(Socket socket){
        Request request = new Request();
        request.setmUnderSocket(socket);
        try{
            //Get the request line
            SocketAddress socketAddress=socket.getRemoteSocketAddress();
            InputStream is=socket.getInputStream();
            String requestLine=IOStreamUtil.readLine(is);
            Log.d(TAG, socketAddress + "requestLine------>>>" + requestLine);
            String requestType=requestLine.split(" ")[0];
            //Log.d(TAG, socketAddress + "requestLine------>>>" + requestType);
            String requestUri=requestLine.split(" ")[1];
            request.setType(requestType);
            request.setUri(requestUri);

            String header="";
            while((header = IOStreamUtil.readLine(is))!=null){
                //Log.d(TAG, socketAddress + "header------>>>" + header);
                String headerKey = header.split(":")[0];
                String headerValue = header.split(":")[1];
                request.addHeader(headerKey,headerValue);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return request;
    }
}
