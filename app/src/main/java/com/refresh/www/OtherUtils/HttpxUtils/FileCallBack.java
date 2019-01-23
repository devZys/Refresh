package com.refresh.www.OtherUtils.HttpxUtils;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by yy on 2017/9/27.
 */
public interface FileCallBack {
    public void onSuccess(File result);
    public void onError(Throwable ex, boolean isOnCallback);
    public void onCancelled(Callback.CancelledException cex);
    public void onFinished();   //网络请求结束下载完毕回调
    public void onWaiting();    //网络请求之前回调
    public void onStarted();    //网络请求开始的时候回调
    public void onLoading(long total, long current, boolean isDownloading);  //当前进度和文件总大小
}
