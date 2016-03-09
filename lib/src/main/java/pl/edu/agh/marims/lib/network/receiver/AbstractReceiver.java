package pl.edu.agh.marims.lib.network.receiver;

public abstract class AbstractReceiver {

    protected String sessionId;
    protected String serverUrl;
    protected ReceiverCallback receiverCallback;

    public abstract void startReceiving();

    public abstract void stopReceiving();

    public void setSenderCallback(ReceiverCallback receiverCallback) {
        this.receiverCallback = receiverCallback;
    }
}
