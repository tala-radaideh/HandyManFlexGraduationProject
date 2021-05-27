package com.example.handymanflexpartserver.callback;

import com.example.handymanflexpartserver.Model.BestDealsModel;
import com.example.handymanflexpartserver.Model.MostPopularModel;

import java.util.List;

public interface IMostPopularCallbackListener {
    void onListMostPopularLoadSuccess(List<MostPopularModel> mostPopularModels) ;
    void onListMostPopularLoadFailed(String message) ;
}
