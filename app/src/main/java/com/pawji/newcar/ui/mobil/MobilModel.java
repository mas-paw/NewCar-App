package com.pawji.newcar.ui.mobil;

public class MobilModel {
    int kode_mobil,harga;
    String nama,image;

    public MobilModel() {
    }

    public MobilModel(int kode_mobil, String nama, String image,int harga) {
        this.kode_mobil = kode_mobil;
        this.harga = harga;
        this.nama = nama;
        this.image = image;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getKode_mobil() {
        return kode_mobil;
    }

    public void setKode_mobil(int kode_mobil) {
        this.kode_mobil = kode_mobil;
    }
}
