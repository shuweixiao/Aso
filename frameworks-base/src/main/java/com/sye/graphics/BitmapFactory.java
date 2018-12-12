package com.sye.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * *****************************************************************************************
 * Created by super.dragon on on 8/29/2017 16:25
 * <p>
 * version 1.0
 * *****************************************************************************************
 */
public class BitmapFactory extends android.graphics.BitmapFactory {


    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();

        // Gets drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应bitmap
        final Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        // 建立对应bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapFromDrawable2(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * 获取图片的原始尺寸
     *
     * @param path
     * @return
     */
    public static Point getOriginalSize(String path) {
        Options options = new Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        decodeFile(path, options);
        Point point = new Point();
        point.x = options.outWidth;
        point.y = options.outHeight;
        return point;
    }

    /**
     * @param o
     * @param w
     * @param h
     * @return
     */
    public static int calculateInSampleSize(final Options o, int w, int h) {
        int iss = 1;
        if (o.outHeight > h || o.outWidth > w) {
            final int heightRatio = Math.round((float) o.outHeight / (float) h);
            final int widthRatio = Math.round((float) o.outWidth / (float) w);
            iss = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return iss;
    }


}
