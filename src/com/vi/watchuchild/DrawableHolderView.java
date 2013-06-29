package com.vi.watchuchild;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dk
 * Date: 13-5-28
 */
public class DrawableHolderView extends View {

    protected List<DrawableHolder> mHolders = new ArrayList<DrawableHolder>();

    public DrawableHolderView(Context context) {
        super(context);
    }

    public DrawableHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mHolders.size(); ++i) {
            DrawableHolder holder = mHolders.get(i);
            if (null != holder
                    && null != holder.getDrawable()
                    && holder.getVisibility()) {
                final Rect rc = holder.getDrawable().getBounds();
                float pivotX = rc.width() * holder.getAnchorPointX();
                float pivotY = rc.height() * holder.getAnchorPointY();
                canvas.save();
                canvas.translate(holder.getX() - pivotX,
                        holder.getY() - pivotY);
                canvas.scale(holder.getScaleX(),
                        holder.getScaleY(),
                        pivotX, pivotY);
                canvas.rotate(holder.getRotation(), pivotX, pivotY);
                holder.getDrawable().draw(canvas);
                canvas.restore();
            }
        }
    }

    public void addHolder(DrawableHolder holder) {
        if (null != holder) {
            mHolders.add(holder);
            holder.attachView(this);
            invalidate();
        }
    }

    public void removeHolder(DrawableHolder holder) {
        mHolders.remove(holder);
    }

    public void clearHolder() {
        mHolders.clear();
    }

    public int getHolderCount() {
        return mHolders.size();
    }

    public DrawableHolder getHolder(int index) {
        return (index < mHolders.size() ? mHolders.get(index) : null);
    }

    public DrawableHolder findHolderByTag(Object tag) {
        for (DrawableHolder holder : mHolders) {
            if (null != tag
                    && holder.getTag() != null
                    && holder.getTag().equals(tag)) {
                return holder;
            }
        }
        return null;
    }
}
