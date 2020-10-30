package com.pawji.newcar.ui.status;

public class TransaksiModel {
    int no_transaksi,lama_hari,denda,total_harga;
    String nama,no_hp,no_ktp,nama_mobil,supir,tgl_sewa;

    public TransaksiModel(int no_transaksi, String nama, String no_hp, String no_ktp, String nama_mobil, String tgl_sewa, int lama_hari, String supir, int denda, int total_harga) {
        this.no_transaksi = no_transaksi;
        this.lama_hari = lama_hari;
        this.denda = denda;
        this.total_harga = total_harga;
        this.nama = nama;
        this.no_hp = no_hp;
        this.no_ktp = no_ktp;
        this.nama_mobil = nama_mobil;
        this.supir = supir;
        this.tgl_sewa = tgl_sewa;
    }

    public int getNo_transaksi() {
        return no_transaksi;
    }

    public void setNo_transaksi(int no_transaksi) {
        this.no_transaksi = no_transaksi;
    }

    public int getLama_hari() {
        return lama_hari;
    }

    public void setLama_hari(int lama_hari) {
        this.lama_hari = lama_hari;
    }

    public int getDenda() {
        return denda;
    }

    public void setDenda(int denda) {
        this.denda = denda;
    }

    public int getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(int total_harga) {
        this.total_harga = total_harga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(String no_ktp) {
        this.no_ktp = no_ktp;
    }

    public String getNama_mobil() {
        return nama_mobil;
    }

    public void setNama_mobil(String nama_mobil) {
        this.nama_mobil = nama_mobil;
    }

    public String getSupir() {
        return supir;
    }

    public void setSupir(String supir) {
        this.supir = supir;
    }

    public String getTgl_sewa() {
        return tgl_sewa;
    }

    public void setTgl_sewa(String tgl_sewa) {
        this.tgl_sewa = tgl_sewa;
    }
}
