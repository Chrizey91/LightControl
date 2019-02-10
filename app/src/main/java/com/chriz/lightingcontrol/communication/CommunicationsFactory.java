package com.chriz.lightingcontrol.communication;

import android.content.Context;

public final class CommunicationsFactory {
    /**
     * Creates a communicator based on a UDP connection
     * @param context the activities context
     * @param receiverAddress IP address of the device we want to communicate with
     * @param receiverPort Port of the device we want to communicate with
     * @return a Communicator that can communicate :)
     */
    public static Communicator createUnreliableButFastCommunicator(Context context, String receiverAddress, int receiverPort) {
        return new UdoCommunicator(context, receiverAddress, receiverPort);
    }

    /**
     * Creates a communicator based on a TCP connection. Won't be encrypted.
     * @param context the activities context
     * @param receiverAddress IP address of the device we want to communicate with
     * @param receiverPort Port of the device we want to communicate with
     * @return a Communicator that can communicate :)
     */
    public static Communicator createReliableButSlowCommunicator(Context context, String receiverAddress, int receiverPort) {
        return new TcpCommunicator(context, receiverAddress, receiverPort, false);
    }

    /**
     * Creates a communicator based on a TCP connection. Can be encrypted based on encrypted parameter.
     * @param context the activities context
     * @param receiverAddress IP address of the device we want to communicate with
     * @param receiverPort Port of the device we want to communicate with
     * @param encrypted if true, the communication will be encrypted using TCL
     * @return a Communicator that can communicate :)
     */
    public static Communicator createReliableButSlowCommunicator(Context context, String receiverAddress, int receiverPort, boolean encrypted) {
        return new TcpCommunicator(context, receiverAddress, receiverPort, encrypted);
    }
}
