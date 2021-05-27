package com.example.handymanflexpartserver.Model;

public class RequestedOrderModel {
private String HandyManName, HandyManPhone,key;
private double currentLat,currrentLng;
private OrderModel orderModel;
private boolean isStart;

    public String getKey() {
        return key;
    }

    public RequestedOrderModel() {
    }

    public String getHandyManName() {
        return HandyManName;
    }

    public void setHandyManName(String handyManName) {
        HandyManName = handyManName;
    }

    public String getHandyManPhone() {
        return HandyManPhone;
    }

    public void setHandyManPhone(String handyManPhone) {
        HandyManPhone = handyManPhone;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrrentLng() {
        return currrentLng;
    }

    public void setCurrrentLng(double currrentLng) {
        this.currrentLng = currrentLng;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }
}
