package com.example.canaladmin;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.notice_view_holder> {

    private ArrayList<QueryItem> list;
    private Context context;
    private static Retrofit retrofit;

    ListAdapter(ArrayList<QueryItem> list, Context c) {
        this.list=new ArrayList<>();
        if(list!=null)
            this.list = list;
        this.context=c;

    }

    public void notifyData(ArrayList<QueryItem> myList) {
        list=myList;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ListAdapter.notice_view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.query_card, parent, false);
        return new ListAdapter.notice_view_holder(v);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onBindViewHolder(@NonNull final ListAdapter.notice_view_holder holder, final int position) {
        holder.createdOn.setText(list.get(position).getCreatedAt());
        holder.queryText.setText(list.get(position).getText());
        ApiInterface api_service = getRetrofitInstance(context.getResources().getString(R.string.base_url), retrofit).create(ApiInterface.class);
        if(list.get(position).getMedia().size()==0)
            return;
        Call<mediaResponse> call = api_service.Photo(list.get(position).getMedia().get(0));
        call.enqueue(new Callback<mediaResponse>() {
                @Override
                public void onResponse(Call<mediaResponse> call, Response<mediaResponse> response) {
                    if(response.body()!=null)
                    {
                        String s=response.body().getImage();
                        String url=context.getResources().getString(R.string.base_url);
                        int fl=0;
                        for(int i=3;i<s.length();++i)
                        {
                            if(fl==1)
                            url=url+s.charAt(i);
                            if(fl==0&&s.charAt(i)=='0'&&s.charAt(i-1)=='0'&&s.charAt(i-2)=='0')
                                fl=1;
                        }
                        Log.d("------------->",url);
                        final String ss=url;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL urll = new URL(ss);

                                    Bitmap bmp = BitmapFactory.decodeStream(urll.openConnection().getInputStream());
                                    holder.image.setImageBitmap(bmp);
                                }
                                catch (Exception e){

                                    Log.d("",e.toString());
                                }

                            }
                        }).start();
                    }
                }

                @Override
                public void onFailure(Call<mediaResponse> call, Throwable t) {
                }
        });
        View.OnClickListener onc=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,QueryView.class);
                i.putExtra("id",list.get(position).getId());
                context.startActivity(i);
            }
        };
        holder.card.setOnClickListener(onc);
        holder.createdOn.setOnClickListener(onc);
        holder.queryText.setOnClickListener(onc);
        holder.image.setOnClickListener(onc);
    }

    class notice_view_holder extends RecyclerView.ViewHolder {
        TextView queryText;
        TextView createdOn;
        ImageView image;
        View card;

        notice_view_holder(View parent) {
            super(parent);
            this.card=parent;
            this.queryText=parent.findViewById(R.id.query_text);
            this.createdOn=parent.findViewById(R.id.created_on);
            this.image=parent.findViewById(R.id.image);
        }
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
