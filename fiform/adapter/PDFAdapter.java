package com.mustafakaya.fiform.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.databinding.RecyclerProfileRowBinding;
import com.mustafakaya.fiform.model.PostFile;
import com.mustafakaya.fiform.view.MyNotesActivity;
import com.mustafakaya.fiform.view.ReadPdfActivity;

import java.util.ArrayList;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PostHolder> {

    private ArrayList<PostFile> postFileArrayList;
    private Context context;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;

    public PDFAdapter(ArrayList<PostFile> postFileArrayList) {

        this.postFileArrayList = postFileArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerProfileRowBinding recyclerProfileRowBinding = RecyclerProfileRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context = parent.getContext();
        return new PostHolder(recyclerProfileRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyclerProfileRowBinding.profilePdfNameText.setText(postFileArrayList.get(position).pdfName);
        holder.recyclerProfileRowBinding.profilePdfImage.setImageResource(R.drawable.newpdfimage);
        holder.recyclerProfileRowBinding.profilePdfNameText.setSelected(true);

        firebaseFirestore =FirebaseFirestore.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnline()){
                    Intent intent = new Intent(holder.itemView.getContext(), ReadPdfActivity.class);
                    intent.putExtra("pdfname",holder.recyclerProfileRowBinding.profilePdfNameText.getText().toString());
                    intent.putExtra("readpdf",holder.getAdapterPosition());
                    intent.putExtra("fileurl", postFileArrayList.get(position).downloadUrl);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.longclick_menu,popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete_option:
                                firebaseFirestore.collection("Posts")
                                        .whereEqualTo("pdfname",holder.recyclerProfileRowBinding.profilePdfNameText.getText().toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                    String documentId = documentSnapshot.getId();
                                                    firebaseFirestore.collection("Posts")
                                                            .document(documentId)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Intent intent = new Intent(holder.itemView.getContext(), MyNotesActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    holder.itemView.getContext().startActivity(intent);
                                                                    Toast.makeText(context, "Successfully deleted. ", Toast.LENGTH_SHORT).show();
                                                                    notifyDataSetChanged();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Some error occured:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                break;
                        }
                        return true;
                    }
                });
                return true;
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

        RecyclerProfileRowBinding recyclerProfileRowBinding;

        public PostHolder(RecyclerProfileRowBinding recyclerProfileRowBinding) {
            super(recyclerProfileRowBinding.getRoot());
            this.recyclerProfileRowBinding = recyclerProfileRowBinding;
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
