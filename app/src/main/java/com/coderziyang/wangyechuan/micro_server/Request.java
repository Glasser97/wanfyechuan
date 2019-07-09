package com.coderziyang.wangyechuan.micro_server;

import java.net.Socket;
import java.util.HashMap;

public class Request {
    private String mUri;
    private String mType;
    private HashMap<String, String> mHeadrMap = new HashMap<>();
    private Socket mUnderSocket;

    public Request(){}

    public Socket getUnderSocket(){
        return mUnderSocket;
    }

    public void setmUnderSocket(Socket socket){
        this.mUnderSocket=socket;
    }

    public String getUri(){
        return mUri;
    }

    public String getType(){
        return this.mType;
    }

    public void setType(String type){
        this.mType=type;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }

    public void addHeader(String key,String value){
        this.mHeadrMap.put(key,value);
    }
}
