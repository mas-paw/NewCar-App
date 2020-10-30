package com.pawji.newcar.ui.mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddMobilClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.supir.SupirModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;

public class MobilEdit extends AppCompatActivity {
    MobilModel mobilModel = new MobilModel();
    EditText txt_nama,txt_harga;
    Button btn_edit,btn_pilih;
    ProgressDialog progressDialog;
    ImageView imageView;
    File imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobil_edit);

        progressDialog      = new ProgressDialog(this);
        AndroidNetworking.initialize(this);
        btn_pilih = findViewById(R.id.btn_pilih);
        btn_edit = findViewById(R.id.btn_edit);
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        imageView = findViewById(R.id.img_mobil);

//        getDataIntent();
//        fillDataIntent();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });
        btn_pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }

//    private void fillDataIntent() {
//        txt_nama.setText(supirModel.getNama());
//        no_hp.setText(supirModel.getNo_hp());
//        alamat.setText(supirModel.getAlamat());
//        umur.setText(new StringBuilder("").append(supirModel.getUmur()));
//    }
//
//    private void getDataIntent() {
//        mobilModel.setId_supir(getIntent().getIntExtra("id_supir",0));
//        mobilModel.setNama(getIntent().getStringExtra("nama"));
//        mobilModel.setImage(getIntent().getIntExtra("umur",0));
//        mobilModel.setNo_hp(getIntent().getStringExtra("no_hp"));
//    }

    private void validasi() {
        if(txt_nama.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Nama Mobil", Toast.LENGTH_SHORT).show();
        }else if(txt_harga.getText().toString().isEmpty()){
            Toast.makeText(this, "Masukkan Harga Mobil", Toast.LENGTH_SHORT).show();
        }else if(imageFile == null){
            Toast.makeText(this, "Masukkan Gambar", Toast.LENGTH_SHORT).show();
        }else{
            uploadData();
        }
    }

    private void uploadData() {
        progressDialog.setMessage("Menambahkan Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.upload(Common.updateMobil)
                        .addMultipartFile("image",imageFile)
                        .addMultipartParameter("kode_mobil",""+mobilModel.getKode_mobil())
                        .addMultipartParameter("nama",""+mobilModel.getNama())
                        .addMultipartParameter("harga",""+mobilModel.getHarga())
                        .setTag("Tambah Data")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d("Cek Tambah",""+response);
                                try{
                                    Boolean status = response.getBoolean("status");
                                    String pesan = response.getString("result");
                                    Toast.makeText(MobilEdit.this, ""+pesan, Toast.LENGTH_SHORT).show();
                                    Log.d("Status",""+status);
                                    if(status){
                                        new AlertDialog.Builder(MobilEdit.this)
                                                .setMessage("Data Mobil Berhasil Ditambahkan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddMobilClick(mobilModel));
                                                        MobilEdit.this.finish();
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(MobilEdit.this)
                                                .setMessage("Gagal Menambahkan Data Mobil")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddMobilClick(mobilModel));
                                                        MobilEdit.this.finish();
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
                                Log.d("Error Nambah Data ",""+anError.getMessage());
                                Log.d("Error Nambah Data",""+anError.getStackTrace());
                                Log.d("Error Nambah Data",""+anError.getErrorCode());
                                Log.d("Error Nambah Data",""+anError.getLocalizedMessage());
                                anError.getResponse();
                                anError.printStackTrace();
                            }
                        });
            }
        },1000);
    }

    private void showFileChooser() {
        ImagePicker.Companion.with(MobilEdit.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Ukuran menjadi dibawah 1 MB
                .maxResultSize(620, 620)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == ImagePicker.REQUEST_CODE) {
                /*
                 * Keterangan :
                 * Mengambil data dari Image Picker
                 * KDV 2019-11-17
                 */
                imageFile = ImagePicker.Companion.getFile(data);

                /*
                 * Keterangan :
                 * Set img View dari File image yang kita set sebelumnya
                 * KDV 2019-11-17
                 */
                imageView.setImageURI(Uri.fromFile(imageFile));

                /*
                 * Keterangan :
                 * Membuat LOG untuk cek lokasi file
                 * KDV 2019-11-17
                 */
                Log.d("TambahKategori_Activity", "onActivityResult: "+imageFile.getPath());
                Log.d("TambahKategori_Activity", "onActivityResult: "+imageFile.getAbsolutePath());

            }


        }
    }
}
