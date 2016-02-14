package pl.edu.agh.marims.hub.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.edu.agh.marims.hub.Config;
import pl.edu.agh.marims.hub.models.LoggedUser;
import pl.edu.agh.marims.hub.util.GsonUtil;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MarimsApiClient {

    private static MarimsApiClient instance = null;
    private MarimsService marimsService;
    private LoggedUser loggedUser;

    private MarimsApiClient() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (loggedUser != null && loggedUser.getToken() != null) {
                    request = request.newBuilder()
                            .addHeader("Authorization", "Bearer " + loggedUser.getToken())
                            .build();
                }
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.getGson()))
                .client(client)
                .build();

        marimsService = retrofit.create(MarimsService.class);
    }

    public static synchronized MarimsApiClient getInstance() {
        if (instance == null) {
            instance = new MarimsApiClient();
        }
        return instance;
    }

    public MarimsService getMarimsService() {
        return marimsService;
    }

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }
}
