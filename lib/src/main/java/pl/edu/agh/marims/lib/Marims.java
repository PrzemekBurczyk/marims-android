package pl.edu.agh.marims.lib;

import android.app.Activity;
import android.view.View;

import java.util.Map;

import pl.edu.agh.marims.lib.intent.IntentReader;
import pl.edu.agh.marims.lib.network.sender.SenderType;
import pl.edu.agh.marims.lib.screen.intercepter.Intercepter;
import pl.edu.agh.marims.lib.screen.intercepter.LogsIntercepter;
import pl.edu.agh.marims.lib.screen.intercepter.MemoryIntercepter;
import pl.edu.agh.marims.lib.screen.intercepter.ScreenIntercepter;
import pl.edu.agh.marims.lib.screen.intercepter.StatisticsCallback;
import pl.edu.agh.marims.lib.screen.manipulator.ScreenManipulator;

public class Marims {

    private static final String SESSION_ID_KEY = "sessionId";
    private final Intercepter screenIntercepter;
    private final Intercepter logsIntercepter;
    private final Intercepter memoryIntercepter;
    private final ScreenManipulator screenManipulator;

    public Marims(Activity activity, View view, String serverUrl) {
        Map<String, String> intentParams = IntentReader.readIntentParams(activity.getIntent());

        String sessionId = null;
        if (!intentParams.isEmpty()) {
            sessionId = intentParams.get(SESSION_ID_KEY);
        }

        screenIntercepter = new ScreenIntercepter(activity, view, serverUrl, sessionId);
        logsIntercepter = new LogsIntercepter(activity, serverUrl, sessionId);
        memoryIntercepter = new MemoryIntercepter(activity, serverUrl, sessionId);
        screenManipulator = new ScreenManipulator(activity, view, serverUrl, sessionId);
    }

    public void setSenderType(SenderType senderType) {
        if (screenIntercepter != null) {
            screenIntercepter.setSenderType(senderType);
        }
    }

    public void setStatisticsCallback(StatisticsCallback statisticsCallback) {
        if (screenIntercepter != null) {
            screenIntercepter.setStatisticsCallback(statisticsCallback);
        }
    }

    public void onResume() {
        screenIntercepter.initialize();
        screenIntercepter.start();
        logsIntercepter.initialize();
        logsIntercepter.start();
        memoryIntercepter.initialize();
        memoryIntercepter.start();
        screenManipulator.initialize();
    }

    public void onPause() {
        screenIntercepter.stop();
        logsIntercepter.stop();
        memoryIntercepter.stop();
        screenManipulator.stop();
    }
}
