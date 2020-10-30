package com.pawji.newcar.ui.status;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pawji.newcar.Adapter.StatusAdapter;
import com.pawji.newcar.Adapter.StatusUserAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddStatusClick;
import com.pawji.newcar.EventBus.HideFABWA;
import com.pawji.newcar.EventBus.HomeClick;
import com.pawji.newcar.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatusUserFragment extends Fragment {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private static final String TAG = "PdfCreatorActivity";
    private File pdfFile;
    List<StatusTempModel> statusModelList;
    List<StatusModel> statusModelList2;
    ProgressDialog progressDialog;
    StatusUserAdapter statusAdapter;
    RecyclerView rv_status;
    StatusTempModel statusTempModel = new StatusTempModel();
    StatusModel statusModel;
   public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_status_user, container, false);
       AndroidNetworking.initialize(getContext());
       EventBus.getDefault().postSticky(new HideFABWA(true));
       statusModelList = new ArrayList<>();
       statusModelList2 = new ArrayList<>();
       progressDialog = new ProgressDialog(getContext());
       rv_status = root.findViewById(R.id.recycler_status);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
       linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
       rv_status.setLayoutManager(linearLayoutManager);

       scrollRefresh();
       final Button submit = root.findViewById(R.id.btn_submit);
       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               submit();
           }
       });

       return root;
    }
    private void submit() {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Common.tambahStatus)
                        .addBodyParameter("nama",""+statusTempModel.getNama())
                        .addBodyParameter("image",""+statusTempModel.getImage())
                        .addBodyParameter("no_ktp",""+statusTempModel.getNo_ktp())
                        .addBodyParameter("no_hp",""+statusTempModel.getNo_hp())
                        .addBodyParameter("nama_mobil",""+statusTempModel.getNama_mobil())
                        .addBodyParameter("tgl_sewa",""+statusTempModel.getTgl_sewa())
                        .addBodyParameter("lama_hari",""+statusTempModel.getLama_hari())
                        .addBodyParameter("supir",""+statusTempModel.getSupir())
                        .addBodyParameter("total_harga",""+statusTempModel.getTotal_harga())
                        .setPriority(Priority.MEDIUM)
                        .setTag("Tambah Data")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d("Respon Invoice",""+response);
                                try{
                                    Boolean status = response.getBoolean("status");
                                    if(status){
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Pemesanan Berhasil. Silahkan Hubungi Via Whatsapp Untuk Konfirmasi")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //eventbus
                                                        deleteStatusTemp();
                                                        getDataStatusInvoice();
//                                                        EventBus.getDefault().postSticky(new HomeClick(statusTempModel));
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDialog.Builder(getContext())
                                                .setMessage("Pemesanan Gagal, Silahkan Coba Lagi")
                                                .setCancelable(false)
                                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        EventBus.getDefault().postSticky(new HomeClick(statusTempModel));
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
                            }
                        });

            }
        },1000);
    }

    private void cetakInvoice() {
        try{
            createPdfWrapperTransaksi();
        }catch(Exception e ){
            e.printStackTrace();
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void createPdfWrapperTransaksi() throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPdfTransaksi();
        }
    }
    private void createPdfTransaksi() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }
        String pdfname = "Invoice "+statusModel.getNama()+".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        document.setPageSize(new Rectangle(300,600));
        try{
            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL, BaseColor.BLACK);
            Font zxc = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph p = new Paragraph("STRUK PEMESANAN \n\n",f);
            String tgl = String.valueOf(new SimpleDateFormat("EEEE, dd MMMM yyyy",new Locale("id")).format(new Date()));
            Paragraph tanggal = new Paragraph("Jakarta "+tgl,g);
            tanggal.setAlignment(Element.ALIGN_RIGHT);
            Paragraph jabatan = new Paragraph("Pemilik\t \n\n\n",g);
            jabatan.setAlignment(Element.ALIGN_RIGHT);
            Paragraph nama = new Paragraph("Syamsudin\t",g);
            nama.setAlignment(Element.ALIGN_RIGHT);
            p.setAlignment(Element.ALIGN_CENTER);
            InputStream ims = getContext().getAssets().open("logo.png");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            image.scaleToFit(150,150);
            Font ban = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph alamat = new Paragraph("Jl. Raya Salihara 1 No.1 Pasar Minggu, Jakarta Selatan, Daerah Khusus Ibukota Jakarta 12540.",ban);
            Paragraph telp = new Paragraph("Telp: (021)78834953",ban);
            alamat.setAlignment(Element.ALIGN_CENTER);
            telp.setAlignment(Element.ALIGN_CENTER);
            Paragraph notransStatus = new Paragraph("No Transaksi",zxc);
            notransStatus.setTabSettings(new TabSettings(56f));
            notransStatus.add(Chunk.TABBING);
            notransStatus.add(new Chunk(": "+statusModel.getNo_transaksi()));
            Paragraph namaStatus = new Paragraph("Nama",zxc);
            namaStatus.setTabSettings(new TabSettings(56f));
            namaStatus.add(Chunk.TABBING);
            namaStatus.add(Chunk.TABBING);
            namaStatus.add(new Chunk(": "+statusModel.getNama()));
            Paragraph no_ktp = new Paragraph("No KTP",zxc);
            no_ktp.setTabSettings(new TabSettings(56f));
            no_ktp.add(Chunk.TABBING);
            no_ktp.add(Chunk.TABBING);
            no_ktp.add(new Chunk(": "+statusModel.getNo_ktp()));
            Paragraph no_hp = new Paragraph("No HP",zxc);
            no_hp.setTabSettings(new TabSettings(56f));
            no_hp.add(Chunk.TABBING);
            no_hp.add(Chunk.TABBING);
            no_hp.add(new Chunk(": "+statusModel.getNo_hp()));
            Paragraph tgl_Sewa = new Paragraph("Tanggal",zxc);
            tgl_Sewa.setTabSettings(new TabSettings(56f));
            tgl_Sewa.add(Chunk.TABBING);
            tgl_Sewa.add(Chunk.TABBING);
            tgl_Sewa.add(new Chunk(": "+statusModel.getTgl_sewa()));
            Paragraph lama = new Paragraph("Lama Hari",zxc);
            lama.setTabSettings(new TabSettings(56f));
            lama.add(Chunk.TABBING);
            lama.add(new Chunk(": "+statusModel.getLama_hari()+" Hari"));
            Paragraph total = new Paragraph("Total",zxc);
            total.setTabSettings(new TabSettings(56f));
            total.add(Chunk.TABBING);
            total.add(Chunk.TABBING);
            total.add(new Chunk(": Rp "+statusModel.getTotal_harga()));
            document.add(image);
            document.add(alamat);
            document.add(telp);
            document.add(p);
            document.add(new Paragraph("\n"));
            document.add(notransStatus);
            document.add(namaStatus);
            document.add(no_ktp);
            document.add(no_hp);
            document.add(tgl_Sewa);
            document.add(lama);
            document.add(total);
            document.add(new Paragraph("\n\n\n"));
            //
            document.add(tanggal);
            document.add(jabatan);
            document.add(nama);
            document.close();
            previewPdfTransaksi();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void previewPdfTransaksi() {
        PackageManager packageManager = getContext().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getContext(),getContext().getPackageName()+".fileprovider",pdfFile);
            intent.setData(uri);
            getContext().startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDataStatusInvoice() {
        AndroidNetworking.post(Common.getSingleStatus)
                .addBodyParameter("no_ktp",statusTempModel.getNo_ktp())
                .setTag("Get Single Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getTag(),"onResponse : "+response);
                        try{
                            Boolean status = response.getBoolean("status");
                            if(status){
                                JSONArray ja = response.getJSONArray("result");
                                Log.d("respon", "" + ja);
                                for(int i = 0 ; i < ja.length() ; i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    statusModel = new StatusModel(
                                            jo.getInt("no_transaksi"),
                                            jo.getString("nama"),
                                            jo.getString("image"),
                                            jo.getString("no_ktp"),
                                            jo.getString("no_hp"),
                                            jo.getString("nama_mobil"),
                                            jo.getString("tgl_sewa"),
                                            jo.getInt("lama_hari"),
                                            jo.getString("supir"),
                                            jo.getInt("total_harga"));
                                }
                                cetakInvoice();
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void deleteStatusTemp() {
        AndroidNetworking.post(Common.deleteStatusUser)
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

                            } else {

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

    public void scrollRefresh(){
        progressDialog.setMessage("Refresh Data");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        },1200);
    }

    public void getData() {
        statusModelList.clear();
        AndroidNetworking.get(Common.getStatusUser)
                .setTag("Get Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d(getTag(),"onResponse : "+response);
                        try{
                            Boolean status = response.getBoolean("status");
                            if(status){
                                JSONArray ja = response.getJSONArray("result");
                                Log.d("respon", "" + ja);
                                for(int i = 0 ; i < ja.length() ; i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    statusTempModel = new StatusTempModel(
                                            jo.getInt("no_transaksi"),
                                            jo.getString("nama"),
                                            jo.getString("image"),
                                            jo.getString("no_ktp"),
                                            jo.getString("no_hp"),
                                            jo.getString("nama_mobil"),
                                            jo.getInt("harga_mobil"),
                                            jo.getString("tgl_sewa"),
                                            jo.getInt("lama_hari"),
                                            jo.getString("supir"),
                                            jo.getInt("total_harga")
                                    );
                                    statusModelList.add(statusTempModel);
                                }
                                statusAdapter = new StatusUserAdapter(getContext(),statusModelList,progressDialog);
                                rv_status.setAdapter(statusAdapter);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                statusAdapter = new StatusUserAdapter(getContext(),statusModelList,progressDialog);
                                rv_status.setAdapter(statusAdapter);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        anError.printStackTrace();
                        anError.getErrorCode();
                        anError.getMessage();
                    }
                });
    }

    @Override
    public void onResume() {
        EventBus.getDefault().postSticky(new HideFABWA(true));
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().postSticky(new HideFABWA(false));
        super.onPause();
    }
}