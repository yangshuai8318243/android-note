package com.mengjia.baseLibrary.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mengjia.baseLibrary.log.AppLog;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements PermissionsListener {
    protected String TAG = "";

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
        TAG = this.getClass().getName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.e(TAG, "-------Activity-->onCreate");
    }

    @Override
    protected void onDestroy() {
        AppLog.e(TAG, "-------Activity-->onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLog.e(TAG, "-------Activity-->onStart");
    }

    @Override
    protected void onStop() {
        AppLog.e(TAG, "-------Activity-->onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.e(TAG, "-------Activity-->onResume");
    }

    @Override
    protected void onPause() {
        AppLog.e(TAG, "-------Activity-->onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AppLog.e(TAG, "-------Activity-->onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        AppLog.e(TAG, "-->onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    /**
     * 单个权限申请
     *
     * @param permission
     */
    protected void requestPermissions(String permission) {
        String[] strings = new String[]{permission};
        requestPermissions(strings);
    }

    /**
     * 多个权限申请
     *
     * @param permissions
     */
    @Override
    public void requestPermissions(String[] permissions) {
        List<String> permissionList = new ArrayList<>();

        for (String pes : permissions) {
            if (ContextCompat.checkSelfPermission(this, pes) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pes);
            } else {
                onSuccessPermissions(pes);
            }
        }

        if (!permissionList.isEmpty()) {  //申请的集合不为空时，表示有需要申请的权限
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }


    @Override
    public void onFailurePermissions(String permission) {

    }

    @Override
    public void onSuccessPermissions(String permission) {

    }

    /**
     * 检查权限
     *
     * @param permission
     * @return
     */
    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 权限申请返回结果
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 申请结果数组，里面都是int类型的数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 获取到Activity下的Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) { //安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String pes = permissions[i];

                        if (grantResult == PackageManager.PERMISSION_DENIED) { //这个是权限拒绝
                            onFailurePermissions(pes);
                        } else { //授权成功了
                            onSuccessPermissions(pes);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass
     * @param viewId
     * @param <F>
     */
    protected <F extends Fragment> void addFragment(Class<F> fragmentClass, int viewId) {
        addFragment(fragmentClass, viewId, fragmentClass.getName());
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     * @param viewId
     */
    protected void addFragment(Fragment fragment, int viewId) {
        addFragment(fragment, viewId, fragment.getClass().getName());
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass
     * @param viewId
     * @param <F>
     */
    protected <F extends Fragment> void addFragment(Class<F> fragmentClass, int viewId, String tag) {
        Fragment baseFragment = null;
        try {
            baseFragment = fragmentClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        if (baseFragment != null) {
            addFragment(baseFragment, viewId, tag);
        } else {
            throw new RuntimeException("baseFragment is Null");
        }
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     * @param viewId
     */
    protected void addFragment(Fragment fragment, int viewId, String tag) {
        if (fragment != null) {
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.add(viewId, fragment, tag);
            trans.commitAllowingStateLoss();
        } else {
            throw new RuntimeException("baseFragment is Null");
        }
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass
     * @param viewId
     * @param <F>
     */
    protected <F extends Fragment> void replaceFragment(Class<F> fragmentClass, int viewId) {
        Fragment baseFragment = null;
        try {
            baseFragment = fragmentClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        if (baseFragment != null) {
            replaceFragment(baseFragment, viewId);
        } else {
            throw new RuntimeException("baseFragment is Null");
        }
    }


    /**
     * 替换Fragment
     *
     * @param fragment
     * @param viewId
     */
    protected void replaceFragment(Fragment fragment, int viewId) {
        if (fragment != null) {
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.replace(viewId, fragment);
            trans.commitAllowingStateLoss();
        } else {
            throw new RuntimeException("baseFragment is Null");
        }
    }


    /**
     * 显示Fragment
     *
     * @param fragment
     */
    protected void shwoFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏Fragment
     *
     * @param fragment
     */
    protected void hideFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragment);
        transaction.commitAllowingStateLoss();
    }


    /**
     * 删除Fragment
     *
     * @param fragment
     */
    protected void removeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * 删除Fragment
     *
     * @param tag
     */
    protected void removeFragment(String tag) {
        if (TextUtils.isEmpty(tag)) return;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Fragment fragment = null;
        for (Fragment f : fragments) {
            if (tag.equals(f.getTag())) {
                fragment = f;
            }
        }
        if (fragment == null) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    protected void removeAllFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        startContainerActivity(canonicalName, bundle, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param activityName  需要显示的ActivityName
     */
    public void startContainerActivity(String canonicalName, String activityName) {
        startContainerActivity(canonicalName, null, activityName);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle, String activityName) {
        Intent intent = new Intent(this, ContainerAppCompatActivity.class);
        intent.putExtra(ContainerAppCompatActivity.FRAGMENT, canonicalName);
        if (activityName != null) {
            intent.putExtra(ContainerAppCompatActivity.ACTIVITY_NAME, activityName);
        }
        if (bundle != null) {
            intent.putExtra(ContainerAppCompatActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }
}
