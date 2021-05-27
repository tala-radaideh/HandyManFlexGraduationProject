package com.example.handymanflex.Callback;
import com.example.handymanflex.Model.PopularCategoryModel;
import java.util.List;



public interface IPopularCallbackListener {


    void onPopularLoadSuccess(List<PopularCategoryModel>popularCategoryModels);
    void onPopularLoadFailed(String message);



}
