package com.example.basecodelibrary.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.example.basecodelibrary.R;


/**
 * 圆弧对称 背景填充
 */
public class RoundTextView extends TextView {
    RectF fillrect;
    Paint mpaint;
    int background_fillcolor;
    float _roundy = 0, _roundx;
    int strokewidth_color = -1;
    int strokewidth_color_set = -1;
    float strokewidth = 5;
    int mTextColorInit = -1;

    public RoundTextView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public RoundTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundTextView);
            background_fillcolor = a.getColor(R.styleable.RoundTextView_background_fillcolor, 0);
            strokewidth = a.getDimension(R.styleable.RoundTextView_v_strokewidth, 1);
            strokewidth_color = a.getColor(R.styleable.RoundTextView_strokewidth_color, 0);
            strokewidth_color_set = strokewidth_color;
            _roundy = a.getDimension(R.styleable.RoundTextView_t_roundy, context.getResources().getDimension(R.dimen.hpv6_roundxy));
            _roundx = a.getDimension(R.styleable.RoundTextView_t_roundx, context.getResources().getDimension(R.dimen.hpv6_roundxy));
            a.recycle();
        }
        mTextColorInit = getCurrentTextColor();
        mpaint = new Paint();
        mpaint.setColor(background_fillcolor);
        mpaint.setStyle(Paint.Style.FILL);
        mpaint.setAntiAlias(true);
        setGravity(Gravity.CENTER);
    }

    public void setBackground_fillcolor(int background_fillcolor) {
        this.background_fillcolor = background_fillcolor;
    }



    public void setSingleSTROKEColor(int color) {
        strokewidth_color = color;
        strokewidth_color_set = strokewidth_color;
        background_fillcolor = 0;
    }

    public int getSingleSTROKEColor(){
        return strokewidth_color;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled){
            if (strokewidth_color_set != -1){
                strokewidth_color = strokewidth_color_set;
            }
            if (mTextColorInit != -1) {
                setTextColor(mTextColorInit);
            }
        }else {
            int color = getContext().getResources().getColor(R.color.gray_text_color);
            if (strokewidth_color_set != -1){
                strokewidth_color = color;
            }
            if (mTextColorInit != -1){
                setTextColor(color);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (background_fillcolor != 0) {
            if (fillrect == null || fillrect.width() != getMeasuredWidth()) {
                fillrect = new RectF(0, 0, getMeasuredWidth() - 0, getMeasuredHeight() - 0);
            }
            mpaint.setColor(background_fillcolor);
            mpaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(fillrect, _roundx, _roundy, mpaint);
        }

        if (strokewidth_color != 0) {
            mpaint.setColor(strokewidth_color);
            mpaint.setStyle(Paint.Style.STROKE);
            mpaint.setStrokeWidth(strokewidth);
            RectF strokerect = new RectF(strokewidth / 2.0f, strokewidth / 2.0f, getMeasuredWidth() - strokewidth / 2.0f, getMeasuredHeight() - strokewidth / 2.0f);
            canvas.drawRoundRect(strokerect, _roundx, _roundy, mpaint);
        }
        super.onDraw(canvas);
    }

    public void setRoundX(float roundX){
        _roundx = roundX;
    }

    public void setRoundY(float roundY){
        _roundy = roundY;
    }

    public void setStrokewidth(float width){
        strokewidth = width;
    }
}
