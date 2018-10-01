package com.tinysoftware.tsshadowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class TSShadowLayout extends FrameLayout {

    private int mShadowColor;
    private int mFillColor;
    private float mShadowRadius;
    private float mCornerRadius;
    private float mDx;
    private float mDy;

    private ImageView imageViewShadow;

    public TSShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public TSShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TSShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            if (imageViewShadow != null) {
                removeView(imageViewShadow);
                imageViewShadow = null;
            }

            updateShadowImageView(right - left, bottom - top);
            disableClipOnParents(this);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);
    }


    private void updateShadowImageView(int width, int height) {
        if (imageViewShadow == null) {
            int additionalSize = (int) (mShadowRadius * 2);

            int marginTop = (int) -mShadowRadius;
            int marginBottom = (int) -mShadowRadius;

            int marginLeft = (int) -(mShadowRadius);
            int marginRight = (int) -(mShadowRadius);

            Bitmap bitmap = createShadowBitmap(width + additionalSize, height + additionalSize, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, mFillColor);
            imageViewShadow = new ImageView(getContext());
            imageViewShadow.setImageBitmap(bitmap);
            addView(imageViewShadow, 0);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width + additionalSize, height + additionalSize);

            layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
            imageViewShadow.setLayoutParams(layoutParams);
        }
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.TSShadowLayout);
        if (attr == null) {
            return;
        }

        try {
            mCornerRadius = attr.getDimension(R.styleable.TSShadowLayout_tssl_corner_radius, 4);
            mShadowRadius = attr.getDimension(R.styleable.TSShadowLayout_tssl_shadow_radius, 10);
            mDx = attr.getDimension(R.styleable.TSShadowLayout_tssl_dx, 0);
            mDy = attr.getDimension(R.styleable.TSShadowLayout_tssl_dy, 0);
            mShadowColor = attr.getColor(R.styleable.TSShadowLayout_tssl_shadow_color, Color.RED);
            mFillColor = attr.getColor(R.styleable.TSShadowLayout_tssl_fill_color, Color.GREEN);
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        return output;
    }


    private void disableClipOnParents(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
        }

        if (v.getParent() instanceof View && !(v.getParent() instanceof ViewPager)) {
            disableClipOnParents((View) v.getParent());
        }
    }

}

