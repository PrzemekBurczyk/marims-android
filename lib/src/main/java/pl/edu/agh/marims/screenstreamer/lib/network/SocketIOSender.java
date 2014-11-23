package pl.edu.agh.marims.screenstreamer.lib.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.SocketIOClient;

import org.json.JSONArray;

import pl.edu.agh.marims.screenstreamer.lib.convert.BitmapToBase64Converter;
import pl.edu.agh.marims.screenstreamer.lib.screen.Intercepter;

public class SocketIOSender extends AbstractSender {

    private BitmapToBase64Converter converter;
    private Handler mainLooper = new Handler(Looper.getMainLooper());

    public SocketIOSender(Intercepter intercepter, String serverUrl) {
        this.intercepter = intercepter;
        this.serverUrl = serverUrl;
        this.converter = new BitmapToBase64Converter();
    }

    public SocketIOSender(Intercepter intercepter, String serverUrl, SenderCallback senderCallback) {
        this(intercepter, serverUrl);
        this.senderCallback = senderCallback;
    }

    @Override
    public void startSending() {
        super.startSending();

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://marims-backend.herokuapp.com", new SocketIOClient.SocketIOConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, final SocketIOClient client) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.d("SOCKET_IO", "Connected...");
                client.setEventCallback(new SocketIOClient.EventCallback() {
                    @Override
                    public void onEvent(String s, JSONArray jsonArray) {
                        Log.d("SOCKET_IO", s);
                        if (s.equals("start")) {
                            JSONArray json = new JSONArray();
                            json.put("wiadomosc");
                            client.emit("image", json/*converter.convert(intercepter.takeScreenshot())*/);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void stopSending() {
        super.stopSending();


    }
}
