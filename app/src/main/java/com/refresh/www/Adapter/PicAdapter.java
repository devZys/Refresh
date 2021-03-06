package com.refresh.www.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refresh.www.Activity.MainActivity;
import com.refresh.www.Activity.PicShowActivity;
import com.refresh.www.BmobObject.Http.BmobHttps;
import com.refresh.www.BmobObject.Object.PicInfo;
import com.refresh.www.OtherUtils.HttpxUtils.ImagexUtils;
import com.refresh.www.R;
import com.refresh.www.UiShowUtils.PopMessageUtil;
import com.refresh.www.UiShowUtils.SwitchUtil;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by yy on 2019/1/23.
 */
public class PicAdapter extends BaseAdapter {
    private MainActivity mContext = null;
    private List<PicInfo> picInfoList;
    private SweetAlertDialog sweetAlertTipeDialog;
    public PicAdapter(MainActivity loginActivity) {
        mContext = loginActivity;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != picInfoList) {
            count = picInfoList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        PicInfo item = null;
        if (null != picInfoList) {
            item = picInfoList.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView item_createTime_txt;
        RelativeLayout item_Pic_layout;
        ImageView item_pic_image,item_new_image;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.item_pic, null);
            viewHolder.item_createTime_txt = (TextView) convertView.findViewById(R.id.item_createTime_txt);
            viewHolder.item_pic_image = (ImageView) convertView.findViewById(R.id.item_pic_image);
            viewHolder.item_new_image = (ImageView) convertView.findViewById(R.id.item_new_image);
            viewHolder.item_Pic_layout = (RelativeLayout) convertView.findViewById(R.id.item_Pic_layout);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        //---------------获取最新数据--------------//
        viewHolder.item_createTime_txt.setText(picInfoList.get(position).getCreateTime());
        if(position == 0)
            viewHolder.item_new_image.setVisibility(View.VISIBLE);
        else
            viewHolder.item_new_image.setVisibility(View.GONE);
        ImagexUtils.displayGifForUrl(viewHolder.item_pic_image,
                picInfoList.get(position).getUrl() == null ? "http://eoptask-file-img.wityun.com/eoptask/o2/p61/t1495/nopic.jpg" : picInfoList.get(position).getUrl());
        //---------------响应点击事件---------------//
        viewHolder.item_Pic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopMessageUtil.showToastShort("点击"+position+"照片!");
                SwitchUtil.switchActivity(mContext, PicShowActivity.class)
                        .addString("NewPicUrl",picInfoList.get(0).getUrl())
                        .addString("NewPicTime",picInfoList.get(0).getCreateTime())
                        .addString("SelectPicUrl",picInfoList.get(position).getUrl())
                        .addString("SelectPicTime",picInfoList.get(position).getCreateTime())
                        .switchTo();
            }
        });
        viewHolder.item_Pic_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sweetAlertTipeDialog = new SweetAlertDialog(mContext,3);
                sweetAlertTipeDialog
                        .setTitleText("Delete image")
                        .setContentText("Are you sure to delete this photo?")
                        .setConfirmText("yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sw) {
                                sweetAlertTipeDialog.cancel();
                                sweetAlertTipeDialog = null;
                                BmobHttps.DelSelectUser(mContext, position);
                            }
                        })
                        .setCancelText("no")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertTipeDialog.cancel();
                                sweetAlertTipeDialog = null;
                            }
                        })
                        .changeAlertType(3);
                sweetAlertTipeDialog.setCanceledOnTouchOutside(true);
                sweetAlertTipeDialog.show();
                return false;
            }
        });
        return convertView;
    }

    public void UpdataPicInfo(List<PicInfo> PICINFO){
        this.picInfoList = PICINFO;
        notifyDataSetChanged();
    }
}
