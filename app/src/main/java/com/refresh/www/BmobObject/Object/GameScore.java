package com.refresh.www.BmobObject.Object;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yy on 2019/1/22.
 */
public class GameScore extends BmobObject {
    private String playerName;
    private Integer score;
    private Boolean isPay;
    private BmobFile pic;

//    public GameScore() {
//        this.setTableName("T_a_b");
//    }

    public String getPlayerName() {return this.playerName;}
    public Integer getScore() {return this.score;}
    public Boolean getIsPay() {return isPay;}
    public BmobFile getPic() {return pic;}

    public void setPlayerName(String playerName) {this.playerName = playerName;}
    public void setScore(Integer score) {this.score = score;}
    public void setIsPay(Boolean isPay) {this.isPay = isPay;}
    public void setPic(BmobFile pic) {this.pic = pic;}
}