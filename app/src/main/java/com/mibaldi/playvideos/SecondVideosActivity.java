package com.mibaldi.playvideos;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.Task;

import com.mibaldi.playvideos.firebaseUtils.VideoUtils;
import com.mibaldi.playvideos.models.MyVideo;

import java.io.File;
import java.util.ArrayList;

public class SecondVideosActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASEFIRESTORE";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VideoAdapter mAdapter;
    private ArrayList<MyVideo> myDataSet = new ArrayList<>();
    public String currentDownload = "";
    public MyVideo currentView = null;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID = -1;
    private BroadcastReceiver onComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_videos);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initBroadcast();
        mAdapter = new VideoAdapter();
        VideoUtils.generateList(this, Environment.DIRECTORY_DOWNLOADS+"/Modulo2","Modulo2", new VideoUtils.onResponse() {
            @Override
            public void onSuccess(MyVideo myVideo) {
                myDataSet.add(myVideo);
                mAdapter.sortAndNotify();
            }

            @Override
            public void onError(Task task) {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        playListener();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void playListener() {
        mAdapter.setDataAndListener(myDataSet, new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, MyVideo myVideo) {
                download(SecondVideosActivity.this,myVideo);
            }

            @Override
            public void onPlayClickListener(View view, String url) {
                VideoUtils.playUrl(SecondVideosActivity.this,Environment.DIRECTORY_DOWNLOADS+"/Modulo2",url);
                //goPlayActivity(url);
            }
        });
    }

    private void goPlayActivity(String url) {
        File mydir = SecondVideosActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/Modulo2");
        File fileWithinMyDir = new File(mydir, url+".mp4");
        fileWithinMyDir.setReadable(true, false);
        String videoResource = fileWithinMyDir.getAbsolutePath();
        Uri intentUri = FileProvider.getUriForFile(SecondVideosActivity.this,"com.mibaldi.playvideos.fileprovider",new File(videoResource));


        Intent playActivity = new Intent(SecondVideosActivity.this, PlayActivity.class);
        playActivity.putExtra("uri",intentUri.toString());
        playActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(playActivity);
    }

    private void initBroadcast() {
        onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Prevents the occasional unintentional call. I needed this.
                if (mDownloadedFileID == -1)
                    return;
                // Grabs the Uri for the file that was downloaded.
                Uri mostRecentDownload =
                        mDownloadManager.getUriForDownloadedFile(mDownloadedFileID);
                // DownloadManager stores the Mime Type. Makes it really easy for us.
                String mimeType =
                        mDownloadManager.getMimeTypeForDownloadedFile(mDownloadedFileID);

                if (mostRecentDownload != null) {
                    myDataSet.get(MyVideo.find(myDataSet,mDownloadedFileID)).setDownload(true);
                    mAdapter.notifyDataSetChanged();
                }

            }
        };
    }


    public void download(Context context,MyVideo myVideo){

        if(PermissionCheck.readAndWriteExternalStorage(context)){
            currentDownload = myVideo.getUrl()+".mp4";
            requestDowload(context,myVideo);
        }
    }
    private void requestDowload(Context context,MyVideo myVideo) {
        int position = MyVideo.find(myDataSet, myVideo.getUrl());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(myVideo.getFullUrl()));
        context.registerReceiver(onComplete, new
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        request.setDescription("Kotlin Video")
                .setTitle(myVideo.getName())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS+"/Modulo2", myVideo.getUrl()+".mp4")
                .allowScanningByMediaScanner();

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (mDownloadManager != null) {
            mDownloadedFileID = mDownloadManager.enqueue(request);
            myDataSet.get(position).setDownloadId(mDownloadedFileID);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
