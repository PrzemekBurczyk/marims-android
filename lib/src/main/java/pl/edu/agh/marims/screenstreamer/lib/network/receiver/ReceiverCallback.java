package pl.edu.agh.marims.screenstreamer.lib.network.receiver;

import org.json.JSONArray;

public interface ReceiverCallback {
    public void onReceive(String event, JSONArray data);
}
