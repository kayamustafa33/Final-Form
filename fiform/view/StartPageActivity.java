package com.mustafakaya.fiform.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mustafakaya.fiform.MainActivity;
import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.RegisterPage;

public class StartPageActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);


        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{

        }
    }

    public void loginClicked(View view){
        Intent intent = new Intent(StartPageActivity.this, SignInPage.class);
        startActivity(intent);
    }

    public void registerClicked(View view) {
        Intent intent = new Intent(StartPageActivity.this, RegisterPage.class);
        startActivity(intent);
    }
}