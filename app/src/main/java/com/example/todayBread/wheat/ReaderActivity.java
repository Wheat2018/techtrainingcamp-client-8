package com.example.todayBread.wheat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.todayBread.R;
import com.example.todayBread.wheat.Utils.FlipQuitListener;
import com.example.todayBread.wheat.Utils.Utils;

public class ReaderActivity extends AppCompatActivity {
    private GestureDetector gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        final String articleId = getIntent().getStringExtra("id");

        Utils.asyncGetArticle(articleId, (article) -> runOnUiThread(() -> {
//                findViewById(R.id.loading).clearAnimation();
            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.textView)).setText(article);
        }));

        gesture = new GestureDetector(this, new FlipQuitListener(this));
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
//        findViewById(R.id.loading).setAnimation(animation);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gesture.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}