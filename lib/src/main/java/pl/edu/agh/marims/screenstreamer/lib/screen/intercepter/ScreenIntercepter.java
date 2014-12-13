package pl.edu.agh.marims.screenstreamer.lib.screen.intercepter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Map;

import pl.edu.agh.marims.screenstreamer.lib.intent.IntentReader;
import pl.edu.agh.marims.screenstreamer.lib.measurement.Measurer;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.AbstractSender;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.UdpSocketSender;

public class ScreenIntercepter implements Intercepter {
    private static final int UDP_PORT = 6666;
    private static final int MEASURE_INTERVAL = 5000;
    private static final String SESSION_ID_KEY = "sessionId";
    private String sessionId = null;
    private Activity activity;
    private View rootView;
    private Map<String, String> intentParams;
    private boolean initialized = false;
    private AbstractSender sender;
    private Measurer measurer;
    private Handler handler = new Handler();

    public ScreenIntercepter(final Activity activity, final View view, String serverUrl) {
        this.activity = activity;
        this.rootView = view;
        this.intentParams = IntentReader.readIntentParams(activity.getIntent());

        if (!intentParams.isEmpty()) {
            this.sessionId = intentParams.get(SESSION_ID_KEY);
            if (sessionId != null) {
                //        sender = new AsyncTaskSender(this, serverUrl + UPLOAD_ENDPOINT);
                sender = new UdpSocketSender(this, serverUrl, UDP_PORT, this.sessionId);
                measurer = new Measurer(sender);
            }
        }
    }

    public void initialize() {
        try {
            if (sessionId != null) {
                rootView.setDrawingCacheEnabled(true);
                initialized = true;
            } else {
                throw new Exception("Unknown session ID, start Activity with an Intent from QR scan");
            }
        } catch (Exception e) {
            Toast.makeText(activity, "Wasn't able to initialize", Toast.LENGTH_SHORT).show();
            initialized = false;
            e.printStackTrace();
        }
    }

    @Override
    public Bitmap takeScreenshot() {
        if (!initialized) {
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
        if (initialized && sender != null) {
            initialized = false;
            sender.stopSending();
        }
    }

}
