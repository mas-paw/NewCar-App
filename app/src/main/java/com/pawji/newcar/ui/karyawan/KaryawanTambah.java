package com.pawji.newcar.ui.karyawan;

import androidx.annotation.NonNull;
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

public class KaryawanTambah extends Fragment {
    KaryawanModel karyawanModel = new KaryawanModel();
    EditText txt_nama,no_hp,jabatan,email,alamat,tgl_lahir;
    DatePickerDialog dialog;
    RadioGroup jenis_kelamin;
    RadioButton laki,perempuan;
    Button btn_tambah;
    ProgressDialog progressDialog;
    SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_karyawan_tambah, container, false);
        progressDialog      = new ProgressDialog(getContext());
        AndroidNetworking.initialize(getContext());
        txt_nama = root.findViewById(R.id.txt_nama);
        no_hp = root.findViewById(R.id.txt_no_hp);
        jabatan = root.findViewById(R.id.txt_jabatan);
        email = root.findViewById(R.id.txt_email);
        alamat = root.findViewById(R.id.txt_alamat);
        tgl_lahir = root.findViewById(R.id.txt_tgl_lahir);
        jenis_kelamin = root.findViewById(R.id.opsi);
        laki = root.findViewById(R.id.laki);
        perempuan = root.findViewById(R.id.perempuan);

        setTanggal();
        setJenisKelamin();

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
        }else if(tgl_lahir.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Tanggal Lahir", Toast.LENGTH_SHORT).show();
        }else if(!(laki.isChecked()||perempuan.isChecked())){
            Toast.makeText(getContext(), "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
        }else if(no_hp.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Nomor Handphone", Toast.LENGTH_SHORT).show();
        }else if(jabatan.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Jabatan", Toast.LENGTH_SHORT).show();
        }else if(alamat.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Alamat", Toast.LENGTH_SHORT).show();
        }else if(email.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Email", Toast.LENGTH_SHORT).show();
        }else{
            addKaryawan();
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

    public void addKaryawan(){
        progressDialog.setMessage("Menambahkan Data...");
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
                AndroidNetworking.post(Common.tambahKaryawan)
                        .addBodyParameter("id_karyawan","")
                        .addBodyParameter("nama",""+karyawanModel.getNama())
                        .addBodyParameter("tgl_lahir",""+karyawanModel.getTgl_lahir())
                        .addBodyParameter("jenis_kelamin",""+karyawanModel.getJenis_kelamin())
                        .addBodyParameter("no_hp",""+karyawanModel.getNo_hp())
                        .addBodyParameter("jabatan",""+karyawanModel.getJabatan())
                        .addBodyParameter("alamat",""+karyawanModel.getAlamat())
                        .addBodyParameter("email",""+karyawanModel.getEmail())
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
                                                .setMessage("Data Karyawan Berhasil Ditambahkan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Gagal Menambahkan Data Karyawan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddKaryawanClick(karyawanModel));
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

    public void setTanggal(){
        tgl_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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


}
