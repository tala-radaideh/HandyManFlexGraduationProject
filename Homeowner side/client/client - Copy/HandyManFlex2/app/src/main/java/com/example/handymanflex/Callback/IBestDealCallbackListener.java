package com.example.handymanflex.Callback;

import com.example.handymanflex.Model.BestDealModel;

import java.util.List;

public interface IBestDealCallbackListener {


    void onBestDealLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);

}
