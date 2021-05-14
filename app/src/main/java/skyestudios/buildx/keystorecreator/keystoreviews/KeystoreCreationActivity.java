package skyestudios.buildx.keystorecreator.keystoreviews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import java.io.File;

import skyestudios.buildx.R;
import skyestudios.buildx.builderx.Logger;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.keystorecreator.CertCreator;
import skyestudios.buildx.keystorecreator.DistinguishedNameValues;
import skyestudios.buildx.keystorecreator.KeyParameters;
import skyestudios.buildx.keystorecreator.KeyStoreFileManager;
import skyestudios.buildx.keystorecreator.PasswordObfuscator;

public class KeystoreCreationActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText mPasswordKeystore;
    private AppCompatEditText mKeystorePasswordConfirm;
    private AppCompatEditText mAliasKey;
    private AppCompatEditText mPasswordKey;
    private AppCompatEditText mKeyPasswordConfirm;
    private EditText mCertValidityYears;
    private AppCompatEditText mFullNameCert;
    private AppCompatEditText mOrgUnitCert;
    private AppCompatEditText mOrgCert;
    private AppCompatEditText mCityCert;
    private AppCompatEditText mStateCert;
    private AppCompatEditText mCountryCodeCert;
    private AppCompatButton mKeyBtnCretae;
    private Params params;
    private ProgressDialog creatingKeyProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keystore);
        initView();

    }

    private void initView() {
        mPasswordKeystore = (AppCompatEditText) findViewById(R.id.keystore_password);
        mKeystorePasswordConfirm = (AppCompatEditText) findViewById(R.id.confirm_keystore_password);
        mAliasKey = (AppCompatEditText) findViewById(R.id.key_alias);
        mPasswordKey = (AppCompatEditText) findViewById(R.id.key_password);
        mKeyPasswordConfirm = (AppCompatEditText) findViewById(R.id.confirm_key_password);
        mCertValidityYears = (EditText) findViewById(R.id.CertValidityYears);
        mFullNameCert = (AppCompatEditText) findViewById(R.id.cert_full_name);
        mOrgUnitCert = (AppCompatEditText) findViewById(R.id.cert_org_unit);
        mOrgCert = (AppCompatEditText) findViewById(R.id.cert_org);
        mCityCert = (AppCompatEditText) findViewById(R.id.cert_city);
        mStateCert = (AppCompatEditText) findViewById(R.id.cert_state);
        mCountryCodeCert = (AppCompatEditText) findViewById(R.id.cert_country_code);
        mKeyBtnCretae = (AppCompatButton) findViewById(R.id.cretae_key_btn);
        mKeyBtnCretae.setOnClickListener(this);
    }

    private void checkInputs(){
        Log.d("Keystore", "Checking Inputs");

        File keystoreDir = new File(Utils.getKeystoresDir().getAbsolutePath().concat("/"));

        if (mPasswordKeystore.getText().toString().length() == 0) {
            Utils.alertDialog(this, R.string.PasswordRequired, R.string.KeystorePasswordRequired, R.string.OkButtonLabel);
            return;
        }
        if (!mPasswordKeystore.getText().toString().equals( mKeystorePasswordConfirm.getText().toString())) {
            Utils.alertDialog(this, R.string.PasswordsDontMatch, 0, R.string.OkButtonLabel);
            return;
        }
        if (mPasswordKey.getText().length() == 0) {
            Utils.alertDialog(this, R.string.PasswordRequired, R.string.KeystorePasswordRequired, R.string.OkButtonLabel);
            return;
        }
        if (!mPasswordKey.getText().toString().equals( mKeyPasswordConfirm.getText().toString())) {
            Utils.alertDialog(this, R.string.PasswordsDontMatch, 0, R.string.OkButtonLabel);
            return;
        }
        if (mAliasKey.getText().length() == 0) {
            Utils.alertDialog(this, R.string.alias, R.string.KeystorePasswordRequired, R.string.OkButtonLabel);
            return;
        }
        File keystoreFile = new File(keystoreDir.getAbsolutePath().concat("/").concat(mAliasKey.getText().toString().trim().concat(".jks")));
        Log.d("Keystore", " File: " + keystoreFile.getAbsolutePath());

        final String password = PasswordObfuscator.getInstance().encodeKeystorePassword(keystoreFile.getAbsolutePath(), mPasswordKeystore.getText().toString());
        if (!keystoreFile.getParentFile().canWrite()) {
            return;
        }
        Log.d("Keystore", "Created Key file");

        if (keystoreFile.exists()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            String message = String.format(getResources().getString(R.string.OverwriteKeystoreFile), keystoreFile.getAbsolutePath());
            alertDialogBuilder.setMessage(message).setTitle(R.string.OverwriteKeystoreTitle);
            alertDialogBuilder.setPositiveButton(R.string.OkButtonLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    launchCreateKeyFormActivity(keystoreFile,  password);
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.CancelButtonLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //FileUtil.createNewFile(keystoreFile.getAbsolutePath());
            launchCreateKeyFormActivity(keystoreFile,password);
        }

    }

    private void launchCreateKeyFormActivity(File ksFile, String password) {
        Log.d("Keystore", " LaunchCreate()");

        Log.d("Keystore", "Cert creation");

        params = new Params();
        String validityYears = mCertValidityYears.getText().toString();
        params.certValidityYears = 30;
        try {
            params.certValidityYears = Integer.parseInt(validityYears);
        } catch (Exception x) {
            Utils.alertDialog(this, R.string.InvalidValidity, R.string.InvalidValidityMessage, R.string.OkButtonLabel);
            return;
        }

        if (params.certValidityYears <= 0) {
            Utils.alertDialog(this, R.string.InvalidValidity, R.string.InvalidValidityMessage, R.string.OkButtonLabel);
            return;
        }
        params.certSignatureAlgorithm =  "SHA1withRSA";

        params.distinguishedNameValues.setCountry(mCountryCodeCert.getText().toString());
        params.distinguishedNameValues.setState(mStateCert.getText().toString());
        params.distinguishedNameValues.setLocality(mCityCert.getText().toString());
        params.distinguishedNameValues.setOrganization(mOrgCert.getText().toString());
        params.distinguishedNameValues.setOrganizationalUnit(mOrgUnitCert.getText().toString());
        params.distinguishedNameValues.setCommonName(mFullNameCert.getText().toString());

        params.storePath = ksFile.getAbsolutePath();
        params.storePass = mPasswordKeystore.getText().toString();
        params.keyAlgorithm = "RSA";
        params.keySize = 2048;
        params.keyName = ksFile.getName();
        params.keyPass  = mPasswordKey.getText().toString();

        if (params.distinguishedNameValues.size() == 0) {
            Utils.alertDialog(this ,R.string.MissingCertInfoTitle, R.string.MissingCertInfoMessage, R.string.OkButtonLabel);
            return;
        }
        if (params.certValidityYears < 25) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.ShortCertValidityMessage).setTitle(R.string.ShortCertValidityTitle);
            alertDialogBuilder.setPositiveButton(R.string.ContinueAnywayButtonLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    doCreateKeystoreAndKey();
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.CancelButtonLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }
        Log.d("Keystore", "doCreateKeystoreAndKey();");

        doCreateKeystoreAndKey();

    }
    private void doCreateKeystoreAndKey() {

        creatingKeyProgressDialog = new ProgressDialog(this);
        creatingKeyProgressDialog.setMessage(getResources().getString(R.string.CreatingKeyProgressMessage));
        creatingKeyProgressDialog.show();

        Log.d("Keystore", "Creating Key");
        KeyTask task = new KeyTask(this,params,creatingKeyProgressDialog);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private void sendMessage(boolean success, String msg){
        if (success){
            new Handler(Looper.getMainLooper()).post(() -> Utils.alertDialog(KeystoreCreationActivity.this,"Done", msg));

        }else {
            new Handler(Looper.getMainLooper()).post(() -> Utils.alertDialog(KeystoreCreationActivity.this,"Error Occured", msg));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cretae_key_btn:
                checkInputs();
                break;
            default:
                break;
        }
    }


}
