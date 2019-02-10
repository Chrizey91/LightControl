package com.chriz.lightingcontrol.communication;

import android.content.Context;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

abstract class BaseIpAsyncTask implements Runnable {
    InetAddress mServerIp;
    int mPort;
    List<DataPackage> mDataPackages;
    Context mContext;

    private BaseIpAsyncTask() {

    }

    BaseIpAsyncTask(Context context, InetAddress serverIP, int port) {
        this.mServerIp = serverIP;
        this.mPort = port;
        this.mDataPackages = new LinkedList<>();
        this.mContext = context;
    }
}
