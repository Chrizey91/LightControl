package com.chriz.lightingcontrol.communication;


class DataPackageSender extends DataPackage {
    private String mData;

    DataPackageSender(String data) {
        this.mData = data;
    }

    String getData() {
        return mData;
    }


}
