package com.wallsplash.bdapp;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.wallsplash.bdapp.wallsplash.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    TextView welcomeTextView,greetingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        initializeAll();

        initializeAnimation();

        gotoNextActivity();

    }

    private void gotoNextActivity() {
        MyThread myThread=new MyThread();
        myThread.start();
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(3500);
                if (Build.VERSION.SDK_INT >= 23) {
                    int PERMISSION_ALL = 1;
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermission(permissions)) {
                        ActivityCompat.requestPermissions(SplashActivity.this, permissions, PERMISSION_ALL);
                    } else {
                        gotoMainActivity();
                    }
                } else {
                    gotoMainActivity();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeAnimation() {
        Animation imageAnimation= AnimationUtils.loadAnimation(this,R.anim.bounce);
        Animation welcomeAnimation= AnimationUtils.loadAnimation(this,R.anim.left_to_right);
        Animation rewardAnimation= AnimationUtils.loadAnimation(this,R.anim.right_to_left);

        imageView.setAnimation(imageAnimation);
        welcomeTextView.setAnimation(welcomeAnimation);
        greetingTextView.setAnimation(rewardAnimation);
    }

    private void initializeAll(){
        imageView=findViewById(R.id.splashActivityImageViewId);
        welcomeTextView=findViewById(R.id.splashActivityWelcomeTextViewId);
        greetingTextView=findViewById(R.id.splashActivityGreetingViewId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoMainActivity();
            } else {
                Toast.makeText(this, "Permission denied, Please accept permission to download this file in your storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean hasPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void gotoMainActivity(){
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        finish();
    }
}
