package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.mobil.MobilModel;

public class TambahMobilClick {
    MobilModel mobilModel;

    public MobilModel getMobilModel() {
        return mobilModel;
    }

    public void setMobilModel(MobilModel mobilModel) {
        this.mobilModel = mobilModel;
    }

    public TambahMobilClick(MobilModel mobilModel) {
        this.mobilModel = mobilModel;
    }
}
