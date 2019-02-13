//package com.refresh.www.Activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.refresh.www.Adapter.PicAdapter;
//import com.refresh.www.DialogUtils.TakePhoto.HeadImageChooseDialog;
//import com.refresh.www.DialogUtils.TakePhoto.HeadImageUtil;
//import com.refresh.www.R;
//import com.refresh.www.UiShowUtils.HorizontalListView;
//import com.refresh.www.UiShowUtils.PopMessageUtil;
//
//import java.io.File;
//import java.util.List;
//
//import cn.bmob.v3.Bmob;
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
//import cn.bmob.v3.listener.SaveListener;
//import cn.bmob.v3.listener.UpdateListener;
//
///**
// * Created by yy on 2019/1/22.
// */
//public class LoginActivity extends Activity {
//    private EditText phone_etxt;
//    private TextView userName_txt, phoneNumber_txt, customerID_txt;
//    private HorizontalListView picListview;
//    private PicAdapter picAdapter;
//    public UserInfo USERINFO;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        InitUi();
//    }
//
//    private void InitUi() {
//        phone_etxt = (EditText) findViewById(R.id.phone_etxt);
//        userName_txt = (TextView) findViewById(R.id.userName_txt);
//        phoneNumber_txt = (TextView) findViewById(R.id.phoneNumber_txt);
//        customerID_txt = (TextView) findViewById(R.id.customerID_txt);
//
//        //===============Listview================//
////        picListview = (HorizontalListView) findViewById(R.id.pic_listview);
////        picListview.setLayoutAnimation(AnimationUtil.getAnimationController());               //添加切换动画
////        picAdapter = new PicAdapter(this);
////        picListview.setAdapter(picAdapter);
//    }
//
//    /***********************************************************************************************
//     * * 功能说明：初始化Bmob
//     **********************************************************************************************/
//    private void InitBmob() {
//        Bmob.initialize(this, "48616d9dc7838d737049b2c36d43268a");
//    }
////    /***********************************************************************************************
////     * * 功能说明：创建数据
////     **********************************************************************************************/
////    public void ClickCreateMethod(View view) {
////        GameScore gameScore = new GameScore();
////        //注意：不能调用gameScore.setObjectId("")方法
////        gameScore.setPlayerName("刘洋 ");
////        gameScore.setScore(Integer.parseInt(edit_etxt.getText().toString()));
////        gameScore.setIsPay(false);
////        gameScore.save(new SaveListener<String>() {
////
////            @Override
////            public void done(String objectId, BmobException e) {
////                if (e == null) {
////                    PopMessageUtil.showToastShort("创建数据成功：" + objectId);
////                } else {
////                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
////                }
////            }
////        });
////    }
////
////    /***********************************************************************************************
////     * * 功能说明：更新数据
////     **********************************************************************************************/
////    public void ClickUpdateMethod(View view) {
////        GameScore gameScore = new GameScore();
////        gameScore.setValue("score", Integer.parseInt(editText2.getText().toString()));
////        gameScore.update("25eda2d15a", new UpdateListener() {
////            @Override
////            public void done(BmobException e) {
////                if (e == null) {
////                    PopMessageUtil.showToastShort("更新成功");
////                } else {
////                    PopMessageUtil.showToastShort("更新失败：" + e.getMessage() + "," + e.getErrorCode());
////                }
////            }
////
////        });
////    }
////
////    /***********************************************************************************************
////     * * 功能说明：查询数据
////     **********************************************************************************************/
////    public void ClickCheckMethod(View view) {
////        BmobQuery<GameScore> query = new BmobQuery<GameScore>();
////        //查询playerName叫“比目”的数据
////        query.addWhereEqualTo("score", 99);
////        //返回50条数据，如果不加上这条语句，默认返回10条数据
////        query.setLimit(50);
////        //执行查询方法
////        query.findObjects(new FindListener<GameScore>() {
////            @Override
////            public void done(List<GameScore> object, BmobException e) {
////                if (e == null) {
////                    PopMessageUtil.showToastShort("查询成功：共" + object.size() + "条数据");
////                    for (GameScore gameScore : object) {
////                        //获得playerName的信息
////                        gameScore.getPlayerName();
////                        //获得数据的objectId信息
////                        gameScore.getObjectId();
////                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
////                        gameScore.getCreatedAt();
////                    }
////                } else {
////                    PopMessageUtil.showToastShort("失败：" + e.getMessage() + "," + e.getErrorCode());
////                }
////            }
////        });
////    }
//    /***********************************************************************************************
//     * * 功能说明：查询用户表
//     **********************************************************************************************/
//    public void ClickCheckUserPhoneMethod(View view) {
//        BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
//        query.addWhereEqualTo("phoneNumber", phone_etxt.getText().toString());
//        query.findObjects(new FindListener<UserInfo>() {
//            @Override
//            public void done(List<UserInfo> list, BmobException e) {
//                if (e == null) {
//                    if (list.size() != 0) {
//                        PopMessageUtil.Log("查询成功!" + list.size() + "条数据");
//                        USERINFO = list.get(0);
//                        userName_txt.setText(USERINFO.getUserName());
//                        phoneNumber_txt.setText(USERINFO.getPhoneNumber());
//                        customerID_txt.setText(USERINFO.getCustormerID());
//
//                        picAdapter.UpdataPicInfo(USERINFO.getPicList());
//                    } else {
//                        PopMessageUtil.showToastShort("查询不到该号码会员");
//                    }
//                } else
//                    PopMessageUtil.showToastShort("查询失败" + e.getMessage() + "," + e.getErrorCode());
//            }
//        });
//    }
//
//    public void CreateUserInfo(String url) {
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUserName("Hari");
//        userInfo.setCustormerID("10010");
//        userInfo.setPhoneNumber("98674625");
//        userInfo.add("picInfos", new PicInfo("2019-1-23 11:52:30", url));
//
//        userInfo.save(new SaveListener<String>() {
//            @Override
//            public void done(String objectId, BmobException e) {
//                if (e == null)
//                    PopMessageUtil.Log("创建成功!" + objectId);
//                else
//                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
//            }
//        });
//    }
//
//
//    public void ClickTakePhotoMethod(View view) {
//        HeadImageChooseDialog.ChangeFacePhotoMethod(LoginActivity.this);
////        String picPath = "/storage/emulated/0/WDhorse/0123112101.jpg";
////        final BmobFile bmobFile = new BmobFile(new File(picPath));
////        bmobFile.uploadblock(new UploadFileListener() {
////            @Override
////            public void done(BmobException e) {
////                if (e == null) {
////                    PopMessageUtil.Log("上传文件成功:" + bmobFile.getFileUrl());
////                    UpdataUserInfo(TimeUtils.getNowTime(), bmobFile.getFileUrl());
////                } else
////                    PopMessageUtil.Log("上传文件失败：" + e.getMessage());
////            }
////
////            @Override
////            public void onProgress(Integer value) {
////                // 返回的上传进度（百分比）
//////                PopMessageUtil.Log(""+value);
////            }
////        });
//    }
//
//    public void UpdataUserInfo(String nowTime, String fileUrl) {
//        USERINFO.getPicList().add(new PicInfo(nowTime, fileUrl));
//        UserInfo userinfo = new UserInfo();
//        userinfo.setValue("picInfos", USERINFO.getPicList());
//        userinfo.update(USERINFO.getObjectId(),new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if (e == null) {
//                    PopMessageUtil.Log("更新成功!");
//                    PopMessageUtil.showToastShort("数据同步成功!");
//                }
//                else {
//                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
//                    PopMessageUtil.showToastShort("数据同步失败!");
//                }
//            }
//        });
//    }
//
//    /**********************************************************************************************
//     * * 功能说明：回调函数返回值
//     *********************************************************************************************/
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, intent);
//        switch (requestCode) {
//            case HeadImageUtil.CODE_GALLERY_REQUEST:
//                if (intent != null)
//                    HeadImageUtil.cropRawPhoto(LoginActivity.this, intent.getData());
//                break;
//            case HeadImageUtil.CODE_CAMERA_REQUEST:
//                if (HeadImageUtil.hasSdcard()) {
//                    File tempFile = new File(Environment.getExternalStorageDirectory(), HeadImageUtil.IMAGE_FILE_NAME);
//                    HeadImageUtil.cropRawPhoto(LoginActivity.this, Uri.fromFile(tempFile));
//                } else
//                    PopMessageUtil.showToastShort("未检测到SD卡");
//                break;
//            case HeadImageUtil.CODE_RESULT_REQUEST:
//                if (intent != null) {
//                    HeadImageChooseDialog.setImageToHeadView(LoginActivity.this, intent);
//                }
//                break;
//        }
//    }
//}
