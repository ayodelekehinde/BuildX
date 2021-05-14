

package skyestudios.buildx.service;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.CompileLogActivity;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.builderx.BuildFiles;
import skyestudios.buildx.builderx.BuildTask;
import skyestudios.buildx.builderx.BuildTaskProgress;
import skyestudios.buildx.builderx.Logger;
import skyestudios.buildx.builderx.Util;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.interfaces.ResultGetter;
import skyestudios.buildx.models.AndroidLibrary;
import skyestudios.buildx.models.AndroidProject;


public class ApkMakerService extends IntentService {

    public static final String SERVICE_NAME = "skyestudios.blocky.service.ApkMakerService";
    public static final String TAG = "ApkMakerService";
    private static final String CHANNEL_ID_DEFAULT = "default";
    private static final int NOTE = 1;
    private static final int DONE_NOTIFICATION_ID = 2;
    ServiceDone serviceDone;


    public ApkMakerService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver getter = intent.getParcelableExtra("collector");
        Bundle b = new Bundle();
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createDefaultChannel(mNotifyManager);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ApkMakerService.this,CHANNEL_ID_DEFAULT);
        mBuilder.setContentTitle("Building Apk")
                .setContentText("Getting started")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_notification);
        startForeground(NOTE, mBuilder.build());

        AndroidProject project = new AndroidProject(MainActivity.getROOTDIR(),MainActivity.getAppPackageName(),MainActivity.getNAME(),getLibs());

        BuildTask buildTask = new BuildTask(this,project);
        buildTask.setProgressListener(new BuildTaskProgress() {
            @Override
            public void onProgress(String progress) {
                if (progress.equals("Done")){
                    sendInstallFinished(progress,mNotifyManager,getter);
                }else {
                    mBuilder.setContentText(progress);
                    mNotifyManager.notify(NOTE, mBuilder.build());
                }
            }

            @Override
            public void onError(String error) {
                if (error.contains("Aapt")) sendBuildError("Aapt Error", mNotifyManager,getter);
                else sendBuildError("Compile Error", mNotifyManager,getter);


            }
        });


    }

    private ArrayList<AndroidLibrary> getLibs(){
        Wood.JAVA("getLibs() called");
        ArrayList<AndroidLibrary> lib = new ArrayList<>();
        File[] rootDir = Utils.getAppLibrary().listFiles(new FileFilter() {
            @Override
            public boolean accept(File jarLib) {
                return (jarLib.isDirectory());
            }
        });
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        String[] libsToAdd = projectBx.split("\n");
        for (File libFile: rootDir) {
            for (String s : libsToAdd) {
                Wood.JAVA("Libs on BX: " +s);
                if (s.equals(libFile.getName())) {
                    File manifestFile = new File(libFile.getAbsolutePath().concat("/AndroidManifest.xml"));
                    File jarFile = new File(libFile.getAbsolutePath());
                    File resFile = new File(libFile.getAbsolutePath().concat("/res"));

                    Wood.JAVA("LibToAdd: "+ s);
                    Wood.JAVA("LibAdded: "+ libFile.getAbsolutePath());

                    if (manifestFile.exists() && resFile.exists()) {
                        AndroidLibrary library = new AndroidLibrary(manifestFile, resFile, jarFile);
                        lib.add(library);
                    } else if (manifestFile.exists() && !resFile.exists()) {
                        AndroidLibrary library = new AndroidLibrary(manifestFile, null, jarFile);
                        lib.add(library);
                    } else if (!manifestFile.exists() && !resFile.exists()) {
                        AndroidLibrary library = new AndroidLibrary(null, null, jarFile);
                        lib.add(library);
                    }
                }
            }
        }


        return lib;
    }


    private void createDefaultChannel(NotificationManager nm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DEFAULT,
                    "Default",
                    NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }
    }
    private void sendInstallFinished(String contentText, NotificationManager manager, ResultReceiver receiver){
        receiver.send(0,new Bundle());
        createDefaultChannel(manager);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID_DEFAULT)
                .setAutoCancel(true)
                .setContentTitle(getStringReplacing(R.string.building_done, "{{appName}}", MainActivity.getNAME()))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification);

        NotificationManager doneNotifyManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        doneNotifyManager.notify(DONE_NOTIFICATION_ID, mBuilder.build());
    }
    private void sendBuildError(String contentText, NotificationManager manager, ResultReceiver receiver){
        receiver.send(0, new Bundle());
        createDefaultChannel(manager);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID_DEFAULT)
                .setAutoCancel(true)
                .setContentTitle(getStringReplacing(R.string.building_failed, "{{appName}}", MainActivity.getNAME()))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification);

        Intent resultIntent = new Intent(this, CompileLogActivity.class);
        if (contentText.contains("Aapt")) resultIntent.putExtra(CompileLogActivity.LOG_TYPE,"Aapt");
        else resultIntent.putExtra(CompileLogActivity.LOG_TYPE,"Compile");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 77, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager doneNotifyManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        doneNotifyManager.notify(DONE_NOTIFICATION_ID, mBuilder.build());
    }


    private String getStringReplacing(int id, String oldStr, String newStr){
        return this.getString(id).replace(oldStr, newStr);
    }

}
