package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.supir.SupirModel;

public class TambahSupirClick {
    SupirModel supirModel;

    public SupirModel getSupirModel() {
        return supirModel;
    }

    public void setSupirModel(SupirModel supirModel) {
        this.supirModel = supirModel;
    }

    public TambahSupirClick(SupirModel supirModel) {
        this.supirModel = supirModel;
    }
}
