package com.example.handymanflex.ui.Serviceslist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.ServiceModel;

import java.util.List;

public class serviceslistViewModel extends ViewModel {

    private MutableLiveData<List<ServiceModel>> MutableLiveDataservicelist;

    public serviceslistViewModel() {

    }

    public MutableLiveData<List<ServiceModel>> getMutableLiveDataservicelist() {
        if(MutableLiveDataservicelist==null){
            MutableLiveDataservicelist = new MutableLiveData<>();
            MutableLiveDataservicelist.setValue(Common.categorySelected.getServices());

        }
        return MutableLiveDataservicelist;
    }
}