package com.pawji.newcar.ui.report;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.pawji.newcar.Adapter.KaryawanAdapter;
import com.pawji.newcar.Adapter.MobilAdapter;
import com.pawji.newcar.Adapter.SupirAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanModel;
import com.pawji.newcar.ui.mobil.MobilModel;
import com.pawji.newcar.ui.status.TransaksiModel;
import com.pawji.newcar.ui.supir.SupirModel;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportFragment extends Fragment {
    private static final String TAG = "PdfCreatorActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;

    List<SupirModel> supirModelList;
    List<KaryawanModel> karyawanModelList;
    List<MobilModel> mobilModelList;
    List<TransaksiModel> transaksiModelList;
    Button karyawan,supir,mobil,transaksi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        supirModelList = new ArrayList<>();
        karyawanModelList = new ArrayList<>();
        mobilModelList = new ArrayList<>();
        transaksiModelList = new ArrayList<>();
        supir = root.findViewById(R.id.btn_report_supir);
        karyawan = root.findViewById(R.id.btn_report_karyawan);
        mobil = root.findViewById(R.id.btn_report_data_mobil);
        transaksi = root.findViewById(R.id.btn_report_transaksi);

        getDataTransaksi();
        getDataMobil();
        getDataSupir();
        getDataKaryawan();

        supir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapperSupir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        karyawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapperKaryawan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapperMobil();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapperTransaksi();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    //
    // supir report
    private void createPdfWrapperSupir() throws FileNotFoundException, DocumentException {
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
            createPdfSupir();
        }
    }
    private void createPdfSupir() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }
        String pdfname = "Laporan Data Supir.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{3, 3, 3, 3, 3});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(40);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("ID");
        table.addCell("Nama");
        table.addCell("Nomor HP");
        table.addCell("Umur");
        table.addCell("Alamat");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < supirModelList.size(); i++) {
            SupirModel supirModel = supirModelList.get(i);
            table.addCell(""+supirModel.getId_supir());
            table.addCell(supirModel.getNama());
            table.addCell(supirModel.getUmur()+" Tahun");
            table.addCell(supirModel.getNo_hp());
            table.addCell(supirModel.getAlamat());
        }
        try{
            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK);
            Font ban = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL, BaseColor.BLACK);
            Paragraph p = new Paragraph("DATA SUPIR \n\n",f);
            Paragraph alamat = new Paragraph("Jl. Raya Salihara 1 No.1 Pasar Minggu, Jakarta Selatan, Daerah Khusus Ibukota Jakarta 12540.",ban);
            Paragraph telp = new Paragraph("Telp: (021)78834953",ban);
            alamat.setAlignment(Element.ALIGN_CENTER);
            telp.setAlignment(Element.ALIGN_CENTER);
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
            document.add(image);
            document.add(alamat);
            document.add(telp);
            document.add(p);
            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(tanggal);
            document.add(jabatan);
            document.add(nama);
            document.close();
            previewPdfSupir();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void previewPdfSupir() {
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
    public void getDataSupir() {
        supirModelList.clear();
        AndroidNetworking.get(Common.getSupir)
                .setTag("Get Data")
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
//                                    String id = jo.getString("id_supir");
//                                    String nama= jo.getString("nama");
//                                    String umur = jo.getString("umur");
//                                    String no_hp = jo.getString("no_hp");
//                                    String alamat = jo.getString("alamat");
//                                    HashMap<String,String> supir = new HashMap<>();
//                                    supir.put("id",id);
//                                    supir.put("nama",nama);
//                                    supir.put("umur",umur);
//                                    supir.put("no_hp",no_hp);
//                                    supir.put("alamat",alamat);

                                    supirModelList.add(new SupirModel(
                                            jo.getInt("id_supir"),
                                            jo.getString("nama"),
                                            jo.getInt("umur"),
                                            jo.getString("no_hp"),
                                            jo.getString("alamat")
                                    ));
                                }
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
    // end supir
    //

    //
    //karyawan report
    private void createPdfWrapperKaryawan() throws FileNotFoundException, DocumentException {
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
            createPdfKaryawan();
        }
    }
    private void createPdfKaryawan() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }
        String pdfname = "Laporan Data Karyawan.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.LEGAL.rotate());
        PdfPTable table = new PdfPTable(8);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(35);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("ID");
        table.addCell("Nama");
        table.addCell("Tanggal Lahir");
        table.addCell("Jenis Kelamin");
        table.addCell("Nomor HP");
        table.addCell("Jabatan");
        table.addCell("Alamat");
        table.addCell("Email");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < karyawanModelList.size(); i++) {
            KaryawanModel karyawanModel = karyawanModelList.get(i);
            table.addCell(""+karyawanModel.getId_karyawan());
            table.addCell(karyawanModel.getNama());
            table.addCell(karyawanModel.getTgl_lahir());
            table.addCell(karyawanModel.getJenis_kelamin());
            table.addCell(karyawanModel.getNo_hp());
            table.addCell(karyawanModel.getJabatan());
            table.addCell(karyawanModel.getAlamat());
            table.addCell(karyawanModel.getEmail());
        }
        try{
            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 19.0f, Font.BOLD, BaseColor.BLACK);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL, BaseColor.BLACK);
            Font ban = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph p = new Paragraph("DATA KARYAWAN \n\n",f);
            Paragraph alamat = new Paragraph("Jl. Raya Salihara 1 No.1 Pasar Minggu, Jakarta Selatan, Daerah Khusus Ibukota Jakarta 12540.",ban);
            Paragraph telp = new Paragraph("Telp: (021)78834953",ban);
            alamat.setAlignment(Element.ALIGN_CENTER);
            telp.setAlignment(Element.ALIGN_CENTER);
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
            document.add(image);
            document.add(alamat);
            document.add(telp);
            document.add(p);
            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(tanggal);
            document.add(jabatan);
            document.add(nama);
            document.close();
            previewPdfKaryawan();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void previewPdfKaryawan() {
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
    public void getDataKaryawan() {
        karyawanModelList.clear();
        AndroidNetworking.get(Common.getKaryawan)
                .setTag("Get Data")
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
                                    karyawanModelList.add(new KaryawanModel(
                                            jo.getInt("id_karyawan"),
                                            jo.getString("nama"),
                                            jo.getString("tgl_lahir"),
                                            jo.getString("jenis_kelamin"),
                                            jo.getString("no_hp"),
                                            jo.getString("jabatan"),
                                            jo.getString("alamat"),
                                            jo.getString("email")
                                    ));
                                }
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
    //end report
    //

    //
    //mobil report
    private void createPdfWrapperMobil() throws FileNotFoundException, DocumentException {
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
            createPdfMobil();
        }
    }
    private void createPdfMobil() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }
        String pdfname = "Laporan Data Mobil.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(3);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(40);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("ID");
        table.addCell("Nama Mobil");
        table.addCell("Harga Sewa");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < mobilModelList.size(); i++) {
            MobilModel mobilModel = mobilModelList.get(i);
            table.addCell(""+mobilModel.getKode_mobil());
            table.addCell(mobilModel.getNama());
            table.addCell("Rp "+mobilModel.getHarga());
        }
        try{
            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL, BaseColor.BLACK);
            Font ban = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph p = new Paragraph("DATA MOBIL \n\n",f);
            Paragraph alamat = new Paragraph("Jl. Raya Salihara 1 No.1 Pasar Minggu, Jakarta Selatan, Daerah Khusus Ibukota Jakarta 12540.",ban);
            Paragraph telp = new Paragraph("Telp: (021)78834953",ban);
            alamat.setAlignment(Element.ALIGN_CENTER);
            telp.setAlignment(Element.ALIGN_CENTER);
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
            document.add(image);
            document.add(alamat);
            document.add(telp);
            document.add(p);
            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(tanggal);
            document.add(jabatan);
            document.add(nama);
            document.close();
            previewPdfMobil();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void previewPdfMobil() {
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
    public void getDataMobil() {
        mobilModelList.clear();
        AndroidNetworking.get(Common.getMobil)
                .setTag("Get Data")
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
                                    mobilModelList.add(new MobilModel(
                                            jo.getInt("kode_mobil"),
                                            jo.getString("nama"),
                                            jo.getString("image"),
                                            jo.getInt("harga")
                                    ));
                                }
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
    //end report
    //

    //
    //transaksi report
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
        String pdfname = "Laporan Data Transaksi.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.LEGAL.rotate());
        PdfPTable table = new PdfPTable(10);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(40);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("No");
        table.addCell("Nama");
        table.addCell("No HP");
        table.addCell("No KTP");
        table.addCell("Nama Mobil");
        table.addCell("Tanggal Sewa");
        table.addCell("Lama Hari");
        table.addCell("Supir");
        table.addCell("Denda");
        table.addCell("Total");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i = 0; i < transaksiModelList.size(); i++) {
            TransaksiModel transaksiModel = transaksiModelList.get(i);
            table.addCell(""+transaksiModel.getNo_transaksi());
            table.addCell(transaksiModel.getNama());
            table.addCell(transaksiModel.getNo_hp());
            table.addCell(transaksiModel.getNo_ktp());
            table.addCell(transaksiModel.getNama_mobil());
            table.addCell(transaksiModel.getTgl_sewa());
            table.addCell(transaksiModel.getLama_hari()+" Hari");
            table.addCell(transaksiModel.getSupir());
            table.addCell("Rp "+transaksiModel.getDenda());
            table.addCell("Rp "+transaksiModel.getTotal_harga());
        }
        try{
            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL, BaseColor.BLACK);
            Font ban = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph p = new Paragraph("DATA TRANSAKSI \n\n",f);
            Paragraph alamat = new Paragraph("Jl. Raya Salihara 1 No.1 Pasar Minggu, Jakarta Selatan, Daerah Khusus Ibukota Jakarta 12540.",ban);
            Paragraph telp = new Paragraph("Telp: (021)78834953",ban);
            alamat.setAlignment(Element.ALIGN_CENTER);
            telp.setAlignment(Element.ALIGN_CENTER);
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
            document.add(image);
            document.add(alamat);
            document.add(telp);
            document.add(p);
            document.add(table);
            document.add(new Paragraph("\n"));
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
    public void getDataTransaksi() {
        transaksiModelList.clear();
        AndroidNetworking.get(Common.getTransaksi)
                .setTag("Get Data")
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
                                    transaksiModelList.add(new TransaksiModel(
                                            jo.getInt("no_transaksi"),
                                            jo.getString("nama"),
                                            jo.getString("no_hp"),
                                            jo.getString("no_ktp"),
                                            jo.getString("nama_mobil"),
                                            jo.getString("tgl_sewa"),
                                            jo.getInt("lama_hari"),
                                            jo.getString("supir"),
                                            jo.getInt("denda"),
                                            jo.getInt("total_harga")
                                    ));
                                }
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
    //end report
    //
}