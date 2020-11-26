package com.example.todayBread.wheat;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public enum UserInfo {
    USER;
    private String username = null, password = null;
    private String token = null;
    private Object[] messages = null;
    private final ArrayList<Runnable> infoChangeListener = new ArrayList<>();

    /**
     * 添加用户信息更改触发。
     * @param runnable 触发函数。
     */
    public void setInfoChangeListener(@NotNull Runnable runnable){
        infoChangeListener.add(runnable);
    }

    /**
     * 立即触发用户信息更改。
     */
    public void onInfoChange(){
        if (!infoChangeListener.isEmpty()) infoChangeListener.forEach(Runnable::run);
    }

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
        onInfoChange();
    }

    /**
     * 仅设置登录信息。该操作将退出登陆状态。
     * 推荐使用asyncLogin函数登录。
     * @param username 用户名。
     * @param password 密码。
     */
    public void setInfo(String username, String password) {
        setInfo(username, password, null, null);
    }

    /**
     * 获取登录状态。
     * @return true已登录， false未登录。
     */
    public boolean getLoginState() { return getToken() != null; }

    // Getter 访问器
    public String getPassword() { return password; }
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public Object[] getMessages() { return messages; }

    /**
     * 退出登录。
     */
    public void logout() { setInfo(getUsername(), getPassword()); }

    /**
     * 同步登录，使用用户账号和密码申请登录，在服务器响应之前，线程处于阻塞状态。
     * 登录后可调用getLoginState检查登陆状态。
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
                dos.writeBytes("{ \"username\": \"" + getUsername()
                        + "\", \"password\": \"" + getPassword() + "\"}");
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
                setInfo(getUsername(), getPassword(), object.getString("token"), getMessages());
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
