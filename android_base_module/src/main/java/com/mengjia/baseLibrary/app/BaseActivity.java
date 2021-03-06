package com.mengjia.baseLibrary.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mengjia.baseLibrary.log.AppLog;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends Activity implements PermissionsListener{
    protected String TAG = "";

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
        TAG = this.getClass().getName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onCreate");
    }

    @Override
    protected void onDestroy() {
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onStart");
    }

    @Override
    protected void onStop() {
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onResume");
    }

    @Override
    protected void onPause() {
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        AppLog.e(TAG, getClass().getName(), "-------Activity-->onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    /**
     * ??????????????????
     *
     * @param permission
     */
    protected void requestPermissions(String permission) {
        String[] strings = new String[]{permission};
        requestPermissions(strings);
    }

    /**
     * ??????????????????
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

        if (!permissionList.isEmpty()) {  //????????????????????????????????????????????????????????????
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
     * ????????????????????????
     *
     * @param requestCode  ?????????
     * @param permissions  ????????????
     * @param grantResults ?????????????????????????????????int????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) { //???????????????????????????0?????????????????????
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String pes = permissions[i];

                        if (grantResult == PackageManager.PERMISSION_DENIED) { //?????????????????????
                            onFailurePermissions(pes);
                        } else { //???????????????
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
     * ????????????
     *
     * @param clz ??????????????????Activity???
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * ????????????
     *
     * @param clz    ??????????????????Activity???
     * @param bundle ????????????????????????
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null, null);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        startContainerActivity(canonicalName, bundle, null);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     * @param activityName  ???????????????ActivityName
     */
    public void startContainerActivity(String canonicalName, String activityName) {
        startContainerActivity(canonicalName, null, activityName);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     * @param bundle        ????????????????????????
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
