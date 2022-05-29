package com.mustafakaya.fiform;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mustafakaya.fiform.databinding.ActivityMainBinding;
import com.mustafakaya.fiform.ui.faculties.ArchitectureActivity;
import com.mustafakaya.fiform.ui.faculties.ArtsActivity;
import com.mustafakaya.fiform.ui.faculties.DentistryActivity;
import com.mustafakaya.fiform.ui.faculties.EconomicsActivity;
import com.mustafakaya.fiform.ui.faculties.EducationActivity;
import com.mustafakaya.fiform.ui.faculties.EngineerActivity;
import com.mustafakaya.fiform.ui.faculties.HealthActivity;
import com.mustafakaya.fiform.ui.faculties.LawActivity;
import com.mustafakaya.fiform.ui.faculties.PharmacyActivity;
import com.mustafakaya.fiform.view.MyNotesActivity;
import com.mustafakaya.fiform.view.NotificationsActivity;
import com.mustafakaya.fiform.view.ShareNoteActivity;
import com.mustafakaya.fiform.view.SignInPage;
import com.mustafakaya.fiform.view.StartPageActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    DrawerLayout drawer;
    NavigationView navigationView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    CardView cardView,cardView2,cardView3,cardView4,cardView5,cardView6,cardView7,cardView8,cardView9;
    TextView navUserName,navUserEmail,not_verify_text,profile_image_text;
    AppCompatButton email_verification_btn;
    EditText feedback_text;
    Button send_btn,ok_btn,yes_log_out,no_log_out;
    TextView emailBodyTextView;
    View dialogView;
    ImageView close_feedback;
    String emailBody,appEmail="yazilimcimustafa33@gmail.com";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isOnline();

        //Add ID
        //ca-app-pub-3547612698000344~7945217247

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        cardView = findViewById(R.id.card_view);
        cardView2 = findViewById(R.id.card_view2);
        cardView3 = findViewById(R.id.card_view3);
        cardView4 = findViewById(R.id.card_view4);
        cardView5 = findViewById(R.id.card_view5);
        cardView6 = findViewById(R.id.card_view6);
        cardView7 = findViewById(R.id.card_view7);
        cardView8 = findViewById(R.id.card_view8);
        cardView9 = findViewById(R.id.card_view9);
        not_verify_text = findViewById(R.id.not_verify_text);
        email_verification_btn = findViewById(R.id.email_verify_button);


        if(!(firebaseUser.isEmailVerified())){
            email_verification_btn.setVisibility(View.VISIBLE);
            not_verify_text.setVisibility(View.VISIBLE);
        }

        email_verification_btn.setOnClickListener(view -> {
            if(!(firebaseUser.isEmailVerified())){
                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,SignInPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(e -> Log.d(TAG,"onFailure: Email not sent " + e.getLocalizedMessage()));
            }
        });

        setSupportActionBar(binding.appBarMain.toolbar);

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        updateNavHeader();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_share_note,R.id.nav_my_note,R.id.nav_feedback,R.id.nav_rate)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                log_out_clicked();

            }else if(item.getItemId() == R.id.nav_share_note){
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, ShareNoteActivity.class);
                startActivity(intent);

            }else if(item.getItemId() == R.id.nav_my_note){
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, MyNotesActivity.class);
                startActivity(intent);

            }else if(item.getItemId() == R.id.nav_rate){
                //Go to PlayStore
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                Toast.makeText(this, "You are going to Google Play", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
            }else if(item.getItemId() == R.id.nav_feedback){
                drawer.closeDrawers();
                Handler handler = new Handler();
                handler.postDelayed(() -> feedback(), 500);

            }else {
                NavigationUI.onNavDestinationSelected(item, navController);
                drawer.closeDrawers();
            }
            return false;
        });

        cardView.setOnClickListener(MainActivity.this);
        cardView2.setOnClickListener(MainActivity.this);
        cardView3.setOnClickListener(MainActivity.this);
        cardView4.setOnClickListener(MainActivity.this);
        cardView5.setOnClickListener(MainActivity.this);
        cardView6.setOnClickListener(MainActivity.this);
        cardView7.setOnClickListener(MainActivity.this);
        cardView8.setOnClickListener(MainActivity.this);
        cardView9.setOnClickListener(MainActivity.this);

    }
    
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_view:
                startActivity(new Intent(MainActivity.this,EngineerActivity.class));
                break;
            case R.id.card_view2:
                startActivity(new Intent(MainActivity.this,LawActivity.class));
                break;
            case R.id.card_view3:
                startActivity(new Intent(MainActivity.this, EducationActivity.class));
                break;
            case R.id.card_view4:
                startActivity(new Intent(MainActivity.this, ArtsActivity.class));
                break;
            case R.id.card_view5:
                startActivity(new Intent(MainActivity.this, HealthActivity.class));
                break;
            case R.id.card_view6:
                startActivity(new Intent(MainActivity.this, EconomicsActivity.class));
                break;
            case R.id.card_view7:
                startActivity(new Intent(MainActivity.this, ArchitectureActivity.class));
                break;
            case R.id.card_view8:
                startActivity(new Intent(MainActivity.this, PharmacyActivity.class));
                break;
            case R.id.card_view9:
                startActivity(new Intent(MainActivity.this, DentistryActivity.class));
                break;
        }
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View viewHeader = navigationView.getHeaderView(0);
        navUserName = viewHeader.findViewById(R.id.userNameData);
        navUserEmail = viewHeader.findViewById(R.id.userEmailData);
        profile_image_text = viewHeader.findViewById(R.id.profile_image_text);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(firebaseUser.getEmail())){
                        navUserName.setText(ds.child("name").getValue(String.class));
                        navUserEmail.setText(ds.child("email").getValue(String.class));
                        profile_image_text.setText(ds.child("name").getValue(String.class).substring(0,2).toUpperCase());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            LayoutInflater inflater = this.getLayoutInflater();
            dialogView = inflater.inflate(R.layout.no_connection, null);

            Dialog dialog = new Dialog(this);
            dialog.setContentView(dialogView);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            dialog.setCancelable(false);

            ok_btn = dialogView.findViewById(R.id.ok_button);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                    System.exit(0);
                }
            });
            return false;
        }
        return true;
    }

    public void feedback(){

        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.feedback, null);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        send_btn = dialogView.findViewById(R.id.feedback_send_btn);
        feedback_text = dialogView.findViewById(R.id.feedback_edit_text);

        close_feedback = dialogView.findViewById(R.id.close_feedback);
        close_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                if(!(feedback_text.getText().toString().equals(""))){

                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Going to Gmail...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    //Send mail
                    firebaseUser = auth.getCurrentUser();

                    emailBodyTextView =  dialogView.findViewById(R.id.feedback_edit_text);
                    emailBody = emailBodyTextView.getText().toString();

                    final Intent send = new Intent(Intent.ACTION_SENDTO);
                    final String email = appEmail;
                    final String subject = "Feedback";
                    final String uriText = "mailto:" + Uri.encode(email) +
                            "?subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(emailBody);
                    final Uri uri = Uri.parse(uriText);

                    send.setData(uri);

                    try {
                        startActivity(Intent.createChooser(send,appEmail));
                        progressDialog.dismiss();
                        dialog.dismiss();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Enter your feedback!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notifications_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_notification);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_notification){
            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void log_out_clicked(){
        drawer.closeDrawers();
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.log_out_dialog, null);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        yes_log_out = dialogView.findViewById(R.id.yes_log_out);
        no_log_out = dialogView.findViewById(R.id.no_log_out);

        yes_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
                startActivity(intent);
            }
        });

        no_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
