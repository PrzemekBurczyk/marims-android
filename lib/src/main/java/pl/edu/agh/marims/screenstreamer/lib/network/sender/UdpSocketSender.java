package pl.edu.agh.marims.screenstreamer.lib.network.sender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import pl.edu.agh.marims.screenstreamer.lib.convert.BitmapToBase64Converter;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.Intercepter;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.ScreenIntercepter;

/**
 * Created by Przemek on 2014-12-10.
 */
public class UdpSocketSender extends AbstractSender {

    private Handler mainLooper = new Handler(Looper.getMainLooper());
    private BitmapToBase64Converter converter;
    private SenderWorker worker;

    public UdpSocketSender(Intercepter intercepter, String serverUrl) {
        this.intercepter = intercepter;
        this.serverUrl = serverUrl;
        this.converter = new BitmapToBase64Converter();
        this.worker = new SenderWorker();
    }

    @Override
    public void startSending() {
        super.startSending();
        worker.start();
    }

    private class SenderWorker extends Thread {

        private InetAddress address;
        private DatagramSocket socket;
        private Bitmap bitmap;
        private long lastScreenshotVersion;
        private long screenshotVersion = Long.MIN_VALUE;
        private boolean loadInProgress = false;

        @Override
        public void run() {
            lastScreenshotVersion = screenshotVersion;
            try {
                socket = new DatagramSocket(6666);
                address = InetAddress.getByName("192.168.0.11");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            while (runSending) {
                if (!loadInProgress) {
                    try {
                        send();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    load();
                }
            }
        }

        private void send() throws IOException {
            if (lastScreenshotVersion != screenshotVersion) {
                byte[] payload = buildPayload().getBytes();
                Log.d("REQUEST", "Post length: " + payload.length);

                DatagramPacket packet = new DatagramPacket(payload, payload.length, address, 6666);
                socket.send(packet);

                lastScreenshotVersion = screenshotVersion;
            }
        }

        private String buildPayload() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sessionId", ScreenIntercepter.SESSION_ID);
                jsonObject.put("screenWidth", bitmap.getWidth());
                jsonObject.put("screenHeight", bitmap.getHeight());
                jsonObject.put("image", converter.convert(bitmap));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject.toString();
        }

        private void load() {
            loadInProgress = true;
            mainLooper.post(new Runnable() {
                @Override
                public void run() {
                    bitmap = intercepter.takeScreenshot();
                    if (bitmap != null) {
                        screenshotVersion++;
                    }
                    loadInProgress = false;
                }
            });
        }
    }

}
