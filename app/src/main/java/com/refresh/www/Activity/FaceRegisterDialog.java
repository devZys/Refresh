package com.refresh.www.Activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.refresh.www.FaceUtils.utils.ImageSaveUtil;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.SwitchUtil;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by yy on 2018/6/14.
 */
public class FaceRegisterDialog {
    private static MainActivity activity;
    private static Dialog dialog;
    private static Window window;
    private static Button btnDialogConfirm, btnDialogCancle;
    private static CircleImageView avatar_iv;
    private static String facePath,faceType;


    public static void showRegisterDialog(MainActivity context,String CustmerID) {
        activity = context;
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_face_bind, null);
        dialog = new Dialog(activity, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        btnDialogConfirm = (Button) window.findViewById(R.id.btnConfirm);
        btnDialogCancle = (Button) window.findViewById(R.id.btnCancle);
        avatar_iv = (CircleImageView) window.findViewById(R.id.avatar_iv);

        ShowHeadPic();
        btnDialogConfirm.setOnClickListener(new ClickConfirmButtonMethod());
        btnDialogCancle.setOnClickListener(new ClickCancleButtonMethod());
    }

    private static Bitmap mHeadBmp;
    private static void ShowHeadPic() {
        facePath = ImageSaveUtil.loadCameraBitmapPath(activity, "head_tmp.jpg");
        if (mHeadBmp != null) {
            mHeadBmp.recycle();
        }
        mHeadBmp = ImageSaveUtil.loadBitmapFromPath(activity, facePath);
        PopMessageUtil.Log("" + mHeadBmp.getByteCount());
        if (mHeadBmp != null)
            avatar_iv.setImageBitmap(mHeadBmp);
        else{
            PopMessageUtil.showToastShort("加载图片失败!");
            activity.finish();
        }
    }
    /********************************************************************
     * * 功能说明： 点击开通人脸
     ********************************************************************/
    static class ClickConfirmButtonMethod implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
//            FaceRegisterRequest.HttpRegisterByUid(activity, facePath, chinaUid,faceType);
        }
    }
    /********************************************************************
     * * 功能说明： 点击取消开通人脸
     ********************************************************************/
    static class ClickCancleButtonMethod implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            SwitchUtil.switchActivity(activity, MainActivity.class).switchToFinish();
        }
    }
}
