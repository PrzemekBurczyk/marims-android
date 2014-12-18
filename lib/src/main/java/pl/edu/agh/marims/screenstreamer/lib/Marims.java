package pl.edu.agh.marims.screenstreamer.lib;

import android.app.Activity;
import android.view.View;

import pl.edu.agh.marims.screenstreamer.lib.network.sender.SenderType;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.ScreenIntercepter;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.StatisticsCallback;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.ScreenManipulator;

/**
 * Created by Przemek on 2014-12-13.
 */
public class Marims {

    private ScreenIntercepter screenIntercepter;
    private ScreenManipulator screenManipulator;

    public Marims(Activity activity, View view, String serverUrl) {
        screenIntercepter = new ScreenIntercepter(activity, view, serverUrl);
        screenManipulator = new ScreenManipulator(activity, view, serverUrl);
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
        screenIntercepter.intercept();
        screenManipulator.initialize();
    }

    public void onPause() {
        screenIntercepter.stop();
        screenManipulator.stop();
    }
}
