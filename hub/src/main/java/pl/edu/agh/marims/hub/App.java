package pl.edu.agh.marims.hub;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pl.edu.agh.marims.hub.models.Session;
import pl.edu.agh.marims.hub.util.GsonUtil;

public class App extends Application {

    public interface DataListener {
        void onFilesUpdated(List<String> files);

        void onSessionsUpdated(List<Session> sessions);
    }

    public static class BaseDataListener implements DataListener {

        @Override
        public void onFilesUpdated(List<String> files) {

        }

        @Override
        public void onSessionsUpdated(List<Session> sessions) {

        }
    }

    private final Type stringListType = new TypeToken<List<String>>() {
    }.getType();

    private final Type sessionListType = new TypeToken<List<Session>>() {
    }.getType();

    private List<DataListener> dataListeners = new ArrayList<>();

    public void addDataListener(DataListener dataListener) {
        dataListeners.add(dataListener);
        dataListener.onFilesUpdated(files);
        dataListener.onSessionsUpdated(sessions);
    }

    public void removeDataListener(DataListener dataListener) {
        dataListeners.remove(dataListener);
    }

    public void removeAllDataListeners() {
        dataListeners.clear();
    }

    private List<String> files = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Socket socket = IO.socket(Config.SERVER_URL + Config.SOCKET_IO_ENDPOINT);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Socket.io", "Socket connected");
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Socket.io", "Socket disconnected");
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Socket.io", "Socket connect error");
                }
            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Socket.io", "Socket connect timeout");
                }
            }).on("files", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray filesJson = (JSONArray) args[0];
                    files = GsonUtil.getGson().fromJson(filesJson.toString(), stringListType);
                    for (DataListener dataListener : dataListeners) {
                        dataListener.onFilesUpdated(files);
                    }
                }
            }).on("sessions", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray sessionsJson = (JSONArray) args[0];
                    sessions = GsonUtil.getGson().fromJson(sessionsJson.toString(), sessionListType);
                    for (DataListener dataListener : dataListeners) {
                        dataListener.onSessionsUpdated(sessions);
                    }
                }
            }).on("sessionCreationFailed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Toast.makeText(App.this, R.string.session_creation_failed, Toast.LENGTH_SHORT).show();
                }
            }).on("sessionRemovalFailed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Toast.makeText(App.this, R.string.session_removal_failed, Toast.LENGTH_SHORT).show();
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
