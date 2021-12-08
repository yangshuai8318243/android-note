package com.mengjia.baseLibrary.app;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;


import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mengjia.baseLibrary.R;

public class ContainerAppCompatActivity extends BaseAppCompatActivity {
    public static final String FRAGMENT = "fragment";
    public static final String ACTIVITY_NAME = "ActivityName";
    public static final String BUNDLE = "bundle";

    private static final String FRAGMENT_TAG = "content_fragment_tag";
    protected WeakReference<Fragment> mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG);
        }else {
            if (fragment == null) {
                fragment = initFromIntent(getIntent());
            }
            if (fragment != null) {
                addFragment(fragment, R.id.container);
            }
        }
        mFragment = new WeakReference<>(fragment);

    }


    private Fragment initFromIntent(Intent data) {
        if (data == null) {
            throw new RuntimeException(
                    "you must provide a page info to display");
        }
        try {
            String fragmentName = data.getStringExtra(FRAGMENT);
            String name = data.getStringExtra(ACTIVITY_NAME);
            ActionBar actionBar = getActionBar();
            androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();

            if (actionBar != null && name != null) {
                actionBar.setTitle(name);
            }

            if (supportActionBar != null && name != null) {
                supportActionBar.setTitle(name);
            }

            if (fragmentName == null || "".equals(fragmentName)) {
                throw new IllegalArgumentException("can not find page fragmentName");
            }
            Class<?> fragmentClass = Class.forName(fragmentName);
            boolean assignableFrom = Fragment.class.isAssignableFrom(fragmentClass);

            if (assignableFrom) {
                Fragment fragment = (Fragment) fragmentClass.newInstance();

                Bundle args = data.getBundleExtra(BUNDLE);

                if (args != null) {
                    fragment.setArguments(args);
                }
                return fragment;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("fragment initialization failed!");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, mFragment.get());
    }

}
