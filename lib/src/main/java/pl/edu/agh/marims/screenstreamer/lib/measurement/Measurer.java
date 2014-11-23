package pl.edu.agh.marims.screenstreamer.lib.measurement;

import pl.edu.agh.marims.screenstreamer.lib.network.sender.AbstractSender;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.SenderCallback;

public class Measurer {

    private AbstractSender sender;

    private boolean started = false;
    private long start;
    private long end;
    private long successCount = 0;
    private long failureCount = 0;

    public Measurer(AbstractSender sender) {
        this.sender = sender;

        sender.setSenderCallback(new SenderCallback() {
            @Override
            public synchronized void onSuccess() {
                successCount++;
            }

            @Override
            public synchronized void onFailure() {
                failureCount++;
            }

            @Override
            public void onSend() {
            }

            @Override
            public void onStart() {
                started = true;
                start = System.currentTimeMillis();
            }

            @Override
            public void onStop() {
                started = false;
            }
        });
    }

    public Statistics getStatistics() {
        end = System.currentTimeMillis();
        long measurementTimeMilis = end - start;
        double successfulSendsPerSecond = (double) successCount * 1000 / (double) measurementTimeMilis;
        return new Statistics(measurementTimeMilis, successfulSendsPerSecond, successCount, failureCount);
    }
}
