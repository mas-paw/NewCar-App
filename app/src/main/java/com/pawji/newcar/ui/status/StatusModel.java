package com.pawji.newcar.ui.status;

public class StatusModel {
    int no_transaksi,lama_hari,total_harga;
    String nama,no_ktp,no_hp,nama_mobil,tgl_sewa,supir,image;

    public StatusModel() {
    }

    public StatusModel(int no_transaksi, String nama,String image, String no_ktp, String no_hp, String nama_mobil, String tgl_sewa,int lama_hari, String supir,int total_harga) {
        this.no_transaksi = no_transaksi;
        this.lama_hari = lama_hari;
        this.total_harga = total_harga;
        this.nama = nama;
        this.no_ktp = no_ktp;
        this.no_hp = no_hp;
        this.nama_mobil = nama_mobil;
        this.tgl_sewa = tgl_sewa;
        this.supir = supir;
        this.image = image;
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

    public String getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(String no_ktp) {
        this.no_ktp = no_ktp;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getNama_mobil() {
        return nama_mobil;
    }

    public void setNama_mobil(String nama_mobil) {
        this.nama_mobil = nama_mobil;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTgl_sewa() {
        return tgl_sewa;
    }

    public void setTgl_sewa(String tgl_sewa) {
        this.tgl_sewa = tgl_sewa;
    }

    public String getSupir() {
        return supir;
    }

    public void setSupir(String supir) {
        this.supir = supir;
    }
}
