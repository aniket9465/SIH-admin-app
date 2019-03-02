package com.example.canaladmin;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("/api/authenticate/")
    Call<LoginResponse> login(@Body LoginRequestBody credentials);

    @GET("/api/query/")
    Call<List<QueryItem>> QueryRequest(@Query("authority") String Authority);

    @GET("/api/photo/{photoid}/")
    Call<mediaResponse> Photo(@Path("photoid") Integer photoid);

    @GET("/api/query/{id}/")
    Call<QueryItem> QueryItemRequest(@Path("id") Integer id);

}