package com.pawji.newcar.ui.karyawan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pawji.newcar.Adapter.KaryawanAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.TambahKaryawanClick;
import com.pawji.newcar.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KaryawanFragment extends Fragment {

    List<KaryawanModel> karyawanModelList;
    ProgressDialog progressDialog;
    SwipeRefreshLayout srl_karyawan;
    KaryawanAdapter karyawanAdapter;
    RecyclerView rv_karyawan;
    Button btn_tambah;
    KaryawanModel karyawanModel = new KaryawanModel();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_karyawan, container, false);
        AndroidNetworking.initialize(getContext());
        karyawanModelList = new ArrayList<>();
        srl_karyawan = (SwipeRefreshLayout)root.findViewById(R.id.srl_karyawan);
         rv_karyawan = root.findViewById(R.id.recycler_karyawan);
        progressDialog = new ProgressDialog(getContext());
        rv_karyawan.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_karyawan.setLayoutManager(linearLayoutManager);
        srl_karyawan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                srl_karyawan.setRefreshing(false);
            }
        });
        btn_tambah = root.findViewById(R.id.btn_tambah);



        scrollRefresh();
        tambahKaryawan();

//        final TextView textView = root.findViewById(R.id.text_gallery);
        return root;
    }

    private void tambahKaryawan() {
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Testing", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().postSticky(new TambahKaryawanClick(karyawanModel));
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
        karyawanModelList.clear();
        AndroidNetworking.get(Common.getKaryawan)
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
                                karyawanAdapter = new KaryawanAdapter(getContext(),karyawanModelList,progressDialog);
                                rv_karyawan.setAdapter(karyawanAdapter);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                karyawanAdapter = new KaryawanAdapter(getContext(),karyawanModelList,progressDialog);
                                rv_karyawan.setAdapter(karyawanAdapter);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        progressDialog.dismiss();
                    }
                });
    }
}