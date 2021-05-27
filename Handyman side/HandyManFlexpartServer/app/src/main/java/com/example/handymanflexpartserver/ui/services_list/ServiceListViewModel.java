package com.example.handymanflexpartserver.ui.services_list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Model.ServiceModel;
import java.util.List;

public class ServiceListViewModel extends ViewModel {
    private MutableLiveData<List<ServiceModel>> mutableLiveDataServiceList;

    public ServiceListViewModel() {
    }

    public MutableLiveData<List<ServiceModel>> getMutableLiveDataServiceList ()
    {
    if(mutableLiveDataServiceList == null)
    mutableLiveDataServiceList= new MutableLiveData<>();
    mutableLiveDataServiceList.setValue(Common.categorySelected.getServices());
    return mutableLiveDataServiceList;

    }
}
