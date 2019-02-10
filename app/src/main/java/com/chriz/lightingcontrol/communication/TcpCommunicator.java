package com.chriz.lightingcontrol.communication;

import android.content.Context;

class TcpCommunicator extends Communicator {

    TcpCommunicator(Context context, String receiverAddress, int receiverPort, boolean encrypted) {
        super(context, CommunicationType.TCP, receiverAddress, receiverPort, encrypted);
        this.mIpAsyncTask = IpAsyncTaskFactory.createTcpAsyncTask(this.mContext, this.mReceiverAddress, this.mReceiverPort, encrypted);
    }
}
