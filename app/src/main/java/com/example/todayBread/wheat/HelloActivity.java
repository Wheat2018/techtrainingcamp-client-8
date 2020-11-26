package com.example.todayBread.wheat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todayBread.R;
import com.example.todayBread.wangjue.StaticInterface;
import com.example.todayBread.wheat.Utils.Utils;

public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        new Thread(() -> {
            try {
                flyingDog();
            } catch (InterruptedException e) {
                Log.e("HelloActivityOnCreate", e.toString());
            }
            StaticInterface.askLogin(null, false);
            //startActivity(new Intent(HelloActivity.this, MainActivity.class));
            finish();
        }).start();
    }

    /**
     * 面包超狗动画。
     */
    private void flyingDog() throws InterruptedException {
        final ImageView flyingDog = findViewById(R.id.flyingDog);
        final int dogWidth = Utils.dp2px(getResources(), 220);
        final int dogHeight = Utils.dp2px(getResources(), 230);

        Thread.sleep(300);

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
            Thread.sleep(3);
        }
        Thread.sleep(500);
    }
}