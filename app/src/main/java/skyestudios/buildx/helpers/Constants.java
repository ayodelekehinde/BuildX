package skyestudios.buildx.helpers;

import android.util.Pair;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Constants {
    public static final String BASE_URL = "https://mvnrepository.com";
    public static final String SEARCH_URL = BASE_URL.concat("/search?q=");
    public static final String LOGIN_AUTH_URL = "https://nsuk.nsuk.edu.ng/nsuk/j_spring_security_check";
    public static final String PROFILE_URL = "https://nsuk.nsuk.edu.ng/nsuk/dashboard";
    public static final String FEED_URL = "https://nsuk.edu.ng/feed";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0";
    public static final String HOST =  "mvnrepository.com";
    public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public static final String ACCEPT_ENCODING = "gzip, deflate, br";
    public static final String ACCEPT_LANGUAGE = "en-US,en;q=0.5";
    public static final String CONTENT_TYPE = "text/html; charset=UTF-8";
    public static final String ORIGIN = "https://nsuk.nsuk.edu.ng";
    public static final String CONNECTION = "keep-alive";
    public static final String REFERER = "https://mvnrepository.com/";
    public static final String UPGRADE = "1";

    public static final String USER_AGENT_K = "User-Agent";
    public static final String HOST_K = "Host";
    public static final String ACCEPT_K = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public static final String ACCEPT_ENCODING_K = "gzip, deflate, br";
    public static final String ACCEPT_LANGUAGE_K = "en-US,en;q=0.5";
    public static final String CONTENT_TYPE_K = "application/x-www-form-urlencoded";
    public static final String ORIGIN_K = "https://nsuk.nsuk.edu.ng";
    public static final String CONNECTION_K = "Connection";
    public static final String REFERER_K = "Referer";
    public static final String UPGRADE_K = "Upgrade-Insecure-Requests";
    public static final String COOKIE_K = "Cookie";
    public static final String CONTENT_LENGTH_K = "Content-Length";


    public static List<Pair<String,String>> getFirstHeaders(){
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>(HOST_K,HOST));
        headers.add(new Pair<>(USER_AGENT_K,USER_AGENT));
        headers.add(new Pair<>(ACCEPT_K,ACCEPT));
        headers.add(new Pair<>(ACCEPT_LANGUAGE_K,ACCEPT_LANGUAGE));
        headers.add(new Pair<>(ACCEPT_ENCODING_K,ACCEPT_ENCODING));
        headers.add(new Pair<>(CONNECTION_K,CONNECTION));
        //headers.add(new Pair<>(REFERER_K,REFERER));
        headers.add(new Pair<>(UPGRADE_K,UPGRADE));
        if (JSONSave.getCookies() != null) {
            for (String cookie : JSONSave.getCookies()) {
                headers.add(new Pair<>(COOKIE_K, cookie.split(";", 1)[0]));
            }
        }
        return headers;
    }
    public static List<Pair<String,String>> itemClickHeaders(String q){
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>(HOST_K,HOST));
        headers.add(new Pair<>(USER_AGENT_K,USER_AGENT));
        headers.add(new Pair<>(ACCEPT_K,ACCEPT));
        headers.add(new Pair<>(ACCEPT_LANGUAGE_K,ACCEPT_LANGUAGE));
        headers.add(new Pair<>(ACCEPT_ENCODING_K,ACCEPT_ENCODING));
        headers.add(new Pair<>(CONNECTION_K,CONNECTION));
        headers.add(new Pair<>(REFERER_K,REFERER.concat("search?q=").concat(q)));
        headers.add(new Pair<>(UPGRADE_K,UPGRADE));
        if (JSONSave.getCookies() != null) {
            for (String cookie : JSONSave.getCookies()) {
                headers.add(new Pair<>(COOKIE_K, cookie.split(";", 1)[0]));
            }
        }
        return headers;
    }


    public static HashMap<String,String> getLoginHeadersMap(String postParams){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Host", "nsuk.nsuk.edu.ng");
        headers.put("User-Agent", USER_AGENT);
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "en-US,en;q=0.5");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Content-Length", Integer.toString(postParams.length()));
        headers.put("Origin", "https://nsuk.nsuk.edu.ng");
        headers.put("Connection", "keep-alive");
        headers.put("Referer", "https://nsuk.nsuk.edu.ng/nsuk");
        headers.put("Upgrade-Insecure-Requests", "1");
        for (String cookie: JSONSave.getCookies()){
            headers.put("Cookie", cookie.split(";",1)[0]);
        }
        return headers;
    }
    public static HashMap<String,String> getLoginHeadersMap(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Host", "nsuk.nsuk.edu.ng");
        headers.put("User-Agent", USER_AGENT);
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "en-US,en;q=0.5");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Referer", "https://nsuk.nsuk.edu.ng/nsuk");
        headers.put("Upgrade-Insecure-Requests", "1");
        if (JSONSave.getCookies() != null){
            for (String cookie: JSONSave.getCookies()){
                headers.put("Cookie", cookie.split(";",1)[0]);
            }
        }
        return headers;
    }

    public static SSLSocketFactory getSSLSocketFactory() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return sslSocketFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }

    }
}
