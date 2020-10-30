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
import com.pawji.newcar.EventBus.AddStatusClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.status.StatusModel;
import com.pawji.newcar.ui.status.StatusResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {
    List<StatusModel> statusModelList;
    Context context;
    ProgressDialog progressDialog;

    public StatusAdapter(Context context,List<StatusModel> statusModelList, ProgressDialog progressDialog) {
        this.statusModelList = statusModelList;
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_status_item,parent,false);
        return new StatusAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final StatusModel statusModel = statusModelList.get(position);
        Glide.with(context).load(statusModel.getImage()).into(holder.img_status);
        holder.txt_nama.setText(statusModel.getNama());
        holder.txt_nama_mobil.setText(statusModel.getNama_mobil());
        holder.txt_nohp.setText(new StringBuilder("").append(statusModel.getNo_hp()));
        holder.txt_tanggal.setText(statusModel.getTgl_sewa());
        holder.txt_lama_hari.setText(new StringBuilder("").append(statusModel.getLama_hari()));
        holder.txt_total.setText(new StringBuilder("").append(statusModel.getTotal_harga()));

        holder.cardViewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StatusResult.class);
                intent.putExtra("no_transaksi",statusModel.getNo_transaksi());
                intent.putExtra("nama",statusModel.getNama());
                intent.putExtra("no_hp",statusModel.getNo_hp());
                intent.putExtra("no_ktp",statusModel.getNo_ktp());
                intent.putExtra("nama_mobil",statusModel.getNama_mobil());
                intent.putExtra("tgl_sewa",statusModel.getTgl_sewa());
                intent.putExtra("lama_hari",statusModel.getLama_hari());
                intent.putExtra("supir",statusModel.getSupir());
                intent.putExtra("total_harga",statusModel.getTotal_harga());
                context.startActivity(intent);
            }
        });

        holder.cardViewStatus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus").setMessage("Apakah Anda Ingin Hapus Data Pemesanan "+statusModel.getNama()+"?")
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
                        AndroidNetworking.post(Common.deleteStatus)
                                .addBodyParameter("no_transaksi",""+statusModel.getNo_transaksi())
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
                                                EventBus.getDefault().postSticky(new AddStatusClick(statusModel));
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
        return statusModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_status;
        TextView txt_nama,txt_nama_mobil,txt_nohp,txt_tanggal,txt_lama_hari,txt_total;
        CardView cardViewStatus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_status = itemView.findViewById(R.id.img_status);
            txt_nama_mobil = itemView.findViewById(R.id.txt_car_name);
            txt_nama = itemView.findViewById(R.id.txt_name);
            txt_nohp = itemView.findViewById(R.id.txt_no_hp);
            txt_tanggal = itemView.findViewById(R.id.tanggal_booking);
            txt_lama_hari = itemView.findViewById(R.id.lama_hari);
            txt_total = itemView.findViewById(R.id.total);
            cardViewStatus = itemView.findViewById(R.id.cardview_status);
            progressDialog = new ProgressDialog(context);


        }
    }
}
