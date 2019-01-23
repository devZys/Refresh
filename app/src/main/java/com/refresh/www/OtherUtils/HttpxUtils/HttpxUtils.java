package com.refresh.www.OtherUtils.HttpxUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by yy on 2017/9/27.
 * 使用方法： 1:build.gradle 添加　  compile 'org.xutils:xutils:3.5.0'
 * ２：application 添加  x.Ext.init(this);   放在  super.onCreate();
 *
 * ====Get请求
 * HttpxUtils.getHttp(XXX).setUrl(XX).addQueryStringParameter(key,value).send()
 *
 * ====Post请求
 * HttpxUtils.postHttp(XXX).setUrl(XX).addBodyParameter(key,value).send()    【addHeader\addBodyParameter\addParameter】
 *
 * ====上传文件
 * HttpxUtils.postHttp(XXX).setUrl(XX).setMultipart().addBodyParameter(key,new File(path)).send()   【String path="/mnt/sdcard/Download/icon.jpg";】
 *
 * ====下载文件
 * HttpxUtils.downloadFiles(new FileCallBack()).setUrl(url).setSaveFilePath(path).downloadFile();
 */
public class HttpxUtils {
    public static String TAG = HttpxUtils.class.getSimpleName();
    private static HttpxUtils instance;

    public static HttpxUtils getInstance() {
        return (instance == null ? instance = new HttpxUtils() : instance);
    }

    //--------------------------------------------------//
    public static PostHttp postHttp(SendCallBack sendCallBack) {
        return getInstance().new PostHttp(sendCallBack);
    }

    public static GetHttp getHttp(SendCallBack sendCallBack) {
        return getInstance().new GetHttp(sendCallBack);
    }

    public static UploadFiles uploadFiles(SendCallBack sendCallBack){
        return getInstance().new UploadFiles(sendCallBack);
    }

    public static DownloadFiles downloadFiles(FileCallBack fileCallBack){
        return getInstance().new DownloadFiles(fileCallBack);
    }
    //--------------------------------------------------//
    public class BaseHttp {
        protected RequestParams params;
        protected SendCallBack sendCallBack;                                //通用返回接口
        protected FileCallBack fileCallBack;                                //文件下载接口
        protected Callback.CommonCallback<String> callBack;                 //通用返回
        protected Callback.ProgressCallback<File> fileBack;                 //文件下载返回

        public BaseHttp(SendCallBack xsendCallBack) {
            this.sendCallBack = xsendCallBack;
        }

        public BaseHttp(FileCallBack xfilecallback){
            this.fileCallBack = xfilecallback;
        }

        public BaseHttp setUrl(String url) {
            params = new RequestParams(url);
            return this;
        }

        //===========Get请求相关============//
        public BaseHttp addQueryStringParameter(String key, String value) {   //get带参数      params.addQueryStringParameter("username","abc");
            params.addQueryStringParameter(key, value);
            return this;
        }

        //===========Post请求相关===========//
        //增加Header
        public BaseHttp addHeader(String key, String value) {
            params.addHeader(key, value);
            return this;
        }

        //增加bodyParameter
        public BaseHttp addBodyParameter(String key, String value) {
            params.addBodyParameter(key, value);
            return this;
        }

        //增加Json数据
        public BaseHttp addJsonParameter(String json){
            params.setAsJsonContent(true);
            params.setBodyContent(json);
            return this;
        }

        //增加parameter
        public BaseHttp addParameter(String key, String value) {
            params.addParameter(key, value);
            return this;
        }

        //==============上传文件====================//    params.setMultipart(true);    params.addBodyParameter("file",new File(path));
        public BaseHttp setMultipart() {
            params.setMultipart(true);
            return this;
        }

        public void send() {
            callBack = new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(final String result) {
                    sendCallBack.onSuccess(result);
                }

                @Override
                public void onError(final Throwable ex, final boolean isOnCallback) {
                    sendCallBack.onError(ex, isOnCallback);
                }

                @Override
                public void onCancelled(final CancelledException cex) {
                    sendCallBack.onCancelled(cex);
                }

                @Override
                public void onFinished() {
                    sendCallBack.onFinished();
                }
            };
        }
        //============下载文件=============//
        public BaseHttp setSaveFilePath(String path){
            params.setSaveFilePath(path);
            return this;
        }

        public void downloadFile() {
            fileBack = new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    fileCallBack.onSuccess(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    fileCallBack.onError(ex, isOnCallback);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    fileCallBack.onCancelled(cex);
                }

                @Override
                public void onFinished() {
                    fileCallBack.onFinished();
                }

                @Override
                public void onWaiting() {
                    fileCallBack.onWaiting();
                }

                @Override
                public void onStarted() {
                    fileCallBack.onStarted();
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    fileCallBack.onLoading(total, current, isDownloading);
                }
            };
        }
    }

    public class PostHttp extends BaseHttp {
        public PostHttp(SendCallBack sendCallBack) {
            super(sendCallBack);
        }

        public void send() {
            super.send();
            new Thread() {
                public void run() {
                    x.http().post(params, callBack);
                }
            }.start();
        }
    }

    public class GetHttp extends BaseHttp {
        public GetHttp(SendCallBack sendCallBack) {
            super(sendCallBack);
        }

        public void send() {
            super.send();
            new Thread() {
                public void run() {
                    x.http().get(params, callBack);
                }
            }.start();
        }
    }

    public class UploadFiles extends BaseHttp {
        public UploadFiles(SendCallBack sendCallBack) { super(sendCallBack);    }
        public void send() {
            super.send();
            new Thread() {
                public void run() {
                    x.http().post(params, callBack);
                }
            }.start();
        }
    }

    public class DownloadFiles extends BaseHttp{
        public DownloadFiles(FileCallBack fileCallBack){    super(fileCallBack);    }
        public void downloadFile(){
            super.downloadFile();
            new Thread(){
                public void run(){
                    x.http().get(params, fileBack);
                }
            }.start();
        }
    }
}
