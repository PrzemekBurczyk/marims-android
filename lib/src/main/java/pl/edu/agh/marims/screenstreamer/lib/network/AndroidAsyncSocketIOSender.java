package pl.edu.agh.marims.screenstreamer.lib.network;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.SocketIOClient;

import pl.edu.agh.marims.screenstreamer.lib.convert.BitmapToBase64Converter;
import pl.edu.agh.marims.screenstreamer.lib.screen.Intercepter;

public class AndroidAsyncSocketIOSender extends AbstractSender {

    private BitmapToBase64Converter converter;

    public AndroidAsyncSocketIOSender(Intercepter intercepter, String serverUrl) {
        this.intercepter = intercepter;
        this.serverUrl = serverUrl;
        this.converter = new BitmapToBase64Converter();
    }

    public AndroidAsyncSocketIOSender(Intercepter intercepter, String serverUrl, SenderCallback senderCallback) {
        this(intercepter, serverUrl);
        this.senderCallback = senderCallback;
    }

    @Override
    public void send() {
        if (senderCallback != null) {
            senderCallback.onSend();
        }
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), serverUrl, new SocketIOClient.SocketIOConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, final SocketIOClient socketIOClient) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                socketIOClient.emit("Hello!");
//                socketIOClient.emit(converter.convert(intercepter.takeScreenshot()));
//                socketIOClient.setEventCallback(new SocketIOClient.EventCallback() {
//                    @Override
//                    public void onEvent(String s, JSONArray jsonArray) {
//                        socketIOClient.emit(converter.convert(intercepter.takeScreenshot()));
//                    }
//                });
            }
        });
    }

}
