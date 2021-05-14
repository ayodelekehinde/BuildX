package skyestudios.buildx.service;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import skyestudios.buildx.helpers.JSONSave;
import skyestudios.buildx.interfaces.ResponseCallback;
import skyestudios.buildx.models.APIResponse;
import skyestudios.buildx.models.Libs;

public class SearchTask extends AsyncTask<Void,Void, APIResponse> {

    private HttpsURLConnection connection;
    private URL url;
    private String method;
    private List<Pair<String, String>> headers;
    private static final String TAG = "SearchTask";
    private ResponseCallback callback;

    public SearchTask(String url, List<Pair<String, String>> headers, ResponseCallback callback) {
        try {
            this.url = new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.headers = headers;
        this.callback = callback;
    }

    @Override
    protected APIResponse doInBackground(Void... voids) {
        CookieHandler.setDefault(new CookieManager());
        StringBuilder result = new StringBuilder();
        try {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setDoInput(true);

//            if (headers != null){
//                for (Pair pair : headers) {
//                    connection.setRequestProperty(pair.first.toString(),pair.second.toString());
//                }
//            }
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 302 || connection.getResponseCode() == 308 ) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                if (connection.getHeaderFields().get("Set-Cookie") != null){
                    setCookie(connection.getHeaderFields().get("Set-Cookie"));

                }

                if (result.toString().isEmpty()) {
                    return new APIResponse(connection.getResponseCode(), connection.getResponseMessage());
                } else {
                    return new APIResponse(connection.getResponseCode(), result.toString());
                }
            }else {
                Log.d("Search","Response: "+ connection.getResponseMessage());
                Log.d("Search","Code: "+ connection.getResponseCode());

                callback.onError();
            }

        }catch (Exception x){
            x.printStackTrace();
        }finally {
            connection.disconnect();

        }


        return null;
    }

    @Override
    protected void onPostExecute(APIResponse apiResponse) {
        if (apiResponse != null)
            callback.onResponse(apiResponse);
        super.onPostExecute(apiResponse);
    }

    private void setCookie(List<String> cookies){
        JSONSave.setCookies(cookies);
        Log.d(TAG, "Cookies: "+JSONSave.getCookies());


    }
}
