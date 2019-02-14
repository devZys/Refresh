package com.refresh.www.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refresh.www.Adapter.PicAdapter;
import com.refresh.www.Application.PublicUrl;
import com.refresh.www.BmobObject.Http.BmobHttps;
import com.refresh.www.H5WebviewUtils.H5WebviewUtils;
import com.refresh.www.OtherUtils.AnimationUtil.AnimationUtil;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.HorizontalListView;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.SwitchUtil;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends Activity {
    public com.tencent.smtt.sdk.WebView webView;
    //*********照片墙**********//
    public RelativeLayout userInfo_layout,ShowLoading_layout;
    public LinearLayout MainFunction_layout;
    public TextView picNumber_txt;
    public HorizontalListView picListview;
    public PicAdapter picAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUi();
        InitH5Web();
    }

    private void InitUi() {
        userInfo_layout = (RelativeLayout) findViewById(R.id.userInfo_layout);
        ShowLoading_layout = (RelativeLayout) findViewById(R.id.ShowLoading_layout);
        MainFunction_layout = (LinearLayout) findViewById(R.id.MainFunction_layout);
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
        picNumber_txt   = (TextView) findViewById(R.id.picNumber_txt);
        //===============Listview================//
        picListview = (HorizontalListView) findViewById(R.id.pic_listview);
        picListview.setLayoutAnimation(AnimationUtil.getAnimationController());               //添加切换动画
        picAdapter = new PicAdapter(this);
        picListview.setAdapter(picAdapter);
        picListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BmobHttps.DelSelectUser(MainActivity.this,position);
                return false;
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：初始化内嵌H5
     **********************************************************************************************/
    private void InitH5Web() {
        H5WebviewUtils.InitH5WebViewSettingMethod(MainActivity.this, webView);
        WebviewDealWork();
        LoadWebview(PublicUrl.HomeUrl);
    }

    /***********************************************************************************************
     * * 功能说明：H5主要工作
     **********************************************************************************************/
    private boolean firstLogin = true;
    private void WebviewDealWork() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                PopMessageUtil.Log("内部加载" + url);
                view.loadUrl(url);
                if(url.compareTo(PublicUrl.ChooseShopUrl)==0){
                    PopMessageUtil.showToastLong("Please select the store address!");
                    webView.setVisibility(View.VISIBLE);
                    MainFunction_layout.setVisibility(View.GONE);
                }
                else if(url.compareTo(PublicUrl.CalendarUrl)==0&&firstLogin==true){
                    firstLogin = false;
                    MainFunction_layout.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
                else if(url.contains(PublicUrl.FindCustomerIDUrl)==true){
                    String CustmerID = url.substring(url.indexOf("=")+1,url.length());
                    PopMessageUtil.Log(CustmerID);
                    if(BindFaceState==true)
                        FaceRegisterDialog.showRegisterDialog(MainActivity.this, CustmerID);
                    else
                        BmobHttps.CheckCustoremerID(MainActivity.this, CustmerID);
                }
                return true;
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();   //接受证书
            }
            public void onPageFinished(WebView view, String url) {}
            public void onReceivedError(WebView var1, int var2, String var3, String var4) {}
        });
        //进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 80) {
                    ShowLoading_layout.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：加载新网页
     **********************************************************************************************/
    public void LoadWebview(String url) {
        webView.setVisibility(View.GONE);
        ShowLoading_layout.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    /***********************************************************************************************
     * * 功能说明：返回主界面
     **********************************************************************************************/
    public void ClickTitleMethod(View view){
        webView.setVisibility(View.GONE);
        userInfo_layout.setVisibility(View.GONE);
        MainFunction_layout.setVisibility(View.VISIBLE);
    }

    /***********************************************************************************************
     * * 功能说明：人脸面部识别
     **********************************************************************************************/
    public void ClickMainFaceIdentify(View view){
        SwitchUtil.switchActivity(MainActivity.this, DetectLoginActivity.class).switchToForResult(FACE_USER);
    }

    /***********************************************************************************************
     * * 功能说明：会员预约
     **********************************************************************************************/
    public void ClickMainAppointment(View view){
        MainFunction_layout.setVisibility(View.GONE);
        LoadWebview(PublicUrl.CalendarUrl);
    }

    /***********************************************************************************************
     * * 功能说明：会员预约
     **********************************************************************************************/
    public void ClickMainManagement(View view){
        MainFunction_layout.setVisibility(View.GONE);
        LoadWebview(PublicUrl.SearchCustomersUrl);
    }

    /***********************************************************************************************
     * * 功能说明：处理返回信息
     **********************************************************************************************/
    private int FACE_USER = 1001;
    public boolean BindFaceState = false;
    private String OBJECTID,CUSTORMERID;
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FACE_USER) {
            if (resultCode == RESULT_FIRST_USER) {
                //用户未绑定   关联用户
                MainFunction_layout.setVisibility(View.GONE);
                LoadWebview(PublicUrl.SearchCustomersUrl);
                BindFaceState = true;
                PopMessageUtil.showToastLong("Please search for the member on the web page");
            }
            else if(resultCode == RESULT_OK){
                //面部识别成功
                OBJECTID = intent.getStringExtra("ObjectId");
                CUSTORMERID = intent.getStringExtra("CustormerId");
                BmobHttps.CheckAndUploadByCustormerId(this, CUSTORMERID);
            }
        }
    }
}
