package pl.edu.agh.marims.lib.network.sender;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pl.edu.agh.marims.lib.measurement.MemoryStatus;
import pl.edu.agh.marims.lib.screen.intercepter.Intercepter;

public class MemorySender extends WebsocketSender<MemoryStatus> {

    private static final boolean DEBUG = false;

    private static final String ANDROID_ENDPOINT = "android";
    private Handler mainLooper = new Handler(Looper.getMainLooper());
    private SenderWorker worker;
    private Socket socket;

    public MemorySender(Intercepter<MemoryStatus> intercepter, String serverUrl, String sessionId) {
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
                try {
                    send();
                    Thread.sleep(100);
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void send() throws JSONException {
            MemoryStatus memoryStatus = intercepter.intercept();
            if (memoryStatus != null) {
                JSONObject memoryStatusJson = new JSONObject();
                memoryStatusJson.put("max", memoryStatus.getMax());
                memoryStatusJson.put("total", memoryStatus.getTotal());
                memoryStatusJson.put("free", memoryStatus.getFree());
                memoryStatusJson.put("used", memoryStatus.getUsed());
                memoryStatusJson.put("timestamp", memoryStatus.getTimestamp());
                socket.emit("memoryStatus", memoryStatusJson);
                if (senderCallback != null) {
                    senderCallback.onSuccess();
                }
            }
        }
    }
}
