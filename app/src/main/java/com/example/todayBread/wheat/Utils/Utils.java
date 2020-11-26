package com.example.todayBread.wheat.Utils;

import android.content.res.Resources;
import android.util.Log;

import com.example.todayBread.wheat.UserInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * 提供常用函数或类。
 */
public class Utils {
    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static int dp2px(@NotNull Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(@NotNull Resources resources, float px) {
        final float scale = resources.getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @return 屏幕宽度像素值(px)。
     */
    public static int screenWidth(@NotNull Resources resources){
        return resources.getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @return 屏幕高度像素值(px)。
     */
    public static int screenHeight(@NotNull Resources resources){
        return resources.getDisplayMetrics().heightPixels;
    }

    /**
     * 将文件以字符串形式读入。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @param fileName 文件名。
     * @return 文件的文本内容。
     */
    public static String fileToString(@NotNull Resources resources, String fileName) {
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

    /**
     * 将文件以JSONArray的形式读入。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @param fileName 文件名。
     * @return 存储文件内容的JSONArray
     */
    public static JSONArray fileToJSONArray(Resources resources, String fileName) {
        try {
            return new JSONArray(fileToString(resources, fileName));
        } catch (JSONException e) {
            Log.e("fileToJSONObject", e.toString());
        }
        return null;
    }

    /**
     * 将JSONObject转换为Map&lt String, String&gt。
     * @param jsonObject JSONObject实例。
     * @return Map&lt String, String&gt实例。
     */
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

    /**
     * 异步获取文章，并调用回调函数。
     * @param id 文章ID。
     * @param callback 回调函数。
     */
    public static void asyncGetArticle(String id, Function<String> callback){
        new Thread(() -> {
            String article = null;
            try {
                article = getArticle(id);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            callback.run(article);
        }).start();
    }

    /**
     * 同步获取文章。
     * @param id 文章ID。
     * @return 文章文本。
     * @throws IOException 网络连接失败或请求被拒等情况下抛出。
     * @throws JSONException 返回的数据包不符合预期等情况下抛出。
     */
    public static String getArticle(final String id) throws IOException, JSONException {
        UserInfo.USER.setInfo("string", "string");
        UserInfo.USER.login();
        String token = UserInfo.USER.getToken();
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

}