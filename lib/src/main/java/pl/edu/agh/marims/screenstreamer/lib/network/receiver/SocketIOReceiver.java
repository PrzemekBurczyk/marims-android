package pl.edu.agh.marims.screenstreamer.lib.network.receiver;

import android.util.Log;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketIOReceiver extends AbstractReceiver {

    private Future<SocketIOClient> socketIOClientFuture;

    public SocketIOReceiver(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public SocketIOReceiver(String serverUrl, ReceiverCallback receiverCallback) {
        this(serverUrl);
        this.receiverCallback = receiverCallback;
    }

    @Override
    public void startReceiving() {

        final String sessionId = "123e4567-e89b-12d3-a456-426655440000";

        socketIOClientFuture = SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), serverUrl, new SocketIOClient.SocketIOConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, final SocketIOClient client) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.d("SOCKET_IO", "Connected...");

                JSONArray json = new JSONArray();
                JSONObject jsonImage = new JSONObject();
                try {
                    jsonImage.put("sessionId", sessionId);
                    client.emit("register", json);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                client.setEventCallback(new SocketIOClient.EventCallback() {
                    @Override
                    public void onEvent(String event, JSONArray data) {
                        if (receiverCallback != null) {
                            receiverCallback.onReceive(event, data);
                        }
                    }
                });

            }
        });

    }

    @Override
    public void stopReceiving() {
        if (socketIOClientFuture != null && !socketIOClientFuture.isCancelled()) {
            socketIOClientFuture.cancel(true);
        }
    }
}
