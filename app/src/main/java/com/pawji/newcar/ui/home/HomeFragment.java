package com.pawji.newcar.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.pawji.newcar.Adapter.MobilUserAdapter;
import com.pawji.newcar.Adapter.PilihanTerbaikAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.R;
import com.pawji.newcar.ui.mobil.MobilModel;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    PilihanTerbaikAdapter pilihanTerbaikAdapter;
    LayoutAnimationController layoutAnimationController;
    List<MobilModel> mobilModelList;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.image_1,R.drawable.image_2};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        AndroidNetworking.initialize(getContext());
        carouselView = root.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        recyclerView = root.findViewById(R.id.recycler_pilihan);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        mobilModelList = new ArrayList<>();
        loadViewPager();
        return root;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    };

    private void loadViewPager() {
        mobilModelList.clear();
        AndroidNetworking.get(Common.getRandomMobil)
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
                                pilihanTerbaikAdapter = new PilihanTerbaikAdapter(getContext(),mobilModelList);
                                recyclerView.setAdapter(pilihanTerbaikAdapter);
                                recyclerView.setLayoutAnimation(layoutAnimationController);
                            }else{
                                Toast.makeText(getContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                                pilihanTerbaikAdapter = new PilihanTerbaikAdapter(getContext(),mobilModelList);
                                recyclerView.setAdapter(pilihanTerbaikAdapter);
                                recyclerView.setLayoutAnimation(layoutAnimationController);
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
}