package com.refresh.www.BmobObject;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by yy on 2019/1/23.
 */
public class UserInfo extends BmobObject {
    private String userName,phoneNumber,CustormerId;
    public List<PicInfo> picInfos;


    public String getCustormerID() {return CustormerId;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getUserName() {return userName;}
    public List<PicInfo> getPicList() {return picInfos;}

    public void setCustormerID(String custormerID) {CustormerId = custormerID;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public void setUserName(String userName) {this.userName = userName;}
    public void setPicList(List<PicInfo> picList) {this.picInfos = picList;}
}
