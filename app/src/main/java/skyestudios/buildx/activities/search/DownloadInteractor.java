package skyestudios.buildx.activities.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.util.concurrent.HandlerExecutor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import skyestudios.buildx.helpers.Constants;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.interfaces.DownloadListener;
import skyestudios.buildx.interfaces.ResponseCallback;
import skyestudios.buildx.models.APIResponse;
import skyestudios.buildx.service.DownloadTask;
import skyestudios.buildx.service.SearchTask;

public class DownloadInteractor {

    private Context context;
    private String URL = "";
    public DownloadInteractor(Context context) {
        this.context = context;
    }
    public void versionPick(String url){
        URL = url;
        new SearchTask(url, Constants.itemClickHeaders(SearchInteractor.getSearchQ()), new ResponseCallback() {
            @Override
            public void onResponse(APIResponse result) {


                Document doc = Jsoup.parse(result.response);
                String ver = doc.select("a.vbtn").first().text();
                versionDownloadPage(url.concat("/"+ver));

            }

            @Override
            public void onError() {

            }
        }).execute();
    }
   private void versionDownloadPage(String url){
        new SearchTask(url, Constants.itemClickHeaders(URL), new ResponseCallback() {
            @Override
            public void onResponse(APIResponse result) {
                Wood.MVNTAG("Version Download Page Code: "+ result.responseCode);
                Wood.MVNTAG("Version Download Page Message: "+ result.response);

                Document doc = Jsoup.parse(result.response);
                String downloadUrl = doc.select("a.vbtn").first().attr("href");
                if (downloadUrl.endsWith("pom")) downloadUrl = doc.select("a.vbtn").get(1).attr("href");
                Wood.MVNTAG("Download Url: "+ downloadUrl);
                download(downloadUrl);

            }

            @Override
            public void onError() {

            }
        }).execute();
    }
    private void download(String url){
        ProgressDialog pg = new ProgressDialog(context);
        pg.setMessage("Downloading..");
        pg.setIndeterminate(true);
        pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pg.setCancelable(false);
        new DownloadTask(url, pg, new DownloadListener() {
            @Override
            public void done() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showMessage(context,"Added Successfully");
                    }
                });
            }

            @Override
            public void error() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showMessage(context,"Error Occurred");
                    }
                });
            }

            @Override
            public void exists() {
                pg.dismiss();
                Utils.showMessage(context,"Lib Exists");
            }

    }).execute();

    }

}
