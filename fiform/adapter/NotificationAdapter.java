package com.mustafakaya.fiform.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafakaya.fiform.databinding.RecyclerNotificationBinding;
import com.mustafakaya.fiform.model.PostFile;
import com.mustafakaya.fiform.view.ReadPdfActivity;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.PostHolder> {

    private ArrayList<PostFile> postFileArrayList;

    public NotificationAdapter(ArrayList<PostFile> postFileArrayList) {

        this.postFileArrayList = postFileArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerNotificationBinding recyclerNotificationBinding = RecyclerNotificationBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerNotificationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyclerNotificationBinding.notificationPdfName.setText(postFileArrayList.get(position).pdfName);
        holder.recyclerNotificationBinding.userNameNotification.setText(postFileArrayList.get(position).email);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ReadPdfActivity.class);
                intent.putExtra("pdfname",holder.recyclerNotificationBinding.notificationPdfName.getText().toString());
                intent.putExtra("readpdf",holder.getAdapterPosition());
                intent.putExtra("fileurl", postFileArrayList.get(position).downloadUrl);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postFileArrayList.size();
    }

    public void filteredList(ArrayList<PostFile> modelArrayList){
        postFileArrayList = modelArrayList;
        notifyDataSetChanged();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerNotificationBinding recyclerNotificationBinding;

        public PostHolder(RecyclerNotificationBinding recyclerNotificationBinding) {
            super(recyclerNotificationBinding.getRoot());
            this.recyclerNotificationBinding = recyclerNotificationBinding;
        }
    }

}
