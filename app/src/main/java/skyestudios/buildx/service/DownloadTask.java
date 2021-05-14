package skyestudios.buildx.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.builderx.Util;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.FilesUtils;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.interfaces.DownloadListener;

public class DownloadTask extends AsyncTask<Void, Integer, String> {

    private String url;
    private ProgressDialog pg;
    private String fileName;
    private DownloadListener listener;

    public DownloadTask(String url, ProgressDialog pg,DownloadListener listener) {
        this.url = url;
        this.pg = pg;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream input = null;
        OutputStream output = null;
        HttpsURLConnection connection = null;
        fileName = url.substring(url.lastIndexOf("/")+1).trim();

        try {
            URL dUrl = new URL(url);
            connection = (HttpsURLConnection) dUrl.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                listener.error();
                Wood.MVNTAG("Response Message: " + connection.getResponseMessage());
            }

            int fileSize = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(Utils.getLibTempDir().getAbsolutePath().concat("/").concat(fileName));
            byte [] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1){
                if (isCancelled()){
                    input.close();
                    return null;
                }
                total += count;
                if (fileSize > 0){
                    publishProgress((int)(total * 100/ fileSize));
                    output.write(data,0,count);
                }
            }

        }catch (IOException e){
            Wood.MVNTAG("Error: "+ e.toString());
        }finally {
            try{
                if (output != null) output.close();
                if (input != null) input.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            if (connection != null) connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pg.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        File pathFile = new File(Utils.getLibTempDir().getAbsolutePath().concat("/").concat(fileName));
        File destFile = new File(Utils.getAppLibrary().getAbsolutePath().concat("/").concat(fileName.substring(0,fileName.lastIndexOf("."))));
        String forFileName = fileName.substring(0,fileName.lastIndexOf("."));

        if (destFile.exists()){
            pathFile.delete();
            listener.exists();
            return;
        }
        if (fileName.endsWith(".jar")) {
            if (!destFile.exists()) destFile.mkdir();
            try {
                FilesUtils.copyFileToDirectory(pathFile,destFile);
                addToLib(forFileName);
                pathFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if (!destFile.exists()) destFile.mkdir();
            Utils.unpackZip(pathFile.getAbsolutePath(),destFile.getAbsolutePath());
            addToLib(forFileName);
            pathFile.delete();
        }
        pg.dismiss();
        listener.done();

    }
    private void addToLib(String name){
        StringBuilder projects = new StringBuilder();
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        projects.append(projectBx);
        projects.append("\n");
        projects.append(name);
        FileUtil.writeFile(MainActivity.getROOTDIR().concat("/app/project.bx"),projects.toString());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
