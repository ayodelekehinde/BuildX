package skyestudios.buildx.layoutinflator;

import android.text.style.ForegroundColorSpan;

import com.duy.dx.util.ListIntSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Wood;

public class XmlParser {

    private String filePath;
    JSONObject baseJson = new JSONObject();
    JSONObject firstLayout = new JSONObject();
    List<JSONObject> views = new ArrayList<>();


    public XmlParser(String filePath) {
        this.filePath = filePath;
    }

    public void parse()throws Exception{
        Wood.XML("Parsing: "+ filePath);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(new StringReader(FileUtil.readFile(filePath)));
        Wood.XML("Jspn Data: "+ getTheTag(xmlPullParser));


//        JSONObject baseAttr = new JSONObject();
//
//        JSONArray view = new JSONArray();
//
//        String path = "";
//
//        while (eventType != XmlPullParser.END_DOCUMENT) {
//
//            if (eventType == XmlPullParser.START_TAG) {
//
//                if (xmlPullParser.getName().endsWith("out") && xmlPullParser.getAttributeName(0).contains("xmlns")){
//                    String className = "android.widget."+xmlPullParser.getName();
//                    baseAttr.put("class",className);
//                    for (int i=0; i<xmlPullParser.getAttributeCount(); i++){
//                        if (!xmlPullParser.getAttributeName(i).contains("xmlns")) {
//                            baseAttr.put(xmlPullParser.getAttributeName(i).replace("android:", ""), xmlPullParser.getAttributeValue(i));
//                        }
//                    }
//                    //views.add(baseAttr);
//                    firstLayout = baseAttr;
//
//                }else if (xmlPullParser.getName().endsWith("out")){
//                    String className = "android.widget."+xmlPullParser.getName();
//                    baseAttr.put("class",className);
//                    for (int i=0; i<xmlPullParser.getAttributeCount(); i++){
//                        baseAttr.put(xmlPullParser.getAttributeName(i).replace("android:", ""), xmlPullParser.getAttributeValue(i));
//                    }
//                    views.add(baseAttr);
//                    path = xmlPullParser.getName();
//                }else {
//                    String className = "android.widget."+xmlPullParser.getName();
//                    baseAttr.put("class",className);
//                    for (int i=0; i<xmlPullParser.getAttributeCount(); i++){
//                        baseAttr.put(xmlPullParser.getAttributeName(i).replace("android:", ""), xmlPullParser.getAttributeValue(i));
//                    }
//
//                    views.add(baseAttr);
//                   //path = path +"/"+ xmlPullParser.getName();
//                }
//
//
//            } else if (eventType == XmlPullParser.END_TAG ) {
//                if (path.equals(xmlPullParser.getName())){
//                    Wood.XML("Closed?: " + true);
//                    getTheObject(views);
//
//                    path = "";
//                    //baseJson.put("views", view);
//                }else {
//                    Wood.XML("Closed?: " + false);
//                }
//                //path = path +"/"+ xmlPullParser.getName();
//            }else if (eventType == XmlPullParser.TEXT){
//                baseAttr = new JSONObject();
//            }
//            eventType = xmlPullParser.next();
//
//        }


    }
    private String getTheTag(XmlPullParser xpp){
        JSONObject baseAttr = new JSONObject();
        String name = "";
        try {
            xpp.nextTag();
            xpp.require(XmlPullParser.START_TAG,"",null);
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().endsWith("out") && xpp.getAttributeName(0).contains("xmlns")) {
                        String className = "android.widget." + xpp.getName();
                        baseAttr.put("class", className);
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            if (!xpp.getAttributeName(i).contains("xmlns")) {
                                baseAttr.put(xpp.getAttributeName(i).replace("android:", ""), xpp.getAttributeValue(i));
                            }
                        }
                       firstLayout = baseAttr;
                    }
                }
            }
            name = firstLayout.toString(4);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }



}
