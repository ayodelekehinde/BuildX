package skyestudios.buildx.helpers;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class JSONSave {

    private static final String filePath = Utils.getAppBuildDir().getAbsolutePath().concat("/cookies.json");
    private static final File fl = new File(filePath);

    public static List<String> getCookies(){
        List<String> cookies = new Gson().fromJson(JSONUtils.readFile(filePath), new TypeToken<List<String>>() {
        }.getType());
        return cookies;
    }
    public static void setCookies(List<String> cookies){
        String json = new Gson().toJson(cookies);
        JSONUtils.writeJsonFile(json, filePath);
    }
    public static void clearCookies(){
        if (fl.exists()){
            fl.delete();
        }
    }
}
