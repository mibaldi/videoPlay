package com.mibaldi.playvideos.models;



import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.mibaldi.playvideos.BuildConfig;
import com.mibaldi.playvideos.utils.Utils;

import java.util.List;

import static com.mibaldi.playvideos.models.MyVideo.STATE_DOWNLOADED;
import static com.mibaldi.playvideos.models.MyVideo.STATE_DOWNLOADING;
import static com.mibaldi.playvideos.models.MyVideo.STATE_INITIAL;

public class MyVideo extends ViewModel {


    private String url = "";
    private String fullUrl = "";
    private String name = "";
    private boolean isDownload = false;
    public static final int STATE_INITIAL = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_DOWNLOADED = 2;
    private long downloadId;
    private MyVideo mCurrentVideo;

    public MyVideo(String name,String url,String fullUrl, boolean isDownload) {
        this.name = name;
        this.url= url;
        this.fullUrl = fullUrl;
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

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public MyVideo getmCurrentVideo() {
        return mCurrentVideo;
    }

    public void setmCurrentVideo(MyVideo mCurrentVideo) {
        this.mCurrentVideo = mCurrentVideo;
    }

    public static MyVideo parseFirebaseVideo(String collectionName,FirebaseMyVideo firebaseMyVideo){

        MyVideo myVideo = new MyVideo(firebaseMyVideo.name, firebaseMyVideo.id, Utils.generateUrl(collectionName,firebaseMyVideo.url),false);
        return myVideo;
    }


}
