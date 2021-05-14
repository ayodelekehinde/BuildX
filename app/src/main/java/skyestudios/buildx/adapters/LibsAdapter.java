package skyestudios.buildx.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.duy.dex.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.FilesUtils;
import skyestudios.buildx.helpers.IOUtils;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.keystorecreator.keystoreviews.KeyParams;
import skyestudios.buildx.keystorecreator.keystoreviews.KeystoreTask;
import skyestudios.buildx.models.KeysModel;
import skyestudios.buildx.models.Libs;

public class LibsAdapter extends RecyclerView.Adapter<LibsAdapter.KeysViewHolder> {

    List<Libs> list = new ArrayList<>();
    Context mContext;
    boolean click;


    public LibsAdapter(Context context, List<Libs> libs){
      this.mContext = context;
      this.list = libs;
    }
    public void setOnclick(boolean click){
        this.click = click;
    }

    @NonNull
    @Override
    public KeysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.lib_item, parent,false);
        return new KeysViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KeysViewHolder holder, int position) {
       final  Libs libs = list.get(position);
        holder.bindView(libs.name,libs.version);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(libs.name);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    delete(libs.name, position);
                    return true;
                }
            });

    }

    public void refresh(List<Libs> libsList){
        list.clear();
        this.list = libsList;
        notifyDataSetChanged();
    }
    private void delete(String libName, int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Remove from Project");
        builder.setMessage("Are you sure you want to remove ".concat(libName).concat(" from project?"));
        final String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder libBuilder = new StringBuilder();
                for (String lib: projectBx.split("\n")){
                    if (!lib.contains(libName)){
                        libBuilder.append(lib);
                        libBuilder.append("\n");
                    }
                }

                FileUtil.writeFile(MainActivity.getROOTDIR().concat("/app/project.bx"), libBuilder.deleteCharAt(libBuilder.lastIndexOf(Utils.NEXT_LINE)).toString());
                list.remove(pos);
                notifyItemRemoved(pos);
                Utils.showMessage(mContext,"Removed ".concat(libName).concat(" from project"));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        d.show();
    }
    private void showDialog(String name){
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        if (projectBx.contains(name)){
            Utils.showMessage(mContext,"Lib exists");
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Compile with Project");
            builder.setMessage("Are you sure you want to add ".concat(name).concat(" to project?"));
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    StringBuilder projects = new StringBuilder();
                    String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
                    projects.append(projectBx);
                    projects.append("\n");
                    projects.append(name);
                    FileUtil.writeFile(MainActivity.getROOTDIR().concat("/app/project.bx"), projects.toString());
                    Utils.showMessage(mContext, "Added ".concat(name).concat(" to project"));
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog d = builder.create();
            d.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
            d.show();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class KeysViewHolder extends RecyclerView.ViewHolder{

        TextView libname;
        TextView vername;

        public KeysViewHolder(@NonNull View itemView) {
            super(itemView);

            libname = itemView.findViewById(R.id.lib_name);
            vername = itemView.findViewById(R.id.lib_ver);
        }

        public void bindView(String name, String ver){
            libname.setText(name);
            vername.setText(ver);
        }
    }
}
