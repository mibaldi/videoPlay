package com.mibaldi.playvideos;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VideoAdapter mAdapter;
    private ArrayList<String> myDataSet;
    public String currentDownload = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataSet = new ArrayList<String>();
        for (int i = 1 ; i<7; i++){
            myDataSet.add(""+i);
        }
        mAdapter = new VideoAdapter(this);
        mAdapter.setDataAndListener(myDataSet, new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, String url) {
                download("http://mibaldi.com/Kotlin/"+url+".mp4",url);
            }

            @Override
            public void onPlayClickListener(View view, String url) {
                File mydir = MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File fileWithinMyDir = new File(mydir, url+".mp4");
                fileWithinMyDir.setReadable(true, false);
                String videoResource = fileWithinMyDir.getAbsolutePath();
                Uri intentUri = FileProvider.getUriForFile(MainActivity.this,"com.mibaldi.playvideos.fileprovider",new File(videoResource));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(intentUri, "video/mp4");
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }
    public void download(String url,String fileName){
        if(PermissionCheck.readAndWriteExternalStorage(MainActivity.this)){
            currentDownload = fileName+".mp4";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Kotlin Video")
                    .setTitle("Video"+fileName)
                    .setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS, fileName+".mp4");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            if (manager != null) {

                manager.enqueue(request);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
