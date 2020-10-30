package com.pawji.newcar.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.bumptech.glide.Glide;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.MobilUserClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.mobil.MobilModel;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;

public class PilihanTerbaikAdapter extends RecyclerView.Adapter<PilihanTerbaikAdapter.MyViewHolder> {
    Context context;
    List<MobilModel> mobilModelList;

    public PilihanTerbaikAdapter(Context context, List<MobilModel> mobilModelList) {
        this.context = context;
        this.mobilModelList = mobilModelList;
    }

    @NonNull
    @Override
    public PilihanTerbaikAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_popular_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PilihanTerbaikAdapter.MyViewHolder holder, final int position) {
        Glide.with(context).load(mobilModelList.get(position).getImage())
                .into(holder.img_pilih);
        holder.txt_pilih.setText(mobilModelList.get(position).getNama());

        holder.img_pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.selectedCar = mobilModelList.get(position);
                EventBus.getDefault().postSticky(new MobilUserClick(mobilModelList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mobilModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_pilih;
        TextView txt_pilih;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_pilih = itemView.findViewById(R.id.img_pilihan);
            txt_pilih = itemView.findViewById(R.id.txt_pilihan_name);
        }
    }
}
