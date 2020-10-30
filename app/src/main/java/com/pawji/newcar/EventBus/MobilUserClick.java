package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.mobil.MobilModel;

public class MobilUserClick {
    MobilModel mobilModel;

    public MobilUserClick(MobilModel mobilModel) {
        this.mobilModel = mobilModel;
    }

    public MobilModel getMobilModel() {
        return mobilModel;
    }

    public void setMobilModel(MobilModel mobilModel) {
        this.mobilModel = mobilModel;
    }
}
