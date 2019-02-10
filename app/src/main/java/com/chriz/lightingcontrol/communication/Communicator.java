package com.chriz.lightingcontrol.communication;

import android.content.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class is the only publicly available class.
 * It should be used to add all the communication that will happen between this app and the
 * receiver.
 *
 * Messages are always send and received in String format.
 *
 * The communication can be encrypted for TCP connections only right now and only the send
 * messages are encrypted, not the received ones. R.raw.cert.crt is used as the public certificate.
 * Consequently, the receiver should either have an official CA oder have the same as in R.raw.cert.
 */
public abstract class Communicator {
    private CommunicationType mCommunicationType;
    InetAddress mReceiverAddress;
    int mReceiverPort;
    BaseIpAsyncTask mIpAsyncTask;
    Context mContext;
    private boolean mEncrypted;

    Communicator(Context context, CommunicationType type, String receiverAddress, int receiverPort, boolean encrypted) {
        this.mCommunicationType = type;
        this.mContext = context;
        this.mReceiverPort = receiverPort;
        this.mEncrypted = encrypted;
        try {
            this.mReceiverAddress = InetAddress.getByName(receiverAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will send a single message to receiver when executed.
     * @param message the message we want to send to the external device
     * @return the self instant
     */
    public Communicator sendMesage(String message) {
        mIpAsyncTask.mDataPackages.add(new DataPackageSender(message));
        return this;
    }

    /**
     * Will continuously send a message to receiver.
     * You should create a DataPackageSenderContinuously, where you can change to message
     * being send over time.
     * @param message a data package wrapping the message we want to send to the external device
     * @return the self instant
     */
    public Communicator sendMessageContinously(DataPackageSenderContinuously message) {
        mIpAsyncTask.mDataPackages.add(message);
        return this;
    }

    /**
     * If an answer is expected from the receiver, then you can add a function that should be called
     * when the specific answer is received.
     * @param answerListener a class that is called when an answer is received
     * @return the self instant
     */
    public Communicator receiveAnswer(OnReceiveAnswerListener answerListener) {
        mIpAsyncTask.mDataPackages.add(new DataPackageReceiver(answerListener));
        return this;
    }

    /**
     * Executes the communication and handles als sender and receiver messages added to this
     * communicator
     */
    public void execute() {
        new Thread(mIpAsyncTask).start();
    }

    public CommunicationType getCommunicationType() {
        return mCommunicationType;
    }

    public boolean isEncrypted() {
        return mEncrypted;
    }

    public interface OnReceiveAnswerListener {
        void onReceiveAnswer(String answer);
    }
}
