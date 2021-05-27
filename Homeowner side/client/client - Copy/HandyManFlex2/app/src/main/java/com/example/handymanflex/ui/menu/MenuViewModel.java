package com.example.handymanflex.ui.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.example.handymanflex.Callback.ICategoryCallbackListener;

public class MenuViewModel extends ViewModel implements ICategoryCallbackListener {

private MutableLiveData<List<CategoryModel>> categoryListMultable;
private MutableLiveData<String> messageError= new MutableLiveData<>();
private ICategoryCallbackListener categoryCallbackListener;

    public MenuViewModel() {
        categoryCallbackListener=this;
    }


    public MutableLiveData<List<CategoryModel>> getCategoryListMultable() {
          if(categoryListMultable==null)
          {
              categoryListMultable=new MutableLiveData<>();
              messageError=new MutableLiveData<>();
              loadCategories();
          }
          return categoryListMultable;
    }

    public void loadCategories() {
        List<CategoryModel> tempList=new ArrayList<>();
        DatabaseReference categoryRef= FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot itemSnapShot:snapshot.getChildren())
                {
                    CategoryModel categoryModel = itemSnapShot.getValue(CategoryModel.class);
                    categoryModel.setMenu_id(itemSnapShot.getKey());
                    tempList.add(categoryModel);
                }
                categoryCallbackListener.onCategoryLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              categoryCallbackListener.onCategoryLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onCategoryLoadSuccess(List<CategoryModel> categoryModels) {
     categoryListMultable.setValue(categoryModels);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        messageError.setValue(message);
    }
}