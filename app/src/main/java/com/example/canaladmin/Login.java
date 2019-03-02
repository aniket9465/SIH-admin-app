package com.example.canaladmin;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity{
    static Retrofit retrofit;
    EditText UsernameText;
    EditText PasswordText;
    Button SubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        findViewById(R.id.clear_focus).requestFocus();
        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        ((ProgressBar)findViewById(R.id.progress)).getIndeterminateDrawable().setColorFilter(0xFF008577, android.graphics.PorterDuff.Mode.MULTIPLY);
        UsernameText = findViewById(R.id.Username);
        PasswordText = findViewById(R.id.Password);
        SubmitButton = findViewById(R.id.Submit);
        SubmitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitButton.setEnabled(false);
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                if(UsernameText.getText().toString().equals("")||PasswordText.getText().toString().equals(""))
                    return;
                ApiInterface api_service = getRetrofitInstance(getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
                LoginRequestBody credentials = new LoginRequestBody(UsernameText.getText().toString(), PasswordText.getText().toString());
                Call<LoginResponse> call = api_service.login(credentials);
                Log.d("", call.request().url() + "");
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.body() == null) {
                            Toast.makeText(Login.this, "wrong credentials :(", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(Login.this, MainActivity.class);
                            startActivity(in);
                            finish();
                        } else {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("canaladmin", 0);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("username",UsernameText.getText()+"" );
                            edit.apply();
                            Intent in = new Intent(Login.this, MainActivity.class);
                            startActivity(in);
                            finish();

                        }

                        SubmitButton.setEnabled(true);
                        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(Login.this, "connection issue", Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(Login.this, MainActivity.class);
                        startActivity(in);
                        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                        SubmitButton.setEnabled(true);
                        finish();
                    }
                });
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
