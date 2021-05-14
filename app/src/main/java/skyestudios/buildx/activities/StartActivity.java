package skyestudios.buildx.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

import skyestudios.buildx.R;
import skyestudios.buildx.helpers.CheckPermissions;
import skyestudios.buildx.helpers.Utils;

public class StartActivity extends AppCompatActivity {


    private final int requestCode = 3;

    private final String[] permissionList = {
            Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private LinearLayout mProgressLoading;
    private TextView mStatusText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        checkPermissions();


    }

    private void initView() {
        mProgressLoading = findViewById(R.id.loading_progress);
        mStatusText =  findViewById(R.id.text_status);
    }

    void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && !CheckPermissions.hasPermissions(this, permissionList)) {
            Utils.showMessage(this, "Please give the required permissions");
            ActivityCompat.requestPermissions(this, permissionList, requestCode);

        } else {
            loadSplash();
        }
    }


    private void initiate() {
        mProgressLoading.setVisibility(View.VISIBLE);
        File libFolder = Utils.getAppLibrary();

        try {
            Utils.unpackAsset(this, "androidDebug.jks", new File(Utils.getKeystoresDir(),"androidDebug.jks"));
            Utils.unpackAsset(this, "libs.zip", new File(Utils.getAppBuildDir(),"libs.zip"));
            Utils.unpackZip(Utils.getAppBuildDir().getAbsolutePath().concat("/libs.zip"), libFolder.getAbsolutePath());
            mProgressLoading.setVisibility(View.GONE);
            startActivity(new Intent(StartActivity.this, HomeActivity.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSplash() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(StartActivity.this, HomeActivity.class));
        }, 2000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3:
                if (!CheckPermissions.hasPermissions(this, permissionList)) {
                    finish();
                }else {
                    initiate();
                }
                break;
        }
    }
}
