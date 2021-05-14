package skyestudios.buildx.activities.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.activities.search.SearchView;
import skyestudios.buildx.helpers.Constants;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.interfaces.ResponseCallback;
import skyestudios.buildx.models.APIResponse;
import skyestudios.buildx.models.SearchLib;
import skyestudios.buildx.service.SearchTask;

public class SearchInteractor {
    private SearchView view;
    public static String SEARCH_Q = "";
    public SearchInteractor(SearchView view) {
        this.view = view;
    }

    void getFirstCookie(){
        new SearchTask(Constants.BASE_URL, Constants.getFirstHeaders(), new ResponseCallback() {
            @Override
            public void onResponse(APIResponse result) {
                if (result.responseCode == 200){
                    Wood.MVNTAG(result.response);
                }else {
                    Wood.MVNTAG("Response Code: " +result.responseCode);
                }
            }

            @Override
            public void onError() {

            }
        }).execute();
    }

    void getSearch(String q){
        SEARCH_Q = q;
        new SearchTask(Constants.SEARCH_URL.concat(q), Constants.getFirstHeaders(), new ResponseCallback() {
            @Override
            public void onResponse(APIResponse result) {
                Wood.MVNTAG(result.response);
                Document doc = Jsoup.parse(result.response);
                Elements elements = doc.select("div.im");
                parseHtml(elements);
            }

            @Override
            public void onError() {
                view.getError();
            }
        }).execute();
    }
    private void parseHtml(Elements content){
        List<SearchLib> libs = new ArrayList<>();
        String title = "";
        String subTitle = "";
        String url = "";
        String usages = "";
        String img = "";
        for (Element ele: content){
            title = ele.select("a").get(1).text();
            if (ele.select("p.im-subtitle").text().endsWith("Apache"))subTitle = ele.select("p.im-subtitle").text().replace("Apache","");
            else subTitle  = ele.select("p.im-subtitle").text();
            url = ele.select("a").attr("href");
            usages = ele.select("a.im-usage").text();
            img = ele.select("img.im-logo").attr("src");
            Wood.MVNTAG("URL: " + url);
            Wood.MVNTAG("IMG: "+ img);
            libs.add(new SearchLib(title,subTitle,url,usages,img));
        }
        view.getResponse(libs);
    }
    public static String getSearchQ(){
        return SEARCH_Q;
    }
}
