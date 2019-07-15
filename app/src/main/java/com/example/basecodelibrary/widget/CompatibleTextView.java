package com.example.basecodelibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import com.example.basecodelibrary.R;

import java.util.ArrayList;

/**
 * ************************************************ <br>
 * 文件名称: CompatibleTextView.java <br>
 * 文件描述: 避免遇到标点符号自动换行的textview <br>
 * *************************************************
 */
public class CompatibleTextView extends View {
    // 画笔
    private Paint mPaint = null;
    // 字体规格参数
    private Paint.FontMetrics mFontMetrics;
    // 每行的高度
    private float mRowHeight;
    // 默认内容
    private String mContent = "《放手爱OST》群星";
    // 行间距
    private float mLineSpace = 0;
    // 拆分后的字体列表
    private ArrayList<String> mCharList = new ArrayList<String>();

    public CompatibleTextView(Context context) {
        super(context);
        init(context);
    }

    public CompatibleTextView(Context context, AttributeSet set) {
        super(context, set, 0);
        init(context);
    }

    public CompatibleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // public CustomTextView(Context context, AttributeSet attrs, int
    // defStyleAttr, int defStyleRes) {
    // super(context, attrs, defStyleAttr, defStyleRes);
    // init(context);
    // }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.title_text_color));
        mPaint.setTextSize(dip2px(context, 15));
        initParams(context);
    }

    private void initParams(Context context) {
        mFontMetrics = mPaint.getFontMetrics();
        if (mLineSpace > 0) {
            mFontMetrics.leading = mLineSpace;
        } else {
            mFontMetrics.leading = dip2px(context, 1);
        }
        mRowHeight = mFontMetrics.descent - mFontMetrics.ascent + mFontMetrics.leading;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        // 测量宽度
        int textWidth = (int) mPaint.measureText(mContent);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(textWidth + getPaddingLeft() + getPaddingRight(), widthSize);
        } else {
            width = textWidth + getPaddingLeft() + getPaddingRight();
        }

        // 测量高度
        int lines = calculateLines(mContent, width - getPaddingLeft() - getPaddingRight()).size();
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int indeedHeight = getPaddingTop() + getPaddingBottom() + (int) mRowHeight * lines
                    + (int) mFontMetrics.bottom;
            height = Math.min(indeedHeight, heightSize);
        } else {
            height = getPaddingTop() + getPaddingBottom() + (int) mRowHeight * lines + (int) mFontMetrics.bottom;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = getPaddingLeft();
        float y = -mFontMetrics.top + getPaddingTop();
        ArrayList<String> list = calculateLines(mContent, getWidth() - getPaddingLeft() - getPaddingRight());
        for (String text : list) {
            canvas.drawText(text, x, y, mPaint);
            y += mRowHeight;
        }
    }

    /**
     * 根据内容和textview宽度计算字符列表
     *
     * @param content
     * @param width
     * @return ArrayList<String>
     */
    private ArrayList<String> calculateLines(String content, int width) {
        mCharList.clear();
        int length = content.length();
        float textWidth = mPaint.measureText(content);
        //1.不满一行的直接add
        if (textWidth <= width) {
            mCharList.add(content);
            return mCharList;
        }
        //2.超过一行的情况
        int start = 0, end = 1;
        while (start < length) {
            if (mPaint.measureText(content, start, end) > width) {
                //第一行
                mCharList.add(content.substring(start, end - 1));
                start = end - 1;
            } else if (end < length) {
                end++;
            }
            //第二行
            if (end == length && start > 0) {
                mCharList.add(content.subSequence(start, end).toString());
                break;
            }
        }
        return mCharList;
    }

    /**
     * 设置内容
     *
     * @param text void
     */
    public void setText(String text) {
        if (null == text || text.trim().length() == 0) {
            mContent = "";
        } else {
            mContent = text;
        }
        invalidate();
    }

    /**
     * 设置文字颜色
     *
     * @param context
     * @param textColor void
     */
    public void setTextColor(Context context, int textColor) {
        mPaint.setColor(context.getResources().getColor(textColor));
        invalidate();
    }

    /**
     * 设置文字大小
     *
     * @param context
     * @param textSize void
     */
    public void setTextSize(Context context, int textSize) {
        mPaint.setTextSize(context.getResources().getDimension(textSize));
        initParams(context);
        invalidate();
    }

    /**
     * 设置行间距
     *
     * @param context
     * @param spacing void
     */
    public void setLineSpacingExtra(Context context, int spacing) {
        this.mLineSpace = context.getResources().getDimension(spacing);
        initParams(context);
        invalidate();
    }
}
