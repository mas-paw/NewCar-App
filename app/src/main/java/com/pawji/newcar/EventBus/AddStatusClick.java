package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.status.StatusModel;
import com.pawji.newcar.ui.status.StatusTempModel;

public class AddStatusClick {
    StatusModel statusModel;

    public AddStatusClick(StatusModel statusModel) {
        this.statusModel = statusModel;
    }

    public StatusModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(StatusModel statusModel) {
        this.statusModel = statusModel;
    }
}
