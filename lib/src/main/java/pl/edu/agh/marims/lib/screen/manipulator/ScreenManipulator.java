package pl.edu.agh.marims.lib.screen.manipulator;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import pl.edu.agh.marims.lib.network.receiver.AbstractReceiver;
import pl.edu.agh.marims.lib.network.receiver.ReceiverCallback;
import pl.edu.agh.marims.lib.network.receiver.SocketIOReceiver;
import pl.edu.agh.marims.lib.screen.manipulator.events.KeyboardEvent;
import pl.edu.agh.marims.lib.screen.manipulator.events.MouseEvent;
import pl.edu.agh.marims.lib.screen.manipulator.events.SpecialKeyEvent;

public class ScreenManipulator implements Manipulator {

    final Instrumentation instrumentation = new Instrumentation();
    private final Activity activity;
    private final View view;
    private final String serverUrl;
    private String sessionId;
    private AbstractReceiver receiver;
    private long lastDownUptime;
    private long systemBrowserDiff;

    public ScreenManipulator(final Activity activity, final View view, String serverUrl, String sessionId) {
        this.activity = activity;
        this.view = view;
        this.serverUrl = serverUrl;
        this.sessionId = sessionId;
    }

    public void initialize() {
        if (sessionId != null) {
            this.receiver = new SocketIOReceiver(serverUrl, sessionId, new ReceiverCallback() {
                @Override
                public void onMouseEvent(final MouseEvent event) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScreenManipulator.this.manipulate(event);
                        }
                    });
                }

                @Override
                public void onKeyboardEvent(final KeyboardEvent event) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScreenManipulator.this.manipulate(event);
                        }
                    });
                }

                @Override
                public void onSpecialKeyEvent(final SpecialKeyEvent event) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScreenManipulator.this.manipulate(event);
                        }
                    });
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
    public void manipulate(final KeyboardEvent keyboardEvent) {
        if (keyboardEvent != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    instrumentation.sendStringSync(keyboardEvent.text);
                }
            }).start();
//            view.dispatchKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), keyboardEvent.text, 0, 0));
        }
    }

    @Override
    public void manipulate(SpecialKeyEvent specialKeyEvent) {
        if (specialKeyEvent != null) {
            switch (specialKeyEvent) {
                case DELETE:
//                    view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
//                    view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            instrumentation.sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                            instrumentation.sendKeySync(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                        }
                    }).start();
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
