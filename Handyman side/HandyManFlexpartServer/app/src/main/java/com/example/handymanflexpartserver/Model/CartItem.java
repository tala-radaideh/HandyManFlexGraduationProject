package com.example.handymanflexpartserver.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CartItem {

    private String ServiceId;
    private String ServiceName;
    private String serviceImage;

    private String userPhone;

    private String uid;

    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(@NonNull String serviceId) {
        ServiceId = serviceId;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
