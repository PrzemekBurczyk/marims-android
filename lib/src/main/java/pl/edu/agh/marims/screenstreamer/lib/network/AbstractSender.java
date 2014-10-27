package pl.edu.agh.marims.screenstreamer.lib.network;

import pl.edu.agh.marims.screenstreamer.lib.screen.Intercepter;

public abstract class AbstractSender {

    protected String serverUrl;
    protected SenderCallback senderCallback;
    protected Intercepter intercepter;

    public abstract void send();

    public void setSenderCallback(SenderCallback senderCallback) {
        this.senderCallback = senderCallback;
    }
}
