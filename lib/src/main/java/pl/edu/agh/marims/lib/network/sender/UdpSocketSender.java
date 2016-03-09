package pl.edu.agh.marims.lib.network.sender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.marims.lib.convert.BitmapToBase64Converter;
import pl.edu.agh.marims.lib.screen.intercepter.Intercepter;

public class UdpSocketSender extends AbstractSender<Bitmap> {

    private static final boolean DEBUG = false;

    private Handler mainLooper = new Handler(Looper.getMainLooper());
    private BitmapToBase64Converter converter;
    private SenderWorker worker;
    private int port;

    public UdpSocketSender(Intercepter<Bitmap> intercepter, String serverUrl, int port, String sessionId) {
        this.intercepter = intercepter;
        this.serverUrl = serverUrl;
        this.converter = new BitmapToBase64Converter();
        this.port = port;
        this.sessionId = sessionId;
    }

    @Override
    public void startSending() {
        super.startSending();
        this.worker = new SenderWorker();
        worker.start();
    }

    private class SenderWorker extends Thread {

        private static final int MAX_SIZE = 65000;

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
                socket = new DatagramSocket();
                String serverAddress = serverUrl.replaceFirst("http://", "").replaceFirst(":(\\d){1,5}", "");
                address = InetAddress.getByName(serverAddress);
                while (runSending) {
                    if (!loadInProgress) {
                        try {
                            send();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        load();
                    }
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }

        private void send() throws IOException, InterruptedException {
            if (lastScreenshotVersion != screenshotVersion) {
                List<byte[]> payloads = buildPayloads();

                if (DEBUG) {
                    Log.d("REQUEST", "Payloads count: " + payloads.size());
                    Log.d("REQUEST", "Payloads length: " + ((payloads.size() - 1) * MAX_SIZE + payloads.get(payloads.size() - 1).length));
                    for (int i = 0; i < payloads.size(); i++) {
                        Log.d("REQUEST", i + ":" + payloads.get(i).length);
                    }
                }

                for (int i = 0; i < payloads.size(); i++) {
                    byte[] payload = payloads.get(i);
                    DatagramPacket packet = new DatagramPacket(payload, payload.length, address, port);
                    socket.send(packet);
                    if (payloads.size() > 2) {
                        Thread.sleep(50);
                    }
                }

                if (senderCallback != null) {
                    senderCallback.onSuccess();
                }
                lastScreenshotVersion = screenshotVersion;
            }
        }

        private List<byte[]> buildPayloads() throws UnsupportedEncodingException {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sessionId", sessionId);
                jsonObject.put("screenWidth", bitmap.getWidth());
                jsonObject.put("screenHeight", bitmap.getHeight());
                jsonObject.put("image", converter.convert(bitmap));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            byte[] data = jsonObject.toString().getBytes();
            List<byte[]> payloads = new ArrayList<byte[]>();
            byte payloadsCount = (byte) (data.length / MAX_SIZE + 1);
            for (byte i = 0; i < payloadsCount; i++) {
                int from;
                int to;
                int length;
                if (i < payloadsCount - 1) {
                    from = i * MAX_SIZE;
                    to = i * MAX_SIZE + MAX_SIZE;
                } else {
                    from = i * MAX_SIZE;
                    to = data.length;
                }
                length = to - from;
                if (DEBUG) {
                    Log.d("REQUEST", "from: " + from + " to: " + to + " length: " + length);
                }
                byte[] payload = new byte[length + 39];
                System.arraycopy(data, from, payload, 39, length);
                payload[0] = (byte) (i + 1);
                payload[1] = payloadsCount;
                payload[2] = (byte) screenshotVersion;
                byte[] sessionId = UdpSocketSender.this.sessionId.getBytes("US-ASCII");
                System.arraycopy(sessionId, 0, payload, 3, sessionId.length);
                payloads.add(payload);
            }
            return payloads;
        }

        private void load() {
            loadInProgress = true;
            mainLooper.post(new Runnable() {
                @Override
                public void run() {
                    bitmap = intercepter.intercept();
                    if (bitmap != null) {
                        screenshotVersion++;
                    }
                    loadInProgress = false;
                }
            });
        }
    }

}
