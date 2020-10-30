package com.pawji.newcar;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.EventBus.AddMobilClick;
import com.pawji.newcar.EventBus.AddStatusClick;
import com.pawji.newcar.EventBus.AddSupirClick;
import com.pawji.newcar.EventBus.DetailKaryawanClick;
import com.pawji.newcar.EventBus.TambahKaryawanClick;
import com.pawji.newcar.EventBus.TambahMobilClick;
import com.pawji.newcar.EventBus.TambahSupirClick;
import com.pawji.newcar.ui.karyawan.KaryawanModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavController navController;
    private AppBarConfiguration mAppBarConfiguration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout_home);
        NavigationView navigationView = findViewById(R.id.nav_view_home);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_karyawan, R.id.nav_supir,
                R.id.nav_mobil,R.id.nav_status,R.id.nav_report)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront(); //fixed bugs
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawer.closeDrawers();
        switch (menuItem.getItemId() ){
            case R.id.nav_karyawan:
                navController.navigate(R.id.nav_karyawan);
                break;
            case R.id.nav_supir:
                navController.navigate(R.id.nav_supir);
                break;
            case R.id.nav_mobil:
                navController.navigate(R.id.nav_mobil);
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
            case R.id.nav_status:
                navController.navigate(R.id.nav_status);
                break;
            case R.id.nav_report:
                navController.navigate(R.id.nav_report);
        }
        return false;
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Keluar").setMessage("Apakah Anda Ingin Keluar ?")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomeActivity.this,UserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onTambahClick(TambahKaryawanClick event){
        navController.navigate(R.id.nav_tambah_karyawan);
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onAddClick(AddKaryawanClick event){
        navController.navigate(R.id.nav_karyawan);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onAddSupirClick(AddSupirClick event){
        navController.navigate(R.id.nav_supir);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onTambahSupirClick(TambahSupirClick event) {
        navController.navigate(R.id.nav_tambah_supir);
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onAddMobilClick(AddMobilClick event){
        navController.navigate(R.id.nav_mobil);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onAddStatusClick(AddStatusClick event){
        navController.navigate(R.id.nav_status);
    }

    @Override
    public void onBackPressed() {
        signOut();
    }
}
