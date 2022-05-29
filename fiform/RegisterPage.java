package com.mustafakaya.fiform;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mustafakaya.fiform.databinding.ActivityRegisterPageBinding;
import com.mustafakaya.fiform.model.Users;
import com.mustafakaya.fiform.view.SignInPage;

public class RegisterPage extends AppCompatActivity {

    private ActivityRegisterPageBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        auth = FirebaseAuth.getInstance();

    }

    public void loginClicked(View view){
        Intent intent = new Intent(RegisterPage.this, SignInPage.class);
        startActivity(intent);
    }


    public void registerBtnClicked(View view){

        String email = binding.userEmailEditText.getText().toString();
        String password = binding.userPasswordEditText.getText().toString();
        String userNameData =  binding.usernameEditText.getText().toString();

        if(!(email.equals("")) && !(password.equals("")) && !(userNameData.equals(""))){
            final LoadingDialog loadingDialog = new LoadingDialog(RegisterPage.this);
            loadingDialog.showLoading();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.disMiss();
                }
            },7000);
        }

        if(email.equals("") || password.equals("") || userNameData.equals("")){
            Toast.makeText(this, "Be sure to fill in the blanks!", Toast.LENGTH_LONG).show();
        }else{
            user = new Users(userNameData,email,password);
            db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference(Users.class.getSimpleName());
            databaseReference.push().setValue(user);

            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterPage.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"onFailure: Email not sent " + e.getLocalizedMessage());
                        }
                    });
                        Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterPage.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}