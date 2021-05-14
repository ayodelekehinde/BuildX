package skyestudios.buildx.helpers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import skyestudios.buildx.BuildX;
import skyestudios.buildx.R;
import skyestudios.buildx.adapters.ViewOptionAdapter;
import skyestudios.buildx.models.OptionsView;

public final class Utils {

    private static final String MINSDK = "MINSDK";
    private static final String TARGETSDK = "TARGETSDK";
    private static final String VERNAME ="VERNAME" ;
    private static final String VERCODE = "VERCODE";
    public static final String NEXT_LINE = "\n";
    static Context context;
    public Utils(Context context){
        this.context = context;
    }
    public static void log(String tag, String msg) {
        Log.d((tag != null && !tag.isEmpty()) ? tag : "EMPTY TAG",
                (msg != null && !msg.isEmpty()) ? msg : "EMPTY MESSAGE");
    }

    public static File getAppBuildDir(){
        File buildXDir = new File(FileUtil.getExternalStorageDir().concat("/BuildX"));
        if (!buildXDir.exists())
            buildXDir.mkdir();
        return buildXDir;
    }
    public static File getAppLibrary(){
        File appLibDir = new File(getAppBuildDir().getAbsolutePath().concat("/Libraries"));
        if (!appLibDir.exists())
            appLibDir.mkdir();
        return appLibDir;
    }
    public static File getDexedLibs(){
        File dexedLibs = new File(getAppBuildDir().getAbsolutePath().concat("/Dexed Libraries"));
        if (!dexedLibs.exists())
            dexedLibs.mkdir();
        return dexedLibs;
    }
    public static File getKeystoresDir(){
        File buildXDir = new File(getAppBuildDir().getAbsolutePath().concat("/KeyStores"));
        if (!buildXDir.exists())
            buildXDir.mkdir();
        return buildXDir;
    }
    public static File getLibTempDir(){
        File buildXDir = new File(getAppBuildDir().getAbsolutePath().concat("/LibTemp"));
        if (!buildXDir.exists())
            buildXDir.mkdir();
        return buildXDir;
    }
    public static File getPreview(){
        File buildXDir = new File(getAppBuildDir().getAbsolutePath().concat("/Preview"));
        if (!buildXDir.exists())
            buildXDir.mkdir();
        return buildXDir;
    }
    /*
    Get project list in a json file
     */
    public static File getProjectList(){
        File projectList = new File(FileUtil.getPackageDataDir(BuildX.getAppContext()).concat("/projects.json"));
        if (!projectList.exists()) {
            try {
                projectList.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return projectList;
    }
    /*
    Projects Path
     */

    public static File getProjectFolder(){
        File appLibDir = new File(getAppBuildDir().getAbsolutePath().concat("/Projects"));
        if (!appLibDir.exists())
            appLibDir.mkdir();
        return appLibDir;
    }

    public static String hash(byte[] chars) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(chars);

        return new BigInteger(1, md.digest()).toString(16);
    }
    public static void saveFile(File file, String content) throws IOException {
        file.getParentFile().mkdirs();
        FileOutputStream output = new FileOutputStream(file);
        IOUtils.write(content, output);
        output.close();
    }
    public static boolean hasNetworkAccess(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        }catch(Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isValidColor(String url) {
        try {
            Color.parseColor(url);
            return true;
        }catch(IllegalArgumentException e) {
            return false;
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public static void showMessage(Context mContext, String message){
        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
    }
    public static File unpackAsset(Context context, String assetName, File dest) throws IOException {
        if(!dest.exists()){
            InputStream assetIn = context.getAssets().open(assetName);
            dest.createNewFile();
            int length = 0;
            byte[] buffer = new byte[4096];
            FileOutputStream rawOut = new FileOutputStream(dest);
            while ((length = assetIn.read(buffer)) > 0) {
                rawOut.write(buffer, 0, length);
            }
            rawOut.flush();
            rawOut.close();
            assetIn.close();
        }
        return dest;
    }

    public static void alertDialog( Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        builder.setPositiveButton(R.string.OkButtonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void alertDialog(Context context, int titleId, int messageId, int buttonLabelId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (titleId != 0) alertDialogBuilder.setTitle(titleId);
        if (messageId != 0) alertDialogBuilder.setMessage(messageId);
        alertDialogBuilder.setPositiveButton(buttonLabelId, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static boolean unpackZip(String pathToZip, String destPath)
    {
        //new File(destPath).mkdirs();
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(pathToZip);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                // zapis do souboru
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(destPath, filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(new File(destPath, filename));

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}