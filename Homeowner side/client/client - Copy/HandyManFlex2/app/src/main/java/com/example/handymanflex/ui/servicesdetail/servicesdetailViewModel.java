package com.example.handymanflex.ui.servicesdetail;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.CommentModel;
import com.example.handymanflex.Model.ServiceModel;
import com.google.firebase.database.MutableData;

import java.util.List;

public class servicesdetailViewModel extends ViewModel {

    private  static MutableLiveData<ServiceModel> mutableLiveDataservicedetail;
    private MutableLiveData<CommentModel> MutableLiveDatascomment;


    public void setServiceModel(ServiceModel serviceModel) {
        if (mutableLiveDataservicedetail != null)
            mutableLiveDataservicedetail.setValue(serviceModel);
    }


    public void setcommentModel(CommentModel commentModel) {
        if (MutableLiveDatascomment != null)
            MutableLiveDatascomment.setValue(commentModel);

    }

    public MutableLiveData<CommentModel> getMutableLiveDatascomment() {
        return MutableLiveDatascomment;
    }

    public servicesdetailViewModel() {
        MutableLiveDatascomment = new MutableLiveData<>();

    }

    public MutableLiveData<ServiceModel> getMutableLiveDataservicedetail() {
        if (mutableLiveDataservicedetail == null)
            mutableLiveDataservicedetail = new MutableLiveData<>();
        mutableLiveDataservicedetail.setValue(Common.selectedService);
        return mutableLiveDataservicedetail;
    }



}
