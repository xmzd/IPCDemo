package com.demo.ipcdemo;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Date    20/09/2017
 * Author  WestWang
 */

public class BaseActivity extends SwipeBackActivity {

    protected SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        setTransparentStatusBar();
    }

    /**
     * 沉浸式状态栏
     */
    protected void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0 full transparent
            Window window = getWindow();
            // 去除半透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4 full transparent
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏颜色
     */
    protected void setStatusBarColor(int color) {
        ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
        setupStatusBarView(contentLayout, color);
        // 设置Activity layout的fitsSystemWindows
        View contentChild = contentLayout.getChildAt(0);
        // 等同于在根布局设置android:fitsSystemWindows="true"
        contentChild.setFitsSystemWindows(true);
    }

    /**
     * 更新状态栏
     */
    private void setupStatusBarView(ViewGroup contentLayout, int color) {
        View statusBarView = null;
        View view = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        contentLayout.addView(view, lp);
        statusBarView = view;
        statusBarView.setBackgroundColor(getResources().getColor(color));
    }

    /**
     * 获取状态栏高度
     *
     * @return int px
     */
    protected int getStatusBarHeight() {
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resId);
    }
}
