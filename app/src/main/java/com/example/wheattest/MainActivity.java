package com.example.wheattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Rect;
import android.os.Bundle;
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

        JSONArray jsonArray = Utils.fileToJSONArray(getResources(), "metadata.json");
        try {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                addAnnounce((JSONObject) jsonArray.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addAnnounce(JSONObject values) throws JSONException {
        int type = values.getInt("type");
        Announce announce = AnnounceFactory.createAnnounce(this, type);
        contentLayout.addView(announce);
        announce.setLayoutParams(Announce.params(Announce.match_parent, Announce.wrap_content, 0,
                new Rect(0, px(16), 0, 0)));
        announce.setPadding(px(16), 0, px(16), px(8));
        announce.SetValues(values);
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
