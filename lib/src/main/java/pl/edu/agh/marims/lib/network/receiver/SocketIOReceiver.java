package pl.edu.agh.marims.lib.network.receiver;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pl.edu.agh.marims.lib.screen.manipulator.events.KeyboardEvent;
import pl.edu.agh.marims.lib.screen.manipulator.events.MouseEvent;
import pl.edu.agh.marims.lib.screen.manipulator.events.MouseEventType;
import pl.edu.agh.marims.lib.screen.manipulator.events.SpecialKeyEvent;

public class SocketIOReceiver extends AbstractReceiver {

    public static final String MOTION_EVENT_NAME = "motionEvent";
    public static final String KEY_EVENT_NAME = "keyEvent";
    public static final String SPECIAL_KEY_EVENT_NAME = "specialKeyEvent";

    private static final String ANDROID_ENDPOINT = "android";
    private Socket socket;

    public SocketIOReceiver(String serverUrl, String sessionId) {
        this.serverUrl = serverUrl.replaceAll("/$", "");
        this.sessionId = sessionId;
    }

    public SocketIOReceiver(String serverUrl, String sessionId, ReceiverCallback receiverCallback) {
        this(serverUrl, sessionId);
        this.receiverCallback = receiverCallback;
    }

    @Override
    public void startReceiving() {
        try {
            socket = IO.socket(serverUrl + "/" + ANDROID_ENDPOINT + "/" + sessionId);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SOCKET_IO", "Connected.");
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SOCKET_IO", "Disconnected.");
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SOCKET_IO", "Connection error.");
                }
            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SOCKET_IO", "Connection timeout.");
                }
            }).on(MOTION_EVENT_NAME, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (receiverCallback != null) {
                        try {
                            JSONObject motionEvent = (JSONObject) args[0];
                            MouseEvent mouseEvent = new MouseEvent();
                            mouseEvent.event = MouseEventType.valueOf((String) motionEvent.get("event"));
                            mouseEvent.time = (Long) motionEvent.get("time");
                            mouseEvent.x = (Integer) motionEvent.get("x");
                            mouseEvent.y = (Integer) motionEvent.get("y");
                            receiverCallback.onMouseEvent(mouseEvent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).on(KEY_EVENT_NAME, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (receiverCallback != null) {
                        try {
                            JSONObject keyEvent = (JSONObject) args[0];
                            KeyboardEvent keyboardEvent = new KeyboardEvent();
                            keyboardEvent.text = (String) keyEvent.get("text");
                            receiverCallback.onKeyboardEvent(keyboardEvent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).on(SPECIAL_KEY_EVENT_NAME, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (receiverCallback != null) {
                        try {
                            JSONObject specialEvent = (JSONObject) args[0];
                            String specialKeyEventName = (String) specialEvent.get("name");
                            SpecialKeyEvent specialKeyEvent = SpecialKeyEvent.valueOf(specialKeyEventName);
                            receiverCallback.onSpecialKeyEvent(specialKeyEvent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopReceiving() {
        if (socket != null) {
            socket.disconnect();
        }
    }
}
