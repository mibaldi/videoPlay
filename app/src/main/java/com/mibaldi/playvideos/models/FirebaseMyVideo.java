package com.mibaldi.playvideos.models;

import com.google.firebase.firestore.PropertyName;

public class FirebaseMyVideo {
    @PropertyName("id")
    public String id;
    @PropertyName("nombre")
    public String name;
    @PropertyName("url")
    public String url;

    public FirebaseMyVideo(){}
}
