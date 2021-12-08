package com.mengjia.baseLibrary.language;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.mengjia.baseLibrary.R;
import com.mengjia.baseLibrary.app.AppHandler;
import com.mengjia.baseLibrary.app.BaseApp;
import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.utils.SharedPreferencesUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 本地化语言管理，处理了Activity内存泄露问题，但是没有处理Fragment的情况
 * 后期可以考虑使用注解的方式绑定view
 */
public class LanguageManager {

    public static final String SHARED_LANGUAGE = "language";

    private static final String TAG = "LanguageManager";
    public static String LAN_EN = Locale.ENGLISH.getLanguage();
    public static String LAN_JA = Locale.JAPAN.getLanguage();
    public static String LAN_ZH = Locale.CHINESE.getLanguage();
    public static String LAN_RU = new Locale("ru", "").getLanguage();
    public static String LAN_TR = new Locale("tr", "").getLanguage();
    public static String LAN_KO = new Locale("ko", "").getLanguage();
    public static String LAN_ID = new Locale("in", "").getLanguage();

    private String currentLanguage = LAN_EN;
    //缓存通过Activity获得的textview对象
    private HashMap<Context, List<LanguageTextHolde>> activityTextViewMap = new HashMap<>();
    //缓存通过自定义view获得的textview对象，需要在view的onDetachedFromWindow方法中移除对象
    private HashMap<View, List<LanguageTextHolde>> textViewWeakHashMap = new HashMap<>();

    private HashMap<String, LanguageListener> listeners = new HashMap<>();

    private AppHandler<Object> appHandler;
    private static final int UPDATE_LAGUAGE = 0X0001;

    public static LanguageManager getInstance() {
        return LanguageManagerHolder.LANGUAGE_MANAGER;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public String getSystemLanguage() {
        return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
    }

    private LanguageManager() {
        appHandler = new AppHandler<>(Looper.getMainLooper(), null);
        appHandler.setListener(new AppHandler.AppHandlerListener<Object>() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_LAGUAGE:
                        updateLaguage();
                        break;
                }
            }
        });
    }

    public void setCurrentLanguage(String language) {
        if (LAN_EN.equals(language)
                || LAN_ZH.equals(language)
                || LAN_JA.equals(language)
                || LAN_TR.equals(language)
                || LAN_KO.equals(language)
                || LAN_RU.equals(language)
                || LAN_ID.equals(language)) {
            this.currentLanguage = language;
            saveLanguage(currentLanguage);
            appHandler.sendEmptyMessage(UPDATE_LAGUAGE);
        }
    }

    public void saveLanguage(String lang) {
        HashMap<String, Object> languageMap = new HashMap<>();
        languageMap.put(SHARED_LANGUAGE, lang);
        SharedPreferencesUtil.putValue(languageMap);
    }

    public void setLocalLanguage() {
        String localLang = SharedPreferencesUtil.getString(SHARED_LANGUAGE);
        if (localLang == null) {
            localLang = LAN_EN;
            HashMap<String, Object> languageMap = new HashMap<>();
            languageMap.put(SHARED_LANGUAGE, localLang);
            SharedPreferencesUtil.putValue(languageMap);
        }
        LanguageManager.getInstance().setCurrentLanguage(localLang);
        LanguageManager.getInstance().updateLaguage();
    }


    /**
     * 这个方式是很消耗性能的，需要遍历所有的view，所以不要频繁调用
     */
    public void updateLaguage() {
        Set<Context> contexts = activityTextViewMap.keySet();
        AppLog.e(TAG, "---updateLaguage-1->", contexts.size());
        for (Context context : contexts) {
            if (context == null) {
                activityTextViewMap.remove(context);
                continue;
            }

            List<LanguageTextHolde> languageTextHoldes = activityTextViewMap.get(context);

            AppLog.e(TAG, "---updateLaguage-1-1--->", languageTextHoldes);

            if (languageTextHoldes == null) {
                continue;
            }
            AppLog.e(TAG, "-updateLaguage--222-->", languageTextHoldes.size());
            AppLog.e(TAG, "-updateLaguage--222-->", currentLanguage);

            for (LanguageTextHolde languageTextHolde : languageTextHoldes) {
                TextView textViewWeak = languageTextHolde.getTextViewWeak();
                if (textViewWeak != null) {
                    Resources resourcesByLocale = getResourcesByLocale(currentLanguage);
                    String string = resourcesByLocale.getString(languageTextHolde.getStrId());
                    AppLog.e(TAG, "-updateLaguage--222-->", string);
                    textViewWeak.setText(string);
                }
            }
        }
        AppLog.e(TAG, "-------updateLaguage----2--->", listeners);

        Set<View> views = textViewWeakHashMap.keySet();
        AppLog.e(TAG, "-------updateLaguage----2-11-->", views, views.size());
        for (View view : views) {
            if (view == null) {
                textViewWeakHashMap.remove(view);
                continue;
            }

            List<LanguageTextHolde> languageTextHoldes = textViewWeakHashMap.get(view);
            AppLog.e(TAG, "-------updateLaguage----2-22-->", languageTextHoldes);
            if (languageTextHoldes == null) continue;

            for (LanguageTextHolde languageTextHolde : languageTextHoldes) {
                TextView textViewWeak = languageTextHolde.getTextViewWeak();
                if (textViewWeak != null) {
                    Resources resourcesByLocale = getResourcesByLocale(currentLanguage);
                    String string = resourcesByLocale.getString(languageTextHolde.getStrId());
                    textViewWeak.setText(string);
                }
            }
        }
        AppLog.e(TAG, "-------updateLaguage----3--->", listeners);

        if (listeners != null) {
            AppLog.e(TAG, "---updateLaguage----listeners---2---->", listeners.size());
            Collection<LanguageListener> values = listeners.values();
            for (LanguageListener listener : values) {
                listener.onUpdate();
            }
        }

    }

    private static final class LanguageManagerHolder {
        private static final LanguageManager LANGUAGE_MANAGER = new LanguageManager();
    }


    /**
     * 绑定context添加text
     * 添加后要及时清除
     *
     * @param context
     * @param textView
     * @param resId
     */
    public void addTextBindContext(Context context, TextView textView, @StringRes int resId) {
        Resources currentLocaleRes = getCurrentLocaleRes();

        if (context == null || textView == null || currentLocaleRes == null) {
            AppLog.e(TAG, "无效的添加");
            return;
        }

        String string = currentLocaleRes.getString(resId);
        textView.setText(string);
        LanguageTextHolde languageTextHolde = new LanguageTextHolde().setStrId(resId).setTextViewWeak(textView);

        List<LanguageTextHolde> languageTextHoldes = activityTextViewMap.get(context);
        if (languageTextHoldes == null) {
            ArrayList<LanguageTextHolde> languageManagers = new ArrayList<>();
            languageManagers.add(languageTextHolde);
            activityTextViewMap.put(context, languageManagers);
        } else {
            languageTextHoldes.add(languageTextHolde);
        }
    }

    /**
     * 自定义view添加text需要在view的onDetachedFromWindow方法中移除对象
     * 添加后要及时清除
     *
     * @param textView
     * @param resId
     */
    public void addTextBindView(View view, TextView textView, @StringRes int resId) {
        if (textView == null) return;
        AppLog.e(TAG, "--addTextBindView-->", view);
        LanguageTextHolde languageTextHolde = new LanguageTextHolde().setStrId(resId).setTextViewWeak(textView);

        List<LanguageTextHolde> languageTextHoldes = textViewWeakHashMap.get(view);
        if (languageTextHoldes == null) {
            ArrayList<LanguageTextHolde> languageManagers = new ArrayList<>();
            languageManagers.add(languageTextHolde);
            textViewWeakHashMap.put(view, languageManagers);
        } else {
            languageTextHoldes.add(languageTextHolde);
        }
    }


    /**
     * 获取当前多语言资源
     *
     * @return
     */
    public Resources getCurrentLocaleRes() {
        return getResourcesByLocale(currentLanguage);
    }

    /**
     * 获取当前语言
     *
     * @return
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * 获取对应语言的资源对象
     *
     * @param localeName
     * @return
     */
    public Resources getResourcesByLocale(String localeName) {
        Application instance = BaseApp.getInstance();
        if (instance == null) return null;
        Resources res = instance.getResources();
        Configuration conf = new Configuration(res.getConfiguration());
        conf.locale = new Locale(localeName);
        return new Resources(res.getAssets(), res.getDisplayMetrics(), conf);
    }


    /**
     * 清除对应context下的text
     *
     * @param context
     */
    public void clearForContext(Context context) {
        List<LanguageTextHolde> languageTextHoldes = activityTextViewMap.get(context);
        if (languageTextHoldes != null) {
            languageTextHoldes.clear();
        }
        activityTextViewMap.remove(context);
    }

    /**
     * 清除对应view下的text
     *
     * @param view
     */
    public void clearForView(View view) {
        List<LanguageTextHolde> languageTextHoldes = textViewWeakHashMap.get(view);
        if (languageTextHoldes != null) {
            languageTextHoldes.clear();
        }
        textViewWeakHashMap.remove(view);
    }

    /**
     * 清除所有text
     */
    public void clearAll() {
        activityTextViewMap.clear();
        textViewWeakHashMap.clear();
        listeners.clear();
    }

    static class LanguageTextHolde {
        private TextView textViewWeak;
        @StringRes
        private int strId;

        public TextView getTextViewWeak() {
            return textViewWeak;
        }

        public LanguageTextHolde setTextViewWeak(TextView textView) {
            this.textViewWeak = textView;
            return this;
        }

        public int getStrId() {
            return strId;
        }

        public LanguageTextHolde setStrId(@StringRes int strId) {
            this.strId = strId;
            return this;
        }
    }

    public interface LanguageListener {
        void onUpdate();
    }

    public void addListener(String tag, LanguageListener languageListener) {
        listeners.put(tag, languageListener);
    }

    public void removeListener(String tag) {
        listeners.remove(tag);
    }

    public void clearLanguageListener() {
        listeners.clear();
    }

    public enum LanguageValue {
        LAN_ZH("zh", String.valueOf(R.string.zh)),
        LAN_ZH_CN("zh-CN", String.valueOf(R.string.zh_CN)),
        LAN_ZH_TW("zh-TW", String.valueOf(R.string.zh_TW)),
        LAN_EN("en", String.valueOf(R.string.en)),
        LAN_ID("in", String.valueOf(R.string.id)),
        LAN_JA("ja", String.valueOf(R.string.ja)),
        LAN_KO("ko", String.valueOf(R.string.ko)),
        LAN_RU("ru", String.valueOf(R.string.ru)),
        LAN_TR("tr", String.valueOf(R.string.tr));

        private String code;
        private String name;

        LanguageValue(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static String getName(String code) {
            for (LanguageValue lang : LanguageValue.values()) {
                if (code.equals(lang.code)) {
                    return lang.name;
                }
            }
            return null;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
}
