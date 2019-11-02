package com.wallsplash.bdapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


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
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
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
}
