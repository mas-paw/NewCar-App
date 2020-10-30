package com.pawji.newcar.ui.supir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.EventBus.AddSupirClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class SupirTambah extends Fragment {
    SupirModel supirModel = new SupirModel();
    EditText txt_nama,no_hp,alamat,umur;
    Button btn_tambah;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_supir_tambah, container, false);
        progressDialog      = new ProgressDialog(getContext());
        AndroidNetworking.initialize(getContext());
        txt_nama = root.findViewById(R.id.txt_nama);
        no_hp = root.findViewById(R.id.txt_no_hp);
        alamat = root.findViewById(R.id.txt_alamat);
        umur     = root.findViewById(R.id.txt_umur);


        btn_tambah = root.findViewById(R.id.btn_tambah);
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });

        return root;
    }

    public void validasi(){
        if(txt_nama.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Nama", Toast.LENGTH_SHORT).show();
        }else if(umur.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Umur", Toast.LENGTH_SHORT).show();
        }else if(no_hp.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Nomor Handphone", Toast.LENGTH_SHORT).show();
        }else if(alamat.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Alamat", Toast.LENGTH_SHORT).show();
        }else{
            addSupir();
        }
    }

    public void addSupir(){
        progressDialog.setMessage("Menambahkan Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        supirModel.setNama(txt_nama.getText().toString());
        supirModel.setUmur(Integer.parseInt(umur.getText().toString()));
        supirModel.setNo_hp(no_hp.getText().toString());
        supirModel.setAlamat(alamat.getText().toString());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Common.tambahSupir)
                        .addBodyParameter("id_supir","")
                        .addBodyParameter("nama",""+supirModel.getNama())
                        .addBodyParameter("umur",""+supirModel.getUmur())
                        .addBodyParameter("no_hp",""+supirModel.getNo_hp())
                        .addBodyParameter("alamat",""+supirModel.getAlamat())
                        .setPriority(Priority.MEDIUM)
                        .setTag("Tambah Data")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d("Cek Tambah",""+response);
                                try{
                                    Boolean status = response.getBoolean("status");
                                    String pesan = response.getString("result");
                                    Toast.makeText(getContext(), ""+pesan, Toast.LENGTH_SHORT).show();
                                    Log.d("Status",""+status);
                                    if(status){
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Data Supir Berhasil Ditambahkan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Gagal Menambahkan Data Supir")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
                                                    }
                                                }).show();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("Error Nambah Data ",""+anError.getErrorBody());
                            }
                        });

            }
        },1000);
    }
}
