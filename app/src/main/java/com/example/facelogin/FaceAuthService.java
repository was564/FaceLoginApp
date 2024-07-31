package com.example.facelogin;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FaceAuthService {
    @Multipart
    @POST("/res/verifying")
    Call<FaceDataResource> AuthFace(@Part MultipartBody.Part image);

    @Multipart
    @POST("/res/registering")
    Call<FaceDataResource> RegisterFace(
            @Part MultipartBody.Part image,
            @Part("name") String name,
            @Part("birth") String birth);
}
