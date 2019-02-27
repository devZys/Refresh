package com.refresh.www.OtherUtils.TakePhotoUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.refresh.www.R;

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
}