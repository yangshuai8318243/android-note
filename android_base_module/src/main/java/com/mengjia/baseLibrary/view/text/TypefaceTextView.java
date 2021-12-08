package com.mengjia.baseLibrary.view.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.mengjia.baseLibrary.R;

public class TypefaceTextView extends androidx.appcompat.widget.AppCompatTextView {
    protected int tvStyle = Typeface.NORMAL;

    public TypefaceTextView(Context context) {
        super(context);
        replaceTypeface();
    }

    public TypefaceTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        replaceTypeface();
    }

    public TypefaceTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        replaceTypeface();
    }

    private void replaceTypeface() {
        Typeface appFont = TextFontManager.getInstance().getAppFont();
        if (appFont != null){
            setTypeface(appFont, tvStyle);
        }
    }

    protected void getAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);
        tvStyle = typedArray.getInt(R.styleable.TypefaceTextView_android_textStyle, 0);
    }
}
