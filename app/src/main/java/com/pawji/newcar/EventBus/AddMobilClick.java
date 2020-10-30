package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.mobil.MobilModel;

public class AddMobilClick {
    MobilModel mobilModel = new MobilModel();

    public AddMobilClick(MobilModel mobilModel) {
        this.mobilModel = mobilModel;
    }

    public MobilModel getMobilModel() {
        return mobilModel;
    }

    public void setMobilModel(MobilModel mobilModel) {
        this.mobilModel = mobilModel;
    }
}
