package com.example.todayBread.wangjue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todayBread.R;
import com.example.todayBread.wheat.UserInfo;
import com.example.todayBread.wheat.Utils.FlipQuitListener;
import com.example.todayBread.wheat.Utils.ImageUtils;
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

    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                try {
                    JSONObject jsonObject = new JSONObject(rts);
                    rts = jsonObject.getString("data");
                    SharedPreferences shp = getSharedPreferences("article",MODE_PRIVATE);
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
                    StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                    RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.3f);
                    SpannableString span = new SpannableString(jsonObject.getString("title"));
                    span.setSpan(styleSpan,0,span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(sizeSpan,0,span.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.append(span);
                    tv.append("\n");
                    SpannableString sp1 = new SpannableString(jsonObject.getString("author")+"  "+jsonObject.getString("publishTime"));
                    sp1.setSpan(new ForegroundColorSpan(Color.parseColor("#808A87")), 0,sp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sizeSpan = new RelativeSizeSpan(0.85f);
                    sp1.setSpan(sizeSpan,0,sp1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.append(sp1);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //下面这两行代码要改成从第三方类中获取token
        SharedPreferences shp = getSharedPreferences("token", MODE_PRIVATE);
        token = shp.getString("token", "none");
        get();

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

    //将内容显示在界面上
    private void show() {
        LinearLayout linearLayout = findViewById(R.id.layout);
        linearLayout.removeViewAt(2);
        SharedPreferences shp1 = getSharedPreferences("article",MODE_PRIVATE);
        rts = shp1.getString("article","none");
        String[] putout =rts.split("\n"); //得到输出在屏幕上的正文字符串数组
        //一些字符格式的设定
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.3f);
        for(int i=0 ; i<putout.length ; i++) {
            if(putout[i].length()==0){
                edt.append("\n");
                continue;
            }
            String t = putout[i];
            if(t.contains("!")){
                /** modified by wheat. 2020-11-24 **/

//                String pic = t.substring(t.indexOf("(")+1,t.indexOf("."));
//                pic = pic.toLowerCase();
//                ApplicationInfo appInfo = getApplicationInfo();
//                int resID = getResources().getIdentifier(pic, "drawable", appInfo.packageName);
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resID);
                String pic = t.substring(t.indexOf('(') + 1, t.indexOf(')'));
                Bitmap bitmap = ImageUtils.safeLoadBitmap(getResources(), pic);
                /** modified by wheat. 2020-11-24 **/

                SpannableString ss = new SpannableString("pict");
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();
                float newWidth = 900;
                bitmap = zoomImage(bitmap,newWidth,height/width*newWidth);
                ImageSpan span = new ImageSpan(ImageTextActivity.this,bitmap);
                ss.setSpan(span,0,4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                edt.append(ss);
                edt.append("\n");
            }
            if(t.contains("#")){
                if(t.contains("##")){
                    t = t.substring(t.indexOf("##")+2);
                    SpannableString sp = new SpannableString(t);
                    styleSpan = new StyleSpan(Typeface.BOLD);
                    sizeSpan = new RelativeSizeSpan(1.2f);
                    sp.setSpan(styleSpan,0,sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(sizeSpan,0,sp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    edt.append(sp);
                    edt.append("\n");
                }
                else{
                    t = t.substring(t.indexOf("#")+1);
                    SpannableString sp = new SpannableString(t);
                    styleSpan = new StyleSpan(Typeface.BOLD);
                    sizeSpan = new RelativeSizeSpan(1.2f);
                    sp.setSpan(styleSpan,0,sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(sizeSpan,0,sp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    edt.append(sp);
                    edt.append("\n");
                }
            }
            else if(t.contains("http")){
                t = t.substring(t.indexOf("h"));
                SpannableString sp = new SpannableString(t);
                sp.setSpan(new URLSpan(t), 0, t.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                edt.append(sp);
                edt.append("\n");
            }
            else if(t.contains("!")){
                continue;
            }
            else{
                edt.append(t+"\n");
            }
        }
    }

    //按比例调整图片大小
    public Bitmap scaleImg(Bitmap bm, float time) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(time, time);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
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
//                    call.enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Log.i("failure", e.getMessage());
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            rts = response.body().string();
//                            myhandler.sendEmptyMessage(0);
//                        }
//                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

}
