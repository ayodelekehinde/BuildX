package skyestudios.buildx.layoutinflator.style;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.nio.charset.StandardCharsets;

import skyestudios.buildx.helpers.Utils;

final class DrawableLoader {

    private static final String TAG = "Dynamico.DrawableLoader";

    private int requestCode;

    private boolean cache, used;
    private OnDrawableLoadedListener listener;
    private Context context;

    DrawableLoader(JSONObject attributes, OnDrawableLoadedListener listener, Context context) throws JSONException {
        this.listener = listener;
        this.context = context;

        if(attributes.has("cache")) {
            cache = attributes.getBoolean("cache");
        }
    }

    void load(String src, int requestCode) {
        if(used) {
            Utils.log(TAG, "DrawableLoader cannot be reused. Please construct new one to load another drawable.");
            return;
        }

        this.used = true;
        this.requestCode = requestCode;

    }

//    private void loadImageFromServer(final String src) {
//        final ImageDownload request = new ImageDownload(src, context);
//        request.addHandler(new ApiResponse() {
//            @Override
//            public void onSuccess(String response) {
//                if(cache) {
//                    try {
//                        FileUtils.writeByteArrayToFile(createFile(src), request.getBytes());
//                    }catch(Exception e) {
//                        Utils.log("Image error", "Loading image from server and caching it produced the following error: " + e.getMessage());
//                    }
//                }
//
//                listener.onDrawableLoaded(request.getDrawable(), requestCode);
//            }
//
//            @Override
//            public void onError(String message) {
//                Utils.log("Image error", "Loading image from server produced the following error: " + message);
//
//                loadImageFromCache(src);
//            }
//        });
//        request.start();
//    }

//    private void loadImageFromCache(String src) {
//        byte[] bytes;
//
//        try {
//            bytes = FileUtils.readFileToByteArray(createFile(src));
//        }catch(Exception e) {
//            Utils.log("Image error", "Loading image from cache produced the following error: " + e.getMessage());
//
//            loadImageFromServer(src);
//
//            return;
//        }
//
//        if(bytes != null && bytes.length > 0) {
//            listener.onDrawableLoaded(new BitmapDrawable(context.getResources(),
//                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length)), requestCode);
//        }else {
//            loadImageFromServer(src);
//        }
//    }

    private File createFile(String src) throws Exception {
        String path = context.getFilesDir() + File.separator + Utils.hash(src.getBytes(StandardCharsets.UTF_8));

        Utils.log(TAG, "Creating file at path: " + path);

        File file = new File(path);
        file.createNewFile();

        return file;
    }
}