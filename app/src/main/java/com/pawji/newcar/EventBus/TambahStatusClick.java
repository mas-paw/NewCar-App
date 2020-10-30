package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.status.StatusTempModel;

public class TambahStatusClick {
    StatusTempModel statusTempModel;

    public TambahStatusClick(StatusTempModel statusTempModel) {
        this.statusTempModel = statusTempModel;
    }

    public StatusTempModel getStatusTempModel() {
        return statusTempModel;
    }

    public void setStatusTempModel(StatusTempModel statusTempModel) {
        this.statusTempModel = statusTempModel;
    }
}
