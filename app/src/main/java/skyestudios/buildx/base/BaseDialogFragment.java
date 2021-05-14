package skyestudios.buildx.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {

    private Activity mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext,getDialogStyle());
        dialog.setContentView(getChildView());
        dialog.setCancelable(canCancel());
        dialog.setCanceledOnTouchOutside(canCancel());
        setWindowAttributes(dialog.getWindow());
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onChildCreate();
    }

    protected abstract void onChildCreate();
    protected abstract View getChildView();
    protected abstract void setWindowAttributes(Window window);
    protected abstract int getDialogStyle();
    protected abstract boolean canCancel();
}
