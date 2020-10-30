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
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanDetail;
import com.pawji.newcar.ui.karyawan.KaryawanFragment;
import com.pawji.newcar.ui.karyawan.KaryawanModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.MyViewHolder>{
    private Context mContext;
    List<KaryawanModel> karyawanModelList;
        ProgressDialog progressDialog;

    public KaryawanAdapter(Context mContext, List<KaryawanModel> karyawanModelList, ProgressDialog progressDialog) {
        this.mContext = mContext;
        this.karyawanModelList = karyawanModelList;
        this.progressDialog = progressDialog;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_alamat,tv_nama,tv_jabatan,tv_nohp;
        public CardView cardViewKaryawan;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewKaryawan = itemView.findViewById(R.id.cardview_karyawan);
            tv_alamat = itemView.findViewById(R.id.alamat);
            tv_nama = itemView.findViewById(R.id.nama);
            tv_jabatan = itemView.findViewById(R.id.jabatan);
            tv_nohp = itemView.findViewById(R.id.no_hp);
            progressDialog = new ProgressDialog(mContext);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.layout_karyawan_item,parent,false);
        return new KaryawanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final KaryawanModel karyawanModel = karyawanModelList.get(position);
        holder.tv_alamat.setText(karyawanModel.getAlamat());
        holder.tv_nama.setText(karyawanModel.getNama());
        holder.tv_jabatan.setText(karyawanModel.getJabatan());
        holder.tv_nohp.setText(karyawanModel.getNo_hp());
        holder.cardViewKaryawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, KaryawanDetail.class);
                intent.putExtra("id_karyawan",karyawanModel.getId_karyawan());
                intent.putExtra("nama",karyawanModel.getNama());
                intent.putExtra("tgl_lahir",karyawanModel.getTgl_lahir());
                intent.putExtra("jenis_kelamin",karyawanModel.getJenis_kelamin());
                intent.putExtra("no_hp",karyawanModel.getNo_hp());
                intent.putExtra("jabatan",karyawanModel.getJabatan());
                intent.putExtra("alamat",karyawanModel.getAlamat());
                intent.putExtra("email",karyawanModel.getEmail());
                mContext.startActivity(intent);
            }
        });
        holder.cardViewKaryawan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Hapus").setMessage("Apakah Anda Ingin Hapus Data "+karyawanModelList.get(position).getNama()+"?")
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
                        AndroidNetworking.post(Common.deleteKaryawan)
                                .addBodyParameter("id_karyawan",""+karyawanModelList.get(position).getId_karyawan())
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
                                                EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
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

        return karyawanModelList.size();
    }


}