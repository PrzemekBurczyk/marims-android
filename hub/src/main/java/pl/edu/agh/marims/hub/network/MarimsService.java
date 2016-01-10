package pl.edu.agh.marims.hub.network;

import com.squareup.okhttp.RequestBody;

import java.util.List;

import pl.edu.agh.marims.hub.models.Session;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MarimsService {

    @GET("/files")
    Call<List<String>> getFiles();

    @Multipart
    @POST("/files")
    Call<Void> postFile(@Part("applicationName") RequestBody applicationName, @Part("applicationVersion") RequestBody applicationVersion, @Part("applicationVersionCode") RequestBody applicationVersionCode, @Part("file\"; filename=\"app.apk\"") RequestBody file);

    @DELETE("/files/{filename}")
    Call<Void> deleteFile(@Path("filename") String filename);

    @GET("/sessions")
    Call<List<Session>> getSessions();

}
