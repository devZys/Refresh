package com.refresh.www.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.refresh.www.Adapter.PicAdapter;
import com.refresh.www.Application.PublicUrl;
import com.refresh.www.BmobObject.PicInfo;
import com.refresh.www.BmobObject.UserInfo;
import com.refresh.www.H5WebviewUtils.H5WebviewUtils;
import com.refresh.www.OtherUtils.AnimationUtil.AnimationUtil;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.HorizontalListView;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends Activity {
    private com.tencent.smtt.sdk.WebView webView;
    //*********照片墙**********//
    private LinearLayout userInfo_layout;
    private TextView phoneNumber_txt,picNumber_txt;
    private HorizontalListView picListview;
    private PicAdapter picAdapter;
    public UserInfo USERINFO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUi();
        InitH5Web();
//        CheckUserPhoneNumber("98674625");
    }

    private void InitUi() {
        userInfo_layout = (LinearLayout) findViewById(R.id.userInfo_layout);
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
        phoneNumber_txt = (TextView) findViewById(R.id.phoneNumber_txt);
        picNumber_txt   = (TextView) findViewById(R.id.picNumber_txt);
        //===============Listview================//
        picListview = (HorizontalListView) findViewById(R.id.pic_listview);
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
        webView.loadUrl(PublicUrl.HomeUrl);
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

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();   //接受证书
            }

            public void onPageFinished(WebView view, String url) {
            }

            public void onReceivedError(WebView var1, int var2, String var3, String var4) {
                PopMessageUtil.Log("网页加载失败");
            }
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

    /***********************************************************************************************
     * * 功能说明：查询用户信息
     **********************************************************************************************/
    private void CheckUserPhoneNumber(String s) {
        BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
        query.addWhereEqualTo("phoneNumber", s);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        PopMessageUtil.Log("查询成功!" + list.size() + "条数据");
                        USERINFO = list.get(0);
                        phoneNumber_txt.setText(USERINFO.getPhoneNumber());
                        picNumber_txt.setText(USERINFO.getPicList().size()+"pcs");
                        picAdapter.UpdataPicInfo(USERINFO.getPicList());
                    } else {
                        PopMessageUtil.showToastShort("查询不到该号码会员");
                    }
                } else
                    PopMessageUtil.showToastShort("查询失败" + e.getMessage() + "," + e.getErrorCode());
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：添加用户新面部信息
     **********************************************************************************************/
    public void UpdataUserInfo(String nowTime, String fileUrl) {
        USERINFO.getPicList().add(new PicInfo(nowTime, fileUrl));
        UserInfo userinfo = new UserInfo();
        userinfo.setValue("picInfos", USERINFO.getPicList());
        userinfo.update(USERINFO.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    PopMessageUtil.Log("更新成功!");
                    PopMessageUtil.showToastShort("数据同步成功!");
                } else {
                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
                    PopMessageUtil.showToastShort("数据同步失败!");
                }
            }
        });
    }
}
