package com.refresh.www.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.refresh.www.Adapter.PicAdapter;
import com.refresh.www.Application.PublicUrl;
import com.refresh.www.BmobObject.Http.BmobHttps;
import com.refresh.www.H5WebviewUtils.H5WebviewUtils;
import com.refresh.www.OtherUtils.AnimationUtil.AnimationUtil;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.SwitchUtil;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public com.tencent.smtt.sdk.WebView webView;
    public RelativeLayout ShowLoading_layout;
    public LinearLayout MainFunction_layout, userInfo_layout;
    public TextView picNumber_txt;
    public ListView picListview;
    public PicAdapter picAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUi();
        InitH5Web();
        CheckUpdata();
    }

    private void InitUi() {
        userInfo_layout = (LinearLayout) findViewById(R.id.userInfo_layout);
        ShowLoading_layout = (RelativeLayout) findViewById(R.id.ShowLoading_layout);
        MainFunction_layout = (LinearLayout) findViewById(R.id.MainFunction_layout);
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
        picNumber_txt = (TextView) findViewById(R.id.picNumber_txt);
        //===============Listview================//
        picListview = (ListView) findViewById(R.id.pic_listview);
        picListview.setLayoutAnimation(AnimationUtil.getAnimationController());               //添加切换动画
        picAdapter = new PicAdapter(this);
        picListview.setAdapter(picAdapter);
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
    private boolean reLogin = false;

    private void WebviewDealWork() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                PopMessageUtil.Log("内部加载" + url);
                view.loadUrl(url);
                if (url.compareTo(PublicUrl.ChooseShopUrl) == 0) {
                    PopMessageUtil.showToastLong("Please select the store address!");
                    webView.setVisibility(View.VISIBLE);
                    MainFunction_layout.setVisibility(View.GONE);
                } else if (url.contains(PublicUrl.ReLoginUrl) == true && reLogin == false) {
                    reLogin = true;
                    PopMessageUtil.showToastLong("Please login again!");
                } else if (url.compareTo(PublicUrl.LoginUrl) == 0 && reLogin == true) {
                    reLogin = false;
                    view.loadUrl(PublicUrl.ChooseShopUrl);
                } else if (url.compareTo(PublicUrl.CalendarUrl) == 0 && firstLogin == true) {
                    firstLogin = false;
                    MainFunction_layout.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                } else if (url.contains(PublicUrl.FindCustomerIDUrl) == true) {
                    String CustmerID = url.substring(url.indexOf("=") + 1, url.length());
                    PopMessageUtil.Log("URL解析用户ID=" + CustmerID);
                    if (BindFaceState == true)
                        FaceRegisterDialog.showRegisterDialog(MainActivity.this, CustmerID);
                    else
                        BmobHttps.CheckCustoremerID(MainActivity.this, CustmerID, false);
                }
                return true;
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();   //接受证书
            }

            public void onPageFinished(WebView view, String url) {
            }

            public void onReceivedError(WebView var1, int var2, String var3, String var4) {
            }
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
    public void ClickTitleMethod(View view) {
        webView.setVisibility(View.GONE);
        userInfo_layout.setVisibility(View.GONE);
        MainFunction_layout.setVisibility(View.VISIBLE);
    }

    /***********************************************************************************************
     * * 功能说明：人脸面部识别
     **********************************************************************************************/
    private static final int CAMERA_OK = 999;
    private static final int SDCARD_OK = 911;

    public void ClickMainFaceIdentify(View view) {
        CheckPermission();
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //存储权限
            List<String> permissionStrs = new ArrayList<>();

            int hasWriteSdcardPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteSdcardPermission != PackageManager.PERMISSION_GRANTED)
                permissionStrs.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            int hasCameraPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED)
                permissionStrs.add(Manifest.permission.CAMERA);

            String[] stringArray = permissionStrs.toArray(new String[0]);
            if (permissionStrs.size() > 0) {
                requestPermissions(stringArray, SDCARD_OK);
                return;
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_OK);
                else
                    SwitchUtil.switchActivity(MainActivity.this, DetectLoginActivity.class).switchToForResult(FACE_USER);
            }
        } else
            SwitchUtil.switchActivity(MainActivity.this, DetectLoginActivity.class).switchToForResult(FACE_USER);
    }

    /***********************************************************************************************
     * * 功能说明：会员预约
     **********************************************************************************************/
    public void ClickMainAppointment(View view) {
        MainFunction_layout.setVisibility(View.GONE);
        LoadWebview(PublicUrl.CalendarUrl);
    }

    /***********************************************************************************************
     * * 功能说明：会员预约
     **********************************************************************************************/
    public void ClickMainManagement(View view) {
        MainFunction_layout.setVisibility(View.GONE);
        LoadWebview(PublicUrl.SearchCustomersUrl);
    }

    /***********************************************************************************************
     * * 功能说明：人脸AI分析
     **********************************************************************************************/
    public void ClickMainFaceAiAnalysis(View view) {
        SwitchUtil.switchActivity(MainActivity.this, FaceAnalysisActivity.class).switchTo();
    }

    private void CheckUpdata() {
        webView.setVisibility(View.GONE);
        PopMessageUtil.Loading(MainActivity.this, "Version verification");
        PgyUpdateManager.register(MainActivity.this, new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                PopMessageUtil.CloseLoading();
                PopMessageUtil.Log("版本是最新!");
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUpdateAvailable(String s) {
                PopMessageUtil.CloseLoading();
                PopMessageUtil.Log("版本更新=" + s);
                final AppBean appBean = getAppBeanFromString(s);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("App Update")
                        .setMessage("New version V" + appBean.getVersionName() + "\nupdate content" + appBean.getReleaseNote())
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startDownloadTask(MainActivity.this, appBean.getDownloadURL());
                            }
                        }).show();
            }
        });
    }

    /********************************************************************************************
     * * 功能说明：重写onKeyDown(keyCode, event)方法 改写物理按键 返回的逻辑
     ******************************************************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (webView.canGoBack()) {
//                webView.goBack();                       //返回上一页面
//                return true;
//            } else {
            webView.setVisibility(View.GONE);
            userInfo_layout.setVisibility(View.GONE);
            MainFunction_layout.setVisibility(View.VISIBLE);
            return true;
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /***********************************************************************************************
     * * 功能说明：处理返回信息
     **********************************************************************************************/
    private int FACE_USER = 1001;
    public boolean BindFaceState = false;
    private String OBJECTID, CUSTORMERID;

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FACE_USER) {
            if (resultCode == RESULT_FIRST_USER) {
                //用户未绑定   关联用户
                MainFunction_layout.setVisibility(View.GONE);
                LoadWebview(PublicUrl.SearchCustomersUrl);
                BindFaceState = true;
                PopMessageUtil.showToastLong("Please search for the member on the web page");
            } else if (resultCode == RESULT_OK) {
                //面部识别成功
                OBJECTID = intent.getStringExtra("ObjectId");
                CUSTORMERID = intent.getStringExtra("CustormerId");
                PopMessageUtil.Loading(this, "Querying user");
                BmobHttps.CheckAndUploadByCustormerId(this, CUSTORMERID);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PopMessageUtil.Log("notifyPermissionsChange");
        if (requestCode == CAMERA_OK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                SwitchUtil.switchActivity(MainActivity.this, DetectLoginActivity.class).switchToForResult(FACE_USER);
            else
                PopMessageUtil.showToastShort("请手动打开相机权限");
        } else if (requestCode == SDCARD_OK) {
            //可以遍历每个权限设置情况
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //这里写你需要相关权限的操作
                PopMessageUtil.showToastLong("输入相关存储权限!");
            } else
                PopMessageUtil.showToastShort("请手动打开存储权限");
        }
    }
}
