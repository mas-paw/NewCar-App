package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.karyawan.KaryawanModel;

public class TambahKaryawanClick {
    private KaryawanModel karyawanModel;

    public TambahKaryawanClick(KaryawanModel karyawanModel) {
        this.karyawanModel = karyawanModel;
    }

    public KaryawanModel getKaryawanModel() {
        return karyawanModel;
    }

    public void setKaryawanModel(KaryawanModel karyawanModel) {
        this.karyawanModel = karyawanModel;
    }
}
