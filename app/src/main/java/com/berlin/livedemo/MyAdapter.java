package com.berlin.livedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by AnHuiNews on 2017/4/25.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {
    private List<String> list;

    private Context context;

    public MyAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_mes,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        private TextView textView;

        public Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }
    }
}
