package com.mengjia.baseLibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Android Studio.
 * User: Avery
 * Date: 2021/1/13
 * Time: 11:14
 * 轻量级的存储类，只能存储基本数据类型和String，而且不适合存储大量的数据
 */
public class SharedPreferencesUtil {
    private static SharedPreferencesManage sharedPreferencesManage;
    private static final Object lock = new Object();

    public static class SharedPreferencesManage {
        private SharedPreferences sharedPreferences;
        private static final Object splock = new Object();

        public SharedPreferencesManage(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }

        public void putValue(HashMap<String, Object> spMap) {
            synchronized (splock) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (String key : spMap.keySet()) {
                    if (spMap.get(key) instanceof String) {
                        editor.putString(key, (String) spMap.get(key));
                    } else if (spMap.get(key) instanceof Integer) {
                        editor.putInt(key, (Integer) spMap.get(key));
                    } else if (spMap.get(key) instanceof Boolean) {
                        editor.putBoolean(key, (Boolean) spMap.get(key));
                    } else if (spMap.get(key) instanceof Long) {
                        editor.putLong(key, (Long) spMap.get(key));
                    }
                }
                editor.apply();
            }
        }

        /**
         * 存str
         *
         * @param key
         * @param var
         */
        public void putString(String key, String var) {
            synchronized (lock) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, var);
                editor.apply();
            }
        }

        /**
         * 存int
         *
         * @param key
         * @param var
         */
        public void putInt(String key, int var) {
            synchronized (lock) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(key, var);
                editor.apply();
            }
        }

        /**
         * 存 bol
         *
         * @param key
         * @param var
         */
        public void putBol(String key, boolean var) {
            synchronized (lock) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(key, var);
                editor.apply();
            }
        }

        /**
         * 存 float
         *
         * @param key
         * @param var
         */
        public void putFloat(String key, float var) {
            synchronized (lock) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(key, var);
                editor.apply();
            }
        }

        /**
         * 存 long
         *
         * @param key
         * @param var
         */
        public void putLong(String key, long var) {
            synchronized (lock) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(key, var);
                editor.apply();
            }
        }


        /**
         * 获取SP中key对应的String类型的值
         *
         * @param key
         * @return
         */
        public String getString(String key) {
            return sharedPreferences.getString(key, null);
        }

        /**
         * 获取SP中key对应的int类型的值
         *
         * @param key
         * @return
         */
        public int getInt(String key) {
            return sharedPreferences.getInt(key, -1);
        }

        /**
         * 获取SP中key对应的Boolean类型的值
         *
         * @param key
         * @return
         */
        public Boolean getBoolean(String key) {
            return sharedPreferences.getBoolean(key, true);
        }

        /**
         * 获取SP中key对应的Long类型的值
         *
         * @param key
         * @return
         */
        public Long getLong(String key) {
            return sharedPreferences.getLong(key, -1);
        }

    }

    /**
     * 初始化SharedPreference（最好在Application里面提前初始化）
     *
     * @param context
     */
    public static void initSP(Context context) {
        if (sharedPreferencesManage == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sharedPreferencesManage == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "-SDK", Context.MODE_PRIVATE);
                    sharedPreferencesManage = new SharedPreferencesManage(sharedPreferences);
                }
            }
        }
    }

    /**
     * 初始化SharedPreference（最好在Application里面提前初始化）
     *
     * @param context
     * @param fileName
     */
    public static SharedPreferencesManage initSP(Context context, String fileName) {
        if (fileName == null) {
            fileName = context.getPackageName() + "-SDK";
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return new SharedPreferencesManage(sharedPreferences);
    }

    /**
     * 存入键值对
     *
     * @param spMap
     */
    public static void putValue(HashMap<String, Object> spMap) {
        sharedPreferencesManage.putValue(spMap);
    }

    /**
     * 存str
     *
     * @param key
     * @param var
     */
    public static void putString(String key, String var) {
        sharedPreferencesManage.putString(key, var);
    }

    /**
     * 存int
     *
     * @param key
     * @param var
     */
    public static void putInt(String key, int var) {
        sharedPreferencesManage.putInt(key, var);
    }

    /**
     * 存 bol
     *
     * @param key
     * @param var
     */
    public static void putBol(String key, boolean var) {
        sharedPreferencesManage.putBol(key, var);
    }

    /**
     * 存 float
     *
     * @param key
     * @param var
     */
    public static void putFloat(String key, float var) {
        sharedPreferencesManage.putFloat(key, var);
    }

    /**
     * 存 long
     *
     * @param key
     * @param var
     */
    public static void putLong(String key, long var) {
        sharedPreferencesManage.putLong(key, var);
    }


    /**
     * 获取SP中key对应的String类型的值
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return sharedPreferencesManage.getString(key);
    }

    /**
     * 获取SP中key对应的int类型的值
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return sharedPreferencesManage.getInt(key);
    }

    /**
     * 获取SP中key对应的Boolean类型的值
     *
     * @param key
     * @return
     */
    public static Boolean getBoolean(String key) {
        return sharedPreferencesManage.getBoolean(key);
    }

    /**
     * 获取SP中key对应的Long类型的值
     *
     * @param key
     * @return
     */
    public static Long getLong(String key) {
        return sharedPreferencesManage.getLong(key);
    }
}
