package com.mustafakaya.fiform.ui.faculties;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.adapter.PostFileAdapter;
import com.mustafakaya.fiform.databinding.ActivityLawBinding;
import com.mustafakaya.fiform.model.PostFile;
import com.mustafakaya.fiform.view.ShareNoteActivity;

import java.util.ArrayList;
import java.util.Map;

public class LawActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityLawBinding binding;
    ArrayList<PostFile> postFileArrayList;
    PostFileAdapter postFileAdapter;
    TextView no_document_text8;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLawBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("Law Notes");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Banner ID
        //ca-app-pub-3547612698000344/4248642677
        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        postFileArrayList = new ArrayList<>();
        no_document_text8 = findViewById(R.id.no_document_text8);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        binding.rvLaw.setLayoutManager(new GridLayoutManager(this,3));
        postFileAdapter = new PostFileAdapter(postFileArrayList,this);
        binding.rvLaw.setAdapter(postFileAdapter);
    }

    private void getData(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(LawActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Map<String,Object> data = snapshot.getData();
                    String pdfName = (String) data.get("pdfname");
                    String faculty = (String) data.get("faculty");
                    String downloadUrl = (String) data.get("downloadurl");
                    String userEmail = (String) data.get("email");

                    if(faculty.equals("Faculty of Law")){
                        PostFile postFile = new PostFile(userEmail,pdfName,downloadUrl);
                        postFileArrayList.add(postFile);
                    }
                    if(faculty.equals("Faculty of Law")){
                        no_document_text8.setVisibility(View.INVISIBLE);
                    }
                }

                postFileAdapter.notifyDataSetChanged();

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
                startActivity(new Intent(LawActivity.this, ShareNoteActivity.class));
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
        postFileAdapter.filteredList(filteredList);
    }
}