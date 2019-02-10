package com.chriz.lightingcontrol.communication;


import android.content.Context;

class UdoCommunicator extends Communicator  {

    UdoCommunicator(Context context, String receiverAddress, int receiverPort) {
        super(context, CommunicationType.UDP, receiverAddress, receiverPort, false);
        this.mIpAsyncTask = IpAsyncTaskFactory.createUdpAsyncTask(this.mContext, this.mReceiverAddress, this.mReceiverPort);
    }
}
