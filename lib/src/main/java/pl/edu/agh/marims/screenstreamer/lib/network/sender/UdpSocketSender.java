package pl.edu.agh.marims.screenstreamer.lib.network.sender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

        private Bitmap bitmap;
        private long lastScreenshotVersion;
        private long screenshotVersion = Long.MIN_VALUE;
        private boolean loadInProgress = false;

        @Override
        public void run() {
            lastScreenshotVersion = screenshotVersion;
            while (runSending) {
                if (!loadInProgress) {
                    send();
                    load();
                }
            }
        }

        private void send() {
            if (lastScreenshotVersion != screenshotVersion) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sessionId", ScreenIntercepter.SESSION_ID);
                    jsonObject.put("screenWidth", bitmap.getWidth());
                    jsonObject.put("screenHeight", bitmap.getHeight());
                    jsonObject.put("image", converter.convert(bitmap));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String postString = jsonObject.toString();
                Log.d("REQUEST", "Post length: " + postString.length());

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
