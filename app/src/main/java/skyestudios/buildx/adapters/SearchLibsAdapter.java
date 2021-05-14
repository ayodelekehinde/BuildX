package skyestudios.buildx.adapters;

import android.content.Context;
import android.icu.text.SearchIterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.activities.search.DownloadInteractor;
import skyestudios.buildx.activities.search.SearchInteractor;
import skyestudios.buildx.helpers.Constants;
import skyestudios.buildx.interfaces.ResponseCallback;
import skyestudios.buildx.models.APIResponse;
import skyestudios.buildx.models.Libs;
import skyestudios.buildx.models.SearchLib;
import skyestudios.buildx.service.SearchTask;

public class SearchLibsAdapter extends RecyclerView.Adapter<SearchLibsAdapter.KeysViewHolder> {

    List<SearchLib> list = new ArrayList<>();
    Context mContext;
    private DownloadInteractor interactor;


    public SearchLibsAdapter(Context context){
      this.mContext = context;
      interactor = new DownloadInteractor(context);
    }
    public void getList(List<SearchLib> postList) {
        this.list.clear();
        this.list.addAll(postList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public KeysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.search_lib_item, parent,false);
        return new KeysViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KeysViewHolder holder, int position) {
       final  SearchLib libs = list.get(position);
        holder.bindView(libs.title,libs.subTitle,libs.usages,libs.img);
        String url = "https://mvnrepository.com".concat(libs.url);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactor.versionPick(url);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class KeysViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView subtitle;
        TextView usages;
        AppCompatImageView libImage;

        public KeysViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.lib_title);
            subtitle = itemView.findViewById(R.id.lib_package_name);
            usages = itemView.findViewById(R.id.lib_usages);
            libImage = itemView.findViewById(R.id.lib_image);


        }

        public void bindView(String tit, String sub, String usa, String src){
            title.setText(tit);
            subtitle.setText(sub);
            usages.setText(usa);
            Picasso.get().load(src).into(libImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    libImage.setVisibility(View.GONE);
                }
            });
        }
    }
}
