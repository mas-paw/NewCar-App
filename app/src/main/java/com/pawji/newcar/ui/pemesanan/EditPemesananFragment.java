package com.pawji.newcar.ui.pemesanan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.HideFABWA;
import com.pawji.newcar.EventBus.TambahStatusClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.status.StatusModel;
import com.pawji.newcar.ui.status.StatusTempModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditPemesananFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    Spinner spinnerSupir;
    String supir;
    EditText tanggal_booking,tanggal_kembali,nama,no_ktp,no_hp;
    TextView lamahari,nama_mobil,harga;
    ImageView imagecar;
    Button btnpesan;
    ProgressDialog progressDialog;
    int a,b,c;
    private DatePickerDialog dialog;
    StatusTempModel statusModel = new StatusTempModel();
    SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy");
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit_pemesanan, container, false);
        EventBus.getDefault().postSticky(new HideFABWA(true));
        progressDialog      = new ProgressDialog(getContext());
        spinnerSupir = root.findViewById(R.id.spinner_supir);
        tanggal_booking = root.findViewById(R.id.tanggal_booking);
        tanggal_kembali = root.findViewById(R.id.tanggal_kembali);
        nama = root.findViewById(R.id.txt_nama);
        no_ktp = root.findViewById(R.id.txt_no_ktp);
        no_hp = root.findViewById(R.id.txt_no_hp);
        lamahari = root.findViewById(R.id.lama);
        nama_mobil = root.findViewById(R.id.car_name);
        harga = root.findViewById(R.id.car_price);
        imagecar = root.findViewById(R.id.img_car);
        btnpesan = root.findViewById(R.id.btnPesan);

        fillData();
        setTanggal();

        btnpesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.supir,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerSupir.setAdapter(adapter);
        if(supir != null){
            int pos = adapter.getPosition(supir);
            spinnerSupir.setSelection(pos);
        }
        spinnerSupir.setOnItemSelectedListener(this);

        return root;
    }

    private void validasi(){
        if(nama.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Nama", Toast.LENGTH_SHORT).show();
        }else if(no_ktp.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Nomor KTP", Toast.LENGTH_SHORT).show();
        }else if(no_hp.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Nomor HP", Toast.LENGTH_SHORT).show();
        }else if(tanggal_booking.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Tanggal Sewa", Toast.LENGTH_SHORT).show();
        }else if(tanggal_kembali.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Masukkan Tanggal Kembali", Toast.LENGTH_SHORT).show();
        }else if(lamahari.getText().toString() == ""){
            Toast.makeText(getContext(), "Masukkan Lama Hari", Toast.LENGTH_SHORT).show();
        }else{
            setTotal();
            addStatusTemp();
        }
    }

    private void setTotal() {
        a = Integer.parseInt(harga.getText().toString());
        b = Integer.parseInt(lamahari.getText().toString());
        c = a*b;
    }

    private void addStatusTemp() {
        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        statusModel.setNo_transaksi(Common.selectedStatus.getNo_transaksi());
        statusModel.setNama(nama.getText().toString());
        statusModel.setImage(Common.selectedStatus.getImage());
        statusModel.setNo_ktp(no_ktp.getText().toString());
        statusModel.setNo_hp(no_hp.getText().toString());
        statusModel.setNama_mobil(Common.selectedStatus.getNama_mobil());
        statusModel.setHarga(Common.selectedStatus.getHarga());
        statusModel.setTgl_sewa(tanggal_booking.getText().toString());
        statusModel.setLama_hari(Integer.parseInt(lamahari.getText().toString()));
        statusModel.setSupir(supir);
        statusModel.setTotal_harga(c);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Common.updateStatusUser)
                        .addBodyParameter("no_transaksi",""+statusModel.getNo_transaksi())
                        .addBodyParameter("nama",""+statusModel.getNama())
                        .addBodyParameter("image",""+statusModel.getImage())
                        .addBodyParameter("no_ktp",""+statusModel.getNo_ktp())
                        .addBodyParameter("no_hp",""+statusModel.getNo_hp())
                        .addBodyParameter("nama_mobil",""+statusModel.getNama_mobil())
                        .addBodyParameter("harga_mobil",""+statusModel.getHarga())
                        .addBodyParameter("tgl_sewa",""+statusModel.getTgl_sewa())
                        .addBodyParameter("lama_hari",""+statusModel.getLama_hari())
                        .addBodyParameter("supir",""+statusModel.getSupir())
                        .addBodyParameter("total_harga",""+statusModel.getTotal_harga())
                        .setPriority(Priority.MEDIUM)
                        .setTag("Tambah Data")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d("Cek Edit",""+response);
                                try{
                                    Boolean status = response.getBoolean("status");
                                    String pesan = response.getString("result");
                                    Toast.makeText(getContext(), ""+pesan, Toast.LENGTH_SHORT).show();
                                    Log.d("Status",""+status);
                                    if(status){
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Data Pemesanan Berhasil Dirubah")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new TambahStatusClick(statusModel));
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Gagal Merubah Data Pemesanan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new TambahStatusClick(statusModel));
                                                    }
                                                }).show();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("Error Merubah Data ",""+anError.getErrorBody());
                                anError.printStackTrace();
                                progressDialog.dismiss();
                            }
                        });

            }
        },1000);
    }

    private void fillData() {
        Glide.with(getContext()).load(Common.selectedStatus.getImage()).into(imagecar);
        nama_mobil.setText(""+Common.selectedStatus.getNama_mobil());
        harga.setText(""+Common.selectedStatus.getHarga());
        nama.setText(Common.selectedStatus.getNama());
        no_ktp.setText(Common.selectedStatus.getNo_ktp());
        no_hp.setText(Common.selectedStatus.getNo_hp());
        tanggal_booking.setText(Common.selectedStatus.getTgl_sewa());
        lamahari.setText(""+Common.selectedStatus.getLama_hari());
        supir = Common.selectedStatus.getSupir();
    }


    private void setTanggal() {
        tanggal_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,month,dayOfMonth);
                        tanggal_booking.setText(fm.format(newDate.getTime()));
                        setLamaHari();
                    }
                },year,month,day);
                dialog.show();
            }
        });

        tanggal_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,month,dayOfMonth);
                        tanggal_kembali.setText(fm.format(newDate.getTime()));
                        setLamaHari();
                    }
                },year,month,day);
                dialog.show();
            }
        });
    }
    private void setLamaHari(){
        if(!TextUtils.isEmpty(tanggal_booking.getText().toString().trim()) && !TextUtils.isEmpty(tanggal_kembali.getText().toString().trim())){
            try{
                String tgl_satu = tanggal_booking.getText().toString();
                String tgl_dua = tanggal_kembali.getText().toString();
                DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                Date tgl_awal = (Date) date.parse(tgl_satu);
                Date tgl_akhir = (Date) date.parse(tgl_dua);

                long bedaHari = tgl_akhir.getTime() - tgl_awal.getTime();
                long selisih = TimeUnit.MILLISECONDS.toDays(bedaHari);

                if (selisih <0){
                    Toast.makeText(getContext(), "Masukan Tanggal Dengan Benar ", Toast.LENGTH_SHORT).show();
                    lamahari.setText("");
                }else{
                    lamahari.setText(Long.toString(selisih));
                }


            }catch(Exception e){
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().postSticky(new HideFABWA(false));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().postSticky(new HideFABWA(true));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        supir = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        supir = "YA";
    }
}
