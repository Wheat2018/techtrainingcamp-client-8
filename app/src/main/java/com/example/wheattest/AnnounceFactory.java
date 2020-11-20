package com.example.wheattest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class AnnounceFactory
{
    private AnnounceFactory()
    {
        throw new UnsupportedOperationException();
    }
    public static Announce createAnnounce(Context context, int typeId)
    {
        switch (typeId)
        {
            case 0:
                break;
            case 1:
                return new AnnounceType1(context);
            default:
                return null;
        }
        return null;
    }
}

class AnnounceType1 extends Announce {
    public ImageView image;
    private final LinearLayout subLayout;
    public AnnounceType1(Context context) {
        super(context);
        subLayout = new LinearLayout(context);
        image = new ImageView(context);
        setOrientation(HORIZONTAL);
        subLayout.setOrientation(VERTICAL);

        subLayout.addView(title);
        subLayout.addView(author);

        // Add
        addView(subLayout);
        addView(image);

        SetInnerMargins(null);
    }

    @Override
    public void SetInnerMargins(Rect innerMargins)
    {
        // Layout
        if (innerMargins == null) innerMargins = new Rect();
        title.setLayoutParams(params(match_parent, wrap_content, 1,
                new Rect(px(innerMargins.left), px(innerMargins.top), px(16), px(0))));
        author.setLayoutParams(params(match_parent, wrap_content, 0,
                new Rect(px(innerMargins.left), px(0), px(16), px(innerMargins.bottom))));
        subLayout.setLayoutParams(params(wrap_content, match_parent, 1, null));
        image.setLayoutParams(params(wrap_content, match_parent, 0,
                new Rect(px(16), px(innerMargins.top), px(innerMargins.right), px(innerMargins.bottom + 8))));
    }

    public void Set(String title, String author, Bitmap image)
    {
        this.title.setText(title);
        this.author.setText(title);
        final int w = getResources().getDisplayMetrics().widthPixels / 4 - px(16) * 2;
        this.image.setImageBitmap(scaleMatrix(image, w, 0));
    }
}

abstract class Announce extends LinearLayout {
    protected Paint paint;
    protected int wrap_content = LinearLayout.LayoutParams.WRAP_CONTENT;
    protected int match_parent = LinearLayout.LayoutParams.MATCH_PARENT;
    public TextView title, author;

    public Announce(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorLine, null));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getResources().getInteger(R.integer.lineWidth));
        title = new TextView(context);
        author = new TextView(context);

        // Style
        title.setTextAppearance(R.style.AnnounceTitleStyle);
        title.setGravity(Gravity.CENTER_VERTICAL);
        author.setTextAppearance(R.style.AnnounceAuthorStyle);
        author.setGravity(Gravity.CENTER_VERTICAL);
        setWillNotDraw(false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
    }

    public abstract void SetInnerMargins(Rect innerMargins);

    public LinearLayout.LayoutParams params(int width, int height, int weight, Rect margin)
    {
        LinearLayout.LayoutParams imParams = new LinearLayout.LayoutParams(width, height);
        imParams.weight = weight;
        if (margin != null)
        {
            imParams.leftMargin = margin.left;
            imParams.rightMargin = margin.right;
            imParams.topMargin = margin.top;
            imParams.bottomMargin = margin.bottom;
        }
        return imParams;
    }

    /**
     * 使用Matrix
     * @param bitmap 原始的Bitmap
     * @param width 目标宽度
     * @param height 目标高度
     * @return 缩放后的Bitmap
     */
    public static Bitmap scaleMatrix(@NotNull Bitmap bitmap, int width, int height){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = (float)width / w;
        float scaleH = (float)height / h;
        if (width <= 0) scaleW = scaleH;
        if (height <= 0) scaleH = scaleW;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public int px(float dp) {
        final float scale = getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int)(dp * scale + 0.5f);
    }
    public int dp(float px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
