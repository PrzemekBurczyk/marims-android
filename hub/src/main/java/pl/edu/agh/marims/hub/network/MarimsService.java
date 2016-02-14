package pl.edu.agh.marims.hub.network;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.edu.agh.marims.hub.models.LoggedUser;
import pl.edu.agh.marims.hub.models.Session;
import pl.edu.agh.marims.hub.models.UserRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MarimsService {

    @POST("/register")
    Call<LoggedUser> register(@Body UserRequest user);

    @POST("/login")
    Call<LoggedUser> logIn(@Body UserRequest user);

    @POST("/logout")
    Call<Void> logOut();

    @GET("/files")
    Call<List<String>> getFiles();

    @GET("/files/{filename}")
    Call<ResponseBody> getFile(@Path("filename") String filename);

    @Multipart
    @POST("/files")
    Call<Void> postFile(@Part("applicationName") RequestBody applicationName, @Part("applicationVersion") RequestBody applicationVersion, @Part("applicationVersionCode") RequestBody applicationVersionCode, @Part("file\"; filename=\"app.apk\"") RequestBody file);

    @DELETE("/files/{filename}")
    Call<Void> deleteFile(@Path("filename") String filename);

    @GET("/sessions")
    Call<List<Session>> getSessions();

}
