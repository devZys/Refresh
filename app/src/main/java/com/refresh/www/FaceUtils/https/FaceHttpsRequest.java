package com.refresh.www.FaceUtils.https;

import com.refresh.www.Activity.MainActivity;
import com.refresh.www.Application.APIService;
import com.refresh.www.BmobObject.Http.BmobHttps;
import com.refresh.www.FaceUtils.exception.FaceError;
import com.refresh.www.FaceUtils.model.RegResult;
import com.refresh.www.FaceUtils.utils.ImageSaveUtil;
import com.refresh.www.FaceUtils.utils.OnResultListener;
import com.refresh.www.OtherUtils.FileUtils.FileUtils;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.PopWindowMessage;

import java.io.File;

/**
 * Created by yy on 2018/6/16.
 */
public class FaceHttpsRequest {
    private static MainActivity activity;
    private static String facePath;
    private static String customerId;
    /***********************************************************************************************
     * * 功能说明：根据用户会员码进行注册
     **********************************************************************************************/
    public static void HttpRegisterByFace(MainActivity CONTEXT, String CUSTMERID) {
        activity = CONTEXT;
        customerId = CUSTMERID;

        BmobHttps.LoadingFacePhotoToBomb(activity,customerId,true);
    }

    /***********************************************************************************************
     * * 功能说明：根据用户uid  上传头像到百度云
     **********************************************************************************************/
    public static void UploadBaiduYun(String OBJECTID, final String CUSTORMERID) {
        PopMessageUtil.Loading(activity,"Cloud synchronization");
        final File file = new File(ImageSaveUtil.loadCameraBitmapPath("head_tmp.jpg"));
        if (!file.exists()) {
            PopMessageUtil.showToastLong("file does not exist");
            PopMessageUtil.Log("百度云文件不存在");
            return;
        }
        APIService.getInstance().reg(new OnResultListener<RegResult>() {
            @Override
            public void onResult(RegResult result) {
                FileUtils.deleteFace(file);
                PopMessageUtil.CloseLoading();
                PopMessageUtil.Log(result.toString());
                PopMessageUtil.showToastShort("Cloud synchronization completed!");
                activity.BindFaceState = false;             //绑定成功
                BmobHttps.CheckCustoremerID(activity,CUSTORMERID,true);
            }

            @Override
            public void onError(FaceError error) {
                FileUtils.deleteFace(file);
                PopMessageUtil.CloseLoading();
                PopWindowMessage.PopWinMessage(activity, "Cloud Fail!", "Cloud synchronization failed=" + error.getMessage(), "error");
                error.printStackTrace();
            }
        }, file, OBJECTID, customerId);

    }


}
