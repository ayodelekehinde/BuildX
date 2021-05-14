package skyestudios.buildx.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.CodeViewActivity;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.othereditor.NewCodeEditor;
import skyestudios.buildx.models.LogicModel;

public class LogicAdapter extends RecyclerView.Adapter<LogicAdapter.ViewsViewHolder> {

    List<LogicModel> list = new ArrayList<>();
    Context mContext;


    public LogicAdapter(Context context, List<LogicModel> list){
      this.list = list;
      this.mContext = context;

    }
    public void addNewItem(List<LogicModel> list){
        this.list = list;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ViewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.logic_items, parent,false);
        return new ViewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsViewHolder holder, int position) {
        final LogicModel lm = list.get(position);
        holder.bindView(lm.getViewName(),lm.getSize());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showDialog();
                Intent i = new Intent(mContext, NewCodeEditor.class);
                i.putExtra(CodeViewActivity.FILE_NAME, lm.getViewName());
                i.putExtra(CodeViewActivity.FILE_PATH, lm.getPath());
                i.putExtra(CodeViewActivity.isLayout, false);
                mContext.startActivity(i);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                File path = new File(lm.getPath());
                showDialog(position,path);
                return true;
            }
        });

    }

    private void showDialog(int pos, File path){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                list.remove(pos);
                notifyItemRemoved(pos);
                if (path.exists())
                    path.delete();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        d.show();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewsViewHolder extends RecyclerView.ViewHolder{

        TextView viewName, fileSize;

        public ViewsViewHolder(@NonNull View itemView) {
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
