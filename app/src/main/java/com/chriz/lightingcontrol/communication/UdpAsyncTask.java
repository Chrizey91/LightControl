package com.chriz.lightingcontrol.communication;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class UdpAsyncTask extends BaseIpAsyncTask {
    private static final String TAG = UdpAsyncTask.class.getSimpleName();

    private DatagramSocket mUdpSocket;

    UdpAsyncTask(Context context, InetAddress serverIP, int port) {
        super(context, serverIP, port);
        try {
            this.mUdpSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // For each DataPacket in the stack that was added we act accordingly
            for (DataPackage dataPackage : this.mDataPackages) {
                // If we expect to receive a packet...
                if (dataPackage instanceof DataPackageReceiver) {
                    DataPackageReceiver dataPackageReceiver = (DataPackageReceiver) dataPackage;
                    byte[] bytes = new byte[255];
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    mUdpSocket.receive(packet);
                    dataPackageReceiver.getAnswerListener().onReceiveAnswer(new String(bytes));
                    mUdpSocket.close();
                // If we want to send a single message...
                } else if (dataPackage instanceof DataPackageSender) {
                    DataPackageSender dataPackageSender = (DataPackageSender) dataPackage;
                    byte[] bytes = dataPackageSender.getData().getBytes();
                    mUdpSocket.send(new DatagramPacket(bytes, bytes.length, mServerIp, mPort));
                    mUdpSocket.close();
                // If we want to continuously send a message...
                } else if (dataPackage instanceof DataPackageSenderContinuously) {
                    DataPackageSenderContinuously dataPackageSenderContinuously = (DataPackageSenderContinuously) dataPackage;
                    Log.d(TAG, "Start sending...");
                    while (dataPackageSenderContinuously.isActive()) {
                        byte[] bytes = dataPackageSenderContinuously.getData().getBytes();
                        Log.d(TAG, "Sending bytes: " + new String(bytes));
                        mUdpSocket.send(new DatagramPacket(bytes, bytes.length, mServerIp, mPort));
                        Thread.sleep(20);
                    }
                    Log.d(TAG, "Stop sending...");
                    mUdpSocket.close();
                }
            }
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Could not send");
            mUdpSocket.close();
        }
    }
}
