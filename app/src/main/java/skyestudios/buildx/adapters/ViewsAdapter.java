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
import skyestudios.buildx.models.ViewHeader;
import skyestudios.buildx.models.ViewsItem;
import skyestudios.buildx.models.ViewsModel;

public class ViewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ViewsModel> list = new ArrayList<>();
    Context mContext;
    private static int VIEW_HEADER = 0;
    private static int VIEW_ITEM = 1;


    public ViewsAdapter(Context context, List<ViewsModel> list){
      this.list = list;
      this.mContext = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater layoutInflater = LayoutInflater.from(mContext);
       if (viewType == VIEW_HEADER){
           return new ViewsHeaderViewHolder(layoutInflater.inflate(R.layout.view_header, parent,false));
       }else {
           return new ViewsViewHolder(layoutInflater.inflate(R.layout.views_items, parent,false));
       }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return VIEW_HEADER;
        return VIEW_ITEM;
    }
    private boolean isPositionHeader(int pos){
        return list.get(pos) instanceof ViewHeader;
    }

    public void addNewItem(List<ViewsModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewsHeaderViewHolder){
            ViewHeader header = (ViewHeader) list.get(position);
            ViewsHeaderViewHolder headerViewHolder = (ViewsHeaderViewHolder) holder;
            headerViewHolder.folderName.setText(header.getHeader());

        }else if (holder instanceof ViewsViewHolder){
            ViewsItem viewsItem = (ViewsItem) list.get(position);
            ViewsViewHolder viewsViewHolder = (ViewsViewHolder) holder;
            if (position == 0)viewsViewHolder.topV.setVisibility(View.VISIBLE);
            else viewsViewHolder.topV.setVisibility(View.GONE);

            if (position == getItemCount() -1)viewsViewHolder.bV.setVisibility(View.VISIBLE);
            else viewsViewHolder.bV.setVisibility(View.GONE);
            viewsViewHolder.viewName.setText(viewsItem.getViewName());
            viewsViewHolder.fileSize.setText(viewsItem.getSize());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.showDialog();
                    Intent i = new Intent(mContext, NewCodeEditor.class);
                    i.putExtra(CodeViewActivity.FILE_NAME, viewsItem.getViewName());
                    i.putExtra(CodeViewActivity.FILE_PATH, viewsItem.getPath());
                    i.putExtra(CodeViewActivity.isLayout, true);
                    mContext.startActivity(i);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    File path = new File(viewsItem.getPath());
                    showDialog(position,path);
                    return true;
                }
            });
        }


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
        View topV, bV;

        public ViewsViewHolder(@NonNull View itemView) {
            super(itemView);

            viewName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            topV = itemView.findViewById(R.id.top_view);
            bV = itemView.findViewById(R.id.bottom_view);
        }

    }

    class ViewsHeaderViewHolder extends RecyclerView.ViewHolder{

        TextView folderName;

        public ViewsHeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            folderName = itemView.findViewById(R.id.folder_name);

        }

    }
}
