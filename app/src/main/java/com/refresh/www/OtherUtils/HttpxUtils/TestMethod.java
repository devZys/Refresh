package com.refresh.www.OtherUtils.HttpxUtils;

/**
 * Created by yy on 2017/9/28.
 */
public @interface TestMethod {
/*
    //================Get请求======================//
    HttpxUtils.getHttp(new SendCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.e("GET",result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("GET","失败"+ex.getMessage());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                Log.e("GET","Cancel");
            }

            @Override
            public void onFinished() {
                Log.e("GET","Finished");
            }
        }).setUrl(url).addQueryStringParameter(key,value).send();

    //=================Post请求====================//
    HttpxUtils.postHttp(new SendCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.e("POST",result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("POST","失败"+ex.getMessage());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                Log.e("POST","Cancel");
            }

            @Override
            public void onFinished() {
                Log.e("POST","Finished");
            }
        })
        .setUrl(url)
        .addBodyParameter(key,value)
        .addBodyParameter(key,value)
        .send();

    //================下载文件=====================//
        String path = Environment.getExternalStorageDirectory().getPath() + "/AdMachine/CanjiaV1.1.4.apk";
        HttpxUtils.downloadFiles(new FileCallBack() {
            @Override
            public void onSuccess(File result) {
                Log.e("下载成功","完成");
                //apk下载完成后，调用系统的安装方法
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("下载错误",ex.toString());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.e("完成下载","完成下载");
            }

            @Override
            public void onWaiting() {
                Log.e("等待下载","等待下载");
            }

            @Override
            public void onStarted() {
                Log.e("开始下载","开始下载");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.e("正在下载",String.valueOf(current)+"/"+String.valueOf(total)+" "+String.valueOf(isDownloading));
            }
        }).setUrl(url)
                .setSaveFilePath(path)
                .downloadFile();

    //===============上传文件=====================//
      String path="/mnt/sdcard/Download/icon.jpg";
      HttpxUtils.postHttp(new SendCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.e("POST",result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("POST","失败"+ex.getMessage());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                Log.e("POST","Cancel");
            }

            @Override
            public void onFinished() {
                Log.e("POST","Finished");
            }
        }).setUrl(PublicUrl.TokenInitUrl)
                .addBodyParameter("file",new File(path)).send();
    */
}
