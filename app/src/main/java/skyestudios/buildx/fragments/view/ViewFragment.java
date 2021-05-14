package skyestudios.buildx.fragments.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.adapters.ViewsAdapter;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.FilesUtils;
import skyestudios.buildx.helpers.IOUtils;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.models.ViewHeader;
import skyestudios.buildx.models.ViewsItem;
import skyestudios.buildx.models.ViewsModel;

public class ViewFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerViews;
    private FrameLayout mLayoutDesign;
    ViewsAdapter adapter;
    private FloatingActionButton mViewAdd;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view, container, false);

        initView(v);
        return v;
    }

    private void initView(@NonNull final View itemView) {
        mRecyclerViews = itemView.findViewById(R.id.views_list);

        mRecyclerViews.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViews.setHasFixedSize(true);
        adapter = new ViewsAdapter(getActivity(), getViews());
        mRecyclerViews.setAdapter(adapter);


        mViewAdd =  itemView.findViewById(R.id.add_view);
        mViewAdd.setOnClickListener(this);
    }

    private List<ViewsModel> getViews() {
        List<ViewsModel> list = new ArrayList<>();
        String path = MainActivity.getFilePath();

        File res = new File(path.concat("/app/src/main/res"));
        for (File resFiles: res.listFiles()){
            if (!resFiles.getName().contains("drawable")){
                ViewHeader header = new ViewHeader();
                header.setHeader(resFiles.getName());
                list.add(header);
                for (File indFile: resFiles.listFiles()){
                    String fileSize = FilesUtils.byteCountToDisplaySize(indFile.length());
                    ViewsItem item = new ViewsItem();
                    item.setPath(indFile.getAbsolutePath());
                    item.setViewName(indFile.getName());
                    item.setSize(fileSize);
                    list.add(item);
                }
            }
        }
        File manifestFile = new File(path.concat("/app/src/main/AndroidManifest.xml"));
        ViewHeader manifestHead = new ViewHeader();
        manifestHead.setHeader("manifest");
        list.add(manifestHead);
        String fileSize = FilesUtils.byteCountToDisplaySize(manifestFile.length());
        ViewsItem item = new ViewsItem();
        item.setPath(manifestFile.getAbsolutePath());
        item.setViewName(manifestFile.getName());
        item.setSize(fileSize);
        list.add(item);

        return list;

    }
    private void createNewXmlFile() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_create_new_xml);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        AppCompatEditText className = dialog.findViewById(R.id.class_name);
        AppCompatButton saveButton = dialog.findViewById(R.id.create_new_class_ok_button);
        AppCompatButton cancelButton = dialog.findViewById(R.id.create_new_class_cancel_button);
        AppCompatSpinner xmls = dialog.findViewById(R.id.xml_spinner);


        File res = new File(MainActivity.getFilePath().concat("/app/src/main/res"));
        ArrayList<String> folders = new ArrayList<>();
        for (File file: res.listFiles()){
            folders.add(file.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,folders);
        xmls.setAdapter(adapter);

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        saveButton.setOnClickListener(view -> {
            String strClassName = className.getText().toString().trim();
            if (strClassName.isEmpty()) className.setError("Enter file name");
            else createTheXml(strClassName,xmls.getSelectedItem().toString());
            Utils.showMessage(getActivity(),"Created Successfully:");
            dialog.dismiss();
        });

        dialog.show();

    }
    private void createTheXml(String className, String folder){
        AssetManager assets = getActivity().getAssets();
        File xmlFile = new File(MainActivity.getROOTDIR().concat("/app/src/main/res/").concat(folder).concat("/"),className+".xml");
        Wood.JAVA("Xml Location: "+ xmlFile);
        if (xmlFile.exists())
            Utils.showMessage(getActivity(),"Xml File Exists");
        else {

            String xmlName = "precodes/xml/main.xml";
            try {
                String content = IOUtils.toString(assets.open(xmlName));
                saveFile(xmlFile,content);
                adapter.addNewItem(getViews());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void saveFile(File file, String content) throws IOException {
        file.getParentFile().mkdirs();
        FileOutputStream output = new FileOutputStream(file);
        IOUtils.write(content, output);
        output.close();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_view:
                createNewXmlFile();
                break;
            default:
                break;
        }
    }
}
