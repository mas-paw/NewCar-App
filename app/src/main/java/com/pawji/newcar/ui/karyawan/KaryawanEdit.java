package com.pawji.newcar.ui.karyawan;

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
import android.widget.DatePicker;
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
import com.pawji.newcar.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class KaryawanEdit extends AppCompatActivity {
    KaryawanModel karyawanModel = new KaryawanModel();
    EditText txt_nama,no_hp,jabatan,email,alamat,tgl_lahir;
    DatePickerDialog dialog;
    RadioGroup jenis_kelamin;
    RadioButton laki,perempuan;
    Button btn_edit;
    ProgressDialog progressDialog;
    SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan_edit);
        progressDialog      = new ProgressDialog(this);
        AndroidNetworking.initialize(this);

        txt_nama = findViewById(R.id.txt_nama);
        no_hp = findViewById(R.id.txt_no_hp);
        jabatan = findViewById(R.id.txt_jabatan);
        email = findViewById(R.id.txt_email);
        alamat = findViewById(R.id.txt_alamat);
        tgl_lahir = findViewById(R.id.txt_tgl_lahir);
        jenis_kelamin = findViewById(R.id.opsi);
        laki = findViewById(R.id.laki);
        perempuan = findViewById(R.id.perempuan);

        getDataIntent();
        fillData();

        setJenisKelamin();
        setTanggal();

        btn_edit = findViewById(R.id.btn_update);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });
    }

    private void fillData() {
        txt_nama.setText(karyawanModel.getNama());
        tgl_lahir.setText(karyawanModel.getTgl_lahir());
        switch(karyawanModel.getJenis_kelamin()){
            case "Laki-laki":
                laki.setChecked(true);
                break;
            case "Perempuan" :
                perempuan.setChecked(true);
        }
        no_hp.setText(karyawanModel.getNo_hp());
        jabatan.setText(karyawanModel.getJabatan());
        alamat.setText(karyawanModel.getAlamat());
        email.setText(karyawanModel.getEmail());
    }

    private void getDataIntent() {
        karyawanModel.setId_karyawan(getIntent().getIntExtra("id_karyawan",0));
        karyawanModel.setNama(getIntent().getStringExtra("nama"));
        karyawanModel.setTgl_lahir(getIntent().getStringExtra("tgl_lahir"));
        karyawanModel.setJenis_kelamin(getIntent().getStringExtra("jenis_kelamin"));
        karyawanModel.setNo_hp(getIntent().getStringExtra("no_hp"));
        karyawanModel.setJabatan(getIntent().getStringExtra("jabatan"));
        karyawanModel.setAlamat(getIntent().getStringExtra("alamat"));
        karyawanModel.setEmail(getIntent().getStringExtra("email"));
    }

    public void validasi(){
        if(txt_nama.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Nama", Toast.LENGTH_SHORT).show();
        }else if(tgl_lahir.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Tanggal Lahir", Toast.LENGTH_SHORT).show();
        }else if(!(laki.isChecked()||perempuan.isChecked())){
            Toast.makeText(this, "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
        }else if(no_hp.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Nomor Handphone", Toast.LENGTH_SHORT).show();
        }else if(jabatan.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Jabatan", Toast.LENGTH_SHORT).show();
        }else if(alamat.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Alamat", Toast.LENGTH_SHORT).show();
        }else if(email.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Email", Toast.LENGTH_SHORT).show();
        }else{
            EditKaryawan();
        }
    }

    private void setJenisKelamin() {
        jenis_kelamin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.laki:
                        karyawanModel.setJenis_kelamin("Laki-laki");
                        break;
                    case R.id.perempuan:
                        karyawanModel.setJenis_kelamin("Perempuan");
                        break;
                }
            }
        });
    }

    public void EditKaryawan(){
        progressDialog.setMessage("Edit Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        karyawanModel.setNama(txt_nama.getText().toString());
        karyawanModel.setTgl_lahir(tgl_lahir.getText().toString());
        karyawanModel.setNo_hp(no_hp.getText().toString());
        karyawanModel.setJabatan(jabatan.getText().toString());
        karyawanModel.setAlamat(alamat.getText().toString());
        karyawanModel.setEmail(email.getText().toString());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Common.updateKaryawan)
                        .addBodyParameter("id_karyawan",""+karyawanModel.getId_karyawan())
                        .addBodyParameter("nama",""+karyawanModel.getNama())
                        .addBodyParameter("tgl_lahir",""+tgl_lahir.getText())
                        .addBodyParameter("jenis_kelamin",""+karyawanModel.getJenis_kelamin())
                        .addBodyParameter("no_hp",""+karyawanModel.getNo_hp())
                        .addBodyParameter("jabatan",""+karyawanModel.getJabatan())
                        .addBodyParameter("alamat",""+karyawanModel.getAlamat())
                        .addBodyParameter("email",""+karyawanModel.getEmail())
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
                                        new AlertDialog.Builder(KaryawanEdit.this)
                                                .setMessage("Data Karyawan Berhasil Diupdate")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
                                                        KaryawanEdit.this.finish();
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(KaryawanEdit.this)
                                                .setMessage("Gagal Edit Data Karyawan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
                                                        KaryawanEdit.this.finish();
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

    public void setTanggal(){
        tgl_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(KaryawanEdit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,month,dayOfMonth);
                        tgl_lahir.setText(fm.format(newDate.getTime()));
                    }
                },year,month,day);
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
        finish();
    }
}
