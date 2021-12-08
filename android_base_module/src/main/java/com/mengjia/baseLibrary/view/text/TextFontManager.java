package com.mengjia.baseLibrary.view.text;

import android.graphics.Typeface;

import com.mengjia.baseLibrary.app.BaseApp;

public class TextFontManager {
    private String fontPath = "font/font_content.ttf";
    private Typeface typefaceData;

    private static final class TextFontManagerHolder {
        private static final TextFontManager TEXT_FONT_MANAGER = new TextFontManager();
    }

    private TextFontManager() {
    }

    public static TextFontManager getInstance() {
        return TextFontManagerHolder.TEXT_FONT_MANAGER;
    }

    public Typeface getAppFont() {
        try {
            if (typefaceData == null) {
                typefaceData = Typeface.createFromAsset(BaseApp.getInstance().getAssets(), fontPath);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return typefaceData;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }
}
