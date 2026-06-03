package com.example.lostandfound.models;

public class AppVersionModel {

    private String version;
    private String apkUrl;
    private String message;
    private boolean forceUpdate;

    public String getVersion() {
        return version;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getMessage() {
        return message;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }
}