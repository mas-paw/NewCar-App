package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.supir.SupirModel;

public class AddSupirClick {
    SupirModel supirModel;

    public SupirModel getSupirModel() {
        return supirModel;
    }

    public void setSupirModel(SupirModel supirModel) {
        this.supirModel = supirModel;
    }

    public AddSupirClick(SupirModel supirModel) {
        this.supirModel = supirModel;
    }
}
