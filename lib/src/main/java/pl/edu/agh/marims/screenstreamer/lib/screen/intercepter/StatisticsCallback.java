package pl.edu.agh.marims.screenstreamer.lib.screen.intercepter;

import pl.edu.agh.marims.screenstreamer.lib.measurement.Statistics;

public interface StatisticsCallback {
    void onNewStatistics(Statistics statistics);
}
