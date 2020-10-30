package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.status.StatusTempModel;

public class HomeClick {
    StatusTempModel statusTempModel;

    public HomeClick(StatusTempModel statusTempModel) {
        this.statusTempModel = statusTempModel;
    }

    public StatusTempModel getStatusTempModel() {
        return statusTempModel;
    }

    public void setStatusTempModel(StatusTempModel statusTempModel) {
        this.statusTempModel = statusTempModel;
    }
}
