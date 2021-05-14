package skyestudios.buildx.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import skyestudios.buildx.R;
import skyestudios.buildx.adapters.KeysAdapter;
import skyestudios.buildx.dialogs.ProjectLibsDialogFragment;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.FilesUtils;
import skyestudios.buildx.helpers.IOUtils;
import skyestudios.buildx.helpers.MainAdapter;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.interfaces.ResultGetter;
import skyestudios.buildx.keystorecreator.keystoreviews.KeystoreCreationActivity;
import skyestudios.buildx.models.KeysModel;
import skyestudios.buildx.service.ApkMakerService;
import skyestudios.buildx.service.ServiceDone;

import static skyestudios.buildx.helpers.Utils.saveFile;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ResultGetter.Collector {

    private TabLayout mTabMain;
    private ViewPager mViewpagerMain;
    private MainAdapter adapter;

    public static final String PROJECT = "project";

    private ImageButton mBuildBtn;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarMain;
    private static ProgressDialog pg;
    private ProgressBar mProgressBuild;
    public ResultGetter resultGetter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setUpViewPager();

        //addProjectBxFile();
    }



    private void initView() {
        resultGetter = new ResultGetter(new Handler());
        resultGetter.setCollector(this);
        pg = new ProgressDialog(this);
        mTabMain = findViewById(R.id.main_tab);
        mViewpagerMain = findViewById(R.id.main_viewpager);
        adapter = new MainAdapter(getSupportFragmentManager());
        mBuildBtn = findViewById(R.id.btn_build);
        AdView mAdview = findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        mAdview.loadAd(request);
        mBuildBtn.setOnClickListener(this);
        mToolbar = findViewById(R.id.toolbar);
        mAppBarMain = findViewById(R.id.main_appBar);
        setSupportActionBar(mToolbar);
        mToolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        mProgressBuild = (ProgressBar) findViewById(R.id.build_progress);
    }


    private void setUpViewPager() {
        mTabMain.setupWithViewPager(mViewpagerMain);
        mViewpagerMain.setAdapter(adapter);
        mTabMain.getTabAt(0).setText(R.string.fragment_view);
        mTabMain.getTabAt(1).setText(R.string.fragment_logic);

        mViewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void buildProject() {
           mProgressBuild.setVisibility(View.VISIBLE);
           mBuildBtn.setVisibility(View.GONE);
           Intent intent = new Intent(this, ApkMakerService.class);
           intent.putExtra("collector",resultGetter);
           startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultGetter.setCollector(this);
    }

    @Override
    protected void onPause() {
        resultGetter.setCollector(null);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_project_settings:
                //openProjectSettings();
                break;
            case R.id.menu_project_release:
                //openKeystoreOptions();
                break;
            case R.id.menu_project_libraries:
                openLibsDialog();
            case R.id.menu_migrate_androidx:
               // migrateAndroidX();
                break;
            case R.id.sub_menu_firebase:
                //firebaseAssistance();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openLibsDialog() {
        ProjectLibsDialogFragment proLibs = new ProjectLibsDialogFragment();
        this.getSupportFragmentManager().beginTransaction();
        proLibs.show(this.getSupportFragmentManager(), "Libs");
    }









    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_build:
                buildProject();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCollect(int result) {
        mProgressBuild.setVisibility(View.GONE);
        mBuildBtn.setVisibility(View.VISIBLE);
    }
}
