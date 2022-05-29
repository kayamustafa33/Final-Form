package com.mustafakaya.fiform.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mustafakaya.fiform.MainActivity;
import com.mustafakaya.fiform.R;

public class LottieActivity extends AppCompatActivity {

    private Handler handler;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView anim_text;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);


        anim_text = findViewById(R.id.splashScreenText);
        animation = AnimationUtils.loadAnimation(getApplicationContext()
        ,R.anim.textview_animation);

        handler = new Handler();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(user != null){
                    Intent intent = new Intent(LottieActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(LottieActivity.this,StartPageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        handler.postDelayed(runnable,7000);

        anim_text.setAnimation(animation);
    }
}