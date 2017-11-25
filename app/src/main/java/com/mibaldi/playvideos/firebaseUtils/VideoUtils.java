package com.mibaldi.playvideos.firebaseUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mibaldi.playvideos.models.FirebaseMyVideo;
import com.mibaldi.playvideos.models.MyVideo;

import java.io.File;

public class VideoUtils {
    public interface onResponse{
        void onSuccess(MyVideo myVideo);
        void onError(Task task);
    }
    public static void generateList(final Context context, final String filesDir, final String collectionName, final onResponse listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                FirebaseMyVideo firebaseMyVideo = document.toObject(FirebaseMyVideo.class);
                                MyVideo myVideo = MyVideo.parseFirebaseVideo(collectionName,firebaseMyVideo);
                                if (fileExists(context,filesDir,firebaseMyVideo.url)){
                                    myVideo.setDownload(true);
                                }
                                listener.onSuccess(myVideo);
                            }
                        } else {
                            listener.onError(task);
                        }
                    }
                });
    }

    public static boolean fileExists(Context context,String filesDir, String id){
        File file = new File(context.getExternalFilesDir(filesDir),id);
        return file.exists();
    }
    public static void playUrl(Context context,String filesDir,String url) {
        File mydir = context.getExternalFilesDir(filesDir);
        File fileWithinMyDir = new File(mydir, url+".mp4");
        fileWithinMyDir.setReadable(true, false);
        String videoResource = fileWithinMyDir.getAbsolutePath();
        Uri intentUri = FileProvider.getUriForFile(context,"com.mibaldi.playvideos.fileprovider",new File(videoResource));

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(intentUri, "video/mp4");
        context.startActivity(intent);
    }


}
