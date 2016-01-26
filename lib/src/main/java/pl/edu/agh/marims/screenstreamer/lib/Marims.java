package pl.edu.agh.marims.screenstreamer.lib;

import android.app.Activity;
import android.view.View;

import java.util.Map;

import pl.edu.agh.marims.screenstreamer.lib.intent.IntentReader;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.SenderType;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.ScreenIntercepter;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.StatisticsCallback;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.ScreenManipulator;

public class Marims {

    private static final String SESSION_ID_KEY = "sessionId";
    private final ScreenIntercepter screenIntercepter;
    private final ScreenManipulator screenManipulator;

    public Marims(Activity activity, View view, String serverUrl) {
        Map<String, String> intentParams = IntentReader.readIntentParams(activity.getIntent());

        String sessionId = null;
        if (!intentParams.isEmpty()) {
            sessionId = intentParams.get(SESSION_ID_KEY);
        }
        sessionId = "98d39f77-cd6f-44ec-b1a2-20756a6fe354";

        screenIntercepter = new ScreenIntercepter(activity, view, serverUrl, sessionId);
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
//        screenIntercepter.intercept();
        screenManipulator.initialize();
    }

    public void onPause() {
        screenIntercepter.stop();
        screenManipulator.stop();
    }
}
