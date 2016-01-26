package pl.edu.agh.marims.screenstreamer.lib.screen.intercepter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import pl.edu.agh.marims.screenstreamer.lib.measurement.Measurer;
import pl.edu.agh.marims.screenstreamer.lib.measurement.Statistics;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.AbstractSender;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.AsyncTaskSender;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.SenderType;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.TcpSocketSender;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.UdpSocketSender;

public class ScreenIntercepter implements Intercepter<Bitmap> {
    private static final String UPLOAD_ENDPOINT = "/upload";
    private static final int UDP_PORT = 6666;
    private static final int TCP_PORT = 7777;
    private static final int MEASURE_INTERVAL = 5000;
    private StatisticsCallback statisticsCallback;
    private String sessionId = null;
    private String serverUrl = null;
    private Activity activity;
    private View rootView;
    private boolean initialized = false;
    private AbstractSender sender;
    private Measurer measurer;
    private Handler handler = new Handler();

    public ScreenIntercepter(final Activity activity, final View view, String serverUrl, String sessionId) {
        this.activity = activity;
        this.rootView = view;
        this.serverUrl = serverUrl;
        this.sessionId = sessionId;

        if (sessionId != null) {
            sender = new TcpSocketSender(this, serverUrl, TCP_PORT, this.sessionId);
            measurer = new Measurer(sender);
        }
    }

    @Override
    public void setStatisticsCallback(StatisticsCallback statisticsCallback) {
        this.statisticsCallback = statisticsCallback;
    }

    @Override
    public void setSenderType(SenderType senderType) {
        if (sender != null) {
            sender.stopSending();
            sender = null;
        }
        switch (senderType) {
            case TCP:
                sender = new TcpSocketSender(this, this.serverUrl, TCP_PORT, this.sessionId);
                break;
            case UDP:
                sender = new UdpSocketSender(this, this.serverUrl, UDP_PORT, this.sessionId);
                break;
            case HTTP:
                sender = new AsyncTaskSender(this, this.serverUrl + UPLOAD_ENDPOINT, this.sessionId);
                break;
            default:
                throw new RuntimeException("Unimplemented sender type: " + senderType.toString());
        }
        measurer = new Measurer(sender);
        if (initialized) {
            sender.startSending();
        }
    }

    @Override
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
    public Bitmap intercept() {
        if (!initialized) {
            return null;
        }
        rootView.invalidate();
        return rootView.getDrawingCache(true);
    }

    @Override
    public void start() {
        if (initialized) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (measurer != null) {
                        Statistics statistics = measurer.getStatistics();
                        Log.d("MEASURER", statistics.toString());
                        if (statisticsCallback != null) {
                            statisticsCallback.onNewStatistics(statistics);
                        }
                    }
                    if (initialized) {
                        handler.postDelayed(this, MEASURE_INTERVAL);
                    }
                }
            }, MEASURE_INTERVAL);

            sender.startSending();
        }
    }

    @Override
    public void stop() {
        if (initialized && sender != null) {
            initialized = false;
            sender.stopSending();
        }
    }

}
