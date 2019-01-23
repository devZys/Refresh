package com.refresh.www.OtherUtils.HttpxUtils;

import android.widget.ImageView;

import com.refresh.www.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by yy on 2017/10/12.
 */
public class ImagexUtils {

    /***********************************************************************************************
     * * 功能说明：显示GIF图片 通过 URL地址
     **********************************************************************************************/
    public static void displayGifForUrl(ImageView imageView, String iconUrl) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.loaderror)
                .setLoadingDrawableId(R.drawable.loading)
                .build();
        x.image().bind(imageView, iconUrl,imageOptions);
    }

    /***********************************************************************************************
     * * 功能说明：显示普通图片 通过 URL地址
     **********************************************************************************************/
    public static void displayPicForUrl(ImageView imageView, String iconUrl) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(true)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.loaderror)
                .setLoadingDrawableId(R.drawable.loading)
                .build();
        x.image().bind(imageView, iconUrl,imageOptions);
    }

    /***********************************************************************************************
     * * 功能说明：显示圆角图片（头像） 通过URL
     **********************************************************************************************/
    public static void displayCircluarPicForUrl(ImageView imageView, String iconUrl) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCircular(true)
                .setCrop(true)
                .setFailureDrawableId(R.drawable.loaderror)
                .setLoadingDrawableId(R.drawable.loading)
                .build();
        x.image().bind(imageView, iconUrl, imageOptions);
    }
}
