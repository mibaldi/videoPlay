package com.mibaldi.playvideos.utils;

import com.mibaldi.playvideos.BuildConfig;

public class Utils {

    public static String generateUrl(String collectionName,String videoUrl){
        String baseUrl = "";
        if (collectionName.equals("Modulo1")){
            baseUrl = BuildConfig.HOST_Modulo1;
        }else {
            baseUrl = BuildConfig.HOST_Modulo2;
        }
        return baseUrl+videoUrl;

    }
}
