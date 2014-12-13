package pl.edu.agh.marims.screenstreamer.lib.screen.manipulator;

import android.app.Activity;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import pl.edu.agh.marims.screenstreamer.lib.intent.IntentReader;
import pl.edu.agh.marims.screenstreamer.lib.network.receiver.AbstractReceiver;
import pl.edu.agh.marims.screenstreamer.lib.network.receiver.ReceiverCallback;
import pl.edu.agh.marims.screenstreamer.lib.network.receiver.SocketIOReceiver;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.KeyboardEvent;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.MouseEvent;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.MouseEventType;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.events.SpecialKeyEvent;

public class ScreenManipulator implements Manipulator {

    private static final String MOTION_EVENT_NAME = "motionEvent";
    private static final String KEY_EVENT_NAME = "keyEvent";
    private static final String SPECIAL_KEY_EVENT_NAME = "specialKeyEvent";
    private static final String SESSION_ID_KEY = "sessionId";
    private final Activity activity;
    private final View view;
    private final String serverUrl;
    private final Map<String, String> intentParams;
    private String sessionId;
    private AbstractReceiver receiver;

    private long lastDownUptime;
    private long systemBrowserDiff;

    public ScreenManipulator(final Activity activity, final View view, String serverUrl) {
        this.activity = activity;
        this.view = view;
        this.serverUrl = serverUrl;
        this.intentParams = IntentReader.readIntentParams(activity.getIntent());

        if (!intentParams.isEmpty()) {
            this.sessionId = intentParams.get(SESSION_ID_KEY);
        }
    }

    public void initialize() {
        if (sessionId != null) {
            this.receiver = new SocketIOReceiver(serverUrl, sessionId, new ReceiverCallback() {
                @Override
                public void onReceive(String event, JSONArray data) {
                    if (event.equals(MOTION_EVENT_NAME)) {
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
                    } else if (event.equals(KEY_EVENT_NAME)) {
                        try {
                            JSONObject keyEvent = (JSONObject) data.get(0);
                            KeyboardEvent keyboardEvent = new KeyboardEvent();
                            keyboardEvent.text = (String) keyEvent.get("text");
                            ScreenManipulator.this.manipulate(keyboardEvent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (event.equals(SPECIAL_KEY_EVENT_NAME)) {
                        try {
                            JSONObject specialEvent = (JSONObject) data.get(0);
                            String specialKeyEventName = (String) specialEvent.get("name");
                            try {
                                SpecialKeyEvent specialKeyEvent = SpecialKeyEvent.valueOf(specialKeyEventName);
                                ScreenManipulator.this.manipulate(specialKeyEvent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            this.receiver.startReceiving();
        }
    }

    @Override
    public void manipulate(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            MotionEvent event = null;
            long uptime;
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
        }
    }

    @Override
    public void manipulate(KeyboardEvent keyboardEvent) {
        if (keyboardEvent != null) {
            view.dispatchKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), keyboardEvent.text, 0, 0));
        }
    }

    @Override
    public void manipulate(SpecialKeyEvent specialKeyEvent) {
        if (specialKeyEvent != null) {
            switch (specialKeyEvent) {
                case BACKSPACE:
                    view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                    break;
            }
        }
    }

    public void stop() {
        if (this.receiver != null) {
            this.receiver.stopReceiving();
        }
    }

}
