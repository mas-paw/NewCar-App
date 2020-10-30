package com.pawji.newcar.ui.supir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pawji.newcar.EventBus.AddSupirClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanDetail;
import com.pawji.newcar.ui.karyawan.KaryawanEdit;

import org.greenrobot.eventbus.EventBus;

public class SupirDetail extends AppCompatActivity {
    SupirModel supirModel = new SupirModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supir_detail);

        TextView txt_nama = findViewById(R.id.txt_nama);
        TextView txt_no_hp = findViewById(R.id.txt_no_hp);
        TextView txt_umur = findViewById(R.id.txt_umur);
        TextView txt_alamat = findViewById(R.id.txt_alamat);
        TextView txt_id = findViewById(R.id.txt_id);

        getDataIntent();

        txt_id.setText(new StringBuilder("").append(supirModel.getId_supir()));
        txt_nama.setText(supirModel.getNama());
        txt_no_hp.setText(supirModel.getNo_hp());
        txt_umur.setText(new StringBuilder("").append(supirModel.getUmur()));
        txt_alamat.setText(supirModel.getAlamat());

        Button btn_edit = findViewById(R.id.btn_Edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupirDetail.this, SupirEdit.class);
                intent.putExtra("id_supir",supirModel.getId_supir());
                intent.putExtra("nama",supirModel.getNama());
                intent.putExtra("no_hp",supirModel.getNo_hp());
                intent.putExtra("umur",supirModel.getUmur());
                intent.putExtra("alamat",supirModel.getAlamat());
                startActivity(intent);
                finish();
            }
        });

    }

    private void getDataIntent() {
        supirModel.setId_supir(getIntent().getIntExtra("id_supir",0));
        supirModel.setNama(getIntent().getStringExtra("nama"));
        supirModel.setNo_hp(getIntent().getStringExtra("no_hp"));
        supirModel.setUmur(getIntent().getIntExtra("umur",0));
        supirModel.setAlamat(getIntent().getStringExtra("alamat"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        EventBus.getDefault().postSticky(new AddSupirClick(supirModel));
    }
}
