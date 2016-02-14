package pl.edu.agh.marims.hub;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import pl.edu.agh.marims.hub.models.ApplicationFile;
import pl.edu.agh.marims.hub.models.LoggedUser;
import pl.edu.agh.marims.hub.models.Session;
import pl.edu.agh.marims.hub.network.MarimsApiClient;
import pl.edu.agh.marims.hub.util.GsonUtil;

public class App extends Application {

    public interface DataListener {
        void onFilesUpdated(List<ApplicationFile> files);

        void onSessionsUpdated(List<Session> sessions);
    }

    public static class BaseDataListener implements DataListener {

        @Override
        public void onFilesUpdated(List<ApplicationFile> files) {

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

    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    private List<ApplicationFile> files = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();
    private MarimsApiClient marimsApiClient = MarimsApiClient.getInstance();

    public void connect() {
        try {
            socket = IO.socket(Config.SERVER_URL + Config.SOCKET_IO_ENDPOINT);
            socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport) args[0];

                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            @SuppressWarnings("unchecked")
                            Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                            LoggedUser loggedUser = marimsApiClient.getLoggedUser();
                            if (loggedUser != null && loggedUser.getToken() != null) {
                                headers.put("Authorization", Collections.singletonList("Bearer " + loggedUser.getToken()));
                            }
                        }
                    });
                }
            });
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
                    List<String> filesStrings = GsonUtil.getGson().fromJson(filesJson.toString(), stringListType);
                    files.clear();
                    for (String fileString : filesStrings) {
                        files.add(new ApplicationFile(fileString));
                    }
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

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off();
        }
    }
}
