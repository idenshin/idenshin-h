package com.zz.danmakuall;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * @author zhangzhao
 * @date 2021/7/14 5:18 下午
 * @describes
 */
public abstract class BaseActivity extends AppCompatActivity {

    abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        doInitView();
        doInitData();
        doInitListener();
    }

    protected void doInitView() {

    }

    protected void doInitData() {

    }

    protected void doInitListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
