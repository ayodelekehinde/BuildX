package skyestudios.buildx.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kellinwood.security.zipsigner.ZipSigner;
import skyestudios.buildx.R;
import skyestudios.buildx.activities.CodeViewActivity;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.keystorecreator.CustomKeySigner;
import skyestudios.buildx.keystorecreator.KeyStoreFileManager;
import skyestudios.buildx.keystorecreator.LoadKeystoreException;
import skyestudios.buildx.keystorecreator.PasswordObfuscator;
import skyestudios.buildx.keystorecreator.keystoreviews.KeyParams;
import skyestudios.buildx.keystorecreator.keystoreviews.KeystoreTask;
import skyestudios.buildx.models.KeysModel;
import skyestudios.buildx.models.ViewsModel;

public class KeysAdapter extends RecyclerView.Adapter<KeysAdapter.KeysViewHolder> {

  List<KeysModel> list = new ArrayList<>();
  Context mContext;
  ProgressDialog progressDialog;


  public KeysAdapter(Context context, List<KeysModel> list){
    this.list = list;
    this.mContext = context;
  }




  @NonNull
  @Override
  public KeysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(mContext).inflate(R.layout.views_items, parent,false);
    return new KeysViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull KeysViewHolder holder, int position) {
    final  KeysModel vm = list.get(position);
    holder.bindView(vm.name, vm.size);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signApk(vm.path,vm.name);
      }
    });

  }
  private void signApk(String jkspath, String jksname){
    progressDialog = new ProgressDialog(mContext);
    progressDialog.setMessage("Signing Release Apk");
    Dialog dialog = new Dialog(mContext);
    dialog.setContentView(R.layout.create_keystore_form);
    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    dialog.getWindow().setAttributes(params);
    dialog.setCancelable(true);
    EditText keystorepw = dialog.findViewById(R.id.keystore_password_et);
    EditText keypw = dialog.findViewById(R.id.key_password_et);
    TextView filename = dialog.findViewById(R.id.keystore_name);
    Button okButton = dialog.findViewById(R.id.okay_btn);
    Button cancelButton = dialog.findViewById(R.id.cancel_btn);
    filename.setText(jksname);
    okButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        progressDialog.show();
        checkPassword(jkspath,jksname,keystorepw.getText().toString(),keypw.getText().toString(),dialog);
      }
    });
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.dismiss();
      }
    });


    dialog.show();



  }
  private void checkPassword(String path, String name,String keystorepw, String keypass, Dialog dialog){
    KeyParams keyParams = new KeyParams(path,name,keystorepw,keypass, MainActivity.getROOTDIR());
    KeystoreTask task = new KeystoreTask(mContext,keyParams,progressDialog,dialog);
    task.execute();
  }

  @Override
  public int getItemCount() {
    return list.size();
  }


  class KeysViewHolder extends RecyclerView.ViewHolder{

    TextView viewName, fileSize;

    public KeysViewHolder(@NonNull View itemView) {
      super(itemView);

      viewName = itemView.findViewById(R.id.file_name);
      fileSize = itemView.findViewById(R.id.file_size);

    }

    void bindView(String name, String size){
      viewName.setText(name);
      fileSize.setText(size);
    }
  }
}
