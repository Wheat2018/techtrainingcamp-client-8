package com.example.wheattest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        final String articleId = getIntent().getStringExtra("id");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String article = Utils.askArticle(articleId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.textView)).setText(article);
                    }
                });
            }
        }).start();
    }


}