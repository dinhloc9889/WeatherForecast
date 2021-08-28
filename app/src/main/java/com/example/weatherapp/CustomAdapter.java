package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<ThoiTiet>  arrayList;

    public CustomAdapter(Context context, ArrayList<ThoiTiet> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView tvDate7;
        TextView tvTrangThai7;
        ImageView imgTrangThai7;
        TextView tvNhietDoCao;
        TextView tvNhietDoThap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_dong_listview,null);
            holder = new ViewHolder();

            holder.tvDate7 = (TextView)convertView.findViewById(R.id.tv_date7);
            holder.tvTrangThai7 = (TextView)convertView.findViewById(R.id.tv_trangthai7);
            holder.imgTrangThai7 = (ImageView)convertView.findViewById(R.id.img_trangthai7);
            holder.tvNhietDoCao = (TextView)convertView.findViewById(R.id.tv_nhietdocao);
            holder.tvNhietDoThap = (TextView)convertView.findViewById(R.id.tv_nhietdothap);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        ThoiTiet thoiTiet = arrayList.get(position);

        if(thoiTiet != null){

            holder.tvDate7.setText(thoiTiet.ngayThang);
            holder.tvTrangThai7.setText(thoiTiet.trangThai);
            holder.tvNhietDoCao.setText(thoiTiet.nhietDoCao + thoiTiet.doCF);
            holder.tvNhietDoThap.setText(thoiTiet.nhietDoThap + thoiTiet.doCF);

            Picasso.get().load("https://openweathermap.org/img/wn/" + thoiTiet.hinh + ".png").into(holder.imgTrangThai7);


        }

        return convertView;
    }
}
