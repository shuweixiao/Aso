package com.sye.graphics;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Created by super.dragon on 2016/6/29 0029 13:41
 * <p>
 * version 1.0.1
 */
public class DrawableFactory {

    public static Drawable getDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * @param border: 一般取较小的值，比如10以内
     */
    public static ShapeDrawable roundCorner(float radius, float border, int color,
                                            float left, float top, float right, float bottom) {
        //外半径
        float[] outerRadii = new float[8];
        //内半径
        float[] innerRadii = new float[8];
        for (int i = 0; i < 8; i++) {
            outerRadii[i] = radius + border;
            innerRadii[i] = radius + 2;
        }

        ShapeDrawable sd = new ShapeDrawable(new RoundRectShape(outerRadii, new RectF(left, top,
                right, bottom), innerRadii));
        //添加画笔颜色
        sd.getPaint().setColor(color);
        // 指定填充模式
        sd.getPaint().setStyle(Paint.Style.FILL);
        return sd;
    }
}
