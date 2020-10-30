package com.pawji.newcar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.pawji.newcar.Adapter.MobilAdapter;
import com.pawji.newcar.Common.Common;
import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.EventBus.AddStatusUserClick;
import com.pawji.newcar.EventBus.EditPemesananClick;
import com.pawji.newcar.EventBus.HideFABWA;
import com.pawji.newcar.EventBus.HomeClick;
import com.pawji.newcar.EventBus.MobilUserClick;
import com.pawji.newcar.EventBus.TambahStatusClick;
import com.pawji.newcar.ui.mobil.MobilModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;
import dmax.dialog.SpotsDialog;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean doubleBackToExitPressedOnce = false;
    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    NavController navController;
    android.app.AlertDialog dialog;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_mobil_user, R.id.nav_status_user,
                R.id.nav_sign_out_user, R.id.login_admin)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawer.closeDrawers();
        switch (menuItem.getItemId() ){
            case R.id.nav_home:
                navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_mobil_user:
                navController.navigate(R.id.nav_mobil_user);
                break;
            case R.id.nav_status_user:
                navController.navigate(R.id.nav_status_user);
                break;
            case R.id.nav_sign_out_user:
                signOut();
                break;
            case R.id.login_admin:
                signInAdmin();
                break;
        }
        return false;
    }

    private void signInAdmin() {
        Intent home=new Intent(UserActivity.this, LoginActivity.class);
        startActivity(home);
    }

    private void sendMessage() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Kirim Pesan");
        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_whatsapp,null);
        final EditText edtpesan = (EditText)itemView.findViewById(R.id.edtpesan);

        //Set
        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //new WhatsappIntent(HomeActivity.this,edthp.getText().toString(),edtpesan.getText().toString());
                String realnumber = "6281387892122@s.whatsapp.net";
                Intent sendMessage = new Intent();
                sendMessage.setAction(Intent.ACTION_SEND);
                sendMessage.putExtra(Intent.EXTRA_TEXT,edtpesan.getText().toString());
                sendMessage.putExtra("jid",realnumber);
                sendMessage.setType("text/plain");
                sendMessage.setPackage("com.whatsapp");
                startActivity(sendMessage);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
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
                finish();
                System.exit(0);
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
    public void onMobilUserClck(MobilUserClick event){
        navController.navigate(R.id.nav_pemesanan);
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPemesananClick(TambahStatusClick event){
        navController.navigate(R.id.nav_status_user);
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHapusClick(AddStatusUserClick event){
        navController.navigate(R.id.nav_status_user);
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPemesananClickEdit(EditPemesananClick event){
        navController.navigate(R.id.nav_edit_pemesanan);
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPemesananSuccess(HomeClick event){
        navController.navigate(R.id.nav_home);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHideFABEvent(HideFABWA event){
        if(event.isHidden()){
            fab.hide();
        }else{
            fab.show();
        }
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            signOut();
        }else{
            super.onBackPressed();
        }

    }
}
