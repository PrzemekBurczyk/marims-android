package pl.edu.agh.marims.lib.network.sender;

public interface SenderCallback {
    void onSuccess();

    void onFailure();

    void onSend();

    void onStart();

    void onStop();
}
