package com.example.basecodelibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.basecodelibrary.R;
import com.example.basecodelibrary.reflect.ReflectHelper;



public class ShapeButton extends Button {
    private AbstractShapeDelegate   mShapeDelegate;
    private boolean                 mCallInDraw;
    public ShapeButton(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ShapeButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ShapeButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs,defStyleAttr, 0);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public ShapeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context, attrs,defStyleAttr, defStyleRes);
//    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyle) {
        mShapeDelegate = createShapeDelegate(context, attrs, defStyleAttr, defStyle);
        if (mShapeDelegate != null) {
            mShapeDelegate.setup(attrs, defStyleAttr, defStyle);
            setWillNotDraw(false);
        }
    }

    protected AbstractShapeDelegate createShapeDelegate(Context context, AttributeSet attrs, int defStyleAttr, int defStyle){
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeView, defStyle, 0);
            String shape = a.getString(R.styleable.ShapeView_shape);
            a.recycle();
            if (!TextUtils.isEmpty(shape)){
                String defShape = AbstractShapeDelegate.getDefaultShape(shape);
                if (!TextUtils.isEmpty(defShape)){
                    shape = defShape;
                }
                return (AbstractShapeDelegate) ReflectHelper.newInstance(shape,
                        new Class<?>[]{View.class},
                        new Object[]{this});
            }
        }
        return null ;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mCallInDraw){
            super.dispatchDraw(canvas);
        }else {
            mCallInDraw = true;
            if (mShapeDelegate == null ){
                super.dispatchDraw(canvas);
            }else{
                mShapeDelegate.beginDrawShape(canvas);
                super.dispatchDraw(canvas);
                mShapeDelegate.endDrawShape(canvas);
            }
        }
        mCallInDraw = false;
    }
    @Override
    public void draw(Canvas canvas) {
        if (mCallInDraw){
            super.draw(canvas);
        }else {
            mCallInDraw = true;
            if (mShapeDelegate == null || !mShapeDelegate.overrideDraw(canvas)) {
                super.draw(canvas);
            }
        }
        mCallInDraw = false;
    }

    public void updateShape (String shapeType){
        String defShape = AbstractShapeDelegate.getDefaultShape(shapeType);
        if (TextUtils.isEmpty(defShape)){
            return ;
        }
        if (mShapeDelegate != null){
            String className = mShapeDelegate.getClass().getName();
            if (className.equals(defShape)){
                return ;
            }
        }
        AbstractShapeDelegate shapeDelegate = (AbstractShapeDelegate) ReflectHelper.newInstance(defShape,
                new Class<?>[]{View.class},
                new Object[]{this});
        if (shapeDelegate != null){
            shapeDelegate.setup(null, 0, 0);
            mShapeDelegate = shapeDelegate;
        }
    }

    public void updateRadius(float topLeft, float topRight, float bottomLeft, float bottomRight){
        if (mShapeDelegate instanceof RoundViewDelegate){
            ((RoundViewDelegate)mShapeDelegate).updateRadius(topLeft,topRight, bottomLeft, bottomRight);
        }
    }
}
