package skyestudios.buildx.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.activities.search.SearchLibActivity;
import skyestudios.buildx.adapters.LibsAdapter;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.models.Libs;

public class ProjectLibsDialog extends DialogFragment {

    private TextView mCountLibs;
    private RecyclerView mListLibs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_libs_existing, container, false);
        initView(v);
        return v;
    }

    private void initView(@NonNull final View itemView) {
        List<Libs> list = new ArrayList<>();
        mCountLibs = (TextView) itemView.findViewById(R.id.libs_count);
        mListLibs = (RecyclerView) itemView.findViewById(R.id.libs_list);

        mListLibs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListLibs.setHasFixedSize(true);

        StringBuilder count = new StringBuilder();
        count.append("(");
        File[] keystoreDir = Utils.getAppLibrary().listFiles();
        count.append(keystoreDir.length).append(")");
        mCountLibs.setText(count.toString());
        for (File file : keystoreDir) {
            if (file.getName().matches(".*\\d.*")) {
                Libs model = new Libs(file.getName(), file.getName().substring(file.getName().lastIndexOf("-") + 1));
                list.add(model);
            }else {
                Libs model = new Libs(file.getName()," ");
                list.add(model);
            }
        }
        LibsAdapter adapter = new LibsAdapter(getActivity(), list);
        adapter.setOnclick(true);
        mListLibs.setAdapter(adapter);

    }



}
