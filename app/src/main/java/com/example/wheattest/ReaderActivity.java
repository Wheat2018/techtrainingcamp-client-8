package com.example.wheattest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {
    private GestureDetector gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        final String articleId = getIntent().getStringExtra("id");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String article = Utils.getArticle(articleId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.textView)).setText(article);
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        gesture = new GestureDetector(this, new Utils.FlipQuitListener(this));
        ((TextView)findViewById(R.id.textView)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("onTouch", "" + event.getAction());

                gesture.onTouchEvent(event);
                (findViewById(R.id.textView)).performClick();
                return false;
            }
        });
    }


}