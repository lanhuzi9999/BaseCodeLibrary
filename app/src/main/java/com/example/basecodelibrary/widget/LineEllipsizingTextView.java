package com.example.basecodelibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.basecodelibrary.R;

import java.util.Vector;

public class LineEllipsizingTextView extends TextView {

    private int maxLines;
    private static final String ELLIPSIS = "…";
    private static final String TAG = "LineEllipsizingTextView";
    private String fullText;
    private String shrinkText;
    private LineTextListener mListener;
//    private boolean programmaticChange = true;
    private TextView mController;
    private ImageView mDrawableController;
    private int mCurLines;
    private String mDisplayText = "";
    private Runnable mRefreshCb;

    public void setListener(LineTextListener mListener) {
        this.mListener = mListener;
    }

    public void setExpanableController(TextView controller) {
        this.mController = controller;
        mController.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isexpand = (Boolean) v.getTag();
                mController.setTag(!isexpand);
                if (isexpand) {
//					setMaxLines(3);
//					setEllipLine(maxLines);
                    setCurMaxLine(maxLines);
                    mController.setText("...更多");
                    mDisplayText = shrinkText;
                } else {
//					setMaxLines(Integer.MAX_VALUE);
//					setEllipLine(Integer.MAX_VALUE);
                    setCurMaxLine(Integer.MAX_VALUE);
                    mController.setText("收起");
                    mDisplayText = fullText;
                }
            }
        });
        setListener(new LineTextListener() {
            @Override
            public void fullLine() {
                // TODO Auto-generated method stub
                mController.setVisibility(View.GONE);
            }

            @Override
            public void expireLine() {
                // TODO Auto-generated method stub
                mController.setVisibility(View.VISIBLE);
                mController.setTag(new Boolean(false));
            }

            @Override
            public void shrinkLine() {
                mController.setVisibility(View.VISIBLE);
                mController.setTag(new Boolean(true));
            }
        });
    }

    public void setDrawableExpanableController(ImageView controller, final Runnable cb) {
        this.mDrawableController = controller;
        mRefreshCb = cb;
        mDrawableController.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isexpand = (Boolean) v.getTag();
                mDrawableController.setTag(!isexpand);
                if (isexpand) {
                    setCurMaxLine(maxLines);
                    //mController.setText("...更多");
//                    mDrawableController.setImageResource(R.drawable.intro_expand);
                    mDisplayText = shrinkText;
                } else {
                    setCurMaxLine(Integer.MAX_VALUE);
                    //mController.setText("收起");
//                    mDrawableController.setImageResource(R.drawable.intro_shrink);
                    mDisplayText = fullText;
                }

                invalidate();
            }
        });
        setListener(new LineTextListener() {
            @Override
            public void fullLine() {
                // TODO Auto-generated method stub
                mDrawableController.setVisibility(View.GONE);
            }

            @Override
            public void expireLine() {
                // TODO Auto-generated method stub
                if (mDrawableController.getVisibility() != VISIBLE) {
                    mDrawableController.setVisibility(View.VISIBLE);
                }
                mDrawableController.setTag(new Boolean(false));
            }

            @Override
            public void shrinkLine() {
                if (mDrawableController.getVisibility() != VISIBLE) {
                    mDrawableController.setVisibility(View.VISIBLE);
                }
                mDrawableController.setTag(new Boolean(true));
            }
        });
    }

    public LineEllipsizingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{R.attr.ellipsizeline});
        setEllipLine(a.getInt(0, Integer.MAX_VALUE));
    }

/*    @Override
    public void setMaxLines(int maxLines) {
	super.setMaxLines(maxLines);
	this.maxLines = maxLines;
    }*/

    private void setCurMaxLine(int maxlines) {
        this.mCurLines = maxlines;
//        programmaticChange = true;
    }

    public void setEllipLine(int maxLines) {
        this.maxLines = maxLines;
        this.mCurLines = maxLines;
        mDisplayText = null;
//        programmaticChange = true;
    }

    @Override
    public int getMaxLines() {
        return maxLines;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mDisplayText == null) {
            resetText();
        }
        if (mDisplayText != null && !mDisplayText.equals(getText())) {
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int width = widthSize;
            int height;

            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize;
            } else {
                TextPaint paint = getPaint();
                if (paint != null){
                    int lines = Math.min(mCurLines,getLineCount());
                    int space = (int)paint.getFontSpacing();
                    height = getPaddingTop() + (space+2) * lines + getPaddingBottom();
                }else {
                    height = heightSize;
                }

            }
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDisplayText != null && !mDisplayText.equals(getText())) {
            setText(mDisplayText);
            if (mRefreshCb != null){
                mRefreshCb.run();
            }
        }
        super.onDraw(canvas);
    }

    public void setFullText(String text){
        fullText = text;
        shrinkText = null;
        mDisplayText = null;
        super.setText(text);
    }

    private String getShrinkText(){
        String workingText = getText().toString();
        Vector<String> lines = new Vector<String>();
        float mw = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
        int maxLines = this.mCurLines > 0 ? this.mCurLines : 1;
        Paint paint = getPaint();

        for (int i = 0; i < maxLines; i++) {
            int nSize = paint.breakText(workingText, true, mw, null);
            if (nSize >= workingText.length()) {
                break;
            }
            lines.add(workingText.substring(0, nSize));
            workingText = workingText.substring(nSize);
        }
        String temp = lines.lastElement();
        int index = temp.length();
        for (int j = temp.length() - 1; j >= 0; j--) {
            float measureLength = paint.measureText(temp.substring(0, j) + ELLIPSIS);
            if (measureLength < mw - 50) {
                index = j;
                break;
            }
        }
        temp = temp.substring(0, index) + ELLIPSIS;
        lines.remove(lines.lastElement());
        lines.add(temp);
        StringBuffer sb = new StringBuffer();
        for (String s : lines)
            sb.append(s);

        return sb.toString();
    }

    private void resetText() {
        if (fullText == null){
            fullText = getText().toString();
        }

        if (maxLines < getLineCount()) {
            shrinkText = getShrinkText();
            mDisplayText = shrinkText;
            if (mListener != null) {
                mListener.expireLine();
            }
        } else {
            mDisplayText = fullText;
            if (mListener != null) {
                if (mCurLines == Integer.MAX_VALUE) {
                    mListener.shrinkLine();
                } else {
                    mListener.fullLine();
                }
            }
        }
    }

    public interface LineTextListener {
        public void shrinkLine();
        public void expireLine();
        public void fullLine();
    }
}
