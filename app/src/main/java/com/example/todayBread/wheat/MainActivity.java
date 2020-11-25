package com.example.todayBread.wheat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todayBread.R;
import com.example.todayBread.wangjue.StaticInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private LinearLayout contentLayout;
    private ScrollView scrollView;
    private ProgressBar progressBar;

    private boolean pullAnnounceLock = false;   // the lock of the methods "pullAnnounce"
    private DataOutlet outlet;

    // numeric resource
    private int batchSize;
    private float pullThresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentLayout = findViewById(R.id.linearLayout);
        progressBar = new ProgressBar(this);
        scrollView = findViewById(R.id.scrollView);
        JSONArray jsonArray = Utils.fileToJSONArray(getResources(), "metadata.json");
        outlet = new DataOutlet(new IteratorConvert.JSONArrayIterator(jsonArray),
                (values) -> createAnnounce((JSONObject) values));

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> tryPullAnnounces());
        tryPullAnnounces();

        batchSize = getResources().getInteger(R.integer.batchSize);
        pullThresh = getResources().getFloat(R.dimen.pullThresh);

        Log.e("onCreate", "MainActivity:" + this.toString() + " on Thread " + Thread.currentThread().getId());
    }

    public void tryPullAnnounces(){
        if (!outlet.empty() && !pullAnnounceLock &&
                contentLayout.getHeight() - scrollView.getHeight() * (1f + pullThresh) - scrollView.getScrollY() <= 0) {
            Log.e("pullAnnounces", "aa");
            pullAnnounceLock = true;
            runOnUiThread(() -> contentLayout.addView(progressBar));
            outlet.asyncGetBatch(batchSize, (announces) -> runOnUiThread(() -> {
                contentLayout.removeView(progressBar);
                for (Object announce : announces) addAnnounce((Announce) announce);
                pullAnnounceLock = false;
                tryPullAnnounces();
            }));
        }
    }

    public Announce createAnnounce(JSONObject values){
        int type = 0;
        try {
            type = values.getInt("type");
        } catch (JSONException e) {
            Log.e("addAnnounce", "Cannot determine type of the announce. Use default type(" + type + ")");
        }
        Announce announce = AnnounceFactory.createAnnounce(this, type);
        if (announce != null) {
            announce.setLayoutParams(Announce.params(Announce.match_parent, Announce.wrap_content, 0, null));
            announce.SetValues(values);
            announce.mainActivity = this;

            announce.setOnClickListener(v -> {
                announce.title.setTextColor(getResources().getColor(R.color.colorMarkRead, null));
                StaticInterface.askArticle(this, values);
                });

        }
        else
            Log.e("addAnnounce", "Unsupported announce type id:" + type);
        return announce;
    }

    public void addAnnounce(Announce announce) {
        if (announce != null)
            contentLayout.addView(announce);
    }

}
