package com.example.handymanflexpartserver.callback;

import com.example.handymanflexpartserver.Model.CategoryModel;
import com.example.handymanflexpartserver.Model.OrderModel;
import com.example.handymanflexpartserver.Model.RequestedOrderModel;

import java.util.List;

public interface  IOrderCallbackListener {
    void onOrderLoadSuccess(List<OrderModel> orderModelList);
    void onOrderLoadFailed(String message);
}
