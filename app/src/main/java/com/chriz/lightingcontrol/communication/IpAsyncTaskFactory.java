package com.chriz.lightingcontrol.communication;

import android.content.Context;

import java.net.InetAddress;

final class IpAsyncTaskFactory {
    static BaseIpAsyncTask createUdpAsyncTask(Context context, InetAddress serverIP, int port) {
        return new UdpAsyncTask(context, serverIP, port);
    }

    static BaseIpAsyncTask createTcpAsyncTask(Context context, InetAddress serverIP, int port, boolean encrypted) {
        return new TcpAsyncTask(context, serverIP, port, encrypted);
    }
}
