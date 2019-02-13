package com.refresh.www.BmobObject.Http;

import android.app.Activity;
import android.view.View;

import com.refresh.www.Activity.MainActivity;
import com.refresh.www.Application.PublicUrl;
import com.refresh.www.BmobObject.Object.PicInfo;
import com.refresh.www.BmobObject.Object.UserInfo;
import com.refresh.www.FaceUtils.https.FaceHttpsRequest;
import com.refresh.www.FaceUtils.utils.ImageSaveUtil;
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
 * Created by yy on 2019/2/13.
 */
public class BmobHttps {
    public static UserInfo USERINFO;
    /***********************************************************************************************
     * * 功能说明：用户头像上传 Bomb云平台
     **********************************************************************************************/
    public static void LoadingFacePhotoToBomb(final MainActivity activity,final String CustormerID,final boolean isRegister) {
        final File file = new File(ImageSaveUtil.loadCameraBitmapPath("head_tmp.jpg"));
        if (!file.exists()) return;
        PopMessageUtil.Loading(activity,"更新中");
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    PopMessageUtil.Log("上传文件成功:" + bmobFile.getFileUrl());
                    PopMessageUtil.showToastShort("图片上传成功!");
                    FileUtils.deleteFace(file);
                    if (isRegister)
                        UserRegister(activity,bmobFile.getFileUrl(),CustormerID);
                    else
                        UpdataUserInfo(activity,TimeUtils.getStandardTime(), bmobFile.getFileUrl(),CustormerID);
                } else {
                    PopMessageUtil.Log("上传文件失败：" + e.getMessage());
                    PopMessageUtil.showToastShort("图片上传失败!");
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：关联用户在Bomb后台创建用户
     **********************************************************************************************/
    private static void UserRegister(final Activity activity,String FileUrl,String CustormerID){
        PopMessageUtil.Loading(activity, "关联会员中");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("");
        userInfo.setCustormerID(CustormerID);
        userInfo.setPhoneNumber("");
        userInfo.add("picInfos", new PicInfo(TimeUtils.getStandardTime(), FileUrl));

        userInfo.save(new SaveListener<String>() {
            @Override
            public void done(String OBJECTID, BmobException e) {
                if (e == null) {
                    PopMessageUtil.Log("创建用户成功!" + OBJECTID);
                    PopMessageUtil.showToastShort("会员关联成功!");
                    FaceHttpsRequest.UploadBaiduYun(OBJECTID);
                } else {
                    PopMessageUtil.CloseLoading();
                    PopWindowMessage.PopWinMessage(activity, "关联用户失败!", "失败原因=" + e.getMessage() + "," + e.getErrorCode(), "error");
                }
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：查询会员信息
     **********************************************************************************************/
    public static void CheckAndUploadByCustormerId(final MainActivity activity, final String CUSTOMERID) {
        PopMessageUtil.Log("查詢用戶，CID="+CUSTOMERID);
        BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
        query.addWhereEqualTo("CustormerId", CUSTOMERID);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        PopMessageUtil.Log("查询成功!" + list.size() + "条数据");
                        //-----用户数据拉取-----//
                        USERINFO = list.get(0);
                        LoadingFacePhotoToBomb(activity,CUSTOMERID,false);
                    } else {
                        PopMessageUtil.showToastShort("查询不到该号码会员");
                    }
                } else
                    PopMessageUtil.showToastShort("查询失败" + e.getMessage() + "," + e.getErrorCode());
            }
        });
    }


    /***********************************************************************************************
     * * 功能说明：更新用户新面部信息
     **********************************************************************************************/
    public static void UpdataUserInfo(final MainActivity activity,String nowTime, String fileUrl,final String CustormerID) {
        USERINFO.getPicList().add(new PicInfo(nowTime, fileUrl));
        UserInfo userinfo = new UserInfo();
        userinfo.setValue("picInfos", USERINFO.getPicList());
        userinfo.update(USERINFO.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                PopMessageUtil.CloseLoading();
                if (e == null) {
                    PopMessageUtil.Log("更新成功!");
                    PopMessageUtil.showToastShort("数据同步成功!");
                    //=========UI界面显示===========//
                    activity.MainFunction_layout.setVisibility(View.GONE);
                    activity.webView.setVisibility(View.VISIBLE);
                    activity.userInfo_layout.setVisibility(View.VISIBLE);
                    activity.webView.loadUrl(PublicUrl.FindCustomerIDUrl+CustormerID);

                    activity.picNumber_txt.setText(USERINFO.getPicList().size() + "pcs");
                    activity.picAdapter.UpdataPicInfo(USERINFO.getPicList());
                } else {
                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
                    PopMessageUtil.showToastShort("数据同步失败!");
                }
            }
        });
    }
}
