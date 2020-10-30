package com.pawji.newcar.ui.supir;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.pawji.newcar.ui.karyawan.KaryawanEdit;
import com.pawji.newcar.ui.karyawan.KaryawanModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

public class SupirEdit extends AppCompatActivity {
    SupirModel supirModel = new SupirModel();
    EditText txt_nama,no_hp,umur,alamat;
    Button btn_edit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supir_edit);

        progressDialog      = new ProgressDialog(this);
        AndroidNetworking.initialize(this);
        txt_nama = findViewById(R.id.txt_nama);
        no_hp = findViewById(R.id.txt_no_hp);
        umur = findViewById(R.id.txt_umur);
        alamat = findViewById(R.id.txt_alamat);

        getDataIntent();
        fillData();


        btn_edit = findViewById(R.id.btn_update);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });
    }

    private void fillData() {
        txt_nama.setText(supirModel.getNama());
        no_hp.setText(supirModel.getNo_hp());
        alamat.setText(supirModel.getAlamat());
        umur.setText(new StringBuilder("").append(supirModel.getUmur()));
    }

    private void getDataIntent() {
        supirModel.setId_supir(getIntent().getIntExtra("id_supir",0));
        supirModel.setNama(getIntent().getStringExtra("nama"));
        supirModel.setUmur(getIntent().getIntExtra("umur",0));
        supirModel.setNo_hp(getIntent().getStringExtra("no_hp"));
        supirModel.setAlamat(getIntent().getStringExtra("alamat"));
    }

    public void validasi(){
        if(txt_nama.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Nama", Toast.LENGTH_SHORT).show();
        }else if(umur.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Umur Supir", Toast.LENGTH_SHORT).show();
        }else if(no_hp.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Nomor Handphone", Toast.LENGTH_SHORT).show();
        }else if(alamat.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Alamat", Toast.LENGTH_SHORT).show();
        }else{
            EditKaryawan();
        }
    }

    public void EditKaryawan(){
        progressDialog.setMessage("Edit Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        supirModel.setNama(txt_nama.getText().toString());
        supirModel.setUmur(Integer.parseInt(umur.getText().toString()));
        supirModel.setNo_hp(no_hp.getText().toString());
        supirModel.setAlamat(alamat.getText().toString());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Common.updateSupir)
                        .addBodyParameter("id_supir",""+supirModel.getId_supir())
                        .addBodyParameter("nama",""+supirModel.getNama())
                        .addBodyParameter("no_hp",""+supirModel.getNo_hp())
                        .addBodyParameter("umur",""+supirModel.getUmur())
                        .addBodyParameter("alamat",""+supirModel.getAlamat())
                        .setPriority(Priority.MEDIUM)
                        .setTag("Update Data")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d("Respon Edit",""+response);
                                try{
                                    Boolean status = response.getBoolean("status");
                                    if(status){
                                        new AlertDialog.Builder(SupirEdit.this)
                                                .setMessage("Data Karyawan Berhasil Diupdate")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
                                                        SupirEdit.this.finish();
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(SupirEdit.this)
                                                .setMessage("Gagal Edit Data Karyawan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
                                                        SupirEdit.this.finish();
                                                    }
                                                }).show();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {


                            }
                        });

            }
        },1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
        finish();
    }
}
