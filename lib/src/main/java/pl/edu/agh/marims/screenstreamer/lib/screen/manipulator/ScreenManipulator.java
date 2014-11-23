package pl.edu.agh.marims.screenstreamer.lib.screen.manipulator;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;

import java.util.Map;

import pl.edu.agh.marims.screenstreamer.lib.network.receiver.AbstractReceiver;
import pl.edu.agh.marims.screenstreamer.lib.network.receiver.ReceiverCallback;
import pl.edu.agh.marims.screenstreamer.lib.network.receiver.SocketIOReceiver;

public class ScreenManipulator implements Manipulator {

    private final Activity activity;
    private final View view;
    private final String serverUrl;
    private final Map<String, String> intentParams;
    private AbstractReceiver receiver;

    public ScreenManipulator(final Activity activity, final View view, String serverUrl, Map<String, String> intentParams) {
        this.activity = activity;
        this.view = view;
        this.serverUrl = serverUrl;
        this.intentParams = intentParams;
    }

    public void initialize() {
        this.receiver = new SocketIOReceiver(serverUrl, new ReceiverCallback() {
            @Override
            public void onReceive(String event, JSONArray data) {
                Log.d("MANIPULATOR", "Event: " + event + " Data: " + data);
//                MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
                ScreenManipulator.this.manipulate(null);
            }
        });
        this.receiver.startReceiving();
    }

    @Override
    public void manipulate(MotionEvent motionEvent) {

    }

    public void stop() {
        this.receiver.stopReceiving();
    }

}
