package com.example.handymanflexpartserver.callback;

import com.example.handymanflexpartserver.Model.CategoryModel;

import java.util.List;

public interface ICategoryCallbackListener {

    void onCategoryLoadSuccess(List<CategoryModel> categoryModels);
    void onCategoryLoadFailed(String message);

}
