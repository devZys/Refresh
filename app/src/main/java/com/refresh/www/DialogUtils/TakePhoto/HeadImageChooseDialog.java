package com.refresh.www.DialogUtils.TakePhoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.refresh.www.Activity.LoginActivity;
import com.refresh.www.OtherUtils.TimeUtils.TimeUtils;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by yy on 2017/10/10.
 */
public class HeadImageChooseDialog {
    public static void ChangeFacePhotoMethod(final Activity activity){
        final AlertDialog myDialog = new AlertDialog.Builder(activity, R.style.dialog).create();
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.getWindow().setContentView(R.layout.dialog_facepic);

        Window window = myDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);                                                          //此处可以设置dialog显示的位置

        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = myDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth());                                                      // 设置宽度
        myDialog.getWindow().setAttributes(lp);

        Button facepic_location_btn = (Button) myDialog.getWindow().findViewById(R.id.facepic_location_btn);
        Button facepic_camera_btn = (Button) myDialog.getWindow().findViewById(R.id.facepic_camera_btn);
        Button facepic_cancel_btn = (Button) myDialog.getWindow().findViewById(R.id.facepic_cancel_btn);
        myDialog.getWindow().findViewById(R.id.facepic_location_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeadImageUtil.choseHeadImageFromGallery(activity);
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().findViewById(R.id.facepic_camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeadImageUtil.choseHeadImageFromCameraCapture(activity);
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().findViewById(R.id.facepic_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }

    /*********************************************************************************************
     * * 函数名称: void setImageToHeadView(Intent intent)
     * * 功能说明：提取保存裁剪之后的图片数据，并设置头像部分的View
     ********************************************************************************************/
    public static void setImageToHeadView(LoginActivity activity, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            File photoFile = HeadImageUtil.changeBitmapFile(photo);                                 //头像图片转文件
            LoadingFacePhotoMethod(activity,photoFile);                                             //上传头像
        }
    }

    private static void LoadingFacePhotoMethod(final LoginActivity activity,File photofile) {
//        BmobFile bmobFile = new BmobFile(photofile);
//        UserInfo userInfo = new UserInfo();
//        //注意：不能调用gameScore.setObjectId("")方法
//        userInfo.setPhoneNumber("98674625");
//        userInfo.setCustormerID("10010");
//        userInfo.setUserName("Hari");
//        userInfo.setAvatar(bmobFile);
//        userInfo.save(new SaveListener<String>() {
//
//            @Override
//            public void done(String objectId, BmobException e) {
//                PopMessageUtil.CloseLoading();
//                if (e == null) {
//                    PopMessageUtil.showToastShort("创建数据成功：" + objectId);
//                } else {
//                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
//                }
//            }
//        });
//        bmobFile.uploadblock(new UploadFileListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null)
//                    PopMessageUtil.showToastShort("上传文件成功:");
//                else
//                    PopMessageUtil.showToastShort("上传文件失败：" + e.getMessage());
//            }
//            @Override
//            public void onProgress(Integer value) {
//                // 返回的上传进度（百分比）
//                PopMessageUtil.Log(String.valueOf(value));
//            }
//        });


//        String picPath = "/storage/emulated/0/WDhorse/0123110650.jpg";
        final BmobFile bmobFile = new BmobFile(photofile);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null) {
                    PopMessageUtil.Log("上传文件成功:" + bmobFile.getFileUrl());
                    PopMessageUtil.showToastShort("图片上传成功!");
                    activity.UpdataUserInfo(TimeUtils.getStandardTime(), bmobFile.getFileUrl());
                }
                else {
                    PopMessageUtil.Log("上传文件失败：" + e.getMessage());
                    PopMessageUtil.showToastShort("图片上传失败!");
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
//                PopMessageUtil.Log(""+value);
            }
        });
    }

}
