package com.example.todayBread.wheat.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图像相关常用函数。
 */
public class ImageUtils {
    private ImageUtils(){
        throw new UnsupportedOperationException();
    }

    /**
     * 获取图片读取失败时的推荐默认显示图片。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @return 加载失败图像。
     */
    public static Bitmap getLoadFailBitmap(@NotNull Resources resources) {
        int picId = resources.getIdentifier("android:drawable/ic_menu_report_image",
                null, null);
        return BitmapFactory.decodeStream(resources.openRawResource(picId));
    }

    /**
     * 加载指定文件名的图片。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @param fileName 图片文件名。
     * @return 加载的图像。
     * @throws IOException 图片读取失败时抛出。
     */
    public static Bitmap loadBitmap(@NotNull Resources resources, String fileName) throws IOException {
        try (InputStream is = resources.getAssets().open(fileName)) {
            return BitmapFactory.decodeStream(is);
        }
    }

    /**
     * 异常安全的图像加载函数。加载指定文件名的图片，读取失败时返回加载失败图像。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @param fileName 图片文件名。
     * @return 加载的图像或加载失败图像。
     */
    public static Bitmap safeLoadBitmap(@NotNull Resources resources, String fileName){
        try {
            return loadBitmap(resources, fileName);
        } catch (IOException e) {
            Log.e("safeLoadBitmap", e.toString());
            return getLoadFailBitmap(resources);
        }
    }

    /**
     * 以指定尺寸加载指定文件名的图片。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @param fileName 图片文件名。
     * @param width 图像宽度(px)。
     * @param height 图像高度(px)。
     * @return 加载的缩放图像。
     * @throws IOException 图片读取失败时抛出。
     */
    public static Bitmap loadScaleBitmap(@NotNull Resources resources, String fileName,
                                         int width, int height) throws IOException {
        Bitmap bitmap = loadBitmap(resources, fileName);
        return scaleBitmap(bitmap, width, height);
    }

    /**
     * 异常安全的指定尺寸图像加载函数。以指定尺寸加载指定文件名的图片，读取失败时返回指定尺寸的加载失败图像。
     * @param resources 资源对象，通常由主调方Activity或context的成员函数getResource获得。
     * @param fileName 图片文件名。
     * @param width 图像宽度(px)。
     * @param height 图像高度(px)。
     * @return 加载的缩放图像或缩放的加载失败图像。
     */
    public static Bitmap safeLoadScaleBitmap(@NotNull Resources resources, String fileName,
                                             int width, int height){
        try {
            return loadScaleBitmap(resources, fileName, width, height);
        } catch (IOException e) {
            Log.e("safeLoadScaleBitmap", e.toString());
            return scaleBitmap(getLoadFailBitmap(resources), width, height);
        }
    }

    /**
     * 缩放位图。
     * @param bitmap 位图。
     * @param width 缩放后的宽。
     * @param height 缩放后的高。
     * @return 缩放后的位图。
     */
    public static Bitmap scaleBitmap(@NotNull Bitmap bitmap, int width, int height) {
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

    /**
     * 圆形裁切位图。
     * @param bitmap 位图。
     * @return 圆形裁切后的位图。
     */
    public static Bitmap roundBitmap(@NotNull Bitmap bitmap){
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap crop = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - size) / 2,
                (bitmap.getHeight() - size) / 2, size, size);
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(crop, rect, rect, paint);
        return result;
    }

}
