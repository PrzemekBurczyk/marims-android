package pl.edu.agh.marims.screenstreamer.lib.screen.intercepter;


import pl.edu.agh.marims.screenstreamer.lib.network.sender.SenderType;

public interface Intercepter<T> {
    void setStatisticsCallback(StatisticsCallback statisticsCallback);

    void setSenderType(SenderType senderType);

    void initialize();

    void start();

    void stop();

    T intercept();
}
