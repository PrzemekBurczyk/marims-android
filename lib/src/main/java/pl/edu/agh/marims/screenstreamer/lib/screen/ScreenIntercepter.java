package pl.edu.agh.marims.screenstreamer.lib.screen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import pl.edu.agh.marims.screenstreamer.lib.measurement.Measurer;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.AbstractSender;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.AsyncTaskSender;

public class ScreenIntercepter implements Intercepter {

    private static final int MEASURE_INTERVAL = 5000;
    private Activity activity;
    private View rootView;
    private boolean initialized = false;
    private AbstractSender sender;
    private Measurer measurer;
    private Handler handler = new Handler();

    public ScreenIntercepter(final Activity activity, final View view, String serverUrl) {
        this.activity = activity;
        this.rootView = view;

        sender = new AsyncTaskSender(this, serverUrl);
        measurer = new Measurer(sender);
    }

    public void initialize() {
        try {
            rootView.setDrawingCacheEnabled(true);
            initialized = true;
        } catch (NullPointerException e) {
            Toast.makeText(activity, "Wasn't able to initialize", Toast.LENGTH_SHORT).show();
            initialized = false;
            e.printStackTrace();
        }
    }

    @Override
    public Bitmap takeScreenshot() {
        if (!initialized) {
            Toast.makeText(activity, "Initialize first", Toast.LENGTH_SHORT).show();
            return null;
        }
        rootView.invalidate();
        return rootView.getDrawingCache(true);
    }

    @Override
    public void intercept() {
        if (initialized) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (measurer != null) {
                        Log.d("MEASURER", measurer.getStatistics().toString());
                    }
                    if (initialized) {
                        handler.postDelayed(this, MEASURE_INTERVAL);
                    }
                }
            }, MEASURE_INTERVAL);

            sender.startSending();
        }
    }

    public void stop() {
        initialized = false;
        sender.stopSending();
    }

}
