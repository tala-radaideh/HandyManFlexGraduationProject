package com.example.handymanflexpartserver.Model.EventBus;

public class ChangeMenuClick {
    private boolean isFromServiceList;

    public ChangeMenuClick(boolean isFromServiceList) {
        this.isFromServiceList = isFromServiceList;
    }


    public boolean isFromServiceList() {
        return isFromServiceList;
    }

    public void setFromServiceList(boolean fromServiceList) {
        isFromServiceList = fromServiceList;
    }
}
