package pl.edu.agh.marims.screenstreamer.lib.screen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import pl.edu.agh.marims.screenstreamer.lib.measurement.Measurer;
import pl.edu.agh.marims.screenstreamer.lib.network.AbstractSender;
import pl.edu.agh.marims.screenstreamer.lib.network.AsyncTaskSender;

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

        sender = new AsyncTaskSender(this);
        measurer = new Measurer(sender);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MEASURER", measurer.getStatistics().toString());
                handler.postDelayed(this, MEASURE_INTERVAL);
            }
        }, MEASURE_INTERVAL);
    }

    public void initialize() {
        try {
//            rootView = activity.findViewById(android.R.id.content).getRootView();

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

    public void intercept() {
        if (initialized) {
            sender.send();
        }
    }

    public void stop() {
        initialized = false;
    }

}
