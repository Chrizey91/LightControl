package com.chriz.lightingcontrol.communication;

/**
 * Use this to send a message using communicator continuously.
 * While the communicator is executed, you can change the message being send by calling setData()
 */
public class DataPackageSenderContinuously extends DataPackage {
    private String mData;
    private boolean mActive;

    public DataPackageSenderContinuously(String data) {
        this.mData = data;
    }

    String getData() {
        return mData;
    }

    public void setData(String data) {
        this.mData = data;
    }

    public void activate() {
        this.mActive = true;
    }

    public void deactivate() {
        this.mActive = false;
    }

    boolean isActive() {
        return mActive;
    }
}
