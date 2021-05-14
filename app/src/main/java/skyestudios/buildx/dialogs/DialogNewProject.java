package skyestudios.buildx.dialogs;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.base.BaseDialogFragment;
import skyestudios.buildx.databinding.DialogCreateNewProjectBinding;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.FilesUtils;
import skyestudios.buildx.helpers.IOUtils;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.models.Project;
import skyestudios.buildx.models.Projects;
import skyestudios.buildx.models.Styles;

public class DialogNewProject  extends BaseDialogFragment {

    private DialogCreateNewProjectBinding binding;
    private static final int INTENT_REQUEST_CODE = 101;
    private String appIcon;
    private Styles appStyle = null;

    public static DialogNewProject getInstance(){
        Bundle args = new Bundle();
        DialogNewProject fragment = new DialogNewProject();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void onChildCreate() {
        binding.btnCreateNewProject.setOnClickListener(view -> {
            if (checkAllInputs()){
                createNewProject();
            }
        });
        binding.createNewProjectLauncher.setOnClickListener(view -> {
            chooseAppIcon();
        });
        try {
            initSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private boolean checkAllInputs(){
        if (binding.createNewProjectAppName.getText().toString().isEmpty()){
            binding.createNewProjectAppName.setError("Pls enter app name");
            return false;
        }
        if (binding.createNewProjectPackageName.getText().toString().isEmpty() ||
                !binding.createNewProjectPackageName.getText().toString().contains(".")){
            binding.createNewProjectPackageName.setError("Pls enter appId name");
            return false;
        }
        return true;
    }

    private void createNewProject(){
        String packageName = binding.createNewProjectPackageName.getText().toString();
        String appName = binding.createNewProjectAppName.getText().toString();
        String projectPath = Utils.getProjectFolder().getAbsolutePath().concat(File.separator).concat(appName);
        String appDir = projectPath.concat(File.separator).concat("app");
        Project project = new Project();
        project.setIcon(appIcon);
        project.setPackageName(packageName);
        project.setPath(projectPath);
        project.setProjectName(appName);
        project.setAppDir(appDir);
        addToProjectList(project);
        createProject(project);
        addToProjectList(project);
    }

    /*
    Add to BuildX projects.json
     */
    private void addToProjectList(Project project){
        String projectJson = FileUtil.readFile(Utils.getProjectList().getAbsolutePath());
        if (projectJson.isEmpty()){
            List<Project> projectList = new ArrayList<>();
            Projects projects = new Projects();
            projectList.add(project);
            projects.setProjectList(projectList);
            String toProjectListJson = new Gson().toJson(projects);
            FileUtil.writeFile(Utils.getProjectList().getAbsolutePath(),toProjectListJson);
        }else {
            Projects projects = new Gson().fromJson(projectJson,Projects.class);
            projects.projectList.add(project);
            String toProjectListJson = new Gson().toJson(projects);
            FileUtil.writeFile(Utils.getProjectList().getAbsolutePath(),toProjectListJson);
        }
    }
    /*
    Create new project with files
     */
    private void createProject(Project project){
        //Creates the RES directory
        createFolders(project.appDir.concat("/src/main/res"));
        //Creates the Java directory
        String packageName = project.packageName.replace(".","/");
        createFolders(project.appDir.concat("/src/main/java/").concat(packageName));
        createFolder(project.appDir.concat("/src/main/res"),"layout");
        createFolder(project.appDir.concat("/src/main/res"),"drawable");
        createFolder(project.appDir.concat("/src/main/res"),"values");
        createFolder(project.appDir.concat("/src/main/res"),"xml");
        createFolder(project.appDir.concat("/src/main/res"),"navigation");

        String resDir = project.appDir.concat("/src/main/res/");

        try {
            //activity_main
            copyAssets(new File(project.appDir.concat("/src/main/res/layout/activity_main.xml")).getAbsolutePath(),
                    "template/app/activity_main.xml");
            //Manifest
            createAndroidManifestFile("template/app/AndroidManifest.xml",
                    project);
            copyAssets(resDir.concat("values/colors.xml"),"template/app/colors.xml");
            //MainActivity
            createMainActivityFile("template/app/MainActivity.java", project);
            //drawable
            if (appIcon.isEmpty()) {
                copyAssets(new File(resDir, "drawable/ic_launcher_background.xml").getAbsolutePath(),
                        "templates/app/ic_launcher_background.xml");
                //drawable v24
                copyAssets(new File(resDir, "drawable-v24/ic_launcher_foreground.xml").getAbsolutePath(),
                        "templates/app/ic_launcher_foreground_v24.xml");
            }else {
                FilesUtils.copyFileToDirectory(new File(appIcon), new File(resDir,"drawable"));
            }
            //Strings.xml
            createStringFile("templates/app/strings.xml",project);

            //styles.xml
            createStylesFile("templates/app/styles.xml",project);


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /*
    Create folder
     */
    private void createFolder(String path, String folder){
        File file = new File(path,folder);
        file.mkdir();
    }
    /*
   Create folders
    */
    private void createFolders(String filePath){
        File file = new File(filePath);
        file.mkdirs();
    }

    /*
    Creates Manifest file
     */
    private void createAndroidManifestFile(String filename, Project project) throws Exception{
        String content = IOUtils.toString(getActivity().getAssets().open(filename));
        content = content.replace("PACKAGE",project.packageName);
        content = content.replace("MAIN_ACTIVITY",project.packageName+".MainActivity");
        saveFile(new File(project.appDir.concat("/src/main/AndroidManifest.xml")),content);
    }
    /*
    Create mainActivity file
     */
    private void createMainActivityFile(String filename, Project project) throws Exception{
        String content = IOUtils.toString(getActivity().getAssets().open(filename));
        content = content.replace("PACKAGE",project.packageName);
        content = content.replace("ACTIVITY_NAME","MainActivity");
        saveFile(new File(project.appDir.concat("/src/main/java/").concat(project.packageName.replace(".","/")).concat("/MainActivity.java")),content);

    }
    /*
    Create string file
     */
    private void createStringFile(String filename, Project project) throws Exception{
        String content = IOUtils.toString(getActivity().getAssets().open(filename));
        content = content.replace("APP_NAME",project.projectName);
        saveFile(new File(project.appDir.concat("/src/main/res/values/strings.xml")),content);
    }
    /*
    Create styles file
     */
    private void createStylesFile(String filename, Project project) throws Exception{
        String content = IOUtils.toString(getActivity().getAssets().open(filename));
        String style = appStyle != null?appStyle.style: "Theme.MaterialComponents.NoActionBar";
        content = content.replace("APP_STYLE",style);
        saveFile(new File(project.appDir.concat("/src/main/res/values/styles.xml")),content);

    }
    /*
    Copy assets
     */
    private void copyAssets(String outFile, String assetsPath) throws Exception {
        FileOutputStream output = new FileOutputStream(outFile);
        InputStream input = getActivity().getAssets().open(assetsPath);
        IOUtils.copy(input, output);
        input.close();
        output.close();
    }
    /*
    Save files
     */
    private void saveFile(File file, String content) throws Exception {
        file.getParentFile().mkdirs();
        FileOutputStream output = new FileOutputStream(file);
        IOUtils.write(content, output);
        output.close();
    }

    /*
    Choose App icon
     */
    private void chooseAppIcon(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select Image"), INTENT_REQUEST_CODE);

        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
        }
    }
    /*
    initSipnner
     */
    private void initSpinner() throws Exception {
        AssetManager assets = getActivity().getAssets();
        String bankFile = IOUtils.toString(assets.open("styles.json"));
        List<Styles> styles = new Gson().fromJson(bankFile,new TypeToken<List<Styles>>(){}.getType());

        ArrayAdapter<Styles> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, styles);
        binding.createNewProjectStyle.setAdapter(arrayAdapter);
        binding.createNewProjectStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                appStyle = (Styles) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected View getChildView() {
        binding = DialogCreateNewProjectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.DialogAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    protected int getDialogStyle() {
        return com.google.android.material.R.style.AlertDialog_AppCompat;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == INTENT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                binding.createNewProjectLauncher.setImageBitmap(bitmap);
                File appicon = new File(imageUri.getPath());
                appIcon = appicon.getAbsolutePath();
                Wood.d(DialogNewProject.class.getSimpleName(),"AppIcon: "+appicon.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
