package com.refresh.www.UiShowUtils;

import android.app.Activity;
import android.content.Intent;

import com.refresh.www.R;

import java.util.ArrayList;

/**
 * Created by yy on 2017/9/13.
 */
public class SwitchUtil {
    private static SwitchUtil instance;
    public static SwitchUtil getInstance(){
        if (instance == null)
            instance = new SwitchUtil();
        return instance;
    }

    public static BaseIntent switchActivity(Activity activity, Class<? extends Activity> target){
        return getInstance().new BaseIntent(activity,target);
    }

    public static void FinishActivity(Activity activity){
        activity.finish();
        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    public class BaseIntent {
        protected Intent intent;
        protected Activity activity;
        protected Class<? extends Activity> target;

        public BaseIntent(Activity activity, Class<? extends Activity> target) {
            this.activity = activity;
            this.target = target;
            intent = new Intent(activity, target);
        }

        public BaseIntent addString(String key, String value) {
            intent.putExtra(key, value);
            return this;
        }

        public BaseIntent addInt(String key,int value){
            intent.putExtra(key,value);
            return this;
        }

        public BaseIntent addBoolean(String key,boolean value){
            intent.putExtra(key,value);
            return this;
        }

        public BaseIntent addStringArray(String key,ArrayList<String> value){
            intent.putStringArrayListExtra(key, value);
            return this;
        }

        public BaseIntent switchTo() {
            activity.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return this;
        }

        public BaseIntent switchToWithBottomForResult(int flag){
            activity.startActivityForResult(intent, flag);
            activity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
            return this;
        }

        public BaseIntent switchToWithBottomAndFinish(int flag){
            activity.setResult(flag, intent);
            activity.finish();
            activity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
            return this;
        }

        public BaseIntent switchToAndFinish(){
            activity.startActivity(intent);
//            activity.overridePendingTransition(0,0);
            activity.finish();
            return this;
        }

        public BaseIntent switchToForResult(int flag) {
            activity.startActivityForResult(intent, flag);
            activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            return this;
        }

        public BaseIntent switchToFinish(){
            activity.finish();
//            activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            return this;
        }
        public BaseIntent switchToFinishWithValue(int flag){
            activity.setResult(flag, intent);
            activity.finish();
//            activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return this;
        }
    }
}
