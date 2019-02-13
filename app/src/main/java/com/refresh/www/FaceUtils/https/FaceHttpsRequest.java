package com.refresh.www.FaceUtils.https;

import android.view.View;

import com.refresh.www.Activity.MainActivity;
import com.refresh.www.Application.APIService;
import com.refresh.www.BmobObject.Http.BmobHttps;
import com.refresh.www.BmobObject.Object.PicInfo;
import com.refresh.www.BmobObject.Object.UserInfo;
import com.refresh.www.FaceUtils.exception.FaceError;
import com.refresh.www.FaceUtils.model.RegResult;
import com.refresh.www.FaceUtils.utils.OnResultListener;
import com.refresh.www.OtherUtils.FileUtils.FileUtils;
import com.refresh.www.OtherUtils.TimeUtils.TimeUtils;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.PopWindowMessage;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

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
    public static void HttpRegisterByFace(MainActivity CONTEXT, String FACEPATH, String CUSTMERID) {
        activity = CONTEXT;
        facePath = FACEPATH;
        customerId = CUSTMERID;

        BmobHttps.LoadingFacePhotoToBomb(activity,customerId,true);
    }



    /***********************************************************************************************
     * * 功能说明：根据用户uid  上传头像到百度云
     **********************************************************************************************/
    public static void UploadBaiduYun(String OBJECTID) {
        PopMessageUtil.Loading(activity,"云端同步中");

        final File file = new File(facePath);
        if (!file.exists()) {
            PopMessageUtil.showToastLong("文件不存在");
            return;
        }
        APIService.getInstance().reg(new OnResultListener<RegResult>() {
            @Override
            public void onResult(RegResult result) {
                FileUtils.deleteFace(file);
                PopMessageUtil.CloseLoading();
                PopMessageUtil.Log(result.toString());
                PopMessageUtil.showToastShort("云端同步完成!");
            }

            @Override
            public void onError(FaceError error) {
                PopMessageUtil.CloseLoading();
                PopWindowMessage.PopWinMessage(activity, "云端同步失败!", "失败原因=" + error.getMessage(), "error");
                error.printStackTrace();
            }
        }, file, OBJECTID, customerId);

    }


}
