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

public class ProjectLibsDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView mCountLibs;
    private RecyclerView mListLibs;
    private FloatingActionButton mAddNewLib, searchNewLib;
    private LibsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_libs, container, false);
        initView(v);
        return v;
    }

    private void initView(@NonNull final View itemView) {
        List<Libs> list = new ArrayList<>();
        mCountLibs = (TextView) itemView.findViewById(R.id.libs_count);
        mListLibs = (RecyclerView) itemView.findViewById(R.id.libs_list);
        mAddNewLib = (FloatingActionButton) itemView.findViewById(R.id.addNewLib);
        searchNewLib = itemView.findViewById(R.id.searchNewLib);
        mAddNewLib.setOnClickListener(this);
        searchNewLib.setOnClickListener(this);
        mListLibs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListLibs.setHasFixedSize(true);

        StringBuilder count = new StringBuilder();
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        count.append("(");
        count.append(projectBx.split("\n").length).append(")");
        mCountLibs.setText(count.toString());
        for (String lib : projectBx.split("\n")) {
                Libs model = new Libs(lib.substring(0,lib.lastIndexOf("-")), lib.substring(lib.lastIndexOf("-") + 1));
                list.add(model);
        }
        adapter = new LibsAdapter(getActivity(), list);
        adapter.setOnclick(false);
        mListLibs.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewLib:
                openLibsDialog();
                break;
            case R.id.searchNewLib:
                startActivity(new Intent(getActivity(),SearchLibActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        List<Libs> list = new ArrayList<>();
        StringBuilder count = new StringBuilder();
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        count.append("(");
        count.append(projectBx.split("\n").length).append(")");
        mCountLibs.setText(count.toString());
        for (String lib : projectBx.split("\n")) {
            Libs model = new Libs(lib.substring(0,lib.lastIndexOf("-")), lib.substring(lib.lastIndexOf("-") + 1));
            list.add(model);
        }
        adapter.refresh(list);

    }

    private void openLibsDialog() {
        ProjectLibsDialog proLibs = new ProjectLibsDialog();
        getActivity().getSupportFragmentManager().beginTransaction();
        proLibs.show(getActivity().getSupportFragmentManager(),"LibsDialog");
    }
}
