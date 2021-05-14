package skyestudios.buildx.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import skyestudios.buildx.R;

public class AboutActivity extends AppCompatActivity {
    private AppCompatTextView mVersionApp;
    private AppCompatTextView mTerms;
    private AppCompatTextView mPrivacy;
    private AppCompatTextView mWebsite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        Spanned terms = Html.fromHtml(getString(R.string.terms_condition));
        Spanned policy = Html.fromHtml(getString(R.string.privacy_policy));
        Spanned website = Html.fromHtml(getString(R.string.website));

        mVersionApp = (AppCompatTextView) findViewById(R.id.app_version);
        mTerms = (AppCompatTextView) findViewById(R.id.terms);
        mPrivacy = (AppCompatTextView) findViewById(R.id.privacy);
        mWebsite = (AppCompatTextView) findViewById(R.id.website);

        mPrivacy.setText(policy);
        mTerms.setText(terms);
        mWebsite.setText(website);
        mPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        mTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mWebsite.setMovementMethod(LinkMovementMethod.getInstance());

        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(getPackageName(),0);
            String version = info.versionName;
            mVersionApp.setText("Version "+ version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

}
