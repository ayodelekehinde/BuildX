package skyestudios.buildx.fragments.logic;

import android.app.Dialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.adapters.LogicAdapter;
import skyestudios.buildx.helpers.FilesUtils;
import skyestudios.buildx.helpers.IOUtils;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.models.LogicModel;

import static skyestudios.buildx.helpers.Utils.saveFile;

public class LogicFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mListLogic;
    private LogicAdapter adapter;
    private FloatingActionButton mLogicAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logic, container, false);
        initView(v);
        return v;
    }

    private void initView(@NonNull final View itemView) {
        mListLogic = itemView.findViewById(R.id.logic_list);
        mListLogic.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListLogic.setHasFixedSize(true);
        adapter = new LogicAdapter(getActivity(), getViews());
        mListLogic.setAdapter(adapter);
        mLogicAdd = (FloatingActionButton) itemView.findViewById(R.id.add_logic);
        mLogicAdd.setOnClickListener(this);
    }

    private List<LogicModel> getViews() {
        List<LogicModel> list = new ArrayList<>();

        String path = MainActivity.getFilePath();
        String pack = MainActivity.getAppPackageName().replace(".", "/");


        File dir = new File(path.concat("/app/src/main/java/").concat(pack));

        File[] listFiles = dir.listFiles();

        list.clear();
        for (File file : listFiles) {
            if (file.getName().contains(".java")) {
                String fileSize = FilesUtils.byteCountToDisplaySize(file.length());
                LogicModel model = new LogicModel(file.getName(), file.getAbsolutePath(),fileSize);
                list.add(model);
            }
        }

        return list;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_logic:
                createNewLogicFile();
                break;
            default:
                break;
        }
    }

    private void createNewLogicFile() {
        File javaFile = new File(MainActivity.getROOTDIR().concat("/app/src/main/java/").concat(MainActivity.getAppPackageName().replace(".","/")));
        Wood.JAVA("Java Location: "+ javaFile);
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_create_new_class);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        AppCompatEditText className = dialog.findViewById(R.id.class_name);
        AppCompatEditText classExtends = dialog.findViewById(R.id.class_extends);
        AppCompatButton saveButton = dialog.findViewById(R.id.create_new_class_ok_button);
        AppCompatButton cancelButton = dialog.findViewById(R.id.create_new_class_cancel_button);

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strClassName = className.getText().toString().trim();
                if (strClassName.isEmpty()) Utils.showMessage(getActivity(),"Empty Class Name");
                else createTheClass(strClassName,classExtends.getText().toString().trim());
                Utils.showMessage(getActivity(),"Created Successfully:");
                dialog.dismiss();

            }
        });

        dialog.show();

    }
    private void createTheClass(String className, String classExtends){
        AssetManager assets = getActivity().getAssets();
        File packageFile = new File(MainActivity.getROOTDIR().concat("/app/src/main/java/").concat(MainActivity.getAppPackageName().replace(".","/")),className+".java");
        Wood.JAVA("Java packageFile: "+ packageFile.getAbsolutePath());
        if (packageFile.exists())
            Utils.showMessage(getActivity(),"Java File Exists");
        else {
            if (!classExtends.isEmpty() && isAvailable(classExtends)) {
                String javaName = "precodes/java/" + classExtends.concat(".java");
                try {
                    String content = IOUtils.toString(assets.open(javaName));
                    content = content.replace("{PACKAGE}", MainActivity.getAppPackageName())
                            .replace("{CLASS_NAME}", className);
                    saveFile(packageFile, content);
                    adapter.addNewItem(getViews());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                String javaName = "precodes/java/EmptyClass.java";

                try {
                    String content = IOUtils.toString(assets.open(javaName));
                    content = content.replace("{PACKAGE}", MainActivity.getAppPackageName())
                            .replace("{CLASS_NAME}", className);
                    saveFile(packageFile, content);
                    adapter.addNewItem(getViews());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private boolean isAvailable(String superClass){
        AssetManager assets = getActivity().getAssets();
        try {
           String[] ass = assets.list("precodes/java");
           for (String s: ass){
               return s.equals(superClass);
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
