package com.pawji.newcar.ui.status;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.pawji.newcar.Adapter.StatusAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {
    List<StatusModel> statusModelList;
    ProgressDialog progressDialog;
    SwipeRefreshLayout srl_status;
    StatusAdapter statusAdapter;
    RecyclerView rv_status;
    StatusModel statusModel= new StatusModel();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);
        AndroidNetworking.initialize(getContext());
        statusModelList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        srl_status = root.findViewById(R.id.srl_status);
        rv_status = root.findViewById(R.id.recycler_status);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_status.setLayoutManager(linearLayoutManager);
        srl_status.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                srl_status.setRefreshing(false);
            }
        });
        scrollRefresh();
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
        statusModelList.clear();
        AndroidNetworking.get(Common.getStatus)
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
                                    statusModelList.add(new StatusModel(
                                            jo.getInt("no_transaksi"),
                                            jo.getString("nama"),
                                            jo.getString("image"),
                                            jo.getString("no_ktp"),
                                            jo.getString("no_hp"),
                                            jo.getString("nama_mobil"),
                                            jo.getString("tgl_sewa"),
                                            jo.getInt("lama_hari"),
                                            jo.getString("supir"),
                                            jo.getInt("total_harga")
                                    ));
                                }
                                statusAdapter = new StatusAdapter(getContext(),statusModelList,progressDialog);
                                rv_status.setAdapter(statusAdapter);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                statusAdapter = new StatusAdapter(getContext(),statusModelList,progressDialog);
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
}