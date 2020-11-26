package com.example.todayBread.wheat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.todayBread.R;
import com.example.todayBread.wangjue.StaticInterface;
import com.example.todayBread.wheat.Utils.ImageUtils;
import com.example.todayBread.wheat.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private LinearLayout contentLayout;
    private NestedScrollView scrollView;
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
        // start hello page
        startActivity(new Intent(MainActivity.this, HelloActivity.class));

        // numeric resource assignment
        batchSize = getResources().getInteger(R.integer.batchSize);
        pullThresh = getResources().getFloat(R.dimen.pullThresh);

        // private symbol assignment
        contentLayout = findViewById(R.id.linearLayout);
        progressBar = new ProgressBar(this);
        scrollView = findViewById(R.id.scrollView);
        JSONArray jsonArray = Utils.fileToJSONArray(getResources(), "metadata.json");
        outlet = new DataOutlet(new IteratorConvert.JSONArrayIterator(jsonArray),
                (values) -> createAnnounce((JSONObject) values),
                getResources().getInteger(R.integer.dataOutletCapacity));

        // content auto pulling
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> tryPullAnnounces());
        contentLayout.post(this::tryPullAnnounces);

        // head portrait
        UserInfo.USER.setInfoChangeListener(() -> runOnUiThread(() -> {
            if (UserInfo.USER.getLoginState()) {
                ((ImageView)findViewById(R.id.portraitView)).setImageBitmap(ImageUtils.roundBitmap(
                        ImageUtils.safeLoadBitmap(getResources(), "portraitLogin.png")));
                String show = "无名";
                String username = UserInfo.USER.getUsername();
                if (username != null && !username.isEmpty()){
                    String[] letters = username.split(" ");
                    if (letters.length >= 2) show = "" + letters[0].charAt(0) + letters[1].charAt(0);
                    else if (letters.length == 1) show = "" + letters[0].charAt(0) + letters[0].charAt(1);
                }
                ((TextView)findViewById(R.id.portraitText)).setText(show);
            }
            else {
                ((ImageView)findViewById(R.id.portraitView)).setImageBitmap(ImageUtils.roundBitmap(
                        ImageUtils.safeLoadBitmap(getResources(), "portrait.png")));
                ((TextView)findViewById(R.id.portraitText)).setText("");
            }
        }));
        UserInfo.USER.onInfoChange();
        findViewById(R.id.portraitView).setOnClickListener(v -> {
            if (UserInfo.USER.getLoginState()){
                new AlertDialog.Builder(this)
                        .setTitle("退出确认")
                        .setMessage("是否退出当前账号？")
                        .setPositiveButton("确认退出", (dialog, which) -> {
                            UserInfo.USER.logout();
                            StaticInterface.askLogin(this, true);
                        }).setNegativeButton("取消", null)
                        .show();
            }
            else StaticInterface.askLogin(this, true);
        });
    }

    /**
     * 尝试拉取公告内容。仅当下列三个条件同时满足时才拉取公告：1.上次拉取操作已结束；2.公告内容池中还有剩余内容；3.当前显示区与底部距离小于阈值。
     */
    public void tryPullAnnounces(){
        if (!outlet.empty() && !pullAnnounceLock &&
                contentLayout.getHeight() - scrollView.getHeight() * (1f + pullThresh) - scrollView.getScrollY() <= 0) {
            pullAnnounceLock = true;
            runOnUiThread(() -> contentLayout.addView(progressBar));
            outlet.asyncGetBatch(batchSize, (announces) -> runOnUiThread(() -> {
                contentLayout.removeView(progressBar);
                for (Object announce : announces) addAnnounce((Announce) announce);
                contentLayout.post(() -> {
                    pullAnnounceLock = false;
                    tryPullAnnounces();
                });
            }));
        }
    }

    /**
     * 创建公告内容项，实例继承自LinearLayout。
     * @param values 内容值。
     * @return 公告项实例。
     */
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
