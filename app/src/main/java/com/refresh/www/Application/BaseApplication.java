package com.refresh.www.Application;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import org.xutils.x;

/**
 * Created by yy on 2017/9/25.
 */
public class BaseApplication extends Application {
    public static BaseApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        x.Ext.init(this);
        X5Init();
    }

    public static BaseApplication getInstance(){
        return instance;
    }
    private void X5Init(){
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                PopMessageUtil.Log("X5内核 onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                PopMessageUtil.Log("X5内核 onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                PopMessageUtil.Log("X5内核 onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                PopMessageUtil.Log("X5内核 onDownloadProgress:" + i);
            }
        });
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    //隐藏虚拟按键：
    public static void hideTopStatusAndBottomUIMenu(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

//    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Window window = activity.getWindow();
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(activity.getResources().getColor(colorResId));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
