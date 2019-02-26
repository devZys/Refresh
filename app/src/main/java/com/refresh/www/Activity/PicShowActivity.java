package com.refresh.www.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.refresh.www.FaceUtils.utils.ImageSaveUtil;
import com.refresh.www.OtherUtils.HttpxUtils.ImagexUtils;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.SwitchUtil;

/**
 * Created by yy on 2019/2/26.
 */
public class PicShowActivity  extends Activity {
    ImageView newImage,selectImage;
    TextView  newTxt,selectTxt;
    String newUrl,selectUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picshow);
        initUi();
        GetIntent();
    }

    private void initUi() {
        newImage = (ImageView) findViewById(R.id.PicShow_new_image);
        selectImage = (ImageView) findViewById(R.id.PicShow_select_image);
        newTxt   = (TextView) findViewById(R.id.PicShow_new_txt);
        selectTxt  = (TextView) findViewById(R.id.PicShow_select_txt);
    }

    private void GetIntent(){
        Intent Extraintent = this.getIntent();

        newUrl = Extraintent.getStringExtra("NewPicUrl");
        selectUrl = Extraintent.getStringExtra("SelectPicUrl");
        newTxt.setText(Extraintent.getStringExtra("NewPicTime"));
        selectTxt.setText(Extraintent.getStringExtra("SelectPicTime"));
        ImagexUtils.displayGifForUrl(newImage, newUrl);
        ImagexUtils.displayGifForUrl(selectImage, selectUrl);
    }

    public void ClickPicShowBack(View view){
        SwitchUtil.FinishActivity(PicShowActivity.this);
    }
}
