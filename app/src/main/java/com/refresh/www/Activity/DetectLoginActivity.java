/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.refresh.www.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.aip.FaceSDKManager;
import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.idl.facesdk.FaceInfo;
import com.refresh.www.Application.APIService;
import com.refresh.www.FaceUtils.exception.FaceError;
import com.refresh.www.FaceUtils.model.FaceModel;
import com.refresh.www.FaceUtils.model.RegResult;
import com.refresh.www.FaceUtils.utils.ImageSaveUtil;
import com.refresh.www.FaceUtils.utils.ImageUtil;
import com.refresh.www.FaceUtils.utils.OnResultListener;
import com.refresh.www.FaceUtils.widget.BrightnessTools;
import com.refresh.www.FaceUtils.widget.FaceRoundView;
import com.refresh.www.FaceUtils.widget.WaveHelper;
import com.refresh.www.FaceUtils.widget.WaveView;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.SwitchUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * 实时检测调用identify进行人脸识别，MainActivity未给出改示例的入口，开发者可以在MainActivity调用
 * Intent intent = new Intent(MainActivity.this, DetectLoginActivity.class);
 * startActivity(intent);
 */
public class DetectLoginActivity extends AppCompatActivity {
    private final static int MSG_INITVIEW = 1001;
    private final static int MSG_DETECTTIME = 1002;
    private final static int MSG_INITWAVE = 1003;
    private TextView nameTextView;
    private PreviewView previewView;
    private View mInitView;
    //  private TextureView textureView;
    private FaceRoundView rectView;
    private boolean mGoodDetect = false;
    private static final double ANGLE = 15;
    private ImageView closeIv;
    private boolean mDetectStoped = false;
    private ImageView mSuccessView;
    private Handler mHandler;
    //  private boolean mReDetect = true;
    private String mCurTips;
    private boolean mDetectTime = true;
    //  private ProgressBar mProgress;
    private boolean mUploading = false;
    private long mLastTipsTime = 0;
    private int mDetectCount = 0;
    private int mCurFaceId = -1;

    private FaceDetectManager faceDetectManager;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private WaveHelper mWaveHelper;
    private WaveView mWaveview;
    private int mBorderColor = Color.parseColor("#28FFFFFF");
    private int mBorderWidth = 0;
    private int mScreenW;
    private int mScreenH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_detected);
        faceDetectManager = new FaceDetectManager(this);
        initScreen();
        initView();
        mHandler = new InnerHandler(this);
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 500);
    }

    private void initScreen() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;
    }

    private void initView() {
        mInitView = findViewById(R.id.camera_layout);
        previewView = (PreviewView) findViewById(R.id.preview_view);
        rectView = (FaceRoundView) findViewById(R.id.rect_view);
        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.setPreviewView(previewView);

        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(final int retCode, FaceInfo[] infos, ImageFrame frame) {
                if (mUploading) return;
                String str = DealReturnCode(retCode, infos, frame);
                //-----处理人脸数据-----//
                boolean faceChanged = true;
                if (infos != null && infos[0] != null) {
                    PopMessageUtil.Log("face id is:" + infos[0].face_id);
                    if (infos[0].face_id == mCurFaceId)
                        faceChanged = false;
                    else
                        faceChanged = true;
                    mCurFaceId = infos[0].face_id;
                }

                if (faceChanged) {
                    showProgressBar(false);
                    onRefreshSuccessView(false);
                }

                final int resultCode = retCode;
                if (!(mGoodDetect && retCode == 0)) {
                    if (faceChanged) {
                        showProgressBar(false);
                        onRefreshSuccessView(false);
                    }
                }

                if (retCode == 6 || retCode == 7 || retCode < 0)
                    rectView.processDrawState(true);
                else
                    rectView.processDrawState(false);

                mCurTips = str;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ((System.currentTimeMillis() - mLastTipsTime) > 500) {
                            nameTextView.setText(mCurTips);
                            mLastTipsTime = System.currentTimeMillis();
                        }
                        if (mGoodDetect && resultCode == 0) {
                            nameTextView.setText("");
                            onRefreshSuccessView(true);
                            showProgressBar(true);
                        }
                    }
                });
                if (infos == null)
                    mGoodDetect = false;
            }
        });
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(FaceFilter.TrackedModel trackedModel) {
                if (trackedModel.meetCriteria() && mGoodDetect) {
                    upload(trackedModel);
                    mGoodDetect = false;
                }
            }
        });

        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                ActivityCompat.requestPermissions(DetectLoginActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 100);
                return true;
            }
        });

        rectView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                start();
                rectView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        ICameraControl control = cameraImageSource.getCameraControl();
        control.setPreviewView(previewView);
        // 设置检测裁剪处理器
        faceDetectManager.addPreProcessor(cropProcessor);

        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);

        if (isPortrait)
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        else
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        cameraImageSource.getCameraControl().setDisplayOrientation(rotation);
        //   previewView.getTextureView().setScaleX(-1);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        closeIv = (ImageView) findViewById(R.id.closeIv);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSuccessView = (ImageView) findViewById(R.id.success_image);

        mSuccessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mSuccessView.getTag() == null) {
                    Rect rect = rectView.getFaceRoundRect();
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSuccessView.getLayoutParams();
                    int w = (int) getResources().getDimension(R.dimen.success_width);
                    rlp.setMargins(rect.centerX() - (w / 2), rect.top - (w / 2), 0, 0);
                    mSuccessView.setLayoutParams(rlp);
                    mSuccessView.setTag("setlayout");
                }
                mSuccessView.setVisibility(View.GONE);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    mSuccessView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    mSuccessView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        init();
    }

    private String DealReturnCode(int retCode, FaceInfo[] infos, ImageFrame frame) {
        String str = "";
        if (retCode == 0) {
            if (infos != null && infos[0] != null) {
                FaceInfo info = infos[0];
                boolean distance = false;
                if (info != null && frame != null) {
                    if (info.mWidth >= (0.9 * frame.getWidth())) {
                        distance = false;
                        str = getResources().getString(R.string.detect_zoom_out);
                    } else if (info.mWidth <= 0.4 * frame.getWidth()) {
                        distance = false;
                        str = getResources().getString(R.string.detect_zoom_in);
                    } else
                        distance = true;
                }
                boolean headUpDown;
                if (info != null) {
                    if (info.headPose[0] >= ANGLE) {
                        headUpDown = false;
                        str = getResources().getString(R.string.detect_head_up);
                    } else if (info.headPose[0] <= -ANGLE) {
                        headUpDown = false;
                        str = getResources().getString(R.string.detect_head_down);
                    } else
                        headUpDown = true;

                    boolean headLeftRight;
                    if (info.headPose[1] >= ANGLE) {
                        headLeftRight = false;
                        str = getResources().getString(R.string.detect_head_left);
                    } else if (info.headPose[1] <= -ANGLE) {
                        headLeftRight = false;
                        str = getResources().getString(R.string.detect_head_right);
                    } else
                        headLeftRight = true;
                    if (distance && headUpDown && headLeftRight)
                        mGoodDetect = true;
                    else
                        mGoodDetect = false;
                }
            }
        } else if (retCode == 1) {
            str = getResources().getString(R.string.detect_head_up);
        } else if (retCode == 2) {
            str = getResources().getString(R.string.detect_head_down);
        } else if (retCode == 3) {
            str = getResources().getString(R.string.detect_head_left);
        } else if (retCode == 4) {
            str = getResources().getString(R.string.detect_head_right);
        } else if (retCode == 5) {
            str = getResources().getString(R.string.detect_low_light);
        } else if (retCode == 6) {
            str = getResources().getString(R.string.detect_face_in);
        } else if (retCode == 7) {
            str = getResources().getString(R.string.detect_face_in);
        } else if (retCode == 10) {
            str = getResources().getString(R.string.detect_keep);
        } else if (retCode == 11) {
            str = getResources().getString(R.string.detect_occ_right_eye);
        } else if (retCode == 12) {
            str = getResources().getString(R.string.detect_occ_left_eye);
        } else if (retCode == 13) {
            str = getResources().getString(R.string.detect_occ_nose);
        } else if (retCode == 14) {
            str = getResources().getString(R.string.detect_occ_mouth);
        } else if (retCode == 15) {
            str = getResources().getString(R.string.detect_right_contour);
        } else if (retCode == 16) {
            str = getResources().getString(R.string.detect_left_contour);
        } else if (retCode == 17) {
            str = getResources().getString(R.string.detect_chin_contour);
        }
        return str;
    }

    private void initWaveview(Rect rect) {
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.root_view);
        RelativeLayout.LayoutParams waveParams = new RelativeLayout.LayoutParams(rect.width(), rect.height());
        waveParams.setMargins(rect.left, rect.top, rect.left, rect.top);
        waveParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        waveParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mWaveview = new WaveView(this);
        rootView.addView(mWaveview, waveParams);
        mWaveHelper = new WaveHelper(mWaveview);

        mWaveview.setShapeType(WaveView.ShapeType.SQUARE);
        mWaveview.setWaveColor(Color.parseColor("#2800A3E0"), Color.parseColor("#3c00A3E0"));

        mBorderColor = Color.parseColor("#28f16d7a");
        mWaveview.setBorder(mBorderWidth, mBorderColor);
    }

    private void visibleView() {
        mInitView.setVisibility(View.INVISIBLE);
    }

    private void initBrightness() {
        int brightness = BrightnessTools.getScreenBrightness(DetectLoginActivity.this);
        if (brightness < 200) {
            BrightnessTools.setBrightness(this, 200);
        }
    }


    private void init() {
        FaceSDKManager.getInstance().getFaceTracker(this).set_min_face_size(200);
        FaceSDKManager.getInstance().getFaceTracker(this).set_isCheckQuality(true);
        // 该角度为商学，左右，偏头的角度的阀值，大于将无法检测出人脸，为了在1：n的时候分数高，注册尽量使用比较正的人脸，可自行条件角度
        FaceSDKManager.getInstance().getFaceTracker(this).set_eulur_angle_thr(15, 15, 15);
        FaceSDKManager.getInstance().getFaceTracker(this).set_isVerifyLive(true);

        initBrightness();
    }

    private void start() {
        Rect dRect = rectView.getFaceRoundRect();
        int preGap = getResources().getDimensionPixelOffset(R.dimen.preview_margin);
        int w = getResources().getDimensionPixelOffset(R.dimen.detect_out);

        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);
        if (isPortrait) {
            // 检测区域矩形宽度
            int rWidth = mScreenW - 2 * preGap;
            // 圆框宽度
            int dRectW = dRect.width();
            // 检测矩形和圆框偏移
            int h = (rWidth - dRectW) / 2;
            int rLeft = w;
            int rRight = rWidth - w;
            int rTop = dRect.top - h - preGap + w;
            int rBottom = rTop + rWidth - w;
            RectF newDetectedRect = new RectF(rLeft, rTop, rRight, rBottom);
            cropProcessor.setDetectedRect(newDetectedRect);
        } else {
            int rLeft = mScreenW / 2 - mScreenH / 2 + w;
            int rRight = mScreenW / 2 + mScreenH / 2 + w;
            int rTop = 0;
            int rBottom = mScreenH;

            RectF newDetectedRect = new RectF(rLeft, rTop, rRight, rBottom);
            cropProcessor.setDetectedRect(newDetectedRect);
        }


        faceDetectManager.start();
        initWaveview(dRect);
    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectManager.stop();
        mDetectStoped = true;
        onRefreshSuccessView(false);
        if (mWaveview != null) {
            mWaveview.setVisibility(View.GONE);
            mWaveHelper.cancel();
        }
    }

    /**
     * 参考https://ai.baidu.com/docs#/Face-API/top 人脸识别接口
     * 无需知道uid，如果同一个人多次注册，可能返回任意一个帐号的uid
     * 建议上传人脸到自己的服务器，在服务器端调用https://aip.baidubce.com/rest/2.0/face/v3/search，比对分数阀值（如：80分），
     * 认为登录通过
     * group_id	是	string	用户组id（由数字、字母、下划线组成），长度限制128B，如果需要查询多个用户组id，用逗号分隔
     * image	是	string	图像base64编码，每次仅支持单张图片，图片编码后大小不超过10M
     * 返回登录认证的参数给客户端
     * @param model
     */
    private void upload(FaceFilter.TrackedModel model) {
        if (mUploading) return;
        mUploading = true;
        if (model.getEvent() != FaceFilter.Event.OnLeave) {
            mDetectCount++;
            try {
                final Bitmap face = model.cropFace();
                final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                ImageUtil.resize(face, file, 200, 200);
                ImageSaveUtil.saveCameraBitmap(DetectLoginActivity.this, face, "head_tmp.jpg");

                APIService.getInstance().identify(new OnResultListener<RegResult>() {
                    @Override
                    public void onResult(RegResult result) {
                        deleteFace(file);
                        mUploading = false;
                        if (result == null) {
                            if (mDetectCount >= 3) {
                                PopMessageUtil.showToastShort("人脸识别不通过，请关联会员");
                                PopMessageUtil.Log("================1");
                                CloseWaveFaceRecognition();
                                SwitchUtil.switchActivity(DetectLoginActivity.this,MainActivity.class).switchToFinishWithValue(RESULT_FIRST_USER);
                                finish();
                            }
                            return;
                        } else {
                            PopMessageUtil.Log("================2");
                            String res = result.getJsonRes();
                            PopMessageUtil.Log("res is:" + res);
                            double maxScore = 0;
                            String userId = "";
                            String userInfo = "";
                            if (TextUtils.isEmpty(res)) return;
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(res);
                                JSONObject resObj = obj.optJSONObject("result");
                                if (resObj != null) {
                                    JSONArray resArray = resObj.optJSONArray("user_list");
                                    int size = resArray.length();
                                    for (int i = 0; i < size; i++) {
                                        JSONObject s = (JSONObject) resArray.get(i);
                                        if (s != null) {
                                            double score = s.getDouble("score");
                                            if (score > maxScore) {
                                                maxScore = score;
                                                userId = s.getString("user_id");
                                                userInfo = s.getString("user_info");
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {e.printStackTrace();}
                            //========用户人脸识别判断===========//
                            if (maxScore > 80 ) {
                                PopMessageUtil.Log("ObjectId=" + userId + "|CustormerId=" + userInfo);
                                mDetectTime = false;
                                CloseWaveFaceRecognition();
                                //识别成功 定位到用户信息
                                SwitchUtil.switchActivity(DetectLoginActivity.this,MainActivity.class)
                                        .addString("ObjectId",userId)
                                        .addString("CustormerId",userInfo)
                                        .switchToFinishWithValue(RESULT_OK);
                                return;
                            } else {
                                if (mDetectCount >= 3 ) {
                                    mDetectTime = false;
                                    CloseWaveFaceRecognition();
                                    PopMessageUtil.showToastShort("人脸识别未找到该会员，请关联会员！");
                                    SwitchUtil.switchActivity(DetectLoginActivity.this,MainActivity.class).switchToFinishWithValue(RESULT_FIRST_USER);
                                    finish();
                                }
                                return;
                            }
                        }
                    }

                    @Override
                    public void onError(FaceError error) {
                        error.printStackTrace();
                        deleteFace(file);
                        mUploading = false;

                        if (error.getErrorCode() == 216611) {
                            CloseWaveFaceRecognition();
                            mDetectTime = false;
                            PopMessageUtil.showToastLong("面部检测失败,错误码=216611");
                            SwitchUtil.FinishActivity(DetectLoginActivity.this);
                            return;
                        }

                        if (mDetectCount >= 3) {
                            mDetectTime = false;
                            CloseWaveFaceRecognition();
                            if (error.getErrorCode() == 10000) {
                                PopMessageUtil.Log("================3");
                                PopMessageUtil.showToastShort("人脸校验不通过,请检查网络后重试");
                                SwitchUtil.switchActivity(DetectLoginActivity.this,MainActivity.class).switchToFinish();
                            }
                            else {
                                PopMessageUtil.Log("================4");
                                PopMessageUtil.showToastShort("人脸校验不通过");
                                SwitchUtil.switchActivity(DetectLoginActivity.this,MainActivity.class).switchToFinishWithValue(RESULT_FIRST_USER);
                            }
                            return;
                        }
                    }
                }, file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else {
            onRefreshSuccessView(false);
            showProgressBar(false);
            mUploading = false;
        }
    }

    private void CloseWaveFaceRecognition(){
        faceDetectManager.stop();
        mDetectStoped = true;
        onRefreshSuccessView(false);
        if (mWaveview != null) {
            mWaveview.setVisibility(View.GONE);
            mWaveHelper.cancel();
        }
    }


    private void showProgressBar(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    if (mWaveview != null) {
                        mWaveview.setVisibility(View.VISIBLE);
                        mWaveHelper.start();
                    }
                } else {
                    if (mWaveview != null) {
                        mWaveview.setVisibility(View.GONE);
                        mWaveHelper.cancel();
                    }
                }

            }
        });
    }

    private void deleteFace(File file) {
        if (file != null && file.exists())
            file.delete();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWaveview != null) {
            mWaveHelper.cancel();
            mWaveview.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDetectTime = true;
        if (mDetectStoped) {
            faceDetectManager.start();
            mDetectStoped = false;
        }

    }

    private void onRefreshSuccessView(final boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSuccessView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private static class InnerHandler extends Handler {
        private WeakReference<DetectLoginActivity> mWeakReference;

        public InnerHandler(DetectLoginActivity activity) {
            super();
            this.mWeakReference = new WeakReference<DetectLoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null)
                return;
            DetectLoginActivity activity = mWeakReference.get();
            if (activity == null)
                return;
            if (msg == null)
                return;
            switch (msg.what) {
                case MSG_INITVIEW:
                    activity.visibleView();
                    break;
                case MSG_DETECTTIME:
                    activity.mDetectTime = true;
                    break;
                default:
                    break;
            }
        }
    }
}
