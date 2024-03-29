package com.mibaldi.playvideos;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mibaldi.playvideos.firebaseUtils.VideoUtils;
import com.mibaldi.playvideos.models.MyVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initRecyclerView();

    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initBroadcast();
        mAdapter = new VideoAdapter();
        VideoUtils.generateList(this,Environment.DIRECTORY_DOWNLOADS, "Modulo1", new VideoUtils.onResponse() {
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
        //getData();
        playListener();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void playListener() {
        mAdapter.setDataAndListener(myDataSet, new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view,MyVideo myVideo) {
                download(MainActivity.this,myVideo);
            }

            @Override
            public void onPlayClickListener(View view, String url) {
                VideoUtils.playUrl(MainActivity.this,Environment.DIRECTORY_DOWNLOADS,url);
            }
        });
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

    private void sortAndNotify(){
        Collections.sort(myDataSet, new Comparator<MyVideo>() {
            @Override
            public int compare(MyVideo o1, MyVideo o2) {
                return (Integer.parseInt(o1.getUrl()) > Integer.parseInt(o2.getUrl()))? 0: -1;
            }
        });
        mAdapter.notifyDataSetChanged();
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
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, myVideo.getUrl()+".mp4")
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_camera) {
            Intent secondVideoActivityIntent = new Intent(MainActivity.this, SecondVideosActivity.class);
            secondVideoActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(secondVideoActivityIntent);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
