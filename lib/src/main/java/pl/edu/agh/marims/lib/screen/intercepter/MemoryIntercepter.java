package pl.edu.agh.marims.lib.screen.intercepter;

import android.app.Activity;
import android.os.Handler;

import pl.edu.agh.marims.lib.measurement.Measurer;
import pl.edu.agh.marims.lib.measurement.MemoryStatus;
import pl.edu.agh.marims.lib.network.sender.AbstractSender;
import pl.edu.agh.marims.lib.network.sender.MemorySender;
import pl.edu.agh.marims.lib.network.sender.SenderType;

public class MemoryIntercepter implements Intercepter<MemoryStatus> {
    private StatisticsCallback statisticsCallback;
    private String sessionId = null;
    private String serverUrl = null;
    private Activity activity;
    private boolean initialized = false;
    private AbstractSender sender;
    private Measurer measurer;
    private Handler handler = new Handler();

    public MemoryIntercepter(final Activity activity, String serverUrl, String sessionId) {
        this.activity = activity;
        this.serverUrl = serverUrl;
        this.sessionId = sessionId;

        if (sessionId != null) {
            sender = new MemorySender(this, serverUrl, this.sessionId);
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
                sender = new MemorySender(this, this.serverUrl, this.sessionId);
                break;
            default:
                throw new RuntimeException("Unimplemented sender type: " + senderType.toString());
        }
        measurer = new Measurer(sender);
        if (initialized) {
            sender.startSending();
        }
    }

    @Override
    public void initialize() {
        initialized = true;
    }

    @Override
    public MemoryStatus intercept() {
        if (!initialized) {
            return null;
        }
        Runtime runtime = Runtime.getRuntime();
        MemoryStatus memoryStatus = new MemoryStatus();
        memoryStatus.setFree(runtime.freeMemory());
        memoryStatus.setTotal(runtime.totalMemory());
        memoryStatus.setMax(runtime.maxMemory());
        memoryStatus.setUsed(runtime.totalMemory() - runtime.freeMemory());
        memoryStatus.setTimestamp(System.currentTimeMillis());
        return memoryStatus;
    }

    @Override
    public void start() {
        if (initialized && sender != null) {
            sender.startSending();
        }
    }

    @Override
    public void stop() {
        if (initialized) {
            if (sender != null) {
                sender.stopSending();
            }
            initialized = false;
        }
    }
}
