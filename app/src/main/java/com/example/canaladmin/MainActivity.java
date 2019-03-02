package com.example.canaladmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ArrayList<QueryItem> pendingList = new ArrayList<>();
    private ArrayList<QueryItem> resolvedList = new ArrayList<>();
    private ArrayList<QueryItem> assignedList = new ArrayList<>();
    private ArrayList<QueryItem> mineList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainerAs;
    private SwipeRefreshLayout swipeContainerpe;
    private SwipeRefreshLayout swipeContainerre;
    private RecyclerView recyclerviewpending;
    private RecyclerView recyclerviewresolved;
    private RecyclerView recyclerviewassigned;
    private ListAdapter adapterpending;
    private ListAdapter adapterresolved;
    private ListAdapter adaptermine;
    private ListAdapter adapterassigned;
    private Retrofit retrofit;
    private String request = "";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.pending: {
                    findViewById(R.id.swipeContainerAs).setVisibility(View.INVISIBLE);
                    findViewById(R.id.swipeContainerpe).setVisibility(View.VISIBLE);
                    findViewById(R.id.swipeContainerre).setVisibility(View.INVISIBLE);
                    request = "pending";
                    return true;
                }
                case R.id.assigned: {
                    findViewById(R.id.swipeContainerAs).setVisibility(View.VISIBLE);
                    findViewById(R.id.swipeContainerpe).setVisibility(View.INVISIBLE);
                    findViewById(R.id.swipeContainerre).setVisibility(View.INVISIBLE);
                    request = "assigned";
                    return true;
                }
                case R.id.resolved: {
                    findViewById(R.id.swipeContainerAs).setVisibility(View.INVISIBLE);
                    findViewById(R.id.swipeContainerpe).setVisibility(View.INVISIBLE);
                    findViewById(R.id.swipeContainerre).setVisibility(View.VISIBLE);
                    request = "ressolved";
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        swipeContainerre = findViewById(R.id.swipeContainerre);
        swipeContainerre.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(".............","1");
                resolvedList.clear();
                queryRequest();
            }
        });
        swipeContainerre.setColorSchemeResources(android.R.color.holo_blue_dark);
        swipeContainerAs = findViewById(R.id.swipeContainerAs);
        swipeContainerAs.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                assignedList.clear();
                queryRequest();
                Log.d(".............","2");

            }
        });
        swipeContainerAs.setColorSchemeResources(android.R.color.holo_blue_dark);
        swipeContainerpe = findViewById(R.id.swipeContainerpe);
        swipeContainerpe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pendingList.clear();
                queryRequest();
                Log.d(".............","3");

            }
        });
        swipeContainerpe.setColorSchemeResources(android.R.color.holo_blue_dark);

        LinearLayoutManager manager1 = new LinearLayoutManager(this.getApplicationContext());
        LinearLayoutManager manager2 = new LinearLayoutManager(this.getApplicationContext());
        LinearLayoutManager manager4 = new LinearLayoutManager(this.getApplicationContext());

        recyclerviewassigned = findViewById(R.id.assigned_list);
        recyclerviewresolved = findViewById(R.id.resolved_list);
        recyclerviewpending = findViewById(R.id.pending_list);

        recyclerviewpending.setLayoutManager(manager1);
        adapterpending = new ListAdapter(pendingList, MainActivity.this);
        recyclerviewpending.setAdapter(adapterpending);

        recyclerviewassigned.setLayoutManager(manager2);
        adapterassigned = new ListAdapter(assignedList, MainActivity.this);
        recyclerviewassigned.setAdapter(adapterassigned);


        recyclerviewresolved.setLayoutManager(manager4);
        adapterresolved = new ListAdapter(resolvedList, MainActivity.this);
        recyclerviewresolved.setAdapter(adapterresolved);

        findViewById(R.id.swipeContainerAs).setVisibility(View.INVISIBLE);
        findViewById(R.id.swipeContainerpe).setVisibility(View.VISIBLE);
        findViewById(R.id.swipeContainerre).setVisibility(View.INVISIBLE);
        queryRequest();
    }

    private void queryRequest()
    {
        ApiInterface api_service = getRetrofitInstance(getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
        Call<List<QueryItem>> call = api_service.QueryRequest("hi");
        Log.d("", call.request().url() + "");
        call.enqueue(new Callback<List<QueryItem>>() {
            @Override
            public void onResponse(Call<List<QueryItem>> call, Response<List<QueryItem>> response) {
                if(response.body()!=null) {
                    if (request.equals("pending")||request.equals("")) {
                        for(int i=0;i<response.body().size();++i)
                        {
                            if(response.body().get(i).getStatusChoice().equals("Pending"))
                            {
                                pendingList.add(response.body().get(i));
                            }
                        }
                    }
                    if (request.equals("ressolved")||request.equals("")) {
                        for(int i=0;i<response.body().size();++i)
                        {
                            if(response.body().get(i).getStatusChoice().equals("Resolved"))
                            {
                                resolvedList.add(response.body().get(i));
                            }
                        }
                    }
                    if (request.equals("assigned")||request.equals("")) {
                        for(int i=0;i<response.body().size();++i)
                        {
                            if(response.body().get(i).getStatusChoice().equals("Assigned"))
                            {
                                assignedList.add(response.body().get(i));
                            }
                        }
                    }
                    adapterpending.notifyDataSetChanged();
                    adapterresolved.notifyDataSetChanged();
                    adapterassigned.notifyDataSetChanged();
                }
                swipeContainerAs.setRefreshing(false);
                swipeContainerpe.setRefreshing(false);
                swipeContainerre.setRefreshing(false);
                if(request.equals(""))
                    request="pending";
            }

            @Override
            public void onFailure(Call<List<QueryItem>> call, Throwable t) {

                swipeContainerAs.setRefreshing(false);
                swipeContainerpe.setRefreshing(false);
                swipeContainerre.setRefreshing(false);
            }
        });
    }

    public static Retrofit getRetrofitInstance(String base_url, Retrofit retrofit) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
