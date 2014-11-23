package pl.edu.agh.marims.screenstreamer.lib.network;

public interface SenderCallback {
    public void onSuccess();

    public void onFailure();

    public void onSend();

    public void onStart();

    public void onStop();
}
