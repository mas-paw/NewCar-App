package com.pawji.newcar.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddMobilClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.mobil.MobilEdit;
import com.pawji.newcar.ui.mobil.MobilModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

public class MobilAdapter extends RecyclerView.Adapter<MobilAdapter.MyViewHolder> {
    Context context;
    List<MobilModel> mobilModelList;
    ProgressDialog progressDialog;

    public MobilAdapter(Context context, List<MobilModel> mobilModelList, ProgressDialog progressDialog) {
        this.context = context;
        this.mobilModelList = mobilModelList;
        this.progressDialog = progressDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_mobil_item,parent,false);
        return new MobilAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MobilModel mobilModel = mobilModelList.get(position);
        Glide.with(context).load(mobilModel.getImage()).into(holder.img_mobil);
        holder.tv_nama.setText(mobilModel.getNama());
        holder.tv_harga.setText(new StringBuilder("Rp ").append(mobilModel.getHarga()));

        holder.cardViewMobil.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus").setMessage("Apakah Anda Ingin Hapus Data "+mobilModel.getNama()+"?")
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("Menghapus...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        AndroidNetworking.post(Common.deleteMobil)
                                .addBodyParameter("kode_mobil",""+mobilModel.getKode_mobil())
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progressDialog.dismiss();
                                        try{
                                            Boolean status = response.getBoolean("status");
                                            Log.d("Statuss",""+status);
                                            String result = response.getString("result");
                                            if(status){
                                                EventBus.getDefault().postSticky(new AddMobilClick(mobilModel));
                                            }else{
                                                Toast.makeText(context, ""+result, Toast.LENGTH_SHORT).show();
                                            }
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        anError.printStackTrace();
                                    }
                                });

                    }

                });


                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mobilModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nama,tv_harga;
        public ImageView img_mobil;
        public CardView cardViewMobil;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewMobil = itemView.findViewById(R.id.cardview_mobil);
            tv_nama = itemView.findViewById(R.id.txt_car_name);
            tv_harga = itemView.findViewById(R.id.txt_car_price);
            img_mobil = itemView.findViewById(R.id.img_car_image);
            progressDialog = new ProgressDialog(context);
        }
    }
}
