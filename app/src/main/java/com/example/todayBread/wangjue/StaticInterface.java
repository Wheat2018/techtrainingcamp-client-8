package com.example.todayBread.wangjue;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

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

public class StaticInterface {
    public static boolean askArticle(Activity activity, JSONObject values) {
        //这里的判断条件改成是否已登录
        if(!UserInfo.USER.getLoginState()){
            Intent in = new Intent(activity,LoginActivity.class);
            try {
                in.putExtra("id",values.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            activity.startActivity(in);
        }
        else {
            Intent in = new Intent(activity, ImageTextActivity.class);
            try {
                in.putExtra("id",values.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            activity.startActivity(in);
        }
        return true;
    }

    public static boolean askLogin(Activity activity, boolean display) {
        if(display){
            Intent in = new Intent(activity,LoginActivity.class);
            in.putExtra("id"," ");
            activity.startActivity(in);
            return true;
        }
        if(UserInfo.USER.getUsername().equals("")||UserInfo.USER.getPassword().equals("")){
            return false;
        }
        post();
        return true;
    }

    public static void post() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Map m= new HashMap();
        m.put("username",UserInfo.USER.getUsername());
        m.put("password",UserInfo.USER.getPassword());
        JSONObject json = new JSONObject(m);
        String jsonstr = json.toString();
        String url="https://vcapi.lvdaqian.cn/login";
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
                String token=response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(token);
                    token = jsonObject.getString("token");
                    UserInfo.USER.setInfo(UserInfo.USER.getUsername(),UserInfo.USER.getPassword(),token,null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

