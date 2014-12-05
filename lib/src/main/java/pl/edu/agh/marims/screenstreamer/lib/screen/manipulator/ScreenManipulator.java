package pl.edu.agh.marims.screenstreamer.lib.screen.manipulator;

import android.app.Activity;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

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

    private long lastDownUptime;
    private long systemBrowserDiff;

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
                try {
                    JSONObject motionEvent = (JSONObject) data.get(0);
                    MouseEvent mouseEvent = new MouseEvent();
                    mouseEvent.event = MouseEventType.valueOf((String) motionEvent.get("event"));
                    mouseEvent.time = (Long) motionEvent.get("time");
                    mouseEvent.x = (Integer) motionEvent.get("x");
                    mouseEvent.y = (Integer) motionEvent.get("y");
                    ScreenManipulator.this.manipulate(mouseEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.receiver.startReceiving();
    }

    @Override
    public void manipulate(MouseEvent mouseEvent) {
        MotionEvent event = null;
        long uptime = 0;
        switch (mouseEvent.event) {
            case MOUSE_DOWN:
                lastDownUptime = SystemClock.uptimeMillis();
                systemBrowserDiff = lastDownUptime - mouseEvent.time;
                event = MotionEvent.obtain(lastDownUptime, lastDownUptime, MotionEvent.ACTION_DOWN, mouseEvent.x, mouseEvent.y, 0);
                break;
            case MOUSE_MOVE:
                uptime = mouseEvent.time + systemBrowserDiff;
                event = MotionEvent.obtain(lastDownUptime, uptime, MotionEvent.ACTION_MOVE, mouseEvent.x, mouseEvent.y, 0);
                break;
            case MOUSE_UP:
                uptime = mouseEvent.time + systemBrowserDiff;
                event = MotionEvent.obtain(lastDownUptime, uptime, MotionEvent.ACTION_UP, mouseEvent.x, mouseEvent.y, 0);
                break;
        }
        view.dispatchTouchEvent(event);
//        int BEGIN = 1300;
//        int END = 1000;
//        long START_TIME = SystemClock.uptimeMillis();
//        view.dispatchTouchEvent(MotionEvent.obtain(START_TIME, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 400, BEGIN, 0));
//        for (int i = BEGIN; i >= END; i--) {
//            view.dispatchTouchEvent(MotionEvent.obtain(START_TIME, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 400, i, 0));
//        }
//        view.dispatchTouchEvent(MotionEvent.obtain(START_TIME, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 400, END, 0));
    }

    public void stop() {
//        this.receiver.stopReceiving();
    }

}
