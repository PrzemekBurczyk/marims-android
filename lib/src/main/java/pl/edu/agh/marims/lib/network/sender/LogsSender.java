package pl.edu.agh.marims.lib.network.sender;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pl.edu.agh.marims.lib.screen.intercepter.Intercepter;

public class LogsSender extends WebsocketSender<String> {

    private static final boolean DEBUG = false;

    private static final String ANDROID_ENDPOINT = "android";
    private Handler mainLooper = new Handler(Looper.getMainLooper());
    private SenderWorker worker;
    private Socket socket;

    public LogsSender(Intercepter<String> intercepter, String serverUrl, String sessionId) {
        this.intercepter = intercepter;
        this.serverUrl = serverUrl;
        this.sessionId = sessionId;
    }

    @Override
    public void startSending() {
        super.startSending();

        try {
            socket = IO.socket(serverUrl + "/" + ANDROID_ENDPOINT + "/" + sessionId);
            socket.on(io.socket.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("LogsSender", "Connected.");
                }
            }).on(io.socket.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("LogsSender", "Disconnected.");
                }
            }).on(io.socket.client.Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("LogsSender", "Connection error.");
                }
            }).on(io.socket.client.Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("LogsSender", "Connection timeout.");
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.worker = new SenderWorker();
        worker.start();
    }

    @Override
    public void stopSending() {
        super.stopSending();
        if (socket != null) {
            socket.disconnect();
        }
    }

    private class SenderWorker extends Thread {

        @Override
        public void run() {
            while (runSending) {
                send();
            }
        }

        private void send() {
            String log = intercepter.intercept();
            if (log != null) {
                socket.emit("logs", log);
                if (senderCallback != null) {
                    senderCallback.onSuccess();
                }
            }
        }
    }
}
