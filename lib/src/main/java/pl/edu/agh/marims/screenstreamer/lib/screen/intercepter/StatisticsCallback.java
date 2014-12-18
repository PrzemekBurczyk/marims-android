package pl.edu.agh.marims.screenstreamer.lib.screen.intercepter;

import pl.edu.agh.marims.screenstreamer.lib.measurement.Statistics;

/**
 * Created by Przemek on 2014-12-18.
 */
public interface StatisticsCallback {
    public void onNewStatistics(Statistics statistics);
}
