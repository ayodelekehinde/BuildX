package skyestudios.buildx.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.databinding.ProjectItemsBinding;
import skyestudios.buildx.interfaces.IProjectAdapter;
import skyestudios.buildx.models.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    List<Project> list = new ArrayList<>();
    Context mContext;
    private OnProjectClicked onProjectClicked;
    public ProjectAdapter(Context context, List<Project> list, OnProjectClicked onProjectClicked){
        mContext = context;
        this.list = list;
        this.onProjectClicked = onProjectClicked;
    }




    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ProjectItemsBinding binding = ProjectItemsBinding.inflate(inflater,parent,false);
        return new ProjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        final Project project = list.get(position);
        holder.bindView(project);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ProjectViewHolder extends RecyclerView.ViewHolder{
        ProjectItemsBinding binding;
        public ProjectViewHolder(@NonNull ProjectItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
                onProjectClicked.projectItemClicked(list.get(getAdapterPosition()));
            });
        }

        public void bindView(Project project){
            Picasso.get().load(project.icon).placeholder(R.drawable.ic_round_android_24).into(binding.projectIcon, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    binding.projectIcon.setImageResource(R.drawable.ic_round_android_24);
                }
            });
            binding.packageName.setText(project.packageName);
            binding.projectName.setText(project.projectName);
        }
    }

    public interface OnProjectClicked{
        void projectItemClicked(Project project);
    }
}
