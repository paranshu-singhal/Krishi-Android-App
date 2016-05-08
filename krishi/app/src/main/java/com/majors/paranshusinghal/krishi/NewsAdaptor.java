package com.majors.paranshusinghal.krishi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NewsAdaptor extends RecyclerView.Adapter<NewsAdaptor.NewsViewHolder> implements RecyclerView.OnClickListener{

    private List<newselement> news;
    private int post;
    private Context ctx;

    public NewsAdaptor(List<newselement> news, Context context) {
        this.news = news;
        this.ctx = context;
    }

    @Override
    public NewsAdaptor.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listview_raw, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsAdaptor.NewsViewHolder holder, int position) {
        holder.title.setText(news.get(position).getTitle());
        holder.desc.setText(news.get(position).getDescription());
        holder.pubDate.setText(news.get(position).getPubDate());
        post = position;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("link", news.get(post).getLink());
        Intent intent = new Intent(ctx, web_view.class);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView title,desc,pubDate;
        public NewsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(NewsAdaptor.this);
            title = (TextView)itemView.findViewById(R.id.news_title);
            desc  = (TextView)itemView.findViewById(R.id.news_description);
            pubDate=(TextView)itemView.findViewById(R.id.news_pubDate);
        }
    }

}
