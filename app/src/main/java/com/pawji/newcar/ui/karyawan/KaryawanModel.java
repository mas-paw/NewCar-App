package com.pawji.newcar.ui.karyawan;

public class KaryawanModel {
    int id_karyawan;
    String nama,tgl_lahir,jenis_kelamin,no_hp,jabatan,alamat,email;

    public KaryawanModel() {
    }

    public KaryawanModel(int id_karyawan, String nama, String tgl_lahir, String jenis_kelamin, String no_hp, String jabatan, String alamat, String email) {
        this.id_karyawan = id_karyawan;
        this.nama = nama;
        this.tgl_lahir = tgl_lahir;
        this.jenis_kelamin = jenis_kelamin;
        this.no_hp = no_hp;
        this.jabatan = jabatan;
        this.alamat = alamat;
        this.email = email;
    }

    public int getId_karyawan() {
        return id_karyawan;
    }

    public void setId_karyawan(int id_karyawan) {
        this.id_karyawan = id_karyawan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
