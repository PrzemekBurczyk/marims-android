package pl.edu.agh.marims.hub.network;

import pl.edu.agh.marims.hub.Config;
import pl.edu.agh.marims.hub.util.GsonUtil;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MarimsApiClient {

    private static MarimsApiClient instance = null;
    private MarimsService marimsService;

    private MarimsApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.getGson()))
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
}
