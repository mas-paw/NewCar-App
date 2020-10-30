package com.pawji.newcar.EventBus;

import com.pawji.newcar.ui.status.StatusModel;
import com.pawji.newcar.ui.status.StatusTempModel;

public class EditPemesananClick {
    StatusTempModel statusModel;

    public EditPemesananClick(StatusTempModel statusModel) {
        this.statusModel = statusModel;
    }

    public StatusTempModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(StatusTempModel statusModel) {
        this.statusModel = statusModel;
    }
}
