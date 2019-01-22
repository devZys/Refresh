package com.refresh.www.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.refresh.www.Application.PublicUrl;
import com.refresh.www.H5WebviewUtils.H5WebviewUtils;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.bmob.v3.Bmob;

public class MainActivity extends Activity {
    private com.tencent.smtt.sdk.WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUi();
        InitH5Web();
//        InitBmob();
    }

    private void InitUi() {
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
    }

    /***********************************************************************************************
     * * 功能说明：初始化内嵌H5
     **********************************************************************************************/
    private void InitH5Web() {
        H5WebviewUtils.InitH5WebViewSettingMethod(MainActivity.this, webView);
        WebviewDealWork();
        webView.loadUrl(PublicUrl.HomeUrl);
    }

    /***********************************************************************************************
     * * 功能说明：初始化Bmob
     **********************************************************************************************/
    private void InitBmob(){
        //第一：默认初始化
        Bmob.initialize(this, "48616d9dc7838d737049b2c36d43268a");
    }

    /***********************************************************************************************
     * * 功能说明：H5主要工作
     **********************************************************************************************/
    private void WebviewDealWork() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                PopMessageUtil.Log("内部加载" + url);
                view.loadUrl(url);
                return true;
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed();   //接受证书
                //handleMessage(Message msg); 其他处理
            }
            public void onPageFinished(WebView view, String url) {}
            public void onReceivedError(WebView var1, int var2, String var3, String var4) { PopMessageUtil.Log("网页加载失败");}
        });
        //进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 80) {
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void ClickPeopleMethod(View view){
        webView.loadUrl(PublicUrl.BobTan);
    }

}
