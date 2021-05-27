package com.example.handymanflex.ui.view_orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.FtsOptions;

import com.example.handymanflex.Model.OrderModel;

import java.util.List;

public class ViewOrdersViewModel extends ViewModel {


    private MutableLiveData<List<OrderModel>> mutableLiveDataOrderList;

    public ViewOrdersViewModel() {
     mutableLiveDataOrderList= new MutableLiveData<>();
    }

    public MutableLiveData<List<OrderModel>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<OrderModel> orderList) {
       mutableLiveDataOrderList.setValue(orderList);

    }
}
