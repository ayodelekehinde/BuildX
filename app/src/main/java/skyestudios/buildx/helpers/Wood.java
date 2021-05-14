package skyestudios.buildx.helpers;

import android.util.Log;

public class Wood {
    public static void MVNTAG(String msg){
        Log.d("MVN", msg);
    }
    public static void GRADLE(String msg){
        Log.d("Gradle", msg);
    }
    public static void JAVA(String msg){
        Log.d("Java", msg);
    }
    public static void XML(String msg){
        Log.d("XmlParser", msg);
    }

    public static void d(String tag,String msg){
        Log.d(tag,msg);
    }
}
