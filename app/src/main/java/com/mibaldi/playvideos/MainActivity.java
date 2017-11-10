package com.mibaldi.playvideos;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mibaldi.playvideos.models.MyVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout mDownloadManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
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
        mAdapter = new VideoAdapter();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                for (String key : data.keySet()) {
                                    // write your code here
                                    String url = (String) data.get(key);
                                    MyVideo myVideo = new MyVideo(key, false);
                                    if (fileExists(key)){
                                        myVideo.setDownload(true);
                                    }
                                    myDataSet.add(myVideo);
                                    Collections.sort(myDataSet, new Comparator<MyVideo>() {
                                        @Override
                                        public int compare(MyVideo o1, MyVideo o2) {
                                            return (Integer.parseInt(o1.getUrl()) > Integer.parseInt(o2.getUrl()))? 0: -1;
                                        }
                                    });

                                    mAdapter.notifyDataSetChanged();
                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


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
            int position = MyVideo.find(myDataSet, fileName);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            MainActivity.this.registerReceiver(onComplete, new
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            request.setDescription("Kotlin Video")
                    .setTitle("Video"+fileName)
                    .setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS, fileName+".mp4");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // get download service and enqueue file
            mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            if (mDownloadManager != null) {

                mDownloadedFileID = mDownloadManager.enqueue(request);
                myDataSet.get(position).setDownloadId(mDownloadedFileID);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public boolean fileExists(String url){
        File file = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),url+".mp4");
        return file.exists();
    }
}
