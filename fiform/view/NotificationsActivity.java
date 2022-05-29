package com.mustafakaya.fiform.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.adapter.NotificationAdapter;
import com.mustafakaya.fiform.databinding.ActivityNotificationsBinding;
import com.mustafakaya.fiform.model.PostFile;

import java.util.ArrayList;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;
    ArrayList<PostFile> postFileArrayList;
    NotificationAdapter notificationAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView noNotif;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("Recently Added");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postFileArrayList = new ArrayList<>();

        noNotif = findViewById(R.id.no_notifications_text);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getData();

        binding.rvNotification.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(postFileArrayList);
        binding.rvNotification.setAdapter(notificationAdapter);
    }

    private void getData(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(NotificationsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Map<String,Object> data = snapshot.getData();
                    String pdfName = (String) data.get("pdfname");
                    String faculty = (String) data.get("faculty");
                    String downloadUrl = (String) data.get("downloadurl");
                    String userEmail = (String) data.get("email");

                    if(!(pdfName.equals(""))){
                        noNotif.setVisibility(View.INVISIBLE);
                        PostFile postFile = new PostFile(userEmail,pdfName,downloadUrl);
                        postFileArrayList.add(postFile);
                    }

                }

                notificationAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

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
        notificationAdapter.filteredList(filteredList);
    }
}