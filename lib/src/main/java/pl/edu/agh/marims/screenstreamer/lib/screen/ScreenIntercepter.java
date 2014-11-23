package pl.edu.agh.marims.screenstreamer.lib.screen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import pl.edu.agh.marims.screenstreamer.lib.measurement.Measurer;
import pl.edu.agh.marims.screenstreamer.lib.network.AbstractSender;
import pl.edu.agh.marims.screenstreamer.lib.network.SocketIOSender;

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

//        sender = new AsyncTaskSender(this, serverUrl);
//        sender = new SocketIOSender(this, "http://marims-backend.herokuapp.com");
        sender = new SocketIOSender(this, "http://192.168.0.14/");
        //measurer = new Measurer(sender);
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
