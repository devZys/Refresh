package com.refresh.www.OtherUtils.TakePhotoUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.refresh.www.OtherUtils.FileUtils.FileUtils;
import com.refresh.www.OtherUtils.TimeUtils.TimeUtils;
import com.refresh.www.UiShowUtils.PopMessageUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yy on 2017/9/4.
 * 显示和上传用户头像
 */
public class HeadImageUtil {
  //*****************摄像头头像********************************//
    /* 头像文件   */
  public static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
  /* 请求识别码 */
  public static final int CODE_GALLERY_REQUEST = 0xa0;
  public static final int CODE_CAMERA_REQUEST = 0xa1;
  public static final int CODE_RESULT_REQUEST = 0xa2;
  // 裁剪后图片的宽(X)和高(Y),120 X 120的正方形。
  public static int output_X = 150;
  public static int output_Y = 150;

  /*********************************************************************************************************
   * * 函数名称: void choseHeadImageFromGallery()
   * * 功能说明：从本地相册选取图片作为头像
   ********************************************************************************************************/
  public static void choseHeadImageFromGallery(Activity activity) {
    Intent intentFromGallery = new Intent(Intent.ACTION_PICK);
    intentFromGallery.setType("image/*");                                       // 设置文件类型
    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
    activity.startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
  }

  /*********************************************************************************************************
   * * 函数名称: void choseHeadImageFromCameraCapture()
   * * 功能说明： 启动手机相机拍摄照片作为头像
   ********************************************************************************************************/
  public static void choseHeadImageFromCameraCapture(Activity activity) {
    Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // 判断存储卡是否可用，存储照片文件
    if (hasSdcard())
      intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
    activity.startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
  }

  /*********************************************************************************************************
   * * 函数名称: void cropRawPhoto(Uri uri)
   * * 功能说明：  裁剪原始的图片
   ********************************************************************************************************/
  public static void cropRawPhoto(Activity activity, Uri uri) {
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(uri, "image/*");
    // 设置裁剪
    intent.putExtra("crop", "true");
    // aspectX , aspectY :宽高的比例
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    // outputX , outputY : 裁剪图片宽高
    intent.putExtra("outputX", output_X);
    intent.putExtra("outputY", output_Y);
    intent.putExtra("return-data", true);
    activity.startActivityForResult(intent, CODE_RESULT_REQUEST);
  }

  public static File changeBitmapFile(Bitmap bitmap) {
    File file = new File(FileUtils.getFilePath() + TimeUtils.getNowTime() + ".jpg");//将要保存图片的路径
    PopMessageUtil.Log("图片存储地址=" + FileUtils.getFilePath() + TimeUtils.getNowTime() + ".jpg");
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
      bos.flush();
      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  /*********************************************************************************************************
   * * 函数名称: static boolean hasSdcard()
   * * 功能说明：检查设备是否存在SDCard的工具方法
   ********************************************************************************************************/
  public static boolean hasSdcard() {
    String state = Environment.getExternalStorageState();
    if (state.equals(Environment.MEDIA_MOUNTED)) {
      // 有存储的SDCard
      return true;
    } else {
      return false;
    }
  }
}