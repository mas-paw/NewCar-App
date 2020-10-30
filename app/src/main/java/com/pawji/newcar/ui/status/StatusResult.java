package com.pawji.newcar.ui.status;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.EventBus.AddStatusClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanEdit;
import com.pawji.newcar.ui.karyawan.KaryawanModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StatusResult extends AppCompatActivity {
    String lamadenda;
    StatusModel statusModel = new StatusModel();
    EditText tgl_kembali,denda;
    TextView no_transaksi,nama,no_hp,no_ktp,nama_mobil,tgl_sewa,lama_hari,supir,total_harga;
    DatePickerDialog dialog;
    Button btn_submit;
    ProgressDialog progressDialog;
    SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
    long charge;
    int totalcharge,total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_result);
        progressDialog      = new ProgressDialog(this);
        AndroidNetworking.initialize(this);

        nama = findViewById(R.id.txt_nama);
        no_hp = findViewById(R.id.txt_no_hp);
        no_ktp = findViewById(R.id.txt_no_ktp);
        nama_mobil = findViewById(R.id.txt_nama_mobil);
        tgl_sewa = findViewById(R.id.txt_tgl_sewa);
        lama_hari = findViewById(R.id.txt_lama_hari);
        supir = findViewById(R.id.txt_supir);
        total_harga = findViewById(R.id.txt_total_harga);
        tgl_kembali = findViewById(R.id.tgl_kembali);
        denda = findViewById(R.id.denda);
        btn_submit = findViewById(R.id.btn_submit);

        getDataIntent();
        fillDataIntent();
        setTanggal();


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });

    }
    private void setDenda(){
        if(!TextUtils.isEmpty(tgl_kembali.getText().toString().trim())){
            try{
                DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                Date tgl_awal = date.parse(tgl_sewa.getText().toString());
                Date tgl_akhir = date.parse(tgl_kembali.getText().toString());

                long bedaHari = tgl_akhir.getTime() - tgl_awal.getTime();
                long selisih = TimeUnit.MILLISECONDS.toDays(bedaHari);
                if(selisih > statusModel.getLama_hari()){
                    charge = selisih - statusModel.getLama_hari();
                    totalcharge = statusModel.getTotal_harga() / 10;
                    denda.setText(String.valueOf(totalcharge));
                    total = totalcharge + statusModel.getTotal_harga();
                    total_harga.setText(new StringBuilder("").append(total));
                }
            }catch(Exception e){
                Toast.makeText(StatusResult.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void getDataIntent(){
        statusModel.setNo_transaksi(getIntent().getIntExtra("no_transaksi",0));
        statusModel.setNama(getIntent().getStringExtra("nama"));
        statusModel.setNo_hp(getIntent().getStringExtra("no_hp"));
        statusModel.setNo_ktp(getIntent().getStringExtra("no_ktp"));
        statusModel.setNama_mobil(getIntent().getStringExtra("nama_mobil"));
        statusModel.setTgl_sewa(getIntent().getStringExtra("tgl_sewa"));
        statusModel.setLama_hari(getIntent().getIntExtra("lama_hari",0));
        statusModel.setSupir(getIntent().getStringExtra("supir"));
        statusModel.setTotal_harga(getIntent().getIntExtra("total_harga",0));

    }
    private void fillDataIntent(){
        nama.setText(statusModel.getNama());
        no_hp.setText(statusModel.getNo_hp());
        no_ktp.setText(statusModel.getNo_ktp());
        nama_mobil.setText(statusModel.getNama_mobil());
        tgl_sewa.setText(statusModel.getTgl_sewa());
        lama_hari.setText(new StringBuilder("").append(statusModel.getLama_hari()));
        supir.setText(statusModel.getSupir());
        total_harga.setText(new StringBuilder("").append(statusModel.getTotal_harga()));
    }
    public void validasi(){
        if(tgl_kembali.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Tanggal Kembali", Toast.LENGTH_SHORT).show();
        }else if(denda.getText().toString().isEmpty()) {
            Toast.makeText(this, "Masukkan Denda", Toast.LENGTH_SHORT).show();
        }else{
            submit();
        }
    }
    private void submit() {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Common.tambahTransaksi)
                        .addBodyParameter("no_transaksi","")
                        .addBodyParameter("nama",statusModel.getNama())
                        .addBodyParameter("no_hp",statusModel.getNo_hp())
                        .addBodyParameter("no_ktp",statusModel.getNo_ktp())
                        .addBodyParameter("nama_mobil",statusModel.getNama_mobil())
                        .addBodyParameter("tgl_sewa",statusModel.getTgl_sewa())
                        .addBodyParameter("lama_hari",""+statusModel.getLama_hari())
                        .addBodyParameter("supir",statusModel.getSupir())
                        .addBodyParameter("denda",denda.getText().toString())
                        .addBodyParameter("total_harga",total_harga.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .setTag("Tambah Data")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d("Respon Transaksi",""+response);
                                try{
                                    Boolean status = response.getBoolean("status");
                                    if(status){
                                        new AlertDialog.Builder(StatusResult.this)
                                                .setMessage("Data Transaksi Ditambahkan.")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        deleteStatus();
                                                        EventBus.getDefault().postSticky(new AddStatusClick(statusModel));
                                                        StatusResult.this.finish();
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(StatusResult.this)
                                                .setMessage("Data Transaksi Gagal Ditambahkan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddStatusClick(statusModel));
                                                        StatusResult.this.finish();
                                                    }
                                                }).show();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(ANError anError) {
                                progressDialog.dismiss();
                                anError.printStackTrace();
                                Toast.makeText(StatusResult.this, "ERROR SAVE DATA "+anError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        },1000);
    }
    public void setTanggal(){
        tgl_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(StatusResult.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,month,dayOfMonth);
                        tgl_kembali.setText(fm.format(newDate.getTime()));
                        setDenda();
                    }
                },year,month,day);
                dialog.show();
            }
        });
    }

    public void deleteStatus() {
        AndroidNetworking.post(Common.deleteStatus)
                .addBodyParameter("no_transaksi", "" + statusModel.getNo_transaksi())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            Boolean status = response.getBoolean("status");
                            Log.d("Statuss", "" + status);
                            String result = response.getString("result");
                            if (status) {
                                Toast.makeText(StatusResult.this, "Pengembalian Berhasil", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StatusResult.this, "" + result, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

}
