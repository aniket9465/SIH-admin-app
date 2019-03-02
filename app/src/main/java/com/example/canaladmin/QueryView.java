package com.example.canaladmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QueryView extends AppCompatActivity {
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_view);
        Intent i=getIntent();
        Integer id=i.getIntExtra("id",-1);

        ApiInterface api_service = getRetrofitInstance(getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);

        Call<QueryItem> call = api_service.QueryItemRequest(id);
        call.enqueue(new Callback<QueryItem>() {
            @Override
            public void onResponse(Call<QueryItem> call, Response<QueryItem> response) {
                ((TextView)findViewById(R.id.query_text)).setText(response.body().getText());
                ((TextView)findViewById(R.id.id)).setText("QUERY : "+response.body().getId());

            }

            @Override
            public void onFailure(Call<QueryItem> call, Throwable t) {

            }
        });



    }
    public static Retrofit getRetrofitInstance(String base_url,Retrofit retrofit) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}