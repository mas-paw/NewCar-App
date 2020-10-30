package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.karyawan.KaryawanModel;

public class DetailKaryawanClick {
    KaryawanModel karyawanModel;

    public KaryawanModel getKaryawanModel() {
        return karyawanModel;
    }

    public void setKaryawanModel(KaryawanModel karyawanModel) {
        this.karyawanModel = karyawanModel;
    }

    public DetailKaryawanClick(KaryawanModel karyawanModel) {
        this.karyawanModel = karyawanModel;
    }
}
