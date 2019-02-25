package com.refresh.www.BmobObject.Http;

import android.view.View;

import com.refresh.www.Activity.MainActivity;
import com.refresh.www.Application.PublicUrl;
import com.refresh.www.BmobObject.Object.PicInfo;
import com.refresh.www.BmobObject.Object.UserInfo;
import com.refresh.www.FaceUtils.https.FaceHttpsRequest;
import com.refresh.www.FaceUtils.utils.ImageSaveUtil;
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
    public static void LoadingFacePhotoToBomb(final MainActivity activity, final String CustormerID, final boolean isRegister) {
        final File file = new File(ImageSaveUtil.loadCameraBitmapPath("head_tmp.jpg"));
        if (!file.exists()) return;
        PopMessageUtil.Loading(activity, "Updating");
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    PopMessageUtil.Log("[1]上传文件到Bmob平台成功:" + bmobFile.getFileUrl());
                    if (isRegister)
                        UserRegister(activity, bmobFile.getFileUrl(), CustormerID);
                    else
                        UpdataUserInfo(activity, TimeUtils.getStandardTime(), bmobFile.getFileUrl(), CustormerID);
                } else {
                    PopMessageUtil.Log("上传文件失败：" + e.getMessage());
                    PopMessageUtil.showToastShort("Image upload failed!");
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
    private static void UserRegister(final MainActivity activity, String FileUrl, final String CustormerID) {
        PopMessageUtil.Loading(activity, "Member association");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("");
        userInfo.setCustormerID(CustormerID);
        userInfo.setPhoneNumber("");
        userInfo.add("picInfos", new PicInfo(TimeUtils.getStandardTime(), FileUrl));

        userInfo.save(new SaveListener<String>() {
            @Override
            public void done(String OBJECTID, BmobException e) {
                if (e == null) {
                    PopMessageUtil.Log("[2]Bmob云平台创建用户成功!" + OBJECTID);
                    PopMessageUtil.showToastShort("Member association success!");
                    FaceHttpsRequest.UploadBaiduYun(OBJECTID, CustormerID);
                } else {
                    PopMessageUtil.CloseLoading();
                    PopWindowMessage.PopWinMessage(activity, "Member association error!", "error=" + e.getMessage() + "," + e.getErrorCode(), "error");
                }
            }
        });
    }

    public static void CheckCustoremerID(final MainActivity activity, final String CUSTORMERID,final boolean isLoading) {
        PopMessageUtil.Loading(activity, "Finding members");
        BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
        query.addWhereEqualTo("CustormerId", CUSTORMERID);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                PopMessageUtil.CloseLoading();
                if (e == null) {
                    if (list.size() != 0) {
                        PopMessageUtil.Log("查询成功!" + list.size() + "条数据");
                        USERINFO = list.get(0);
                        ShowCustormerPic(activity, CUSTORMERID,isLoading);
                    } else {
                        PopMessageUtil.showToastShort("Can't find the member");
                    }
                } else
                    PopMessageUtil.showToastShort("Query failed" + e.getMessage() + "," + e.getErrorCode());
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：查询会员信息
     **********************************************************************************************/
    public static void CheckAndUploadByCustormerId(final MainActivity activity, final String CUSTOMERID) {
        PopMessageUtil.Log("查詢用戶，CID=" + CUSTOMERID);
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
                        LoadingFacePhotoToBomb(activity, CUSTOMERID, false);
                    } else {
                        PopMessageUtil.CloseLoading();
                        PopWindowMessage.PopWinMessage(activity, "Warning", "Can't find the member", "warning");
                    }
                } else {
                    PopMessageUtil.CloseLoading();
                    PopWindowMessage.PopWinMessage(activity, "error", "Query failed" + e.getMessage() + "," + e.getErrorCode(), "error");
                }
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：更新用户新面部信息
     **********************************************************************************************/
    public static void UpdataUserInfo(final MainActivity activity, String nowTime, String fileUrl, final String CustormerID) {
        USERINFO.getPicList().add(0, new PicInfo(nowTime, fileUrl));
        UserInfo userinfo = new UserInfo();
        userinfo.setValue("picInfos", USERINFO.getPicList());
        userinfo.update(USERINFO.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                PopMessageUtil.CloseLoading();
                if (e == null) {
                    PopMessageUtil.Log("更新成功!");
                    PopMessageUtil.showToastShort("Data synchronization succeeded!");
                    ShowCustormerPic(activity, CustormerID,true);
                } else {
                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
                    PopMessageUtil.showToastShort("Data synchronization failed!");
                }
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：显示用户人像画册
     **********************************************************************************************/
    public static void ShowCustormerPic(final MainActivity activity, final String CustormerID, boolean isLoading) {
        activity.MainFunction_layout.setVisibility(View.GONE);
        activity.userInfo_layout.setVisibility(View.VISIBLE);
        if (isLoading)
            activity.LoadWebview(PublicUrl.FindCustomerIDUrl + CustormerID);
        activity.picNumber_txt.setText("Total " + USERINFO.getPicList().size() + " photos");
        activity.picAdapter.UpdataPicInfo(USERINFO.getPicList());
    }

    public static void DelSelectUser(final MainActivity activity, final int Postion) {
        PopMessageUtil.Loading(activity, "Deleting image");
        PopMessageUtil.Log("删除文件" + USERINFO.getPicList().get(Postion).getCreateTime());
        //云后台删除文件
        BmobFile file = new BmobFile();
        file.setUrl(USERINFO.getPicList().get(Postion).getUrl());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    USERINFO.getPicList().remove(Postion);
                    UserInfo userinfo = new UserInfo();
                    userinfo.setValue("picInfos", USERINFO.getPicList());
                    userinfo.update(USERINFO.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            PopMessageUtil.CloseLoading();
                            if (e == null) {
                                PopMessageUtil.showToastShort("successfully deleted!");
                                activity.picNumber_txt.setText(USERINFO.getPicList().size() + "pcs");
                                activity.picAdapter.UpdataPicInfo(USERINFO.getPicList());
                            } else {
                                PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
                                PopMessageUtil.showToastShort("Data deletion failed! (Cloud data)");
                            }
                        }
                    });
                } else {
                    PopMessageUtil.CloseLoading();
                    PopMessageUtil.Log("失败：" + e.getErrorCode() + "," + e.getMessage());
                    PopMessageUtil.showToastShort("Data deletion failed!(Cloud file)");
                }
            }
        });
    }
}
