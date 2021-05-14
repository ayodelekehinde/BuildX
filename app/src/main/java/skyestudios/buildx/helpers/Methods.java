package skyestudios.buildx.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.HomeActivity;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.adapters.KeysAdapter;
import skyestudios.buildx.dialogs.DialogNewProject;
import skyestudios.buildx.dialogs.ProjectLibsDialogFragment;
import skyestudios.buildx.keystorecreator.keystoreviews.KeystoreCreationActivity;
import skyestudios.buildx.models.KeysModel;
import skyestudios.buildx.models.Project;

import static skyestudios.buildx.helpers.Utils.saveFile;

public class Methods {
    private static Methods instance;

    public static Methods getInstance(){
        if (instance == null)
            instance = new Methods();
        return instance;
    }
    /*
    Adds the required project.bx file to the project esp for AppCompat
    @params: Context, File: projectBx
     */

    public void addProjectBxFile(Context context, File projectBx) {
        AssetManager assets = context.getAssets();
        if (!projectBx.exists()) {
            try {
                String content = IOUtils.toString(assets.open("project.bx"));
                saveFile(projectBx, content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*
    opens the list of libs been used ny the project
    @param: MainActivity
     */
    private void openLibsDialog(MainActivity context) {
        ProjectLibsDialogFragment proLibs = new ProjectLibsDialogFragment();
        context.getSupportFragmentManager().beginTransaction();
        proLibs.show(context.getSupportFragmentManager(), "Libs");
    }

    /*
    Compiles firebase constants with the project manifest
    @params: Context, File (googleServices), File valuesXml
     */

    private void firebaseAssistance(Context context, Project project) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Firebase Assistance");
        builder.setMessage(context.getString(R.string.firebase_instructions));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        d.show();

        AssetManager assets = context.getAssets();
        File googleServices = new File(project.appDir.concat("/app/google-services.json"));
        File valuesXml = new File(project.appDir.concat("/app/src/main/res/values/values.xml"));
        if (googleServices.exists()) {
            String firebaseJson = FileUtil.readFile(googleServices.getAbsolutePath());
            try {
                JSONObject obj = new JSONObject(firebaseJson);
                String webClientId = obj.getString("client_id");
                String databaseUrl = obj.getString("firebase_url");
                String gcmSenderId = obj.getString("project_number");
                String apiKey = obj.getString("current_key");
                String appId = obj.getString("mobilesdk_app_id");
                String storageBucket = obj.getString("storage_bucket");
                String projectId = obj.getString("project_id");

                String javaName = "values.xml";

                String content = IOUtils.toString(assets.open(javaName));
                content = content.replace("{WEB_CLIENT}", webClientId)
                        .replace("{DATABASE_URL}", databaseUrl)
                        .replace("{GCM}", gcmSenderId)
                        .replace("{API_KEY}", apiKey)
                        .replace("{APP_ID}", appId)
                        .replace("{BUCKET}", storageBucket)
                        .replace("{PROJECT_ID}", projectId);
                saveFile(valuesXml, content);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
    This opens the Keystore for signing the project
    @params: Context
     */
    private void openKeystoreOptions(Context context) {
        ArrayList<KeysModel> keysModels = new ArrayList<>();
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_keystores);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        RecyclerView keyLists = dialog.findViewById(R.id.keystore_list);
        TextView empty = dialog.findViewById(R.id.message);
        Button createBtn = dialog.findViewById(R.id.createNew);
        keyLists.setLayoutManager(new LinearLayoutManager(context));
        keyLists.setHasFixedSize(true);

        File[] keystoreDir = Utils.getKeystoresDir().listFiles(file -> file.getName().endsWith(".jks"));
        for (File file : keystoreDir) {
            String fileSize = FilesUtils.byteCountToDisplaySize(file.length());
            KeysModel model = new KeysModel(file.getName(), file.getAbsolutePath(), fileSize);
            keysModels.add(model);
        }
        KeysAdapter adapter = new KeysAdapter(context, keysModels);
        keyLists.setAdapter(adapter);
        if (adapter.getItemCount() == 0) {
            empty.setVisibility(View.VISIBLE);
        } else
            empty.setVisibility(View.GONE);

        createBtn.setOnClickListener(view -> {
            context.startActivity(new Intent(context, KeystoreCreationActivity.class));
            dialog.dismiss();
        });
        dialog.show();
    }
    /*
    This opens the settings dialog to set the minSdkVersion, targetSdkVersion, versionCode, versionName;

    @params: Context, File
     */


    private void openProjectSettings(Context context, Project project) {
        String minSdkVersion = "";
        String targetSdkVersion = "";
        String verCode = "";
        String verName = "";
        File file = new File(project.appDir.concat("/app/build.gradle"));
        String gradle = FileUtil.readFile(file.getAbsolutePath());
        String[] lines = gradle.split("\n");
        for (String s : lines) {
            String line = s.split(" ")[0].trim();
            if (line.split(" ")[0].equalsIgnoreCase("minsdkversion")) {
                minSdkVersion = s.split(" ")[1];
            } else if (line.split(" ")[0].equalsIgnoreCase("targetsdkversion")) {
                targetSdkVersion = s.split(" ")[1];
            } else if (line.split(" ")[0].equalsIgnoreCase("versioncode")) {
                verCode = s.split(" ")[1];
            } else if (line.split(" ")[0].equalsIgnoreCase("versionName")) {
                verName = s.split(" ")[1].replace("\"", "");
            }
        }

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_project_options);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        //TODO Auto set CompileSDK
        AppCompatEditText minSdk = dialog.findViewById(R.id.min_sdk_version);
        AppCompatEditText targetSdk = dialog.findViewById(R.id.target_sdk_version);
        AppCompatEditText versionName = dialog.findViewById(R.id.version_name);
        AppCompatEditText versionCode = dialog.findViewById(R.id.version_code);
        minSdk.setText(minSdkVersion);
        targetSdk.setText(targetSdkVersion);
        versionName.setText(verName);
        versionCode.setText(verCode);
        AppCompatButton saveButton = dialog.findViewById(R.id.project_settings_save_button);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder grad = new StringBuilder();

                for (String s : lines) {
                    String line = s.split(" ")[0].trim();
                    if (line.split(" ")[0].equalsIgnoreCase("minsdkversion")) {
                        String min = s.split(" ")[1];
                        grad.append(s.replace(min, minSdk.getText().toString().trim()));
                        grad.append("\n");
                    } else if (line.split(" ")[0].equalsIgnoreCase("targetsdkversion")) {
                        String target = s.split(" ")[1];
                        grad.append(s.replace(target, targetSdk.getText().toString().trim()));
                        grad.append("\n");
                    } else if (line.split(" ")[0].equalsIgnoreCase("versioncode")) {
                        String verCode = s.split(" ")[1];
                        grad.append(s.replace(verCode, versionCode.getText().toString().trim()));
                        grad.append("\n");
                    } else if (line.split(" ")[0].equalsIgnoreCase("versionName")) {
                        String verName = s.split(" ")[1];
                        grad.append(s.replace(verName, "\"".concat(versionName.getText().toString().trim()).concat("\"")));
                        grad.append("\n");
                    } else {
                        grad.append(s);
                        grad.append("\n");
                    }

                }
                Wood.GRADLE("New Gradle: " + grad.toString());
                saveButton.setEnabled(false);
                FileUtil.writeFile(file.getAbsolutePath(), grad.toString());
                dialog.dismiss();
                Utils.showMessage(context, "Done");

            }
        });

        dialog.show();
    }

    /*
    Creates new project by inflating a dialog
    @params: Context
     */
    public void createNewProject(HomeActivity context){
        DialogNewProject dialogNewProject = DialogNewProject.getInstance();
        dialogNewProject.show(context.getSupportFragmentManager(),"NewAndroidProject");
    }
}
