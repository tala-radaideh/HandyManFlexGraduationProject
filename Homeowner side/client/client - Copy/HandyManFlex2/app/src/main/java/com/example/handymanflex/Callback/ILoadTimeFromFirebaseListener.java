package com.example.handymanflex.Callback;

import com.example.handymanflex.Model.OrderModel;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderModel order, long estimateTimeInMs);
    void onLoadOnlyTimeSuccess(long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
