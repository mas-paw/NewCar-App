package com.pawji.newcar.ui.supir;

import android.app.ProgressDialog;
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
import com.pawji.newcar.Adapter.KaryawanAdapter;
import com.pawji.newcar.Adapter.SupirAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.TambahKaryawanClick;
import com.pawji.newcar.EventBus.TambahSupirClick;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.karyawan.KaryawanModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SupirFragment extends Fragment {
    List<SupirModel> supirModelList;
    ProgressDialog progressDialog;
    SwipeRefreshLayout srl_supir;
    SupirAdapter supirAdapter;
    RecyclerView rv_supir;
    Button btn_tambah;
    SupirModel supirModel = new SupirModel();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_supir, container, false);
        AndroidNetworking.initialize(getContext());
        supirModelList = new ArrayList<>();
        srl_supir = (SwipeRefreshLayout)root.findViewById(R.id.srl_supir);
        rv_supir = root.findViewById(R.id.recycler_supir);
        progressDialog = new ProgressDialog(getContext());
        rv_supir.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_supir.setLayoutManager(linearLayoutManager);
        srl_supir.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                srl_supir.setRefreshing(false);
            }
        });
        btn_tambah = root.findViewById(R.id.btn_tambah);

        scrollRefresh();
        tambahSupir();

//        final TextView textView = root.findViewById(R.id.text_gallery);
        return root;
    }

    private void tambahSupir() {
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Testing", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().postSticky(new TambahSupirClick(supirModel));
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
        supirModelList.clear();
        AndroidNetworking.get(Common.getSupir)
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
                                    supirModelList.add(new SupirModel(
                                            jo.getInt("id_supir"),
                                            jo.getString("nama"),
                                            jo.getInt("umur"),
                                            jo.getString("no_hp"),
                                            jo.getString("alamat")
                                    ));
                                }
                                supirAdapter = new SupirAdapter(getContext(),supirModelList,progressDialog);
                                rv_supir.setAdapter(supirAdapter);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                supirAdapter = new SupirAdapter(getContext(),supirModelList,progressDialog);
                                rv_supir.setAdapter(supirAdapter);
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