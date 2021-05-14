package skyestudios.buildx.layoutinflator;

import android.content.Context;
import android.view.ViewGroup;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.layoutinflator.style.ViewFactory;

final class DynamicoLayoutLoader {

    private static final String TAG = "Dynamico.DynamicoLayoutLoader";

    private String res, name;
    private ViewGroup layout;
    private Context context;
    private DynamicoListener listener;

    DynamicoLayoutLoader(String res, String name, ViewGroup layout) {
        this.res = res;
        this.name = name;
        this.layout = layout;
        this.context = layout.getContext();
    }

    public void setListener(DynamicoListener listener) {
        this.listener = listener;
    }

//    public void loadLayoutFromCache() {
//        Utils.log(TAG, "Loading from cache");
//
//        File file = new File(getStoragePath(name, context));
//
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//
//            StringBuilder content = new StringBuilder();
//            String line;
//
//            while((line = reader.readLine()) != null) {
//                content.append(line);
//            }
//
//            addViews(content.toString());
//
//            reader.close();
//        }catch(IOException e) {
//            Utils.log("File error", "Loading layout from cache produced the following error: " + e.getMessage());
//
//            loadLayoutWithoutCache();
//
//            if(listener != null) {
//                listener.onError(e.getMessage());
//            }
//        }
//    }

//    public void loadLayoutWithoutCache() {
//        loadLayoutFromString();
//
//    }



//    public void loadLayoutFromString() {
//        Utils.log(TAG, "Loading from String");
//        File file = new File(getStoragePath(name, context));
//
//
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//
//            StringBuilder content = new StringBuilder();
//            String line;
//
//            while((line = reader.readLine()) != null) {
//                content.append(line);
//            }
//            addViews(content.toString());
//
//            reader.close();
//        }catch(IOException e) {
//            Utils.log("File error", "Loading layout from String produced the following error: " + e.getMessage());
//
////            loadLayoutFromServer();
//
//            if(listener != null) {
//                listener.onError(e.getMessage());
//            }
//        }
//    }

    private void addViews(JSONObject content){
        addViews(content.toString());
    }

    private void addViews(String content) {
        ViewFactory factory = new ViewFactory(context);

        layout.removeAllViews();

        try {
            JSONObject obj = new JSONObject(content);
            factory.addViews(layout, obj);

            if(listener != null) {
                listener.onSuccess(content);
            }
        }catch(Exception e) {
            Utils.log("Layout error", e.getMessage());

            if(listener != null) {
                listener.onError(content);
            }
        }
    }

    private String getDirectoryUrl(String name) {
        return this.res + "/" + name;
    }

    private String getStoragePath(String name, Context context) {
        return context.getFilesDir() + File.separator + name;
    }
}