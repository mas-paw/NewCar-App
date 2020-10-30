package com.pawji.newcar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pawji.newcar.EventBus.AddKaryawanClick;
import com.pawji.newcar.R;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends AppCompatActivity {
    EditText txt_username,txt_password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        txt_username = findViewById(R.id.txt_username);
        txt_username.setText("Admin");
        txt_password = findViewById(R.id.txt_password);
        txt_password.requestFocus();
        Button btn_submit  = findViewById(R.id.btn_login);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_username.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Masukkan Username", Toast.LENGTH_SHORT).show();
                }else if(txt_password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Masukkan Password", Toast.LENGTH_SHORT).show();
                }else{
                    Login();
                }
            }
        });
    }

    private void Login() {
        String user,pass;
        user = "Admin";
        pass = "008";
        if(txt_username.getText().toString().equals(user) && txt_password.getText().toString().equals(pass)){
            progressDialog.setMessage("Tunggu Sebentar...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Login Berhasil")
                            .setCancelable(false)
                            .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent home=new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(home);
                                    finish();
                                }
                            }).show();
                }
            },1000);
        }else{
            Toast.makeText(this, "Username Atau Password Salah", Toast.LENGTH_SHORT).show();
        }
    }
}
