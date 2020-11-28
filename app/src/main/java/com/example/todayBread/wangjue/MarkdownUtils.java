package com.example.todayBread.wangjue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.example.todayBread.wheat.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MarkdownUtils {

    //显示文章标题、作者、时间
    public static void titlePart(TextView tv, JSONObject jsonObject) throws JSONException {
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
    }

    //加载本地图标
    public static void picture(String t, Context context, TextView tv) {
        String pic = t.substring(t.indexOf('(') + 1, t.indexOf(')'));
        Bitmap bitmap = ImageUtils.safeLoadBitmap(context.getResources(), pic);
        SpannableString ss = new SpannableString("pict");
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float newWidth = 900;
        bitmap = zoomImage(bitmap,newWidth,height/width*newWidth);
        ImageSpan span = new ImageSpan(context,bitmap);
        ss.setSpan(span,0,4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(ss);
        tv.append("\n");
    }

    //一级标题
    public static void firstTitle(String t, TextView tv) {
        t = t.substring(t.indexOf("#")+1);
        SpannableString sp = new SpannableString(t);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.2f);
        sp.setSpan(styleSpan,0,sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(sizeSpan,0,sp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(sp);
        tv.append("\n");
    }

    //二级标题
    public static void secondTitle(String t, TextView tv) {
        t = t.substring(t.indexOf("##")+2);
        SpannableString sp = new SpannableString(t);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.2f);
        sp.setSpan(styleSpan,0,sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(sizeSpan,0,sp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(sp);
        tv.append("\n");
    }

    //网址超链接
    public static void httplink(String t, TextView tv) {
        t = t.substring(t.indexOf("h"));
        SpannableString sp = new SpannableString(t);
        sp.setSpan(new URLSpan(t), 0, t.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.append(sp);
        tv.append("\n");
    }

    //设置固定大小的图片
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
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
