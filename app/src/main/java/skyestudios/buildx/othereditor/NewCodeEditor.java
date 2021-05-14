package skyestudios.buildx.othereditor;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import skyestudios.buildx.R;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.layoutinflator.PreviewActivity;
import skyestudios.buildx.othereditor.android.RoseEditor;
import skyestudios.buildx.othereditor.langs.java.JavaLanguage;
import skyestudios.buildx.service.ApkMakerService;

public class NewCodeEditor extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private RoseEditor mEditor;
    private ImageButton mBackButtonCodeview;
    private TextView mNameFile;

    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filepath";
    public static final String isLayout = "isLayout";

    private String filename, filepath;
    private boolean isLay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_code_editor);
        initView();

    }

    private void initView() {
        filename = getIntent().getStringExtra(FILE_NAME);
        filepath = getIntent().getStringExtra(FILE_PATH);
        isLay = getIntent().getBooleanExtra(isLayout, false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mEditor = findViewById(R.id.mTextEditor);
        mBackButtonCodeview = (ImageButton) findViewById(R.id.codeview_back_button);
        mBackButtonCodeview.setOnClickListener(this);
        mNameFile = (TextView) findViewById(R.id.file_name);

        LinearLayout symbolLayout = findViewById(R.id.symbolLayout);
        View.OnClickListener symbolClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mEditor.getText().insert(mEditor.getCursor().getLeftLine(),mEditor.getCursor().getLeftColumn(),((TextView)view).getText().toString());
            }
        };
        for (int i = 0; i < symbolLayout.getChildCount(); i++) {
            symbolLayout.getChildAt(i).setOnClickListener(symbolClickListener);
        }
        mNameFile.setText(filename);


        mEditor.setEditorLanguage(JavaLanguage.getInstance());
        mEditor.setAutoIndent(true);
        mEditor.hideAutoCompletePanel();
        mEditor.setTabWidth(5);
        mEditor.hideSoftInput();
        mEditor.setText(FileUtil.readFile(filepath));

        //mEditor.requestFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.code_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_undo:
                mEditor.undo();
                break;
            case R.id.action_preview:
                preview();
                break;
            case R.id.action_redo:
                mEditor.redo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void preview() {
        Intent in = new Intent(this, PreviewActivity.class);
        in.putExtra(PreviewActivity.FILE_PATH,filepath);
        startActivity(in);
    }

    public void save() {
        FileUtil.writeFile(filepath, mEditor.getText().toString());
    }


    @Override
    protected void onStop() {
        save();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.codeview_back_button:
                finish();
                break;
            default:
                break;
        }
    }
}
