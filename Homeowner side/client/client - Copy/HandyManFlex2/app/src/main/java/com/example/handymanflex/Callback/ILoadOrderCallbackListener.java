package com.example.handymanflex.Callback;

import com.example.handymanflex.Model.OrderModel;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void  onLoadOrderSuccess(List<OrderModel> orderList);
    void  onLoadOrderFailed(String message );

}
