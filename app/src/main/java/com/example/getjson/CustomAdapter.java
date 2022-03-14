package com.example.getjson;


import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Test> mList;
    private LayoutInflater inflater;

    // 뷰홀더 클래스 정의.
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView job;
        protected TextView many;

        public CustomViewHolder(View view){
            super(view);
            this.name = (TextView) view.findViewById((R.id.name_listitem));
            this.job = (TextView) view.findViewById(R.id.job_listitem);
            this.many = (TextView) view.findViewById(R.id.many_listitem);
        }
    }

    public CustomAdapter(ArrayList<Test> list){
        this.mList = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test_item_list, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position){

        holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        holder.job.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        holder.many.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        holder.name.setGravity(Gravity.CENTER);
        holder.job.setGravity(Gravity.CENTER);
        holder.many.setGravity(Gravity.CENTER);

        holder.name.setText(mList.get(position).getTest1());
        holder.job.setText(mList.get(position).getTest2());
        holder.many.setText(mList.get(position).getTest3());
    }

    @Override
    public int getItemCount(){
        return (null != mList ? mList.size() : 0);
    }
}



