package com.example.wheattest;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static class FlipQuitListener extends GestureDetector.SimpleOnGestureListener{
        Activity activity;
        public FlipQuitListener(Activity activity){
            this.activity = activity;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x = e2.getX() - e1.getX();
            float y = Math.abs(e2.getY() - e1.getY());
            if (x > Utils.screenWidth(activity.getResources()) / 4.0 && Math.abs(y / x) < 0.577){
                activity.finish();
                return true;
            }
            return false;
        }
    }

    public static int dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Resources resources, float px) {
        final float scale = resources.getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int screenWidth(Resources resources){
        return resources.getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(Resources resources){
        return resources.getDisplayMetrics().heightPixels;
    }

    public static Bitmap getLoadFailBitmap(Resources resources)
    {
        int picId = resources.getIdentifier("android:drawable/ic_menu_report_image",
                null, null);
        return BitmapFactory.decodeStream(resources.openRawResource(picId));
    }

    public static Bitmap loadBitmap(@NotNull Resources resources, String fileName) throws IOException {
        try (InputStream is = resources.getAssets().open(fileName)) {
            return BitmapFactory.decodeStream(is);
        }
    }

    public static Bitmap loadScaleBitmap(@NotNull Resources resources, String fileName,
                                         int width, int height) throws IOException {
        Bitmap bitmap = loadBitmap(resources, fileName);
        return Utils.scaleMatrix(bitmap, width, height);
    }

    public static Bitmap scaleMatrix(@NotNull Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = (float) width / w;
        float scaleH = (float) height / h;
        if (width <= 0) scaleW = scaleH;
        if (height <= 0) scaleH = scaleW;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public static String fileToString(Resources resources, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream is = resources.getAssets().open(fileName)) {
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e("fileToString", e.toString());
        }
        return stringBuilder.toString();
    }

    public static JSONArray fileToJSONArray(Resources resources, String fileName) {
        try {
            return new JSONArray(fileToString(resources, fileName));
        } catch (JSONException e) {
            Log.e("fileToJSONObject", e.toString());
        }
        return null;
    }

    public static Map<String, String> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        Iterator<String> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = it.next();
            String value = jsonObject.optString(key);
            map.put(key, value);
        }
        return map;
    }

    public static String getToken(String username, String password) throws IOException, JSONException {
        URL url = new URL("https://vcapi.lvdaqian.cn/login");
        HttpsURLConnection connection = null;
        try{
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream()))
            {
                dos.writeBytes("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\"}");
                dos.flush();
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())))
            {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                JSONObject object = new JSONObject(builder.toString());
                return  object.getString("token");
            }

        }finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static String getArticle(final String id) throws IOException, JSONException {
        String token = getToken("string", "string");
        Log.e("getArticle", token);

        URL url = new URL("https://vcapi.lvdaqian.cn/article/" + id + "?markdown=true");
        HttpsURLConnection connection = null;
        try{
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())))
            {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                JSONObject object = new JSONObject(builder.toString());
                return object.getString("data");
            }

        }finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static void newThreadRunOnUI(final Activity activity, final Runnable runnable)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(runnable);
            }
        }).start();
    }
}