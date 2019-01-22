package com.refresh.www.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.refresh.www.BmobObject.GameScore;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by yy on 2019/1/22.
 */
public class LoginActivity extends Activity{
    EditText edit_etxt,editText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitUi();
        InitBmob();
    }

    private void InitUi() {
        edit_etxt = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
    }

    /***********************************************************************************************
     * * 功能说明：初始化Bmob
     **********************************************************************************************/
    private void InitBmob(){
        Bmob.initialize(this, "48616d9dc7838d737049b2c36d43268a");
    }

    /***********************************************************************************************
     * * 功能说明：创建数据
     **********************************************************************************************/
    public void ClickCreateMethod(View view){
        GameScore gameScore = new GameScore();
        //注意：不能调用gameScore.setObjectId("")方法
        gameScore.setPlayerName("刘洋 ");
        gameScore.setScore(Integer.parseInt(edit_etxt.getText().toString()));
        gameScore.setIsPay(false);
        gameScore.save(new SaveListener<String>() {

            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    PopMessageUtil.showToastShort("创建数据成功：" + objectId);
                } else {
                    PopMessageUtil.Log("失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /***********************************************************************************************
     * * 功能说明：更新数据
     **********************************************************************************************/
    public void ClickUpdateMethod(View view){
        GameScore gameScore = new GameScore();
        gameScore.setValue("score", Integer.parseInt(editText2.getText().toString()));
        gameScore.update("25eda2d15a", new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    PopMessageUtil.showToastShort("更新成功");
                } else {
                    PopMessageUtil.showToastShort("更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }

        });
    }

    /***********************************************************************************************
     * * 功能说明：查询数据
     **********************************************************************************************/
    public void ClickCheckMethod(View view){
        BmobQuery<GameScore> query = new BmobQuery<GameScore>();
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("score", 99);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<GameScore>() {
            @Override
            public void done(List<GameScore> object, BmobException e) {
                if(e==null){
                    PopMessageUtil.showToastShort("查询成功：共"+object.size()+"条数据");
                    for (GameScore gameScore : object) {
                        //获得playerName的信息
                        gameScore.getPlayerName();
                        //获得数据的objectId信息
                        gameScore.getObjectId();
                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
                        gameScore.getCreatedAt();
                    }
                }else{
                    PopMessageUtil.showToastShort("失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

//        BmobQuery<GameScore> query = new BmobQuery<GameScore>();
//        query.getObject("25eda2d15a", new QueryListener<GameScore>() {
//
//            @Override
//            public void done(GameScore object, BmobException e) {
//                if(e==null){
//                    //获得playerName的信息
//                    object.getPlayerName();
//                    //获得数据的objectId信息
//                    object.getObjectId();
//                    //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
//                    object.getCreatedAt();
//                    PopMessageUtil.Log("查询成功!"+object.getPlayerName()+object.getObjectId()+object.getCreatedAt());
//                }else{
//                    PopMessageUtil.showToastShort("失败：" + e.getMessage() + "," + e.getErrorCode());
//                }
//            }
//        });
    }
}
