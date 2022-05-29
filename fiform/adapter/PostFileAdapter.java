package com.mustafakaya.fiform.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafakaya.fiform.databinding.RecyclerRowBinding;
import com.mustafakaya.fiform.model.PostFile;
import com.mustafakaya.fiform.view.ReadPdfActivity;

import java.util.ArrayList;

public class PostFileAdapter extends RecyclerView.Adapter<PostFileAdapter.PostHolder>{

    private ArrayList<PostFile> postFileArrayList;
    private Context context;


    public PostFileAdapter(ArrayList<PostFile> postFileArrayList,Context context) {

        this.postFileArrayList = postFileArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context = parent.getContext();
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.recyclerRowBinding.recyclerViewPdfName.setText(postFileArrayList.get(position).pdfName);
        holder.recyclerRowBinding.recyclerViewPdfName.setSelected(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnline()){
                    Intent intent = new Intent(holder.itemView.getContext(), ReadPdfActivity.class);
                    intent.putExtra("pdfname",holder.recyclerRowBinding.recyclerViewPdfName.getText().toString());
                    intent.putExtra("readpdf",holder.getAdapterPosition());
                    intent.putExtra("fileurl", postFileArrayList.get(position).downloadUrl);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return postFileArrayList.size();
    }

    public void filteredList(ArrayList<PostFile> filteredList){
        postFileArrayList = filteredList;
        notifyDataSetChanged();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(context, "No connection!", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        return true;
    }
}
