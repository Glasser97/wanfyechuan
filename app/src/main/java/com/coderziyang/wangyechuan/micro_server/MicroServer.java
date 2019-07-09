package com.coderziyang.wangyechuan.micro_server;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MicroServer {
    private static final String TAG = MicroServer.class.getSimpleName();

    /**
     * the Server port
     */
    private int mPort;

    /**
     * the Server socket
     */
    private ServerSocket mServerSocket;

    /**
     * the ThreadPool which handle the incoming requests;
     */
    private ExecutorService mTreadPool = Executors.newCachedThreadPool();

    /**
     * uri router handler
     */
    private List<ResUriHandler> mResUriHandlerList=new ArrayList<>();

    /**
     * the flag that microServer enable
     */
    private boolean mIsEnable = true;

    public MicroServer(int port){
        this.mPort=port;
    }

    /**
     * register the resource uri handler
     */
    public void registerResUriHandler(ResUriHandler resUriHandler){
        this.mResUriHandlerList.add(resUriHandler);
    }

    /**
     * unregisterResUriHandler
     */
    public void unregisterResUriHandlerList(){
        for(ResUriHandler resUriHandler:this.mResUriHandlerList){
            resUriHandler.destroy();
            resUriHandler=null;
        }
    }

    /**
     * Start the Android micro Server
     */
    public void start(){
        mTreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    mServerSocket= new ServerSocket(mPort);
                    while(mIsEnable){
                        Socket socket=mServerSocket.accept();
                        handlerSocketAsyn(socket);
                    }
                }catch(Exception e){
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
        //release the handler resource
        unregisterResUriHandlerList();

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
     * handler the incoming socket
     */
    private void handlerSocketAsyn(final Socket socket){
        mTreadPool.submit(new Runnable() {
            @Override
            public void run() {
                //create request object
                Request request=createRequest(socket);

                //loop and match the mResUriHandlerList, assign the task to the specify ResUrihandler
                for(ResUriHandler resUriHandler:mResUriHandlerList){
                    if (!resUriHandler.matches(request.getUri())){
                        continue;
                    }
                    resUriHandler.handler(request);
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
            String requestUri=requestLine.split(" ")[1];

            request.setUri(requestUri);

            String header="";
            while((header = IOStreamUtil.readLine(is))!=null){
                Log.d(TAG, socketAddress + "header------>>>" + requestLine);
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
