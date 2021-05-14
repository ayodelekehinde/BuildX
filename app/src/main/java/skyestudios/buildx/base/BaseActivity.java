package skyestudios.buildx.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;

import skyestudios.buildx.databinding.DialodWaitingBinding;

public abstract class BaseActivity extends AppCompatActivity {
    private Dialog dialog;
    protected void iniLoading(){
        dialog = new Dialog(this);
        dialog.getContext().setTheme(com.google.android.material.R.style.AlertDialog_AppCompat);
        DialodWaitingBinding binding = DialodWaitingBinding.inflate(dialog.getLayoutInflater());
        binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(binding.getRoot());
    }

    protected void showLoading(){
        dialog.show();
    }
    protected void hideLoading(){
        if (dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onStop() {
        hideLoading();
        super.onStop();
    }
}
