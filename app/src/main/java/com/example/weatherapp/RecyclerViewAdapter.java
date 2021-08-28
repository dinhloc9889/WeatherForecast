package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.GioViewHolder> {

    private Context context;
    private ArrayList<ThoiTietGio> arrayListGio;

    public RecyclerViewAdapter(Context context, ArrayList<ThoiTietGio> arrayListGio) {
        this.context = context;
        this.arrayListGio = arrayListGio;
    }


    public GioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_recyclerview, parent, false);
        return new GioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GioViewHolder holder, int position) {
        ThoiTietGio thoiTietGio = arrayListGio.get(position);

        if(thoiTietGio == null){
            return;
        }

        holder.tvNhietDoGio.setText(thoiTietGio.nhietDoGio + thoiTietGio.doCF);
        holder.tvTrangThaiGio.setText(thoiTietGio.trangThaiGio);
        holder.tvGioCapNhat2.setText(thoiTietGio.gioCapNhat);

        Picasso.get().load("https://openweathermap.org/img/wn/" + thoiTietGio.hinhGio + ".png").into(holder.cirimgTrangThaiGio);

    }

    @Override
    public int getItemCount() {
        if(arrayListGio == null){
            return 0;
        }else{
            return arrayListGio.size();
        }
    }

    public static class GioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNhietDoGio;
        CircleImageView cirimgTrangThaiGio;
        TextView tvTrangThaiGio;
        TextView tvGioCapNhat2;

        public GioViewHolder(View itemView) {
            super(itemView);
            tvNhietDoGio = itemView.findViewById(R.id.tv_nhietdogio);
            cirimgTrangThaiGio = itemView.findViewById(R.id.cirimg_trangthaigio);
            tvTrangThaiGio = itemView.findViewById(R.id.tv_trangthaigio);
            tvGioCapNhat2 = itemView.findViewById(R.id.tv_giocapnhat2);
        }
    }

}
