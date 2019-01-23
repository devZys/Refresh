package com.refresh.www.BmobObject;

/**
 * Created by yy on 2019/1/23.
 */
public class PicInfo {
    private String createTime, Url;

    public String getCreateTime() {
        return createTime;
    }

    public String getUrl() {
        return Url;
    }

    public PicInfo(String CREATETIME, String URL){
        this.createTime = CREATETIME;
        this.Url = URL;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
