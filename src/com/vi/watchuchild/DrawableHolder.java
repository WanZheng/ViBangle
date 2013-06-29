package com.vi.watchuchild;

import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: dk
 * Date: 13-5-28
 */
public class DrawableHolder {
    protected Drawable mDrawable;
    protected float mX = 0, mY = 0;
    protected float mAlpha = 1;
    protected float mScaleX = 1, mScaleY = 1;
    protected float mRotation = 0;
    protected View mView;
    protected PointF mAnchorPoint = new PointF(0, 0);
    protected boolean mVisibility = true;
    private Object mTag;


    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setX(float value) {
        mX = value;
        updateView();
    }

    public float getX() {
        return mX;
    }

    public void setY(float value) {
        mY = value;
        updateView();
    }

    public float getY() {
        return mY;
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
        mDrawable.setAlpha((int) ((alpha * 255f) + .5f));
        updateView();
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setScaleX(float scaleX) {
        mScaleX = scaleX;
        updateView();
    }

    public float getScaleX() {
        return mScaleX;
    }

    public void setScaleY(float scaleY) {
        mScaleY = scaleY;
        updateView();
    }

    public float getScaleY() {
        return mScaleY;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        updateView();
    }

    public float getRotation() {
        return mRotation;
    }

    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    public void attachView(View view) {
        mView = view;
    }

    public void setAnchorPointX(float value) {
        mAnchorPoint.x = value;
        updateView();
    }

    public float getAnchorPointX() {
        return mAnchorPoint.x;
    }

    public void setAnchorPointY(float value) {
        mAnchorPoint.y = value;
        updateView();
    }

    public float getAnchorPointY() {
        return mAnchorPoint.y;
    }

    public void setVisibility(boolean isVisibility) {
        mVisibility = isVisibility;
        updateView();
    }

    public boolean getVisibility() {
        return mVisibility;
    }

    public void setAnchorPoint(float x, float y) {
        setAnchorPointX(x);
        setAnchorPointY(y);
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    private void updateView() {
        if (null != mView) {
            // TODO: Optimized by invalidateRect.
            mView.invalidate();
        }
    }
}
