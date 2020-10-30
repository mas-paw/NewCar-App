package com.pawji.newcar.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.EventBus.AddSupirClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanDetail;
import com.pawji.newcar.ui.supir.SupirDetail;
import com.pawji.newcar.ui.supir.SupirModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

public class SupirAdapter extends RecyclerView.Adapter<SupirAdapter.MyViewHolder> {
    private Context mContext;
    ProgressDialog progressDialog;
    List<SupirModel> supirModelList;

    public SupirAdapter(Context mContext, List<SupirModel> supirModelList, ProgressDialog progressDialog) {
        this.mContext = mContext;
        this.progressDialog = progressDialog;
        this.supirModelList = supirModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_alamat,tv_nama,tv_umur,tv_nohp;
        public CardView cardViewSupir;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSupir = itemView.findViewById(R.id.cardview_supir);
            tv_alamat = itemView.findViewById(R.id.alamat);
            tv_nama = itemView.findViewById(R.id.nama);
            tv_umur = itemView.findViewById(R.id.umur);
            tv_nohp = itemView.findViewById(R.id.no_hp);
            progressDialog = new ProgressDialog(mContext);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_supir_item,parent,false);
        return new SupirAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final SupirModel supirModel = supirModelList.get(position);
        holder.tv_alamat.setText(supirModel.getAlamat());
        holder.tv_nama.setText(supirModel.getNama());
        holder.tv_umur.setText(new StringBuilder("").append(supirModel.getUmur()));
        holder.tv_nohp.setText(supirModel.getNo_hp());
        holder.cardViewSupir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SupirDetail.class);
                intent.putExtra("id_supir",supirModel.getId_supir());
                intent.putExtra("nama",supirModel.getNama());
                intent.putExtra("no_hp",supirModel.getNo_hp());
                intent.putExtra("umur",supirModel.getUmur());
                intent.putExtra("alamat",supirModel.getAlamat());
                mContext.startActivity(intent);
            }
        });
        holder.cardViewSupir.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Hapus").setMessage("Apakah Anda Ingin Hapus Data "+supirModelList.get(position).getNama()+"?")
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
                        AndroidNetworking.post(Common.deleteSupir)
                                .addBodyParameter("id_supir",""+supirModelList.get(position).getId_supir())
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
                                                EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
                                            }else{
                                                Toast.makeText(mContext, ""+result, Toast.LENGTH_SHORT).show();
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
        return supirModelList.size();
    }


}
