package com.example.wheattest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public enum UserInfo {
    USER;
    private String username = null, password = null;
    private String token = null;
    private Object[] messages = null;

    /**
     * 外部登录，设置用户信息。
     * @param username 用户名。
     * @param password 密码。
     * @param token 口令。
     * @param messages 其他信息，如无则传入null.
     */
    public void setInfo(String username, String password, String token, Object[] messages){
        this.username = username;
        this.password = password;
        this.token = token;
        this.messages = messages;
    }

    /**
     * 仅设置登录信息。
     * 推荐使用asyncLogin函数登录。
     * @param username 用户名。
     * @param password 密码。
     */
    public void setInfo(String username, String password){
        this.username = username;
        this.password = password;
    }

    /**
     * 获取登录状态。
     * @return true已登录， false未登录。
     */
    public boolean getLoginState(){
        return token != null;
    }

    // Getter 访问器
    public String getPassword() { return password; }
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public Object[] getMessages() { return messages; }

    /**
     * 同步登录，使用用户账号和密码申请登录，在服务器响应之前，线程处于阻塞状态。
     * 登录后调用getLoginState检查登陆状态。
     * @throws IOException URL创建或参数错误、服务器连接失败、拒绝访问等情况下抛出。
     * @throws JSONException 服务器响应数据不符合预期，如不包含"token"字段等情况下抛出。
     */
    public void login() throws IOException, JSONException {
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
                token = object.getString("token");
            }

        }finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * 异步登录。函数立即返回，并创建线程请求登录，登录结束时调用回调函数。
     * 在回调中，可调用getLoginState检查登录是否成功。
     * @param callBack 登录结束回调，可以为null。
     */
    public void asyncLogin(Runnable callBack) {
        new Thread(() -> {
            try {
                login();
            } catch (IOException | JSONException e) {
                Log.e("asyncLogin", e.toString());
            }
            if (callBack != null) callBack.run();
        });
    }
}
