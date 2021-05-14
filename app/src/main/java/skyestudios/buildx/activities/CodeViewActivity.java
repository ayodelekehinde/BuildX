package skyestudios.buildx.activities;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.adapters.ViewOptionAdapter;

import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.interfaces.GetViewCompletion;
import skyestudios.buildx.layoutinflator.PreviewActivity;
import skyestudios.buildx.models.OptionsView;
import skyestudios.buildx.service.ApkMakerService;


public class CodeViewActivity extends AppCompatActivity  {


    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filepath";
    public static final String isLayout = "isLayout";

    private StringBuilder loaded;
    private int CHUNK = 20000;
    private String FILE_CONTENT;
    private String currentBuffer, filename, filepath;
    private TextView mFileName;
    private ImageButton mSaveButton, mPreviewButton;
    private boolean isLay;
    private ImageButton mBackButtonCodeview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
       // initView();

    }

//    private void initView() {
//        filename = getIntent().getStringExtra(FILE_NAME);
//        filepath = getIntent().getStringExtra(FILE_PATH);
//        isLay = getIntent().getBooleanExtra(isLayout, false);
//        contentView = findViewById(R.id.fileContent);
//        mFileName = findViewById(R.id.file_name);
//        mPreviewButton = findViewById(R.id.codeview_preview_button);
//        mSaveButton = findViewById(R.id.codeview_save_button);
//        mFileName.setText(filename);
//
////        if (!isLay)
////            mPreviewButton.setVisibility(View.GONE);
//
//        LinearLayout symbolLayout = findViewById(R.id.symbolLayout);
//        View.OnClickListener symbolClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        };
//        for (int i = 0; i < symbolLayout.getChildCount(); i++) {
//            symbolLayout.getChildAt(i).setOnClickListener(symbolClickListener);
//        }
////        contentView.setOnLongClickListener(view -> {
////            showViewsOptionsDialog();
////            return true;
////        });
//
//
//        mSaveButton.setOnClickListener(view -> {
//            save();
//            Utils.showMessage(this, "Saved");
//        });
//        mPreviewButton.setOnClickListener(view -> buildProject());
//
//
//        mBackButtonCodeview = (ImageButton) findViewById(R.id.codeview_back_button);
//        mBackButtonCodeview.setOnClickListener(this);
//        loadTheDoc();
//    }
//    private void loadTheDoc(){
//        if (filename != null) {
//            contentView.setVisibility(View.GONE);
//
//            scrollView = findViewById(R.id.scrollView);
//            scrollView.setOnBottomReachedListener(null);
//            FILE_CONTENT = FileUtil.readFile(filepath);
//            loadDocument(FileUtil.readFile(filepath));
//
//        }
//    }
//
//    private void buildProject() {
//        startService(new Intent(this, ApkMakerService.class));
//    }
//
//
//    private void loadDocument(final String fileContent) {
//        scrollView.smoothScrollTo(0, 0);
//
//        contentView.setFocusable(false);
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                contentView.setFocusableInTouchMode(true);
//            }
//        });
//
//        loaded = new StringBuilder();
//        if (fileContent.length() > CHUNK)
//            loadInChunks(scrollView, fileContent);
//        else {
//            loaded.append(fileContent);
//            contentView.setTextHighlighted(loaded);
//        }
//
//
//        contentView.setVisibility(View.VISIBLE);
//        contentView.addTextChangedListener(this);
//        currentBuffer = contentView.getText().toString();
//
//    }
//
//    private void loadInChunks(InteractiveScrollView scrollView, final String bigString) {
//        loaded.append(bigString.substring(0, CHUNK));
//        contentView.setTextHighlighted(loaded);
//        scrollView.setOnBottomReachedListener(new OnBottomReachedListener() {
//            @Override
//            public void onBottomReached() {
//                if (loaded.length() >= bigString.length())
//                    return;
//                else if (loaded.length() + CHUNK > bigString.length()) {
//                    String buffer = bigString.substring(loaded.length(), bigString.length());
//                    loaded.append(buffer);
//                } else {
//                    String buffer = bigString.substring(loaded.length(), loaded.length() + CHUNK);
//                    loaded.append(buffer);
//                }
//
//                contentView.setTextHighlighted(loaded);
//            }
//        });
//    }
//
//    public void save() {
//        FileUtil.writeFile(filepath, contentView.getCleanText());
//    }
//
//
//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                FileUtil.writeFile(FILE_PATH, contentView.getText().toString());
//            }
//        }, 2000);
//    }
//
//    public boolean isChanged() {
//        if (FILE_CONTENT == null) {
//            return false;
//        }
//
//        if (FILE_CONTENT.length() >= CHUNK && FILE_CONTENT.substring(0, loaded.length()).equals(currentBuffer))
//            return false;
//        else if (FILE_CONTENT.equals(currentBuffer))
//            return false;
//
//        return true;
//    }
//
//    private void showViewsOptionsDialog() {
//        Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(1);
//        dialog.setContentView(R.layout.dialog_view_options);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        dialog.setCancelable(true);
//
//        RecyclerView mListViews = dialog.findViewById(R.id.view_options_list);
//        mListViews.setHasFixedSize(true);
//        mListViews.setItemViewCacheSize(20);
//        mListViews.setDrawingCacheEnabled(true);
//        List<OptionsView> views = new Gson().fromJson(FileUtil.readFile(new File(FileUtil.getExternalStorageDir().concat("/BuildX/views.json")).getAbsolutePath()), new TypeToken<List<OptionsView>>() {
//        }.getType());
//
//        LinearLayoutManager mManager = new LinearLayoutManager(this);
//        ViewOptionAdapter mAdapter = new ViewOptionAdapter(this, views, dialog);
//        mListViews.setLayoutManager(mManager);
//        mListViews.setAdapter(mAdapter);
//
//        mAdapter.setViewOptionsListener(new GetViewCompletion() {
//            @Override
//            public void getViewString(String path) {
//                contentView.getText().insert(contentView.getSelectionStart(), path.replace("''", "\"\""));
//            }
//        });
//
//        dialog.show();
//
//    }
//
//    private void preview() {
//        Intent i = new Intent(this, PreviewActivity.class);
//        i.putExtra(PreviewActivity.FILE_PATH, filepath);
//        startActivity(i);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        save();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        File viewsOptions = new File(FileUtil.getExternalStorageDir().concat("/BuildX/views.json"));
//        if (!viewsOptions.exists()) {
//            try {
//                Utils.unpackAsset(this, "views.json", viewsOptions);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.codeview_back_button:
//               finish();
//                break;
//            default:
//                break;
//        }
//    }
}
