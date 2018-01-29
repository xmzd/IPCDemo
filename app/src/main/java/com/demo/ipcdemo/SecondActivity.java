package com.demo.ipcdemo;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * Date    20/09/2017
 * Author  WestWang
 */

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setStatusBarColor(R.color.colorPrimary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}
