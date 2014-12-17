package pl.edu.agh.marims.screenstreamer.lib.network.sender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import pl.edu.agh.marims.screenstreamer.lib.convert.BitmapToBase64Converter;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.Intercepter;

/**
 * Created by Przemek on 2014-12-16.
 */
public class TcpSocketSender extends AbstractSender {

    private static final boolean DEBUG = false;

    private Handler mainLooper = new Handler(Looper.getMainLooper());
    private BitmapToBase64Converter converter;
    private SenderWorker worker;
    private int port;

    public TcpSocketSender(Intercepter intercepter, String serverUrl, int port, String sessionId) {
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
        private Socket socket;
        private Bitmap bitmap;
        private long lastScreenshotVersion;
        private long screenshotVersion = Long.MIN_VALUE;
        private boolean loadInProgress = false;
        private PrintWriter printWriter;

        @Override
        public void run() {
            lastScreenshotVersion = screenshotVersion;
            try {
                String serverAddress = serverUrl.replaceFirst("http://", "");
                address = InetAddress.getByName(serverAddress);
                socket = new Socket(address, port);
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                while (runSending) {
                    if (!loadInProgress) {
                        try {
                            send();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        load();
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void send() throws IOException, InterruptedException {
            if (lastScreenshotVersion != screenshotVersion) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sessionId", sessionId);
                    jsonObject.put("screenWidth", bitmap.getWidth());
                    jsonObject.put("screenHeight", bitmap.getHeight());
                    jsonObject.put("image", converter.convert(bitmap));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                printWriter.println(jsonObject.toString());

                if (senderCallback != null) {
                    senderCallback.onSuccess();
                }
                lastScreenshotVersion = screenshotVersion;
            }
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
