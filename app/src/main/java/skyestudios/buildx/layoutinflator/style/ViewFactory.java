package skyestudios.buildx.layoutinflator.style;

import android.content.Context;
import android.provider.DocumentsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.othereditor.langs.internal.TrieTree;

public final class ViewFactory {

    private static final String TAG = "Dynamico.ViewFactory";
    ViewGroup layout;

    private Context context;
    private String androidTag = "android.widget.";

    public ViewFactory(Context context) {
        this.context = context;
    }

    public void addXmlViews(ViewGroup layout, String xmlString) throws Exception{
        Utils.log(TAG, "Adding the views  "+ layout.getClass().getSimpleName());

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(xmlString));
        doc.getDocumentElement().normalize();

        JSONObject object = new JSONObject();
        JSONObject atrr = new JSONObject();

        object.put("class",androidTag.concat(doc.getDocumentElement().getNodeName()));


        for (int i = 0; i< doc.getDocumentElement().getAttributes().getLength(); i++){
            Node node = doc.getDocumentElement().getAttributes().item(i);
            atrr.put(node.getNodeName().replace("android:", ""),node.getNodeValue());
        }
        object.put("attributes", atrr);

        if (doc.getDocumentElement().hasChildNodes()){
            readNodes(doc.getDocumentElement().getChildNodes(),object);
        }

        Wood.XML("JSON Data: "+ object.toString(3));
        layout.addView(getView(object,layout.getClass()));
    }

    private void readNodes(NodeList list, JSONObject obj) throws Exception{
        JSONObject object;
        JSONObject attr;
        JSONArray views = new JSONArray();

        for (int i = 0; i <list.getLength(); i++){
            Node tempNode = list.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE){
                object = new JSONObject();
                attr = new JSONObject();
                object.put("class", androidTag.concat(tempNode.getNodeName()));

                if (tempNode.hasAttributes()){
                    NamedNodeMap nodeMap = tempNode.getAttributes();

                    for (int map = 0; map < nodeMap.getLength(); map++){
                        Node node = nodeMap.item(map);
                        attr.put(node.getNodeName().replace("android:", ""),node.getNodeValue());
                    }
                }
                object.put("attributes", attr);
                if (tempNode.hasChildNodes()){
                    readNodes(tempNode.getChildNodes(),object);
                }
                views.put(object);
            }


        }
        obj.put("views", views);
    }


    public View addViews(ViewGroup layout, JSONObject object) throws Exception {
        Utils.log(TAG, "Adding children for view " + layout.getClass().getSimpleName());

        JSONArray views = object.getJSONArray("views");

        for(int i = 0; i < views.length(); i++) {
            try {
                layout.addView(getView(views.getJSONObject(i), layout.getClass()));
            }catch(Exception e) {
                Utils.log("View error", "Caused by JSON object at index " + i + "\nDetails: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return layout;
    }

    private View getView(JSONObject object, Class parentClass) throws Exception {
        View view = createView(object.getString("class"));

        if(object.has("views")) {
            view = addViews((ViewGroup) view, object);
        }

        if(object.has("attributes")) {
            view = styleView(view, object.getJSONObject("attributes").put("parent_class", parentClass));
        }

        return view;
    }

    private View createView(String className) throws Exception {
        Utils.log(TAG, "Creating view " + className);

        Class elementClass = Class.forName(className);

        Constructor constructor = elementClass.getConstructor(Context.class);

        return (View) constructor.newInstance(context);
    }

    public View styleView(View view, JSONObject attributes) throws Exception {
        Utils.log(TAG, "Styling view " + view.getClass().getSimpleName());

        if(view instanceof Switch) {
            view = new SwitchStyler(this, context).style(view, attributes);
        }else if(view instanceof ToggleButton) {
            view = new ToggleButtonStyler(this, context).style(view, attributes);
        }else if(view instanceof CompoundButton) {
            view = new CompoundButtonStyler(this, context).style(view, attributes);
        }else if(view instanceof TextView) {
            view = new TextViewStyler(this, context).style(view, attributes);
        }else if(view instanceof ImageView) {
            view = new ImageViewStyler(this, context).style(view, attributes);
        }else if(view instanceof LinearLayout) {
            view = new LinearLayoutStyler(this, context).style(view, attributes);
        }else if(view instanceof RelativeLayout) {
            view = new RelativeLayoutStyler(this, context).style(view, attributes);
        }else if(view instanceof GridLayout) {
            view = new GridLayoutStyler(this, context).style(view, attributes);
        }else if(view instanceof GridView) {
            view = new GridViewStyler(this, context).style(view, attributes);
        }else if(view instanceof ScrollView) {
            view = new ScrollViewStyler(this, context).style(view, attributes);
        }else if(view instanceof FrameLayout) {
            view = new FrameLayoutStyler(this, context).style(view, attributes);
        }

        view = new CustomViewStyler(this, context).style(view, attributes);

        return view;
    }
}