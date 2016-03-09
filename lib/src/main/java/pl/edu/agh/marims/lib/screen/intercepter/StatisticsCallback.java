package pl.edu.agh.marims.lib.screen.intercepter;

import pl.edu.agh.marims.lib.measurement.Statistics;

public interface StatisticsCallback {
    void onNewStatistics(Statistics statistics);
}
