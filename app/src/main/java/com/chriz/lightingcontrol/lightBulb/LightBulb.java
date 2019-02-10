package com.chriz.lightingcontrol.lightBulb;

public class LightBulb {
    private int mID;
    private String ipAddress;
    private String name;

    public LightBulb(int id, String ipAddress, String name) {
        this.mID = id;
        this.ipAddress = ipAddress;
        this.name = name;
    }

    public int getID() {
        return mID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
