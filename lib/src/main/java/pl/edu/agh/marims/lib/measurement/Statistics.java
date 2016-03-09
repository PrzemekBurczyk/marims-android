package pl.edu.agh.marims.lib.measurement;

public class Statistics {
    private long measurementTimeMilis;
    private double successfulSendsPerSecond;
    private long successfulSendsCount;
    private long failedSendsCount;

    public Statistics(long measurementTimeMilis, double successfulSendsPerSecond, long successfulSendsCount, long failedSendsCount) {
        this.measurementTimeMilis = measurementTimeMilis;
        this.successfulSendsPerSecond = successfulSendsPerSecond;
        this.successfulSendsCount = successfulSendsCount;
        this.failedSendsCount = failedSendsCount;
    }

    public long getMeasurementTimeMilis() {
        return measurementTimeMilis;
    }

    public void setMeasurementTimeMilis(long measurementTimeMilis) {
        this.measurementTimeMilis = measurementTimeMilis;
    }

    public double getSuccessfulSendsPerSecond() {
        return successfulSendsPerSecond;
    }

    public void setSuccessfulSendsPerSecond(double successfulSendsPerSecond) {
        this.successfulSendsPerSecond = successfulSendsPerSecond;
    }

    public long getSuccessfulSendsCount() {
        return successfulSendsCount;
    }

    public void setSuccessfulSendsCount(long successfulSendsCount) {
        this.successfulSendsCount = successfulSendsCount;
    }

    public long getFailedSendsCount() {
        return failedSendsCount;
    }

    public void setFailedSendsCount(long failedSendsCount) {
        this.failedSendsCount = failedSendsCount;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "successfulSendsPerSecond=" + successfulSendsPerSecond +
                ", successfulSendsCount=" + successfulSendsCount +
                ", failedSendsCount=" + failedSendsCount +
                ", measurementTimeSeconds=" + measurementTimeMilis / 1000 +
                '}';
    }
}
