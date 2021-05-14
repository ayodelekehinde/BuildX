package skyestudios.buildx.helpers;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter {

    static String fileName = "myview.json";
    static File rootpath, jsonPath;

    public static void saveJson(Context context, String mJsonResponse){
        try{
            rootpath = context.getFilesDir();
            jsonPath = initDir(rootpath,"Jsons");
            if (jsonPath.exists()) {
                jsonPath.delete();

                FileWriter file = new FileWriter(jsonPath + "/" + fileName,false);
                file.write(mJsonResponse);
                file.flush();
                file.close();
            }else {
                FileWriter file = new FileWriter(jsonPath + "/" + fileName);
                file.write(mJsonResponse);
                file.flush();
                file.close();
            }
        }catch (IOException e){
            Utils.log(JSONWriter.class.getSimpleName(), "Error in writing the file " +e.getLocalizedMessage());
        }
    }

    public static File getJason(Context context) {
        rootpath = context.getFilesDir();
        jsonPath = initDir(rootpath,"Jsons");
        return new File(jsonPath + "/" + fileName);

    }

   static File initDir(File parent, String childName){
        File dir = new File(parent, childName);
        if(!dir.exists()) dir.mkdir();
        return dir;
    }


}
