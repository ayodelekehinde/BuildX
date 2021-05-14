package skyestudios.buildx.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.CodeViewActivity;
import skyestudios.buildx.interfaces.GetViewCompletion;
import skyestudios.buildx.models.OptionsView;

public class ViewOptionAdapter extends RecyclerView.Adapter<ViewOptionAdapter.OptionsViewsViewHolder> {

    List<OptionsView> list;
    Context mContext;
    GetViewCompletion completion;
    Dialog dialog;


    public ViewOptionAdapter(Context context, List<OptionsView> list, Dialog dialog){
      this.list = list;
      this.mContext = context;
      this.dialog = dialog;

    }

    public void setViewOptionsListener(GetViewCompletion completion){
        this.completion = completion;
    }


    @NonNull
    @Override
    public OptionsViewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.views_options_item, parent,false);
        return new OptionsViewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewsViewHolder holder, int position) {
        final OptionsView optionsView = list.get(position);
        holder.bindView(optionsView.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completion.getViewString(optionsView.getImpl());
                dialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class OptionsViewsViewHolder extends RecyclerView.ViewHolder{

        TextView viewName;
        TextView viewDes;

        public OptionsViewsViewHolder(@NonNull View itemView) {
            super(itemView);

            viewName = itemView.findViewById(R.id.view_name);
            viewDes = itemView.findViewById(R.id.view_description);

        }

        private void bindView(String name){
            viewName.setText(name);
            viewDes.setText("Click to add "+ name);
        }
    }
}
