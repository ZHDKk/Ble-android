package com.example.bluetooth.le.activity;

import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.base.BaseWebActivity;

/**
 * Created by zhdk on 2017/8/17.
 */

public class WelcomeActivity extends BaseActivity {
    private TextView tvWeb;
    private boolean isOnClick;
    @Override
    public int getContentView() {
        return R.layout.welcome_layout;
    }

    @Override
    public void initView() {
        tvWeb = (TextView) findViewById(R.id.tvWeb);
        tvWeb.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvWeb.getPaint().setAntiAlias(true);
        tvWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityParam(WelcomeActivity.this, BaseWebActivity.class,"title","www.gforce-tools.com","url","http://www.gforce-tools.com");
                isOnClick = true;
                finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    if (!isOnClick) {
                        startActivity(WelcomeActivity.this, HomeActivity.class);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public void setWindowFeature() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
