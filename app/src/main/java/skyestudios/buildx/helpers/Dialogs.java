package skyestudios.buildx.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import skyestudios.buildx.R;

public  class Dialogs {
    private static Context context;
    public Dialogs(Context context){
        this.context = context;
    }

    public Dialogs showAboutDialog(int layout){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
        return this;
    }
}
