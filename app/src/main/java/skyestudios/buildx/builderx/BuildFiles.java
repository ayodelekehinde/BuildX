/*
 * Copyright (C) 2016 B. Clint Hall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package skyestudios.buildx.builderx;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

import skyestudios.buildx.helpers.FileUtil;

/**
 * Created by bclinthall on 8/19/16.
 */
public class BuildFiles {
    private File extFileDir;
    File extApk;
    private File filesDir;
    private File buildDir;
    public File buildFiles;

    public File androidJar;
    public File aapt;
    public File aapt2;
    public File buildX;
    public static File initDir(File parent, String childName){
        File dir = new File(parent, childName);
        if(!dir.exists()) dir.mkdir();
        return dir;
    }
    public BuildFiles(Context context){

        extFileDir = context.getExternalFilesDir(null);
        extApk = new File(extFileDir, "extApk.apk");
        filesDir = context.getFilesDir();
        buildDir = initDir(filesDir, "buildStuff");
        buildX = initDir(new File(FileUtil.getExternalStorageDir()),"BuildX");
        androidJar = new File(buildX, "android.jar");
        aapt = new File(buildDir, "aapt");
        aapt2 = new File(buildDir, "aapt2");



//        extApk = new File(extFileDir, "extApk.apk");
//        filesDir = initDir(extFileDir,"Files");
//        buildDir = initDir(filesDir, "Build Stuffs");
//        apks = initDir(buildDir, "Apks");
//        signed = new File(apks, "signed.apk");
//        unsigned = new File(apks, "unsigned.apk");
//        buildFiles = initDir(buildDir, "Build Files");
//        androidJar = new File(buildFiles, "android.jar");
//        aapt = new File(buildFiles, "aapt");
    }

    public BuildFiles clear(){
        deleteFiles(extApk);
        return this;
    }


    static void deleteFiles(File files){
        //deleteFileLoud(files);
        new DeleteFileTask().execute(files);
    }

    private static class DeleteFileTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            for(File file : params){
                file.delete();
            }
            return null;
        }
    }
}
