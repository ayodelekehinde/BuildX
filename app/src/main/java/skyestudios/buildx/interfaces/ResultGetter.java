package skyestudios.buildx.interfaces;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ResultGetter extends ResultReceiver {
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    private Collector collector;
    public ResultGetter(Handler handler) {
        super(handler);
    }

    public void setCollector(Collector collector){
        this.collector = collector;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (collector != null){
            collector.onCollect(resultCode);
        }
    }

    public interface Collector{
        void onCollect(int result);
    }
}
