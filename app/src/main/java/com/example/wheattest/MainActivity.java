package com.example.wheattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentLayout = findViewById(R.id.linearLayout);
        Log.e("onCreate", "MainActivity:" + this.toString());
        JSONArray jsonArray = Utils.fileToJSONArray(getResources(), "metadata.json");
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    addAnnounce((JSONObject) jsonArray.get(i));
                }
            }
        } catch (JSONException e) {
            Log.e("onCreate", e.toString());
        }
    }

    public void addAnnounce(JSONObject values) {
        int type = 0;
        try {
            type = values.getInt("type");
        } catch (JSONException e) {
            Log.e("addAnnounce", "Cannot determine type of the announce. Use default type(" + type + ")");
        }
        Announce announce = AnnounceFactory.createAnnounce(this, type);
        if (announce != null)
        {
            contentLayout.addView(announce);
            announce.setLayoutParams(Announce.params(Announce.match_parent, Announce.wrap_content, 0,
                    new Rect(0, px(16), 0, 0)));
            announce.setPadding(px(16), 0, px(16), px(8));
            announce.SetValues(values);
            announce.mainActivity = this;
        }
        else
            Log.e("addAnnounce", "Unsupported announce type id:" + type);
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
