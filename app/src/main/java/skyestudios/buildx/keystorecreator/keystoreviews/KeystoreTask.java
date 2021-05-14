package skyestudios.buildx.keystorecreator.keystoreviews;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import kellinwood.security.zipsigner.ZipSigner;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.keystorecreator.CertCreator;
import skyestudios.buildx.keystorecreator.CustomKeySigner;
import skyestudios.buildx.keystorecreator.LoadKeystoreException;

public class KeystoreTask extends AsyncTask<Void,Void, Boolean> {

    KeyParams params;
    Context context;
    ProgressDialog pDialog;
    Dialog dialog;

    public KeystoreTask(Context context, KeyParams params, ProgressDialog pDialog, Dialog dialog){
        this.params = params;
        this.context = context;
        this.pDialog = pDialog;
        this.dialog = dialog;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        File jksfile = new File(params.path);

        boolean result = false;
        try {
            ZipSigner zipSigner = new ZipSigner();
            File releaseApk = new File(params.rootDir.concat("/release.apk"));
            if (releaseApk.exists()) releaseApk.delete();
            CustomKeySigner.signZip(zipSigner,
                    jksfile.getAbsolutePath(),
                    params.keystorepw.toCharArray(),
                    params.name,
                    params.keypass.toCharArray(),
                    "SHA1withRSA",
                    params.rootDir.concat("/unsigned.apk"),
                    params.rootDir.concat("/release.apk"));

            result = true;


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (LoadKeystoreException e){
            e.printStackTrace();
            result = false;

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean){
            dialog.dismiss();
            pDialog.dismiss();
            Toast.makeText(context,"Release Apk Ready", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"Incorrect Password!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
