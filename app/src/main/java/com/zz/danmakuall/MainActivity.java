package com.zz.danmakuall;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_start)
    TextView mStart;

    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void doInitListener() {
        super.doInitListener();
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DanmuActivity.class);
                startActivity(intent);
            }
        });
    }
}