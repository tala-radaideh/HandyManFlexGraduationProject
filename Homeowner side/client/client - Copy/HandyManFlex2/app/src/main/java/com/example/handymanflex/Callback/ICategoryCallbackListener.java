package com.example.handymanflex.Callback;

import com.example.handymanflex.Model.CategoryModel;

import java.util.List;

public interface ICategoryCallbackListener {


    void onCategoryLoadSuccess(List<CategoryModel> categoryModels);
    void onCategoryLoadFailed(String message);
}
