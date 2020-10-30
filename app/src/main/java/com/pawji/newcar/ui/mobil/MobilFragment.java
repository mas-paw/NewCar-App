package com.pawji.newcar.ui.mobil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pawji.newcar.Adapter.MobilAdapter;
import com.pawji.newcar.Adapter.SupirAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.TambahMobilClick;
import com.pawji.newcar.EventBus.TambahSupirClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.supir.SupirDetail;
import com.pawji.newcar.ui.supir.SupirModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MobilFragment extends Fragment {
    List<MobilModel> mobilModelList;
    ProgressDialog progressDialog;
    Button btn_tambah;
    RecyclerView rv_mobil;
    SwipeRefreshLayout srl_mobil;
    MobilAdapter mobilAdapter;
    MobilModel mobilModel = new MobilModel();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mobil, container, false);
        AndroidNetworking.initialize(getContext());
        mobilModelList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        srl_mobil = root.findViewById(R.id.srl_mobil);
        rv_mobil = root.findViewById(R.id.recycler_mobil);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_mobil.setLayoutManager(linearLayoutManager);
        srl_mobil.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                srl_mobil.setRefreshing(false);
            }
        });

        btn_tambah = root.findViewById(R.id.btn_tambah);
        scrollRefresh();
        tambahMobil();

        return root;
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
        mobilModelList.clear();
        AndroidNetworking.get(Common.getMobil)
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
                                    mobilModelList.add(new MobilModel(
                                            jo.getInt("kode_mobil"),
                                            jo.getString("nama"),
                                            jo.getString("image"),
                                            jo.getInt("harga")
                                            ));
                                }
                                mobilAdapter = new MobilAdapter(getContext(),mobilModelList,progressDialog);
                                rv_mobil.setAdapter(mobilAdapter);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                mobilAdapter = new MobilAdapter(getContext(),mobilModelList,progressDialog);
                                rv_mobil.setAdapter(mobilAdapter);
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

    private void tambahMobil() {
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MobilTambah.class);
                getContext().startActivity(intent);
            }
        });
    }
}