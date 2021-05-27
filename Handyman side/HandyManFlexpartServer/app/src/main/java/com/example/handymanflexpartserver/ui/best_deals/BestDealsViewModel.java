package com.example.handymanflexpartserver.ui.best_deals;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Model.BestDealsModel;
import com.example.handymanflexpartserver.callback.IBestDealsCallbackListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BestDealsViewModel extends ViewModel implements IBestDealsCallbackListener {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<BestDealsModel>> bestDealsListMutable;
    private IBestDealsCallbackListener ibestDealsCallbackListener ;

    public BestDealsViewModel() { ibestDealsCallbackListener = this; }

    public MutableLiveData<List<BestDealsModel>> getBestDealsListMutable() {
       if(bestDealsListMutable == null)
           bestDealsListMutable= new MutableLiveData<>();
       loadBestDeals();
        return bestDealsListMutable;
    }

    public void loadBestDeals() {
        List<BestDealsModel> temp = new ArrayList<>();
        DatabaseReference bestDealsRef= FirebaseDatabase.getInstance()
                .getReference(Common.BEST_DEALS);
        bestDealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             for(DataSnapshot bestdealsSnapShot : snapshot.getChildren())
             {
                 BestDealsModel bestDealsModel= bestdealsSnapShot.getValue(BestDealsModel.class);
                 bestDealsModel.setKey(bestdealsSnapShot.getKey());
                 temp.add(bestDealsModel);
             }
             ibestDealsCallbackListener.onListBestDealsLoadSuccess(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
           ibestDealsCallbackListener.onListBestDealsLoadFailed(error.getMessage());
            }
        });
    }


    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onListBestDealsLoadSuccess(List<BestDealsModel> bestDealsModels) {
       bestDealsListMutable.setValue(bestDealsModels);

    }

    @Override
    public void onListBestDealsLoadFailed(String message) {
   messageError.setValue(message);
    }
}