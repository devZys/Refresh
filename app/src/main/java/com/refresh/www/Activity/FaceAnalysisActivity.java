package com.refresh.www.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.gson.Gson;
import com.megvii.facepp.api.FacePPApi;
import com.megvii.facepp.api.IFacePPCallBack;
import com.megvii.facepp.api.bean.CommonRect;
import com.megvii.facepp.api.bean.DetectResponse;
import com.megvii.facepp.api.bean.Face;
import com.megvii.facepp.api.bean.FaceAnalyzeResponse;
import com.refresh.www.Application.PublicUrl;
import com.refresh.www.OtherUtils.PicUtils.BitmapUtil;
import com.refresh.www.OtherUtils.TakePhotoUtils.HeadImageChooseDialog;
import com.refresh.www.OtherUtils.TakePhotoUtils.HeadImageUtil;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.SwitchUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yy on 2019/2/27.
 */
public class FaceAnalysisActivity extends Activity {
    private static final int FaceAnalysisCompleted = 0;
    private static final int FaceAnalysisFailed = 1;
    private LinearLayout AnalysisLayout;

    private ImageView FaceAnalysis_image,FaceAnalysis_sex_image,anger_image,disgust_image,fear_image,happiness_image,neutral_image,sadness_image,surprise_image;
    private FacePPApi faceppApi;
    private TextView FaceAnalysis_age_txt,FaceAnalysis_ethnicity_txt,FaceAnalysis_beautyScore_txt,FaceAnalysis_skinScore_txt;

    private CircleProgress stain_progress,acne_progress,dark_circle_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faceanalysis);
        initUi();
        initFacePlus();
    }

    private void initUi() {
        AnalysisLayout = (LinearLayout) findViewById(R.id.AnalysisLayout);
        FaceAnalysis_image = (ImageView) findViewById(R.id.FaceAnalysis_image);
        FaceAnalysis_sex_image = (ImageView) findViewById(R.id.FaceAnalysis_sex_image);

        stain_progress = (CircleProgress) findViewById(R.id.stain_progress);
        acne_progress  = (CircleProgress) findViewById(R.id.acne_progress);
        dark_circle_progress = (CircleProgress) findViewById(R.id.dark_circle_progress);

        FaceAnalysis_age_txt = (TextView) findViewById(R.id.FaceAnalysis_age_txt);
        FaceAnalysis_ethnicity_txt = (TextView) findViewById(R.id.FaceAnalysis_ethnicity_txt);
        FaceAnalysis_beautyScore_txt = (TextView) findViewById(R.id.FaceAnalysis_beautyScore_txt);
        FaceAnalysis_skinScore_txt = (TextView) findViewById(R.id.FaceAnalysis_skinScore_txt);

        anger_image = (ImageView) findViewById(R.id.anger_image);
        disgust_image = (ImageView) findViewById(R.id.disgust_image);
        fear_image = (ImageView) findViewById(R.id.fear_image);
        happiness_image = (ImageView) findViewById(R.id.happiness_image);
        neutral_image = (ImageView) findViewById(R.id.neutral_image);
        sadness_image = (ImageView) findViewById(R.id.sadness_image);
        surprise_image = (ImageView) findViewById(R.id.surprise_image);
    }

    private void initFacePlus() {
        faceppApi = new FacePPApi(PublicUrl.FacePlus_apiKey, PublicUrl.FacePlus_apiSecret);

    }

    public void ClickFaceAnalysisStartMethod(View view) {
        AnalysisLayout.setVisibility(View.GONE);
        HeadImageChooseDialog.ChangeFacePhotoMethod(FaceAnalysisActivity.this);
    }

    public void ClickFaceAnalysisBackMethod(View view){
        SwitchUtil.FinishActivity(FaceAnalysisActivity.this);
    }

    private void SetProgressView(CircleProgress view,int progressNum){
        ObjectAnimator anim = ObjectAnimator.ofInt(view, "progress", 0, progressNum);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(1000);
        anim.start();
    }

    /**********************************************************************************************
     * * 功能说明：回调函数返回值
     *********************************************************************************************/
    private File photoFile;
    private Bitmap photoBit;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case HeadImageUtil.CODE_GALLERY_REQUEST:
                if (intent != null)
                    HeadImageUtil.cropRawPhoto(FaceAnalysisActivity.this, intent.getData());
                break;
            case HeadImageUtil.CODE_CAMERA_REQUEST:
                if (HeadImageUtil.hasSdcard()) {
                    File tempFile = new File(Environment.getExternalStorageDirectory(), HeadImageUtil.IMAGE_FILE_NAME);
                    HeadImageUtil.cropRawPhoto(FaceAnalysisActivity.this, Uri.fromFile(tempFile));
                } else
                    PopMessageUtil.showToastShort("未检测到SD卡");
                break;
            case HeadImageUtil.CODE_RESULT_REQUEST:
                if (intent != null) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        photoBit = extras.getParcelable("data");
                        photoFile = HeadImageUtil.changeBitmapFile(photoBit);         //头像图片转文件
                        FaceAnalysis_image.setImageBitmap(photoBit);
                        //=====开启人脸分析线程====//
                        PopMessageUtil.Loading(FaceAnalysisActivity.this, "Face analysis");
                        new DealFaceAnalysisThread().start();
                    }
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            PopMessageUtil.CloseLoading();
            String message = msg.getData().getString("Result");
            if (msg.what == FaceAnalysisCompleted) {
                PopMessageUtil.showToastShort("Face analysis is successful!");
                AnalysisLayout.setVisibility(View.VISIBLE);
                PopMessageUtil.Log(message);
                Gson gson = new Gson();
                FaceAnalyzeResponse faceAnalyzeResponse = gson.fromJson(message, FaceAnalyzeResponse.class);
                Face face = faceAnalyzeResponse.getFaces().get(0);
                FaceAnalysis_image.setImageBitmap(markFacesInThePhoto(photoBit, face.getFace_rectangle()));
                //------显示基本信息--------//
                if(face.getAttributes().getGender().getValue().compareTo("Male")==0) {
                    FaceAnalysis_sex_image.setImageResource(R.drawable.male);
                    FaceAnalysis_beautyScore_txt.setText(""+(int)face.getAttributes().getBeauty().getMale_score());
                }
                else {
                    FaceAnalysis_sex_image.setImageResource(R.drawable.female);
                    FaceAnalysis_beautyScore_txt.setText("" + (int) face.getAttributes().getBeauty().getFemale_score());
                }
                FaceAnalysis_age_txt.setText(face.getAttributes().getAge().getValue());
                FaceAnalysis_ethnicity_txt.setText(face.getAttributes().getEthnicity().getValue());
                FaceAnalysis_skinScore_txt.setText("" + (int) face.getAttributes().getSkinstatus().getHealth());
                if((int)face.getAttributes().getEmotion().getAnger()>5)
                    anger_image.setImageResource(R.drawable.anger_on);
                if((int)face.getAttributes().getEmotion().getDisgust()>5)
                    disgust_image.setImageResource(R.drawable.disgust_on);
                if((int)face.getAttributes().getEmotion().getFear()>5)
                    fear_image.setImageResource(R.drawable.fear_on);
                if((int)face.getAttributes().getEmotion().getHappiness()>5)
                    happiness_image.setImageResource(R.drawable.happiness_on);
                if((int)face.getAttributes().getEmotion().getNeutral()>5)
                    neutral_image.setImageResource(R.drawable.neutral_on);
                if((int)face.getAttributes().getEmotion().getSadness()>5)
                    sadness_image.setImageResource(R.drawable.sadness_on);
                if((int)face.getAttributes().getEmotion().getSurprise()>5)
                    surprise_image.setImageResource(R.drawable.surprise_on);

                SetProgressView(stain_progress, (int) (face.getAttributes().getSkinstatus().getStain()));
                SetProgressView(acne_progress,(int)(face.getAttributes().getSkinstatus().getAcne()));
                SetProgressView(dark_circle_progress,(int)(face.getAttributes().getSkinstatus().getDark_circle()));
            } else if (msg.what == FaceAnalysisFailed) {
                PopMessageUtil.Log(message);
                PopMessageUtil.showToastShort("人脸分析失败" + message);
            }
        }
    };

    private Bitmap markFacesInThePhoto(Bitmap bitmap, CommonRect faceRectangle) {
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        int top = faceRectangle.getTop();
        int left = faceRectangle.getLeft();
        int height = faceRectangle.getHeight();
        int width = faceRectangle.getWidth();
        canvas.drawRect(left, top, left + width, top + height, paint);

        return tempBitmap;
    }

    private class DealFaceAnalysisThread extends Thread {
        @Override
        public void run() {
            PicHttpMethod();
        }
    }

    public void PicHttpMethod() {
        Map<String, String> params = new HashMap<>();
        params.put("return_attributes", "gender,age");
        byte[] data = BitmapUtil.File2byte(photoFile);

        faceppApi.detect(params, data, new IFacePPCallBack<DetectResponse>() {
            @Override
            public void onSuccess(final DetectResponse response) {
                PopMessageUtil.Log("人脸=" + response);
                PopMessageUtil.Log("Token=" + response.getFaces().get(0).getFace_token());
                FaceAnalyzeMethod(response.getFaces().get(0).getFace_token());
            }

            @Override
            public void onFailed(String error) {
                SendMessageThread(FaceAnalysisFailed, "Server Error_1" + error);
            }
        });
    }


    private void FaceAnalyzeMethod(String face_token) {
        Map<String, String> params = new HashMap<>();
        params.put("face_tokens", face_token);
        params.put("return_attributes", "gender,age,smiling,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
        faceppApi.faceAnalyze(params, new IFacePPCallBack<FaceAnalyzeResponse>() {
            @Override
            public void onSuccess(FaceAnalyzeResponse faceAnalyzeResponse) {
                Gson gson = new Gson();
                String faceAnalyResult = gson.toJson(faceAnalyzeResponse);
                SendMessageThread(FaceAnalysisCompleted, faceAnalyResult);
            }

            @Override
            public void onFailed(String s) {
                SendMessageThread(FaceAnalysisFailed, "Server Error_2" + s);
            }
        });
    }

    private void SendMessageThread(int what, String result) {
        Message msg = new Message();
        msg.what = what;
        Bundle b = new Bundle();
        b.putString("Result", result);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}
