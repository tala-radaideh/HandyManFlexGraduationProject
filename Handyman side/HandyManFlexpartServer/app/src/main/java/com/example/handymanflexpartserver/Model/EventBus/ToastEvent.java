package com.example.handymanflexpartserver.Model.EventBus;

import com.example.handymanflexpartserver.Common.Common;

public class ToastEvent {

    private Common.ACTION action;
    private boolean isFromServiceList;

    public ToastEvent(Common.ACTION action, boolean isFromServiceList) {
        this.action = action;
        this.isFromServiceList = isFromServiceList;
    }

    public Common.ACTION getAction() {
        return action;
    }

    public void setAction(Common.ACTION action) {
        this.action = action;
    }

    public boolean isFromServiceList() {
        return isFromServiceList;
    }

    public void setFromServiceList(boolean fromServiceList) {
        isFromServiceList = fromServiceList;
    }
}