package com.example.handymanflex.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflex.Model.CommentModel;
import com.example.handymanflex.Model.ServiceModel;

import java.util.List;

public class CommentViewHolder  extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataServiceList;

    public CommentViewHolder() {
        mutableLiveDataServiceList = new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getMutableLiveDataServiceList() {
        return mutableLiveDataServiceList;
    }
    public void setCommentList(List<CommentModel> commentList){
        mutableLiveDataServiceList.setValue(commentList);
    }

}
