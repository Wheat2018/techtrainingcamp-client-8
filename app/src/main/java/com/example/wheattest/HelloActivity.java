package com.example.wheattest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        final ImageView flyingDog = findViewById(R.id.flyingDog);
        final int dogWidth = Utils.dp2px(getResources(), 220);
        final int dogHeight = Utils.dp2px(getResources(), 230);

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(float i = 0.01f; i <= 2.0f; i += 0.01f){
                final float finalI = i;
                runOnUiThread(() -> {
                    if (finalI <= 1.0f){
                        flyingDog.setVisibility(View.VISIBLE);
                        ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams)
                                flyingDog.getLayoutParams();
                        layout.height = (int)(dogHeight * finalI);
                        layout.width = (int)(dogWidth * finalI);
                        layout.horizontalBias = 1f - 0.5f * finalI;
                        flyingDog.setLayoutParams(layout);
                        findViewById(R.id.helloTitle).setAlpha(finalI);
                    }
                    else findViewById(R.id.helloText).setAlpha(finalI - 1f);
                });
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(HelloActivity.this, MainActivity.class));
            finish();
        }).start();

    }
}