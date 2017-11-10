package com.mibaldi.playvideos.models;

import java.util.Iterator;
import java.util.List;

public class MyVideo {
    private String url = "";
    private boolean isDownload = false;
    private long downloadId;

    public MyVideo(String url, boolean isDownload) {
        this.url= url;
        this.isDownload = isDownload;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public static int find(List<MyVideo> list, long downloadId){

        for (MyVideo video : list){

            if (video.getDownloadId()== downloadId) {
                return list.indexOf(video);
            }
        }
        return -1;
    }
    public static int find(List<MyVideo> list, String url){
        for (MyVideo video : list){
            if(video.getUrl().equals(url)){
                return list.indexOf(video);
            }
        }
        return -1;
    }
}
