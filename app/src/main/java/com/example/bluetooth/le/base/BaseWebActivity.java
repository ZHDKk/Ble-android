package com.example.bluetooth.le.base;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.activity.HomeActivity;
import com.example.bluetooth.le.activity.WelcomeActivity;
import com.example.bluetooth.le.view.ProgressWebView;

/**
 * Created by zhdk on 2017/8/18.
 */

public class BaseWebActivity extends BaseActivity {
    protected ProgressWebView mWebView;
    private ProgressBar web_progressbar;
    private Toolbar toolbar;
    private String url;
    private String title;

    @Override
    public int getContentView() {
        return R.layout.baseweb_layout;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();

         url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BaseWebActivity.this, HomeActivity.class);
                finish();
//                System.exit(0);
            }
        });
        mWebView = (ProgressWebView) findViewById(R.id.baseweb_webview);


        mWebView.setInitialScale(25);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setSavePassword(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setUseWideViewPort(true);
        initData();
    }

    private void initData() {


        if(url!=null) {
            mWebView.loadUrl(url);
        }
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

//        mWebView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){
//                    if (i== KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
//                        mWebView.goBack();
//                    }else {
//                        startActivity(BaseWebActivity.this, HomeActivity.class);
//                        finish();
//                    }
//                }
//                return true;
//            }
//        });
    }

    @Override
    public void setWindowFeature() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }
            else{
                startActivity(BaseWebActivity.this, HomeActivity.class);
                finish();
//                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView!=null) {
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }
}
