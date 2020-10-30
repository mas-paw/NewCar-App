package com.pawji.newcar.ui.supir;

public class SupirModel {
    int id_supir,umur;
    String nama,alamat,no_hp;

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public int getId_supir() {
        return id_supir;
    }

    public void setId_supir(int id_supir) {
        this.id_supir = id_supir;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public SupirModel() {
    }

    public SupirModel(int id_supir, String nama, int umur, String no_hp, String alamat) {
        this.id_supir = id_supir;
        this.umur = umur;
        this.nama = nama;
        this.alamat = alamat;
        this.no_hp = no_hp;
    }
}
