package pl.edu.agh.marims.lib.screen.intercepter;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.edu.agh.marims.lib.measurement.Measurer;
import pl.edu.agh.marims.lib.network.sender.AbstractSender;
import pl.edu.agh.marims.lib.network.sender.LogsSender;
import pl.edu.agh.marims.lib.network.sender.SenderType;

public class LogsIntercepter implements Intercepter<String> {
    private static final int MEASURE_INTERVAL = 5000;
    private StatisticsCallback statisticsCallback;
    private String sessionId = null;
    private String serverUrl = null;
    private Activity activity;
    private boolean initialized = false;
    private AbstractSender sender;
    private Measurer measurer;
    private Handler handler = new Handler();

    private Process logcatProcess;
    private BufferedReader logcatReader;

    public LogsIntercepter(final Activity activity, String serverUrl, String sessionId) {
        this.activity = activity;
        this.serverUrl = serverUrl;
        this.sessionId = sessionId;

        if (sessionId != null) {
            sender = new LogsSender(this, serverUrl, this.sessionId);
            measurer = new Measurer(sender);
        }
    }

    @Override
    public void setStatisticsCallback(StatisticsCallback statisticsCallback) {
        this.statisticsCallback = statisticsCallback;
    }

    @Override
    public void setSenderType(SenderType senderType) {
        if (sender != null) {
            sender.stopSending();
            sender = null;
        }
        switch (senderType) {
            case WEBSOCKET:
                sender = new LogsSender(this, this.serverUrl, this.sessionId);
                break;
            default:
                throw new RuntimeException("Unimplemented sender type: " + senderType.toString());
        }
        measurer = new Measurer(sender);
        if (initialized) {
            sender.startSending();
        }
    }

    private void clearLogs() {
        try {
            new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        try {
            if (sessionId != null) {
                logcatProcess = new ProcessBuilder().command("logcat").start();
                initialized = true;
            } else {
                throw new Exception("Unknown session ID, start Activity with an Intent from QR scan");
            }
        } catch (Exception e) {
            Toast.makeText(activity, "Wasn't able to initialize", Toast.LENGTH_SHORT).show();
            initialized = false;
            e.printStackTrace();
        }
    }

    @Override
    public String intercept() {
        if (!initialized) {
            return null;
        }
        try {
            return logcatReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void start() {
        if (initialized) {
            logcatReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            sender.startSending();
        }
    }

    @Override
    public void stop() {
        if (initialized) {
            if (sender != null) {
                sender.stopSending();
            }
            if (logcatReader != null) {
                try {
                    logcatReader.close();
                    logcatReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (logcatProcess != null) {
                logcatProcess.destroy();
            }
            initialized = false;
        }
    }
}
