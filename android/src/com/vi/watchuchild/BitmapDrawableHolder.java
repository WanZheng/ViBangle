package com.vi.watchuchild;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.vi.watchuchild.DrawableHolder;

/**
 * Created with IntelliJ IDEA.
 * User: dk
 * Date: 13-5-28
 */
public class BitmapDrawableHolder extends DrawableHolder {

    public BitmapDrawableHolder(BitmapDrawable drawable) {
        setDrawable(drawable);
    }

    @Override
    public void setDrawable(Drawable drawable) {
        if (null != drawable && drawable instanceof BitmapDrawable) {
            super.setDrawable(drawable);
            // SetBounds is very important else it's can't be drawn into canvas.
            getDrawable().setBounds(0, 0,
                    drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
    }
}
