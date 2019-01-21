package com.refresh.www.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.refresh.www.Application.PublicUrl;
import com.refresh.www.H5WebviewUtils.H5WebviewUtils;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private com.tencent.smtt.sdk.WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUi();
        H5WebviewUtils.InitH5WebViewSettingMethod(MainActivity.this, webView);
        WebviewDealWork();
        webView.loadUrl(PublicUrl.HomeUrl);
    }

    private void InitUi() {
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
    }

    private void WebviewDealWork() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                PopMessageUtil.Log("内部加载" + url);
//                if(url.contains("tel")==true){
//                    PopMessageUtil.Log("拨打电话" + url);
//                    CallPhone.CallPhone(MainActivity.this,url);
//                }
//                else if (url.compareTo(PublicUrl.MyUrl) != 0 && url.compareTo(PublicUrl.MainUrl) != 0) {
//                    String title = "唐小腰";
//                    if (url.contains(PublicUrl.HomeUrl + "/my/settings") == true)
//                        title = "设置";
//                    SwitchUtil.switchActivity(MainActivity.this, WebviewActivity.class)
//                            .addString("url", url)
//                            .addString("title", title)
//                            .switchToForResult(2);
//                }
                return true;
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
}
