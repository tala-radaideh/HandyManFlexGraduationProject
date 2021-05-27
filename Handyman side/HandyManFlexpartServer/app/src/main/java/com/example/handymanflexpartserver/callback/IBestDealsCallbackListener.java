package com.example.handymanflexpartserver.callback;

import com.example.handymanflexpartserver.Model.BestDealsModel;

import java.util.List;

public interface IBestDealsCallbackListener {

    void onListBestDealsLoadSuccess(List<BestDealsModel> bestDealsModels) ;
    void onListBestDealsLoadFailed(String message) ;

}
