package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.status.StatusTempModel;

public class AddStatusUserClick {
    StatusTempModel statusModel;

    public AddStatusUserClick(StatusTempModel statusModel) {
        this.statusModel = statusModel;
    }

    public StatusTempModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(StatusTempModel statusModel) {
        this.statusModel = statusModel;
    }
}
