package com.example.todayBread.wangjue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todayBread.R;
import com.example.todayBread.wheat.UserInfo;
import com.example.todayBread.wheat.Utils.FlipQuitListener;
import com.example.todayBread.wheat.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageTextActivity extends AppCompatActivity {
    private TextView edt;
    private TextView tv;
    private String url;
    private String rts;
    private String token;
    private  Intent in;
    private String id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_text);
        in = getIntent();
        id = in.getStringExtra("id");
        edt = findViewById(R.id.edt);
        tv = findViewById(R.id.tv);
        JSONArray jsonArray = Utils.fileToJSONArray(getResources(), "metadata.json");
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String t = jsonObject.getString("id");
                if(t.equals(id)){
                    MarkdownUtils.titlePart(tv,jsonObject);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences shp1 = getSharedPreferences(id,MODE_PRIVATE);
        rts = shp1.getString("article","none");
        if(rts.equals("none"))
            get();
        else
            show();
        /** Add by wheat. 2020-11-24 **/
        gesture = new GestureDetector(this, new FlipQuitListener(this));
        /** Add by wheat. 2020-11-24 **/

    }
    /** Add by wheat. 2020-11-24 **/
    private GestureDetector gesture;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gesture.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    /** Add by wheat. 2020-11-24 **/

    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                try {
                    JSONObject jsonObject = new JSONObject(rts);
                    rts = jsonObject.getString("data");
                    SharedPreferences shp = getSharedPreferences(id,MODE_PRIVATE);
                    SharedPreferences.Editor editor = shp.edit();
                    editor.putString("article",rts);
                    boolean isSuccess = editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                show();
            }
        }
    };

    //将内容显示在界面上
    private void show() {
        LinearLayout linearLayout = findViewById(R.id.layout);
        linearLayout.removeViewAt(2);
        String[] putout =rts.split("\n"); //得到输出在屏幕上的正文字符串数组
        for(int i=0 ; i<putout.length ; i++) {
            if(putout[i].length()==0){
                edt.append("\n");
                continue;
            }
            String t = putout[i];
            //根据不同的markdown格式，进行相应处理
            if(t.contains("!")){
                MarkdownUtils.picture(t,this,edt);
            }
            if(t.contains("#")){
                if(t.contains("##")){
                    MarkdownUtils.secondTitle(t,edt);
                }
                else{
                    MarkdownUtils.firstTitle(t,edt);
                }
            }
            else if(t.contains("http")){
                MarkdownUtils.httplink(t,edt);
            }
            else if(t.contains("!")){
                continue;
            }
            else{
                edt.append(t+"\n");
            }
        }
    }

    private void get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    url = "https://vcapi.lvdaqian.cn/article/"+id+"?markdown=true";
                    OkHttpClient client = new OkHttpClient.Builder().build();
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + UserInfo.USER.getToken())
                            .get()
                            .build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    rts = response.body().string();
                    myhandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

}
