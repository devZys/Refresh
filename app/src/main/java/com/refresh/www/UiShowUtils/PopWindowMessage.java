package com.refresh.www.UiShowUtils;

import android.app.Activity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by yy on 2017/9/29.
 */
public class PopWindowMessage {
    private static SweetAlertDialog sweetAlertDialog;
    /*      error  success  warning  normal      */
    public static void PopWinMessage(Activity activity,String title,String context,String type) {
        int info_type = 0;
        if(type.equals("error")==true)
            info_type = 1;
        else if(type.equals("success")==true)
            info_type = 2;
        else if(type.equals("warning")==true)
            info_type = 3;
        else if(type.equals("normal")==true)
            info_type = 0;

        if (sweetAlertDialog == null)
            sweetAlertDialog = new SweetAlertDialog(activity,info_type);
        sweetAlertDialog
                .setTitleText(title)
                .setContentText(context)
                .setConfirmText("确认")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sw) {
                        sweetAlertDialog.cancel();
                        sweetAlertDialog = null;
                    }
                })
                .changeAlertType(info_type);
        sweetAlertDialog.setCanceledOnTouchOutside(true);
        sweetAlertDialog.show();
    }
}
