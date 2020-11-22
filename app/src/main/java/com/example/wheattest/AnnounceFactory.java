package com.example.wheattest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

public class AnnounceFactory
{
    private AnnounceFactory()
    {
        throw new UnsupportedOperationException();
    }
    public static Announce createAnnounce(Context context, int typeId)
    {
        try {
            Constructor cst = Class.forName("com.example.wheattest.AnnounceType" + typeId)
                    .getConstructor(Context.class);
            return (Announce) cst.newInstance(context);
        } catch (IllegalAccessException
                | InstantiationException
                | ClassNotFoundException
                | NoSuchMethodException
                | InvocationTargetException e) {
            Log.e("createAnnounce", e.toString());
        }
        return null;
    }
}

abstract class Announce extends LinearLayout {
    private final Paint paint;

    public static final int wrap_content = LinearLayout.LayoutParams.WRAP_CONTENT;
    public static final int match_parent = LinearLayout.LayoutParams.MATCH_PARENT;
    protected TextView title, author;
    protected Map<String, String> values;

    public Announce(final Context context) {
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

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (values != null){
                    Intent intent = new Intent(context, ReaderActivity.class);
                    intent.putExtra("id", values.get("id"));
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
    }

    // Set values
    public void SetValues(JSONObject values) { SetValues(Utils.jsonObjectToMap(values));}
    public void SetValues(Map<String, String> values) {
        this.values = values;
        title.setText(values.get("title"));
        author.setText((values.get("author") + "  " + values.get("publishTime")));
    }

    // ==========Utility Methods===========

    public static LinearLayout.LayoutParams params(int width, int height, int weight) { return params(width, height, weight, null); }
    public static LinearLayout.LayoutParams params(int width, int height, int weight, Rect margins) {
        LinearLayout.LayoutParams imParams = new LinearLayout.LayoutParams(width, height);
        imParams.weight = weight;
        if (margins != null)
        {
            imParams.leftMargin = margins.left;
            imParams.rightMargin = margins.right;
            imParams.topMargin = margins.top;
            imParams.bottomMargin = margins.bottom;
        }
        return imParams;
    }
    public int px(int dp) { return Utils.dp2px(getResources(), dp);}
}


class AnnounceType0 extends Announce {
    protected LinearLayout subLayout;
    public AnnounceType0(Context context) {
        super(context);
        subLayout = new LinearLayout(context);
        setOrientation(HORIZONTAL);
        subLayout.setOrientation(VERTICAL);


        // Add
        subLayout.addView(title);
        subLayout.addView(author);
        addView(subLayout);

        // Layout
        title.setLayoutParams(params(match_parent, wrap_content, 1));
        author.setLayoutParams(params(match_parent, wrap_content, 0));
        subLayout.setLayoutParams(params(wrap_content, match_parent, 1));
    }

}


class AnnounceType1 extends AnnounceType0 {
    protected ImageView image;

    public AnnounceType1(Context context) {
        super(context);
        image = new ImageView(context);
        // Add
        addView(image);

        // Layout
        image.setLayoutParams(params(wrap_content, match_parent, 0,
                new Rect(px(32), 0, 0, 0)));
    }

    @Override
    public void SetValues(Map<String, String> values) {
        super.SetValues(values);
        final int w = Utils.screenWidth(getResources()) / 4 - px(16) * 2;
        try {
            image.setImageBitmap(Utils.loadScaleBitmap(getResources(), values.get("cover"), w, 0));
        } catch (IOException e) {
            Log.e("SetValues", "Fail to load picture: " + e.toString());
            image.setImageBitmap(Utils.getLoadFailBitmap(getResources()));
        }
    }
}

class AnnounceType2 extends AnnounceType0 {
    protected ImageView image;

    public AnnounceType2(Context context) {
        super(context);
        image = new ImageView(context);
        // Add
        addView(image, 0);

        // Layout
        image.setLayoutParams(params(wrap_content, match_parent, 0,
                new Rect(0, 0, px(32), 0)));
    }

    @Override
    public void SetValues(Map<String, String> values)
    {
        super.SetValues(values);
        final int w = Utils.screenWidth(getResources()) / 4 - Utils.dp2px(getResources(), 16) * 2;
        try {
            image.setImageBitmap(Utils.loadScaleBitmap(getResources(), values.get("cover"), w, 0));
        } catch (IOException e) {
            Log.e("SetValues", "Fail to load picture: " + e.toString());
            image.setImageBitmap(Utils.getLoadFailBitmap(getResources()));
        }
    }
}

class AnnounceType3 extends Announce {
    protected ImageView image;

    public AnnounceType3(Context context) {
        super(context);
        image = new ImageView(context);

        setOrientation(VERTICAL);

        // Add
        addView(title);
        addView(image);
        addView(author);

        // Layout
        title.setLayoutParams(params(match_parent, wrap_content, 1));
        image.setLayoutParams(params(match_parent, wrap_content, 2,
                new Rect(0, px(16), 0, px(16))));
        author.setLayoutParams(params(match_parent, wrap_content, 0));
    }
    @Override
    public void SetValues(Map<String, String> values) {
        super.SetValues(values);
        try {
            image.setImageBitmap(Utils.loadBitmap(getResources(), values.get("cover")));
        } catch (IOException e) {
            Log.e("SetValues", "Fail to load picture: " + e.toString());
            image.setImageBitmap(Utils.getLoadFailBitmap(getResources()));
        }

    }
}

class AnnounceType4 extends Announce {
    protected LinearLayout subLayout;
    protected ArrayList<ImageView> images;

    public AnnounceType4(Context context) {
        super(context);
        subLayout = new LinearLayout(context);
        images = new ArrayList<>();

        setOrientation(VERTICAL);
        subLayout.setOrientation(HORIZONTAL);

        // Add
        addView(title);
        addView(subLayout);
        addView(author);

        // Layout
        title.setLayoutParams(params(match_parent, wrap_content, 1));
        subLayout.setLayoutParams(params(match_parent, wrap_content, 2,
                new Rect(0, px(16), 0, px(16))));
        author.setLayoutParams(params(match_parent, wrap_content, 0));
    }

    @Override
    public void SetValues(Map<String, String> values)
    {
        super.SetValues(values);
        final int w = getResources().getDisplayMetrics().widthPixels / 4 - Utils.dp2px(getResources(), 16) * 2;

        subLayout.removeAllViews();
        String covers = values.get("covers");
        if (covers == null) {
            Log.e("SetValues", "The announce requires multiple pictures, but json values have no \"covers\"");
            ImageView image = new ImageView(getContext());
            image.setLayoutParams(params(match_parent, match_parent, 0, null));
            image.setImageBitmap(Utils.getLoadFailBitmap(getResources()));
            subLayout.addView(image);
        }
        else {
            covers = covers.substring(1, covers.length() - 1);
            for (String name : covers.split(",")) {
                name = name.substring(1, name.length() - 1);
                ImageView image = new ImageView(getContext());
                try {
                    image.setImageBitmap(Utils.loadScaleBitmap(getResources(), name, w, 0));
                } catch (IOException e) {
                    Log.e("SetValues", "Fail to load picture: " + e.toString());
                    image.setImageBitmap(Utils.getLoadFailBitmap(getResources()));
                }

                if (subLayout.getChildCount() == 0)
                    image.setLayoutParams(params(wrap_content, match_parent, 0, null));
                else
                    image.setLayoutParams(params(wrap_content, match_parent, 0,
                            new Rect(px(16), px(0), px(0), px(0))));
                subLayout.addView(image);
            }
        }
    }
}
