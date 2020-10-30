package com.pawji.newcar.ui.mobil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddMobilClick;
import com.pawji.newcar.EventBus.AddSupirClick;
import com.pawji.newcar.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MobilTambah extends AppCompatActivity {
    Button btn_tambah,btn_pilih;
    ImageView imageView;
    EditText txt_name,txt_harga;
    File imageFile;
    ProgressDialog progressDialog;
    MobilModel mobilModel = new MobilModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mobil_tambah);
        progressDialog      = new ProgressDialog(this);
        AndroidNetworking.initialize(this);
        btn_pilih = findViewById(R.id.btn_pilih);
        btn_tambah = findViewById(R.id.btn_tambah);
        txt_name = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        imageView = findViewById(R.id.img_mobil);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
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

    private void validasi() {
        if(txt_name.getText().toString().isEmpty()){
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

        mobilModel.setNama(txt_name.getText().toString());
        mobilModel.setHarga(Integer.parseInt(txt_harga.getText().toString()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.upload(Common.mobilUpload)
                        .addMultipartFile("image",imageFile)
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
                                    Toast.makeText(MobilTambah.this, ""+pesan, Toast.LENGTH_SHORT).show();
                                    Log.d("Status",""+status);
                                    if(status){
                                        new AlertDialog.Builder(MobilTambah.this)
                                                .setMessage("Data Mobil Berhasil Ditambahkan")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddMobilClick(mobilModel));
                                                        MobilTambah.this.finish();
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(MobilTambah.this)
                                                .setMessage("Gagal Menambahkan Data Mobil")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        EventBus.getDefault().postSticky(new AddMobilClick(mobilModel));
                                                        MobilTambah.this.finish();
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
        ImagePicker.Companion.with(MobilTambah.this)
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().postSticky(new AddMobilClick(mobilModel));
        finish();

    }
}
