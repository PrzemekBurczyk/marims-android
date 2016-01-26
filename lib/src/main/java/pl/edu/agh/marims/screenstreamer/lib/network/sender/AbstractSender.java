package pl.edu.agh.marims.screenstreamer.lib.network.sender;

import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.Intercepter;

public abstract class AbstractSender<T> {

    protected String sessionId;
    protected String serverUrl;
    protected SenderCallback senderCallback;
    protected Intercepter<T> intercepter;
    protected boolean runSending = false;

    public void startSending() {
        runSending = true;
        if (senderCallback != null) {
            senderCallback.onStart();
        }
    }

    public void stopSending() {
        runSending = false;
        if (senderCallback != null) {
            senderCallback.onStop();
        }
    }

    public void setSenderCallback(SenderCallback senderCallback) {
        this.senderCallback = senderCallback;
    }
}
