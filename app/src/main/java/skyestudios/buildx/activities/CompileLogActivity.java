package skyestudios.buildx.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import skyestudios.buildx.R;
import skyestudios.buildx.helpers.FileUtil;

public class CompileLogActivity extends AppCompatActivity {

    public static final String LOG_TYPE = "LOG_TYPE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile_log);
        TextView log = findViewById(R.id.compile_log);
        log.setText(FileUtil.readFile(FileUtil.getExternalStorageDir().concat("/BuildX/errFile.txt")));
    }
}
