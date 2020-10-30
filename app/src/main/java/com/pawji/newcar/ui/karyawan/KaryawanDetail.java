package com.pawji.newcar.ui.karyawan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.R;

import org.greenrobot.eventbus.EventBus;

public class KaryawanDetail extends AppCompatActivity {
    KaryawanModel karyawanModel = new KaryawanModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan_detail);

        TextView txt_nama = findViewById(R.id.txt_nama);
        TextView txt_tgl_lahir = findViewById(R.id.txt_tgl_lahir);
        TextView txt_jenis_kelamin = findViewById(R.id.txt_jenis_kelamin);
        TextView txt_no_hp = findViewById(R.id.txt_no_hp);
        TextView txt_jabatan = findViewById(R.id.txt_jabatan);
        TextView txt_alamat = findViewById(R.id.txt_alamat);
        TextView txt_email = findViewById(R.id.txt_email);
        TextView txt_id = findViewById(R.id.txt_id);
        getDataIntent();
        txt_id.setText(new StringBuilder("").append(karyawanModel.getId_karyawan()));
        txt_nama.setText(karyawanModel.getNama());
        txt_tgl_lahir.setText(karyawanModel.getTgl_lahir());
        txt_jenis_kelamin.setText(karyawanModel.getJenis_kelamin());
        txt_no_hp.setText(karyawanModel.getNo_hp());
        txt_jabatan.setText(karyawanModel.getJabatan());
        txt_alamat.setText(karyawanModel.getAlamat());
        txt_email.setText(karyawanModel.getEmail());

        Button btn_edit = findViewById(R.id.btn_Edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KaryawanDetail.this, KaryawanEdit.class);
                intent.putExtra("id_karyawan",karyawanModel.getId_karyawan());
                intent.putExtra("nama",karyawanModel.getNama());
                intent.putExtra("tgl_lahir",karyawanModel.getTgl_lahir());
                intent.putExtra("jenis_kelamin",karyawanModel.getJenis_kelamin());
                intent.putExtra("no_hp",karyawanModel.getNo_hp());
                intent.putExtra("jabatan",karyawanModel.getJabatan());
                intent.putExtra("alamat",karyawanModel.getAlamat());
                intent.putExtra("email",karyawanModel.getEmail());
                startActivity(intent);
                finish();
            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
        finish();
    }
}