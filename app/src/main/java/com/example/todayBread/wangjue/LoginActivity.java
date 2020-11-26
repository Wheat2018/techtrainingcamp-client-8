package com.example.todayBread.wangjue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todayBread.R;
import com.example.todayBread.wheat.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    private String url="https://vcapi.lvdaqian.cn/login";
    private Button bt1;
    private Button bt2;
    private String rts;
    private EditText edt1;
    private EditText edt2;
    private Intent in;
    String id;
    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                JSONObject jsonObject = new JSONObject(rts);
                rts = jsonObject.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserInfo.USER.setInfo(UserInfo.USER.getUsername(),UserInfo.USER.getPassword(),rts,null);
//            SharedPreferences shp = getSharedPreferences("token",MODE_PRIVATE);
//            SharedPreferences.Editor editor = shp.edit();
//            editor.putString("token",rts);
//            boolean isSuccess = editor.commit();
            if(id.equals(" "))
                finish();
            else {
                in = new Intent(LoginActivity.this, ImageTextActivity.class);
                in.putExtra("id",id);
                startActivity(in);
            }
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.clear);
        edt1 = (EditText) findViewById(R.id.etname);
        edt2 = (EditText) findViewById(R.id.etpass);
        in = getIntent();
        id = in.getStringExtra("id");
        if(!(UserInfo.USER.getUsername()==null)&&!(UserInfo.USER.getPassword()==null)){
            edt1.setText(UserInfo.USER.getUsername());
            edt2.setText(UserInfo.USER.getPassword());
        }
        bt1.setOnClickListener(v -> {
            post();
            ProgressBar progressBar = findViewById(R.id.pb);
            progressBar.setVisibility(View.VISIBLE);
        });
        bt2.setOnClickListener(v -> {
            UserInfo.USER.setInfo(null,null);
            edt1.setText(null);
            edt2.setText(null);
        });
    }

    private void post() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Map m= new HashMap();
        String username = edt1.getText().toString();
        String password = edt2.getText().toString();
        m.put("username",username);
        m.put("password",password);
        UserInfo.USER.setInfo(username, password);
        JSONObject json = new JSONObject(m);
        String jsonstr = json.toString();
        RequestBody req = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonstr);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("contentType","application/json;charset=utf-8")
                .post(req)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("onFailure",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                rts = response.body().string();
                //这里要加段代码，如果选项框选中了，则保存输入框中的账号密码到本地
                myhandler.sendEmptyMessage(0);
            }
        });
    }
}