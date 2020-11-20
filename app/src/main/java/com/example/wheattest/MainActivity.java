package com.example.wheattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstraintLayout parentLayout = findViewById(R.id.parentLayout);

        AnnounceType1 announce = new AnnounceType1(this);
        parentLayout.addView(announce);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(px(32), px(16), px(32), px(16));
        layoutParams.topToBottom = R.id.linearLayout;
        layoutParams.startToStart = R.id.parentLayout;
        layoutParams.endToEnd = R.id.parentLayout;
        announce.setLayoutParams(layoutParams);

        try (InputStream is = getResources().getAssets().open("tb09_2.jpeg")) {
            Bitmap bitmap  = BitmapFactory.decodeStream(is);

            announce.Set("TitleTitleTitleTitle", "author", bitmap);
        } catch (IOException e) {
            Log.e("pic", e.toString());
        }

    }


    public int px(float dp) {
        final float scale = getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int)(dp * scale + 0.5f);
    }
    public int dp(float px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
