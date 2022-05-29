package com.mustafakaya.fiform.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.adapter.PDFAdapter;
import com.mustafakaya.fiform.databinding.ActivityMyNotesBinding;
import com.mustafakaya.fiform.model.PostFile;

import java.util.ArrayList;
import java.util.Map;

public class MyNotesActivity extends AppCompatActivity {

    private ActivityMyNotesBinding binding;
    TextView profile_username, profile_user_email,sharedCountText,profile_image_text2;
    AppCompatButton save_btn;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private PDFAdapter pdfAdapter;
    ArrayList<PostFile> postFileArrayList;
    private StorageReference storageReference;
    String username;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyNotesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postFileArrayList = new ArrayList<>();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        profile_username = findViewById(R.id.profile_username);
        profile_user_email = findViewById(R.id.profile_user_email);
        sharedCountText = findViewById(R.id.sharedCountText);

        update_profile();
        getDataProfile();
        setProfileImage();

        binding.profileRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        pdfAdapter = new PDFAdapter(postFileArrayList);
        binding.profileRecyclerView.setAdapter(pdfAdapter);
    }

    public void update_profile(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(firebaseUser.getEmail())){
                        profile_username.setText(ds.child("name").getValue(String.class));
                        profile_user_email.setText(ds.child("email").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getDataProfile(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(MyNotesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Map<String,Object> data = snapshot.getData();
                    String pdfName = (String) data.get("pdfname");
                    String downloadUrl = (String) data.get("downloadurl");
                    String userEmail = (String) data.get("email");

                    if (firebaseUser.getEmail().equals(userEmail)){
                        count++;
                        PostFile postFile = new PostFile(userEmail,pdfName,downloadUrl);
                        postFileArrayList.add(postFile);
                        sharedCountText.setText("You shared " + count + " PDF");
                    }
                }

                pdfAdapter.notifyDataSetChanged();

            }
        });
    }

    public void setProfileImage(){
        profile_image_text2 = findViewById(R.id.profile_image_text2);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(firebaseUser.getEmail())){
                        profile_image_text2.setText(ds.child("name").getValue(String.class).substring(0,2).toUpperCase());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem menuItem2 = menu.findItem(R.id.goToShare);

        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MyNotesActivity.this, ShareNoteActivity.class));
                return true;
            }
        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String newText) {
        ArrayList<PostFile> filteredList = new ArrayList<>();
        for(PostFile item : postFileArrayList){
            if(item.pdfName.toLowerCase().contains(newText)){
                filteredList.add(item);
            }
        }
        pdfAdapter.filteredList(filteredList);
    }
}