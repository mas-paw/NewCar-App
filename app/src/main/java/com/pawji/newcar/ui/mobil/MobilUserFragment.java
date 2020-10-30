package com.pawji.newcar.ui.mobil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
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
import com.pawji.newcar.Adapter.MobilAdapter;
import com.pawji.newcar.Adapter.MobilUserAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MobilUserFragment extends Fragment {
    List<MobilModel> mobilModelList;
    ProgressDialog progressDialog;
    Button btn_tambah;
    RecyclerView rv_mobil;
    MobilUserAdapter mobilAdapter;
    MobilModel mobilModel = new MobilModel();
    LayoutAnimationController layoutAnimationController;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mobil_user, container, false);
        AndroidNetworking.initialize(getContext());
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        mobilModelList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        rv_mobil = root.findViewById(R.id.recycler_daftar_mobil);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_mobil.setLayoutManager(linearLayoutManager);
        getData();

        return root;
    }
    public void getData() {
        progressDialog.setMessage("Refresh Data");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                                mobilAdapter = new MobilUserAdapter(getContext(),mobilModelList,progressDialog);
                                rv_mobil.setAdapter(mobilAdapter);
                                rv_mobil.setLayoutAnimation(layoutAnimationController);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                mobilAdapter = new MobilUserAdapter(getContext(),mobilModelList,progressDialog);
                                rv_mobil.setAdapter(mobilAdapter);
                                rv_mobil.setLayoutAnimation(layoutAnimationController);
                            }
                        }catch(Exception e){
                            progressDialog.dismiss();
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

    @Override
    public void onResume() {
        super.onResume();
    }
}