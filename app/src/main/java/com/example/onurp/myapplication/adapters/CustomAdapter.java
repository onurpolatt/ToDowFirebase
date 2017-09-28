package com.example.onurp.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.onurp.myapplication.R;
import com.example.onurp.myapplication.Tasks;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onurp on 25.08.2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<Tasks> task;
    public Context mContext;

    public CustomAdapter(Context context,ArrayList<Tasks> task) {
        this.task = task;
        this.mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content_headers, parent, false);
            return new SectionViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content, parent, false);
            return new RowViewHolder(v);
        }
    }

    public class RowViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txtContent)
        TextView tContent;
        @BindView(R.id.endDate)
        TextView tDate;

        public RowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txtSection)
        TextView sectionHeader;
        public SectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        Tasks item = task.get(position);
        return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tasks tasks=task.get(position);
        RowViewHolder h = (RowViewHolder) holder;
        h.tContent.setText(tasks.getContent());
        h.tDate.setText(tasks.getEndDate());
    }

    @Override
    public int getItemCount() {
        return task.size();
    }
}