package skyestudios.buildx.keystorecreator.keystoreviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import skyestudios.buildx.keystorecreator.CertCreator;
import skyestudios.buildx.keystorecreator.PasswordObfuscator;

public class KeyTask extends AsyncTask<Void,Void, Boolean> {

    Params params;
    AppCompatActivity context;
    ProgressDialog dialog;
    public KeyTask(AppCompatActivity context, Params params, ProgressDialog dialog){
        this.params = params;
        this.context = context;
        this.dialog = dialog;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d("Keystore", "Now Runnable run()");

        //char[] storePass = PasswordObfuscator.getInstance().decodeKeystorePassword( params.storePath, params.storePass);
        //char[] keyPass = PasswordObfuscator.getInstance().decodeAliasPassword ( params.storePath, params.keyName, params.keyPass);
        try {
            Log.d("Keystore", "Now Creating Key");
            CertCreator.createKeystoreAndKey(params.storePath,
                    params.storePass.toCharArray(),
                    params.keyAlgorithm,
                    params.keySize,
                    params.keyName,
                    params.keyPass.toCharArray(),
                    params.certSignatureAlgorithm,
                    params.certValidityYears,
                    params.distinguishedNameValues);
            Log.d("Keystore", "Done Creating Key");
            return true;


        } catch (Exception x) {
            x.printStackTrace();
            return false;
            //sendMessage(false, x.getMessage());
        } finally {
            //PasswordObfuscator.flush(keyPass);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean){
            Toast.makeText(context,"Created Successfully", Toast.LENGTH_SHORT).show();
            context.finish();
            dialog.dismiss();
        }else {
            dialog.dismiss();
            Toast.makeText(context,"Error Occured", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
