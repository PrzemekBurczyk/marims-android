package pl.edu.agh.marims.screenstreamer.lib;

import android.app.Activity;
import android.view.View;

import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.ScreenIntercepter;
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
