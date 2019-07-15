package com.example.basecodelibrary.util;

import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {
    public static Bitmap getBitmapFrom(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int width, int height, int corner) {
        int sourcewidth = bitmap.getWidth();
        int sourceheight = bitmap.getHeight();
        // 缩放适应宽或者高
        float scale;
        int destwidth;
        int destheight;
        if (sourceheight > sourcewidth) {
            scale = ((float) width) / sourcewidth;
            destwidth = width;
            destheight = (int) (sourceheight * scale);
        } else {
            scale = ((float) height) / sourceheight;
            destheight = height;
            destwidth = (int) (sourcewidth * scale);
        }
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, destwidth, destheight, false);
        Bitmap bitmap2 = null;
        //再裁剪
        if (sourceheight > sourcewidth) {
            bitmap2 = Bitmap.createBitmap(bitmap1, 0, (destheight - height) / 2, width, height);
        } else {
            bitmap2 = Bitmap.createBitmap(bitmap1, (destwidth - width) / 2, 0, width, height);
        }
        //画圆角
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, corner, corner, paint);
        canvas.clipRect(rect);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap2, rect, rect, paint);
//	if(bitmap != null){ //2015.12.15 不应用将传入的图片释放！
//	    bitmap.recycle();
//	}
        if (bitmap1 != null && bitmap1 != bitmap) {
            bitmap1.recycle();
        }
        if (bitmap2 != null && bitmap2 != bitmap) {
            bitmap2.recycle();
        }
        return output;
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int width, int height) {
        int sourcewidth = bitmap.getWidth();
        int sourceheight = bitmap.getHeight();
        float sale;
        int destwidth;
        int destheight;
        if (sourceheight > sourcewidth) {
            sale = sourceheight / sourcewidth * 1.0f;
            destwidth = width;
            destheight = (int) (destwidth * sale);
        } else {
            sale = sourcewidth / sourceheight * 1.0f;
            destheight = height;
            destwidth = (int) (destheight * sale);
        }
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, destwidth, destheight, false);
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        canvas.clipRect(rect);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap1, rect, rect, paint);
//	if(bitmap != null){ //2015.12.15 Mic:不应用将传入的图片释放
//	    bitmap.recycle();
//	}
        if (bitmap1 != null && bitmap1 != bitmap) {
            bitmap1.recycle();
        }
        return output;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, boolean strok_it, BitmapDrawable strok_bg) {
        if (bitmap == null) {
            return null;
        }
        // 设置显示区域
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = (int) (Math.min(width, height));
        // 传入图像为正方形
        // 传入图像为长方形
        int differ = Math.abs(width - height) / 2;
        if (width > height) {
            bitmap = Bitmap.createBitmap(bitmap, differ, 0, min, min);
        } else if (width < height) {
            bitmap = Bitmap.createBitmap(bitmap, 0, differ, min, min);
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        // 设置一个图片大小的矩形
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        // bm是一个刚好canvas大小的空Bitmap ，画完后应该会自动保存到bm
        Canvas cns = new Canvas(output);

        // 构造渲染器BitmapShader
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        // 构建ShapeDrawable对象并定义形状为椭圆
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        // 得到画笔并设置渲染器
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        shapeDrawable.setBounds(1, 1, min - 1, min - 1);
        // 绘制shapeDrawable
        shapeDrawable.draw(cns);
        if (strok_it && strok_bg != null) {
            Bitmap bitmap2 = strok_bg.getBitmap();
            bitmap2 = Bitmap.createScaledBitmap(bitmap2, min, min, true);

            paint = new Paint();
            paint.setAntiAlias(true);
            cns.drawBitmap(bitmap2, rect, rect, paint);
            if (bitmap2 != output && bitmap2 != bitmap) {
                bitmap2.recycle();
            }
        }
//	if (bitmap != output) { //2015.12.15 Mic:不应用将传入的图片释放！
//	    bitmap.recycle();
//	}
        return output;
    }

    public static Bitmap getCroppedCloudBitmap(Bitmap bitmap) {
        return getCroppedBitmap(bitmap, false, null);
//	if (bitmap == null) {
//	    return null;
//	}
//	// 设置显示区域
//	int width = bitmap.getWidth();
//	int height = bitmap.getHeight();
//	int min = (int) (Math.min(width, height));
//	// 传入图像为正方形
//	int differ = Math.abs(width - height) / 2;
//	if (width > height) {
//	    bitmap = Bitmap.createBitmap(bitmap, differ, 0, min, min);
//	} else if (width < height) {
//	    bitmap = Bitmap.createBitmap(bitmap, 0, differ, min, min);
//	}
//
//	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
//	Canvas cns = new Canvas(output);
//
//	// 构造渲染器BitmapShader
//	BitmapShader bitmapShader = new BitmapShader(bitmap, android.graphics.Shader.TileMode.CLAMP,
//		android.graphics.Shader.TileMode.CLAMP);
//
//	// 绘制一个椭圆
//	// 设置椭圆高宽
//	// 将图片裁剪为椭圆形
//	// 构建ShapeDrawable对象并定义形状为椭圆
//	ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
//	// 得到画笔并设置渲染器
//	shapeDrawable.getPaint().setShader(bitmapShader);
//
//	int strokeWidth = 0;
//	shapeDrawable.setBounds(1, 1, min - 1, min - 1);
//	// 绘制shapeDrawable
//	shapeDrawable.draw(cns);
//	return output;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        return bmpToByteArray(bmp, needRecycle, Long.MAX_VALUE);

    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle, long limit) {
        int quality = 100;
        byte[] result;
        do {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bmp.compress(CompressFormat.JPEG, quality, output);
            result = output.toByteArray();
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            quality -= 20;
        } while ((result != null) && (result.length >= limit) && (quality > 0));

        if (needRecycle) {
            bmp.recycle();
        }
        return result;
    }
}
