package com.coderziyang.wangyechuan.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiMgr {

    public static WifiMgr mWifiMgr;
    private Context mContext;
    private WifiManager mWifiManager;

    WifiInfo mWifiInfo;

    private WifiMgr(Context context){
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public static WifiMgr getInstance(Context context){
        if(mWifiMgr == null){
            synchronized (WifiMgr.class){
                if(mWifiMgr == null){
                    mWifiMgr = new WifiMgr(context);
                }
            }
        }
        return mWifiMgr;
    }
    /**
     * 转换成ip形式
     *
     */
    public static String intToIp(int ipInt){
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8)& 0xFF).append(".");
        sb.append((ipInt >> 16)& 0xFF).append(".");
        sb.append((ipInt >>24)& 0xFF);
        return sb.toString();
    }

    /**
     * 判断WIFI是否开启
     */
    public boolean isWifiEnable(){
        return mWifiManager != null && mWifiManager.isWifiEnabled();
    }


    /**
     * 获取当前Ip地址
     *
     */
    public String getLocalIpAddr(){
        int ip = mWifiInfo.getIpAddress();
        return intToIp(ip);
    }

    /**
     * 获取当前Wifi的SSID
     */
    public String getWifiSSid(){
        return mWifiInfo.getSSID();
    }

}
